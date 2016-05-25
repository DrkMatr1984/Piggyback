package me.blubdalegend.piggyback;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

public class Piggyback extends org.bukkit.plugin.java.JavaPlugin
{
  public Piggyback plugin;
  private Logger log = getLogger();
  private PluginManager pm = getServer().getPluginManager();
  public ConfigAccessor config;
  
  public String version;
  public String clazzName;
  public String sendPacket;
  public int Version;
  public static Class<?> clazz;
  
  public void onEnable()
  {
	plugin = this; 
	config = new ConfigAccessor(plugin);
    config.initConfig();
    this.version = getNmsVersion().replace("_", "").toLowerCase();
    if(!checkCompat()){
    	this.setEnabled(false);
    }else{
    	this.pm.registerEvents(new Events(plugin), plugin);
        this.log.info("Piggyback enabled!");
    }
  }
  
  public void onDisable(){
	  config.saveUserList();
	  plugin.getPluginLoader().disablePlugin(plugin);
  }
  
  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
	  try{
		  if (cmd.getName().equalsIgnoreCase("pback")) {
		        if ((args.length == 0) || (args.equals(null)))
		        {
		        	if(sender.hasPermission("piggyback.toggle") || sender.isOp()){
		        		if (!(sender instanceof Player)) {
		                    sender.sendMessage(config.prefix + " " + config.notAPlayer);
		                    return true;
		                }
		        		Player p = (Player)sender;
		        		if (!config.disabledPlayers.contains(p.getUniqueId().toString())) {
		                    p.sendMessage(plugin.config.prefix + " " + plugin.config.toggleOff);
		                    config.disabledPlayers.add(p.getUniqueId().toString());
		                } else {
		                    p.sendMessage(plugin.config.prefix + " " + plugin.config.toggleOn);
		                    config.disabledPlayers.remove(p.getUniqueId().toString());
		                }
		                return true;
		        	}else{
		        		sender.sendMessage(plugin.config.prefix + " " + plugin.config.noPerms);
		        	}
		        }
		  }
	  }catch (Exception e) {
	      sender.sendMessage(plugin.config.prefix + " " + plugin.config.error);
	  }
	  return false;
  }
  
  private boolean checkCompat()
  {
	String baseVersion = this.version.substring(1 ,3);
	Version = Integer.parseInt(baseVersion);
	if(this.Version < 19){
		this.version = "pre1_9";
		this.clazzName = (getClass().getPackage().getName() + ".nms." + this.version + ".SendPacketTask");
	}
	if(this.Version > 18){
		this.clazzName = (getClass().getPackage().getName() + ".nms." + getNmsVersion().toLowerCase() + ".SendPacketTask");
	}
    
    try {
      clazz = Class.forName(this.clazzName);
      if (AbstractPacketTask.class.isAssignableFrom(clazz)) {
        return true;
      }
      getLogger().log(Level.WARNING, "PiggyBack could not be loaded, version {" + getNmsVersion().toLowerCase() + "} is not supported yet!");
      setEnabled(false);
      return false;
    } catch (ClassNotFoundException e) {
      getLogger().log(Level.WARNING, "PiggyBack could not be loaded, version {" + getNmsVersion().toLowerCase() + "} is not supported yet!");
      setEnabled(false);
      return false;
    }
  }
  
  private String getNmsVersion()
  {
    return Bukkit.getServer().getClass().getPackage().getName().replace("org.bukkit.craftbukkit.", "");
  }
}
