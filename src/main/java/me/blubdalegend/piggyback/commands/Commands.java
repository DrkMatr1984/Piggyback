package me.blubdalegend.piggyback.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.blubdalegend.piggyback.Piggyback;

public class Commands implements CommandExecutor
{
	private Piggyback plugin;
	
    public Commands(Piggyback plugin){
    	this.plugin = plugin; 
    }
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		try{
			if (cmd.getName().equalsIgnoreCase("pback")) {
				if ((args.length == 0) || (args.equals(null)))
			    {
					if(sender.hasPermission("piggyback.toggle") || sender.isOp()){
						if (!(sender instanceof Player)) {
							if(!((plugin.config.prefix + " " + plugin.config.notAPlayer).equals(" "))){
								sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.config.prefix + " " + plugin.config.notAPlayer));
			        		}
							return true;
						}
			        	Player p = (Player)sender;
			       		if (!plugin.config.disabledPlayers.contains(p.getUniqueId().toString())) {
			       			if(!((plugin.config.prefix + " " + plugin.config.toggleOff).equals(" "))){
			       				p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.config.prefix + " " + plugin.config.toggleOff));
			       			}
			       			plugin.config.disabledPlayers.add(p.getUniqueId().toString());
			       		} else {
			               	if(!((plugin.config.prefix + " " + plugin.config.toggleOn).equals(" "))){
			               		p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.config.prefix + " " + plugin.config.toggleOn));
			               	}
			               	plugin.config.disabledPlayers.remove(p.getUniqueId().toString());
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
}
