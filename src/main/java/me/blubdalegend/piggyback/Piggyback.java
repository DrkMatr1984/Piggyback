package me.blubdalegend.piggyback;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
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
import me.blubdalegend.piggyback.config.MySQLStorage;
import me.blubdalegend.piggyback.config.ToggleLists;
import me.blubdalegend.piggyback.listeners.PiggybackEventsListener;
import me.blubdalegend.piggyback.listeners.EntityInteractListener;
import me.blubdalegend.piggyback.listeners.BukkitListeners;
import me.blubdalegend.piggyback.listeners.PickupClickListener;
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
	private CCommand command = null;
  
	public void onEnable()
	{
		plugin = this;
		log = getLogger();
		PluginManager pm = getServer().getPluginManager();
		initConfigs();
		commands = new Commands(this);
		this.RegisterCommands();
		pm.registerEvents(new EntityInteractListener(plugin), plugin);
		pm.registerEvents(new PickupClickListener(plugin), plugin);
		pm.registerEvents(new PiggybackEventsListener(plugin), plugin);
		pm.registerEvents(new BukkitListeners(), plugin);	  
		new PiggybackAPI(plugin);
		if(Bukkit.getPluginManager().isPluginEnabled("MVdWPlaceholderAPI")) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6Piggyback&7] &aHooked into MVdWPlaceholderAPI!"));
			new me.blubdalegend.piggyback.placeholders.mvdwPlaceholderAPI();
		}
     	if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
     		Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6Piggyback&7] &aHooked into PlaceholderAPI!"));
     		new me.blubdalegend.piggyback.placeholders.PlaceholderAPI(plugin).register();
    	 }
     	/*if(Bukkit.getPluginManager().isPluginEnabled("PlotSquared")) {
    	  	plotSquared = new DenyPiggybackFlag(false);
    	  	com.plotsquared.core.plot.flag.GlobalFlagContainer.getInstance().addFlag(plotSquared);
    	  	log.info("Hooked into PlotSquared! Flag is deny-piggyback");
      	}*/
     	checkForUpdates();
     	if(this.config.bStats) {
     		new Metrics(this, 15398);
     		log.info("Starting bStats...");
     	}      
     	Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6Piggyback&7] &aPiggyback v" + plugin.getDescription().getVersion() + " enabled!"));
	}
  
  @Override
  public void onLoad() {
  	// Worldguard Hook
      if (Bukkit.getServer().getPluginManager().getPlugin("WorldGuard") != null) {
          wgHook = new WorldGuardHook();
          Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&e* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *"));
          Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6Piggyback&7] &ahooked into &fWorldGuard! &aFlag &fallow-piggyback&a registered. Set"));
          Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6Piggyback&7] \"&fallow-piggyback = DENY\" &ato deny &6Piggyback &ain regions."));
          Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&e* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *"));
      }
  }
  
  @Override
  public void onDisable(){	  
	  super.onDisable();
	  if(this.lists!=null)
	      lists.saveData();
	  Bukkit.getScheduler().cancelTasks(this);
	  if(!this.config.storageType.equalsIgnoreCase("yml")) {
		  try {
			  MySQLStorage.closeConnection();
			  Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6Piggyback&7] &b" + this.config.storageType.toUpperCase() + " &adatabase connection closed successfully!"));
		  } catch (SQLException e) {
			  // TODO Auto-generated catch block
			  e.printStackTrace();
		  }
	  }	  	  
	  this.unRegisterCommands();
	  try {
		  plugin.getPluginLoader().disablePlugin(plugin);
	  }catch (java.lang.IllegalStateException e) {
		  
	  }  
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
  						Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6Piggyback&7] &aCommand &e" + lang.command + " &chas already been taken. Defaulting to &e'pback' &cfor PiggyBack command."));
  					}else {
  						Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6Piggyback&7] &aCommand &e" + lang.command + " &aRegistered!"));
  					}
  					this.command.setExecutor(this.commands);        
  				} 
  			} catch (Exception e) {
  				e.printStackTrace();
  			} 
    	 } catch (ClassNotFoundException e) {
    		 Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6Piggyback&7]  &ccould not be loaded, is this even Spigot or CraftBukkit?"));
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
                if (this.command!=null) {
                    this.command.unregister(cmap);
                    Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6Piggyback&7] &aCommand &e" + lang.command + " &aUnregistered!"));
                } 
            } catch (Exception e) {
            e.printStackTrace();
            } 
        } catch (ClassNotFoundException e) {
        	Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6Piggyback&7] &ccould not be unloaded, is this even Spigot or CraftBukkit?"));
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
  
    @SuppressWarnings("deprecation")
    public void checkForUpdates() {
    	try {
    		URL url = new URL("https://api.spigotmc.org/legacy/update.php?resource=14130");
    		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    		connection.setRequestMethod("GET");

    		try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
    			String latestVersion = reader.readLine();

    			if (!plugin.getDescription().getVersion().equalsIgnoreCase(latestVersion)) {
    				Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6Piggyback&7] &aAn update is available! &eLatest: &6Piggyback &fv" + latestVersion));
    				Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6Piggyback&7] &aGet it Here! &7: "));
    				Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6Piggyback&7] &e* * * &fhttps://www.spigotmc.org/resources/piggyback-more-than-just-a-stacker.14130/ &e* * *"));
    			} else {
    				Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6Piggyback&7] &aYou are using the latest version."));
    			}
    		}
    	} catch (Exception e) {
    		Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6Piggyback&7] &cCould not check for updates: "));
    		e.printStackTrace();
    	}
    }
  
    /*public DenyPiggybackFlag getPlotSquared() {
	  	return plotSquared;
  	}*/
}
