package me.blubdalegend.piggyback;

import java.lang.reflect.Field;
import java.util.*;
import java.util.logging.Logger;

import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.PluginManager;

import me.blubdalegend.piggyback.config.ConfigAccessor;
import me.blubdalegend.piggyback.config.LanguageFile;
import me.blubdalegend.piggyback.config.ToggleLists;
import me.blubdalegend.piggyback.listeners.PiggybackEventsListener;
import me.blubdalegend.piggyback.listeners.BukkitListeners;
import me.blubdalegend.piggyback.listeners.PickupClickListener;
import me.blubdalegend.piggyback.compatibility.Console;
//import me.blubdalegend.piggyback.compatibility.DenyPiggybackFlag;
import me.blubdalegend.piggyback.compatibility.WorldGuardHook;

import me.blubdalegend.piggyback.commands.Commands;

public class Piggyback extends org.bukkit.plugin.java.JavaPlugin
{
  private static Piggyback plugin;
  public ConfigAccessor config;
  public ToggleLists lists;
  public LanguageFile lang;
  
  public static List<UUID> toggleCooldownPlayers = new ArrayList<>();
  public static List<UUID> emptyHandCooldownPlayers = new ArrayList<>();
  public static List<UUID> noPermsCooldownPlayers = new ArrayList<>();
  public static HashMap<UUID,Long> piggybackPickupCooldownPlayers = new HashMap<>();
  public static HashMap<UUID,Long> piggybackRideCooldownPlayers = new HashMap<>();
  public static HashMap<UUID, List<Entity>> passengers = new HashMap<>();
  private WorldGuardHook wgHook;
  //private DenyPiggybackFlag plotSquared;
  public String clazzName;
  public String sendPacket;
  public static Class<?> clazz;
  private Logger log;
  private Commands commands;
  
  private static CommandMap cmap;   
  private CCommand command;
  
  public void onEnable()
  {
	  plugin = this;
      log = getLogger();
      PluginManager pm = getServer().getPluginManager();
	  initConfigs();
	  commands = new Commands(this);
	  this.RegisterCommands();
	  pm.registerEvents(new PickupClickListener(plugin), plugin);
	  pm.registerEvents(new BukkitListeners(), plugin);
	  pm.registerEvents(new PiggybackEventsListener(plugin), plugin);
      new PiggybackAPI(plugin);
      if(Bukkit.getPluginManager().isPluginEnabled("MVdWPlaceholderAPI")) {
      	  log.info("Hooked into MVdWPlaceholderAPI!");
      	  new me.blubdalegend.piggyback.placeholders.mvdwPlaceholderAPI();
      }
      if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
      	log.info("Hooked into PlaceholderAPI!");
      	new me.blubdalegend.piggyback.placeholders.PlaceholderAPI(plugin).register();
      }
      /*if(Bukkit.getPluginManager().isPluginEnabled("PlotSquared")) {
    	  plotSquared = new DenyPiggybackFlag(false);
    	  com.plotsquared.core.plot.flag.GlobalFlagContainer.getInstance().addFlag(plotSquared);
    	  log.info("Hooked into PlotSquared! Flag is deny-piggyback");
      }*/
      if(this.config.bStats) {
          new Metrics(this, 15398);
          log.info("Starting bStats...");
      }      
      log.info("Piggyback v" + plugin.getDescription().getVersion() + " enabled!");
  }
  
  @Override
  public void onLoad() {
  	// Worldguard Hook
      if (Bukkit.getServer().getPluginManager().getPlugin("WorldGuard") != null) {
          wgHook = new WorldGuardHook();
          Console.sendConsoleMessage(String.format(ChatColor.GRAY + "[" + ChatColor.YELLOW + "Piggyback" + ChatColor.GRAY + "]" 
          + ChatColor.GREEN + " hooked into worldguard! Flag allow-piggyback registered. Set \"allow-piggyback = DENY\" to deny piggyback in regions."));
      }
  }
  
  public void onDisable(){
	  lists.saveData();
	  if(!lists.isYML()) {
		  lists.closeMySQLConnection();
	  }
	  this.unRegisterCommands();
	  plugin.getPluginLoader().disablePlugin(plugin);
  }
  
  public void initConfigs(){
	  config = new ConfigAccessor(plugin);
	  lang = new LanguageFile(plugin);
	  lists = new ToggleLists(plugin);
  }
  
  public CommandMap getCommandMap() {
      return cmap;
  }
    
  public class CCommand extends Command {
      private CommandExecutor exe = null;
      
      protected CCommand(String name) {
        super(name);
      }
      
      public boolean execute(CommandSender sender, String commandLabel, String[] args) {
          if (this.exe != null)
              this.exe.onCommand(sender, this, commandLabel, args); 
          return false;
      }
      
      public void setExecutor(CommandExecutor exe) {
          this.exe = exe;
      }
      
  }
  
  private void RegisterCommands() {
      String cbukkit = Bukkit.getServer().getClass().getPackage().getName() + ".CraftServer";
      try {
        Class<?> clazz = Class.forName(cbukkit);
        try {
          Field f = clazz.getDeclaredField("commandMap");
          f.setAccessible(true);
          cmap = (CommandMap)f.get(Bukkit.getServer());
          if (!lang.command.equals(null)) {
            this.command = new CCommand(lang.command);
            if(!cmap.register("pback", this.command)) {
            	Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', lang.prefix + " &aCommand &e" + lang.command + " &chas already been taken. Defaulting to &e'pback' &cfor PiggyBack command."));
            }else {
          	  Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', lang.prefix + " &aCommand &e" + lang.command + " &aRegistered!"));
            }
            this.command.setExecutor(this.commands);        
          } 
        } catch (Exception e) {
          e.printStackTrace();
        } 
      } catch (ClassNotFoundException e) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', lang.prefix + " &ccould not be loaded, is this even Spigot or CraftBukkit?"));
        setEnabled(false);
      } 
    }
    
    private void unRegisterCommands() {
        String cbukkit = Bukkit.getServer().getClass().getPackage().getName() + ".CraftServer";
        try {
            Class<?> clazz = Class.forName(cbukkit);
            try {
                Field f = clazz.getDeclaredField("commandMap");
                f.setAccessible(true);
                cmap = (CommandMap)f.get(Bukkit.getServer());
                if (!this.command.equals(null)) {
                    this.command.unregister(cmap);
                    Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', lang.prefix + " &aCommand " + lang.command + " Unregistered!"));
                } 
            } catch (Exception e) {
            e.printStackTrace();
            } 
        } catch (ClassNotFoundException e) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', lang.prefix + " &ccould not be unloaded, is this even Spigot or CraftBukkit?"));
            setEnabled(false);
        } 
  }
  
  public static Piggyback getPlugin()
  {
	  return plugin;
  }

  public WorldGuardHook getWgHook() {
	  return wgHook;
  }
  
  /*public DenyPiggybackFlag getPlotSquared() {
	  return plotSquared;
  }*/
}
