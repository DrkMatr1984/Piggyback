package me.blubdalegend.piggyback;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
  
  public static String version;
  public String clazzName;
  public String sendPacket;
  public int Version;
  public static Class<?> clazz;
  
  public void onEnable()
  {
	plugin = this;
	version = getNmsVersion().replace("_", "").toLowerCase();
	getVersion();
	config = new ConfigAccessor(plugin);
    config.initConfig();
    this.pm.registerEvents(new Events(plugin), plugin);
    this.log.info("Piggyback enabled!");  
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
		        			if(!((config.prefix + " " + config.notAPlayer).equals(" "))){
		        				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.prefix + " " + config.notAPlayer));
		        			}
		        			return true;
		                }
		        		Player p = (Player)sender;
		        		if (!config.disabledPlayers.contains(p.getUniqueId().toString())) {
		        			if(!((plugin.config.prefix + " " + plugin.config.toggleOff).equals(" "))){
		        				p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.config.prefix + " " + plugin.config.toggleOff));
		        			}
		                    config.disabledPlayers.add(p.getUniqueId().toString());
		                } else {
		                	if(!((plugin.config.prefix + " " + plugin.config.toggleOn).equals(" "))){
		                		p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.config.prefix + " " + plugin.config.toggleOn));
		                	}
		                    config.disabledPlayers.remove(p.getUniqueId().toString());
		                }
		                return true;
		        	}else{
		        		if(!((plugin.config.prefix + " " + plugin.config.noPerms).equals(" "))){
		        			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.config.prefix + " " + plugin.config.noPerms));
		        		}	
		        	}
		        }
		  }
	  }catch (Exception e) {
		  if(!((plugin.config.prefix + " " + plugin.config.error).equals(" "))){
			  sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.config.prefix + " " + plugin.config.error));
		  }
	  }
	  return false;
  }
  
  private void getVersion()
  {
	String baseVersion = version.substring(1 ,3);
	Version = Integer.parseInt(baseVersion);
	Integer i = Version;
	getLogger().info("Version : " + i.toString());
	if(this.Version < 19 && this.Version > 11){
		version = "pre1_9";
	}    
  }
  
  private String getNmsVersion()
  {
    return Bukkit.getServer().getClass().getPackage().getName().replace("org.bukkit.craftbukkit.", "");
  }
}
