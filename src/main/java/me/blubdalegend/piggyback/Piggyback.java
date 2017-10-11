package me.blubdalegend.piggyback;

import java.util.logging.Logger;

import org.bukkit.plugin.PluginManager;

import me.blubdalegend.piggyback.config.ConfigAccessor;
import me.blubdalegend.piggyback.listeners.Events;
import me.blubdalegend.piggyback.nms.NMStools;
import me.blubdalegend.piggyback.commands.Commands;

public class Piggyback extends org.bukkit.plugin.java.JavaPlugin
{
  private static Piggyback plugin;
  private Logger log = getLogger();
  private PluginManager pm = getServer().getPluginManager();
  public ConfigAccessor config;
  
  public static String version;
  public String clazzName;
  public String sendPacket;
  public int Version;
  public static Class<?> clazz;
  
  public void onEnable()
  {
	  plugin = this;
	  version = NMStools.getNmsVersion().replace("_", "").toLowerCase();
	  getVersion();
	  config = new ConfigAccessor(plugin);
	  config.initConfig();
	  getCommand("pback").setExecutor(new Commands(this));
	  this.pm.registerEvents(new Events(plugin), plugin);
	  this.log.info("Piggyback enabled!");  
  }
  
  public void onDisable(){
	  config.saveUserList();
	  plugin.getPluginLoader().disablePlugin(plugin);
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
