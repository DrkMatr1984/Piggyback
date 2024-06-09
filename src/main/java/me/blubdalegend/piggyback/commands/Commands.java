package me.blubdalegend.piggyback.commands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
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
			if (cmd.getName().equalsIgnoreCase(plugin.lang.command)) {
				if ((args.length == 0) || (args.equals(null)))
			    {
					displayHelp(sender);
					return true;
			    }else if(args.length == 1){
					String s = args[0];					
					if((s.toLowerCase()).equals(plugin.lang.helpCommand)){
						displayHelp(sender);
						return true;
					}
					if((s.toLowerCase()).equals(plugin.lang.reloadCommand)){
						if(sender.hasPermission("piggyback.reload") || sender.isOp() || !(sender instanceof Player)){
							plugin.lang.initLanguageFile();
							plugin.config.initConfig();
							if(!((plugin.lang.prefix + " " + plugin.lang.reload).equals(" "))){
					    		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.lang.prefix + " " + plugin.lang.reload));
					    	}
							return true;
						}else{
					   		if(!((plugin.lang.prefix + " " + plugin.lang.noPerms).equals(" "))){
					   			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.lang.prefix + " " + plugin.lang.noPerms));
					   		}
					   		return true;
					   	}
					}	
					if((s.toLowerCase()).equals(plugin.lang.toggleCommand)){
						if(sender.hasPermission("piggyback.toggle") || sender.isOp()){
							if(sender instanceof Player) {
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
							}else {
								if(!((plugin.lang.prefix + " " + plugin.lang.notAPlayer).equals(" "))){
									sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.lang.prefix + " " + plugin.lang.notAPlayer));
				        		}
								return true;
							}   	
					    }else{
					    	if(!((plugin.lang.prefix + " " + plugin.lang.noPerms).equals(" "))){
					       		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.lang.prefix + " " + plugin.lang.noPerms));
					       	}
					    	return true;
					    }
					}
					if((s.toLowerCase()).equals(plugin.lang.messagesCommand)){
						if(sender.hasPermission("piggyback.messages") || sender.isOp()){
							if(sender instanceof Player) {
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
							}else {
								if(!((plugin.lang.prefix + " " + plugin.lang.notAPlayer).equals(" "))){
									sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.lang.prefix + " " + plugin.lang.notAPlayer));
				        		}
								return true;
							}
						}else{
					   		if(!((plugin.lang.prefix + " " + plugin.lang.noPerms).equals(" "))){
					   			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.lang.prefix + " " + plugin.lang.noPerms));
					   		}						   	
						}
					}
				}else if(args.length > 1) {
					if(args[0].toLowerCase().equals(plugin.lang.toggleCommand)) {
						if(sender.hasPermission("piggyback.toggle.other") || sender.isOp() || !(sender instanceof Player)){
							for(String s : args){
								if(s!=args[0]) {
									if(getOfflinePlayerUUID(s)!=null) {
								    	UUID id = getOfflinePlayerUUID(s);
								    	if (!plugin.lists.disabledPlayers.contains(id.toString())) {
								   			if(!((plugin.lang.prefix + " " + plugin.lang.toggleOff).equals(" "))){
								   				if(Bukkit.getOfflinePlayer(id).isOnline())
								   					Bukkit.getPlayer(id).sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.lang.prefix + " " + plugin.lang.toggleOff));
								   			}
								   			if(!((plugin.lang.prefix + " " + plugin.lang.toggleOffOther).equals(" "))){
								   				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ((plugin.lang.prefix + " " + plugin.lang.toggleOffOther).replace("%player%", s))));
								   			}
								   			plugin.lists.disabledPlayers.add(id.toString());
								   		} else {
								           	if(!((plugin.lang.prefix + " " + plugin.lang.toggleOn).equals(" "))){
								           		if(Bukkit.getOfflinePlayer(id).isOnline())
								           			Bukkit.getPlayer(id).sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.lang.prefix + " " + plugin.lang.toggleOn));
								           	}
								           	if(!((plugin.lang.prefix + " " + plugin.lang.toggleOnOther).equals(" "))){
								           		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ((plugin.lang.prefix + " " + plugin.lang.toggleOnOther).replace("%player%", s))));
								   			}
								           	plugin.lists.disabledPlayers.remove(id.toString());
								        }
								   		return true;
								    }else {
								    	if(!((plugin.lang.prefix + " " + plugin.lang.hasNotPlayed).equals(" "))){
								    		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ((plugin.lang.prefix + " " + plugin.lang.hasNotPlayed).replace("%player%", s))));
									    	return false;
								    	}						    	
								    }
								}							    
							}				  		
					    }else{
					    	if(!((plugin.lang.prefix + " " + plugin.lang.noPerms).equals(" "))){
					       		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.lang.prefix + " " + plugin.lang.noPerms));
					       	}	
					    }
					}
				    if(args[0].toLowerCase().equals(plugin.lang.messagesCommand)) {
				    	if(sender.hasPermission("piggyback.messages.other") || sender.isOp() || !(sender instanceof Player)){
							for(String s : args){
								if(s!=args[0]) {
									if(getOfflinePlayerUUID(s)!=null) {
								    	UUID id = getOfflinePlayerUUID(s);
								    	if (!plugin.lists.messagePlayers.contains(id.toString())) {
								   			if(!((plugin.lang.prefix + " " + plugin.lang.messageOff).equals(" "))){
								   				if(Bukkit.getOfflinePlayer(id).isOnline())
								   					Bukkit.getPlayer(id).sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.lang.prefix + " " + plugin.lang.messageOff));
								   			}
								   			if(!((plugin.lang.prefix + " " + plugin.lang.messageOffOther).equals(" "))){
								   				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ((plugin.lang.prefix + " " + plugin.lang.messageOffOther).replace("%player%", s))));
								   			}
								   			plugin.lists.messagePlayers.add(id.toString());
								   		} else {
								           	if(!((plugin.lang.prefix + " " + plugin.lang.messageOn).equals(" "))){
								           		if(Bukkit.getOfflinePlayer(id).isOnline())
								           			Bukkit.getPlayer(id).sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.lang.prefix + " " + plugin.lang.messageOn));
								           	}
								           	if(!((plugin.lang.prefix + " " + plugin.lang.messageOnOther).equals(" "))){
								           		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ((plugin.lang.prefix + " " + plugin.lang.messageOnOther).replace("%player%", s))));
								   			}
								           	plugin.lists.messagePlayers.remove(id.toString());
								        }
								   		return true;
								    }else {
								    	if(!((plugin.lang.prefix + " " + plugin.lang.hasNotPlayed).equals(" "))){
								    		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ((plugin.lang.prefix + " " + plugin.lang.hasNotPlayed).replace("%player%", s))));
									    	return false;
								    	}						    	
								    }
								}							    
							}				  		
					    }else{
					    	if(!((plugin.lang.prefix + " " + plugin.lang.noPerms).equals(" "))){
					       		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.lang.prefix + " " + plugin.lang.noPerms));
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
	
	public void displayHelp(CommandSender sender){
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5&o#### &r" + plugin.lang.title + " " + plugin.lang.help + "&r &5&o####"));
		sender.sendMessage("");
		if(sender.hasPermission("piggyback.use") || sender.isOp())
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&7/&e" + plugin.lang.command + " &7- &r" + plugin.lang.helpMain));
		if(sender.hasPermission("piggyback.toggle") || sender.isOp())
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&7/&e" + plugin.lang.command + " &b" + plugin.lang.toggleCommand +  " &7- &r" + plugin.lang.helpToggle));
		if(sender.hasPermission("piggyback.toggle.other") || sender.isOp() || !(sender instanceof Player))
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&7/&e" + plugin.lang.command + " &b" + plugin.lang.toggleCommand + " " + plugin.lang.helpToggleOther));
		if(sender.hasPermission("piggyback.messages") || sender.isOp())
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&7/&e" + plugin.lang.command + " &3" + plugin.lang.messagesCommand + " &7- &r" + plugin.lang.helpMessageToggle));
		if(sender.hasPermission("piggyback.messages.other") || sender.isOp() || !(sender instanceof Player))
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&7/&e" + plugin.lang.command + " &3" + plugin.lang.messagesCommand + " " + plugin.lang.helpMessageToggleOther));
		if(sender.hasPermission("piggyback.reload") || sender.isOp() || !(sender instanceof Player))
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&7/&e" + plugin.lang.command + " &8" + plugin.lang.reloadCommand + " &7- &r" + plugin.lang.helpReload));
		sender.sendMessage("");
		int j = ((plugin.lang.title + " " + plugin.lang.help).length());
		String s = "";
		for(int i = 0; i<j; i++){
			s = s + "#";
		}
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"  &7&lSHIFT+" + (plugin.config.clickType.toString()).toUpperCase() + "-CLICK"));
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5&o####" + s));	
	}
	
	private UUID getOfflinePlayerUUID(String playerName){
		for(OfflinePlayer op : Bukkit.getOfflinePlayers()) {
			if(op.getName().equalsIgnoreCase(playerName)) {
				return op.getUniqueId();
			}
		}
		return null;
	}
}
