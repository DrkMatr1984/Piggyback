package me.blubdalegend.piggyback;

import java.util.logging.Logger;

import org.bukkit.plugin.PluginManager;

import me.blubdalegend.piggyback.config.ConfigAccessor;
import me.blubdalegend.piggyback.config.LanguageFile;
import me.blubdalegend.piggyback.config.ToggleLists;
import me.blubdalegend.piggyback.listeners.PiggybackEventsListener;
import me.blubdalegend.piggyback.listeners.BukkitListeners;
import me.blubdalegend.piggyback.listeners.LeftClickListener;
import me.blubdalegend.piggyback.listeners.RightClickListener;
import me.blubdalegend.piggyback.nms.NMStools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import me.blubdalegend.piggyback.commands.Commands;

public class Piggyback extends org.bukkit.plugin.java.JavaPlugin
{
  private static Piggyback plugin;
  private Logger log;
  private PluginManager pm;
  public ConfigAccessor config;
  public ToggleLists lists;
  public LanguageFile lang;
  
  public static List<UUID> toggleCooldownPlayers = new ArrayList<UUID>();
  public static List<UUID> emptyHandCooldownPlayers = new ArrayList<UUID>();
  public static List<UUID> noPermsCooldownPlayers = new ArrayList<UUID>();
  public static List<UUID> clickTimerCooldownPlayers = new ArrayList<UUID>();
  public static HashMap<UUID,Long> piggybackPickupCooldownPlayers = new HashMap<UUID, Long>();
  public static String version;
  public String clazzName;
  public String sendPacket;
  public int Version;
  public static Class<?> clazz;
  
  public void onEnable()
  {
	  plugin = this;
	  log = getLogger();
	  pm = getServer().getPluginManager();
	  version = NMStools.getNmsVersion().replace("_", "").toLowerCase();
	  getVersion();
	  initConfigs();	  
	  getCommand("pback").setExecutor(new Commands(this));
	  if(config.clickType.equals(ConfigAccessor.Clicks.RIGHT)){
		  this.pm.registerEvents(new RightClickListener(plugin), plugin); //done
	  }else{
		  this.pm.registerEvents(new LeftClickListener(plugin), plugin);  // not done
	  }
	  this.pm.registerEvents(new BukkitListeners(), plugin);
	  this.pm.registerEvents(new PiggybackEventsListener(plugin), plugin); //not done
	  this.log.info("Piggyback v" + plugin.getDescription().getVersion() + " enabled!");  
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
