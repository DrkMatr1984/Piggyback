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
				if (!(sender instanceof Player)) {
					if(!((plugin.lang.prefix + " " + plugin.lang.notAPlayer).equals(" "))){
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.lang.prefix + " " + plugin.lang.notAPlayer));
	        		}
					return true;
				}
				if ((args.length == 0) || (args.equals(null)))
			    {
					displayHelp((Player)sender);			
			    }else if(args.length > 0){
					for(String s : args){
						if((s.toLowerCase()).equals("toggle")){
							if(sender.hasPermission("piggyback.toggle") || sender.isOp()){
					        	Player p = (Player)sender;
					       		if (!plugin.lists.disabledPlayers.contains(p.getUniqueId().toString())) {
					       			if(!((plugin.lang.prefix + " " + plugin.lang.toggleOff).equals(" "))){
					       				p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.lang.prefix + " " + plugin.lang.toggleOff));
					       			}
					       			plugin.lists.disabledPlayers.add(p.getUniqueId().toString());
					       		} else {
					               	if(!((plugin.lang.prefix + " " + plugin.lang.toggleOn).equals(" "))){
					               		p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.lang.prefix + " " + plugin.lang.toggleOn));
					               	}
					               	plugin.lists.disabledPlayers.remove(p.getUniqueId().toString());
					            }
					       		return true;
					        }else{
					       		if(!((plugin.lang.prefix + " " + plugin.lang.noPerms).equals(" "))){
					       			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.lang.prefix + " " + plugin.lang.noPerms));
					       		}	
					       	}
						}
						if((s.toLowerCase()).equals("messages")){
							if(sender.hasPermission("piggyback.messages") || sender.isOp()){
								Player p = (Player)sender;
					       		if (!plugin.lists.messagePlayers.contains(p.getUniqueId().toString())) {
					       			if(!((plugin.lang.prefix + " " + plugin.lang.messageOff).equals(" "))){
					       				p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.lang.prefix + " " + plugin.lang.messageOff));
					       			}
					       			plugin.lists.messagePlayers.add(p.getUniqueId().toString());
					       		} else {
					               	if(!((plugin.lang.prefix + " " + plugin.lang.messageOn).equals(" "))){
					               		p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.lang.prefix + " " + plugin.lang.messageOn));
					               	}
					               	plugin.lists.messagePlayers.remove(p.getUniqueId().toString());
					            }
					       		return true;
							}else{
					       		if(!((plugin.lang.prefix + " " + plugin.lang.noPerms).equals(" "))){
					       			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.lang.prefix + " " + plugin.lang.noPerms));
					       		}	
					       	}
						}
						if((s.toLowerCase()).equals("help")){
							displayHelp((Player)sender);
						}
						if((s.toLowerCase()).equals("reload")){
							if(sender.hasPermission("piggyback.reload")){
								plugin.lang.loadLanguageFile();
								plugin.config.loadConfig();
								if(!((plugin.lang.prefix + " " + plugin.lang.reload).equals(" "))){
					       			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.lang.prefix + " " + plugin.lang.reload));
					       		}
							}else{
					       		if(!((plugin.lang.prefix + " " + plugin.lang.noPerms).equals(" "))){
					       			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.lang.prefix + " " + plugin.lang.noPerms));
					       		}	
					       	}
						}
					}
				}
			}
		}catch (Exception e) {
			if(!((plugin.lang.prefix + " " + plugin.lang.error).equals(" "))){
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.lang.prefix + " " + plugin.lang.error));
			}
		}
		return false;
	}
	
	public void displayHelp(Player player){
		player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5&o#### &r" + plugin.lang.title + " " + plugin.lang.help + "&r &5&o####"));
		player.sendMessage("");
		if(player.hasPermission("piggyback.help") || player.isOp())
			player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&7/&epback &7- &r" + plugin.lang.helpMain));
		if(player.hasPermission("piggyback.toggle") || player.isOp())
			player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&7/&epback &btoggle &7- &r" + plugin.lang.helpToggle));
		if(player.hasPermission("piggyback.messages") || player.isOp())
			player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&7/&epback &3messages &7- &r" + plugin.lang.helpMessageToggle));
		if(player.hasPermission("piggyback.reload") || player.isOp())
			player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&7/&epback &8reload &7- &r" + plugin.lang.helpReload));
		player.sendMessage("");
		int j = ((plugin.lang.title + " " + plugin.lang.help).length());
		String s = "";
		for(int i = 0; i<j; i++){
			s = s + "#";
		}
		player.sendMessage(ChatColor.translateAlternateColorCodes('&',"  &7&lSHIFT+" + (plugin.config.clickType.toString()).toUpperCase() + "-CLICK"));
		player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5&o####" + s));	
	}
}
