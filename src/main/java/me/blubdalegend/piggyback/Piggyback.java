package me.blubdalegend.piggyback;

import java.util.logging.Logger;

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
  
  public void onEnable()
  {
	plugin = this; 
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
}
