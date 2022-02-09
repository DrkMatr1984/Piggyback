package me.blubdalegend.piggyback;

import java.util.*;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.PluginManager;

import me.blubdalegend.piggyback.config.ConfigAccessor;
import me.blubdalegend.piggyback.config.LanguageFile;
import me.blubdalegend.piggyback.config.ToggleLists;
import me.blubdalegend.piggyback.listeners.PiggybackEventsListener;
import me.blubdalegend.piggyback.listeners.BukkitListeners;
import me.blubdalegend.piggyback.listeners.PickupClickListener;
import me.blubdalegend.piggyback.nms.NMStools;
import me.drkmatr1984.customevents.CustomEvents;

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
  public static List<UUID> clickTimerCooldownPlayers = new ArrayList<>();
  public static HashMap<UUID,Long> piggybackPickupCooldownPlayers = new HashMap<>();
  public static HashMap<UUID, List<Entity>> passengers = new HashMap<>();
  public static String version;
  public String clazzName;
  public String sendPacket;
  public int Version;
  public static Class<?> clazz;
  
  public void onEnable()
  {
	  plugin = this;
      Logger log = getLogger();
      PluginManager pm = getServer().getPluginManager();
	  version = NMStools.getNmsVersion().replace("_", "").toLowerCase();
	  getVersion();
	  initConfigs();	  
	  Objects.requireNonNull(getCommand("pback")).setExecutor(new Commands(this));
	  new CustomEvents(plugin, false, true, false, false, false).initializeLib();
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
	  log.info("Piggyback v" + plugin.getDescription().getVersion() + " enabled!");
  }
  
  public void onDisable(){
	  lists.saveUserList();
	  plugin.getPluginLoader().disablePlugin(plugin);
  }
  
  public void initConfigs(){
	  config = new ConfigAccessor(plugin);
	  config.initConfig();
	  lists = new ToggleLists(plugin);
	  lists.initLists();
	  lang = new LanguageFile(plugin);
	  lang.initLanguageFile();
  }
  
  private void getVersion()
  {
	  String baseVersion = version.substring(1 ,3);
	  Version = Integer.parseInt(baseVersion);
	  if(this.Version < 19 && this.Version > 11){
		  version = "pre1_9";
	  }    
  }
  
  public static Piggyback getPlugin()
  {
	  return plugin;
  }
}
