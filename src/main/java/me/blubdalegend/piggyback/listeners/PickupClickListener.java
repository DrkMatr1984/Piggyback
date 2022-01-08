package me.blubdalegend.piggyback.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import me.blubdalegend.piggyback.Piggyback;
import me.blubdalegend.piggyback.config.ConfigAccessor.Actions;
import me.blubdalegend.piggyback.config.ConfigAccessor.Clicks;
import me.blubdalegend.piggyback.events.PiggybackDropEntityEvent;
import me.blubdalegend.piggyback.events.PiggybackPickupEntityEvent;
import me.blubdalegend.piggyback.events.PiggybackThrowEntityEvent;
import me.blubdalegend.piggyback.tasks.EmptyHandMessageCooldown;
import me.blubdalegend.piggyback.tasks.NoPermsMessageCooldown;
import me.blubdalegend.piggyback.tasks.ToggleMessageCooldown;
import me.drkmatr1984.customevents.interactEvents.PlayerInteractEntityCrouchLeftClickEvent;
import me.drkmatr1984.customevents.interactEvents.PlayerInteractEntityCrouchRightClickEvent;

import java.util.Objects;

public class PickupClickListener implements org.bukkit.event.Listener
{
	private final Piggyback plugin;
	
	public PickupClickListener(Piggyback plugin)
	{
		this.plugin = plugin;
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
	public void checkLeftClick(PlayerInteractEntityCrouchLeftClickEvent event)
	{
		if(this.plugin.config.clickType.equals(Clicks.EITHER) || this.plugin.config.clickType.equals(Clicks.LEFT)) {
			boolean perm = false;
			Player player = event.getPlayer();
			Entity clicked = event.getClickedEntity();
			if ((player.hasPermission("piggyback.use")) || (player.isOp()))
			{
				perm = true;
			}
			//pickup or ride
			if(this.plugin.config.actionType.equals(Actions.PICKUP) || (this.plugin.config.actionType.equals(Actions.RIDE) && this.plugin.config.onlyRidePlayers && !(clicked instanceof Player))) {
				if((player.isInsideVehicle()&& Objects.equals(player.getVehicle(), clicked))){
					return;
				}
				if(perm){
					if(!player.getPassengers().isEmpty()){
						if (player.getPassengers().contains(clicked))
						{
							if (plugin.config.throwRiderPickup) 
							{
				            	//call my throw event
				            	PiggybackThrowEntityEvent throwEnt = new PiggybackThrowEntityEvent(clicked, player);
				            	Bukkit.getServer().getPluginManager().callEvent(throwEnt);
				            	if(!throwEnt.isCancelled()){
				            		event.setDamageCancelled(true);
				            	}		
							}else{
								//call my drop event
								PiggybackDropEntityEvent dropEnt = new PiggybackDropEntityEvent(clicked, player);
								Bukkit.getServer().getPluginManager().callEvent(dropEnt);
								if(!dropEnt.isCancelled()){
									event.setDamageCancelled(true);
				            	}
							}
						}	  
					}else {
						if (plugin.config.disabledEntities.contains(clicked.getType().toString().toUpperCase()))
					    {						
					    	return;
					    }
					    if ((clicked.hasMetadata("NPC")) && (!plugin.config.pickupNPC)) {
					    	if (plugin.config.send && (!(plugin.lists.messagePlayers.contains(player.getUniqueId().toString())))){
					    		if(!((plugin.lang.prefix + " " + plugin.lang.noPickUpNPC).equals(" "))){
					    			player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.lang.prefix + " " + plugin.lang.noPickUpNPC));
					    		}
					    	}
					    	return;
					    }
					    if(plugin.config.disabledWorlds.contains(clicked.getWorld().toString().toUpperCase()))
					    {
					    	return;
					    }
					    if(plugin.config.requireEmptyHand){
							if(!Objects.equals(Piggyback.version, "pre1_9")){
								if(player.getInventory().getItemInMainHand().getType()!=Material.AIR){
									if (plugin.config.send && (!(plugin.lists.messagePlayers.contains(player.getUniqueId().toString())))){
										if(!((plugin.lang.prefix + " " + plugin.lang.emptyHand).equals(" "))){
											if(!(Piggyback.emptyHandCooldownPlayers.contains(player.getUniqueId()))){
												player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.lang.prefix + " " + plugin.lang.emptyHand));
												Piggyback.emptyHandCooldownPlayers.add(player.getUniqueId());
												new EmptyHandMessageCooldown(player).runTaskLater(plugin, plugin.config.messageCooldown);
											}
										}
									}								
									return;
								}
							}else{
								if(player.getItemInHand().getType()!=Material.AIR){
									if (plugin.config.send && (!(plugin.lists.messagePlayers.contains(player.getUniqueId().toString())))){
										if(!((plugin.lang.prefix + " " + plugin.lang.emptyHand).equals(" "))){
											if(!(Piggyback.emptyHandCooldownPlayers.contains(player.getUniqueId()))){
												player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.lang.prefix + " " + plugin.lang.emptyHand));
												Piggyback.emptyHandCooldownPlayers.add(player.getUniqueId());
												new EmptyHandMessageCooldown(player).runTaskLater(plugin, plugin.config.messageCooldown);
											}
										}
									}
									return;
								}
							}
						}
					    if(clicked instanceof Player p)
					    {
							if(plugin.lists.disabledPlayers.contains(player.getUniqueId().toString())){
					    		if(!((plugin.lang.prefix + " " + plugin.lang.noPickUpPlayerToggle).equals(" "))){
					    			player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.lang.prefix + " " + (plugin.lang.noPickUpPlayerToggle).replace("%player%", p.getDisplayName())));
					    		}
					    		return;
					    	}
					    	if(plugin.lists.disabledPlayers.contains(clicked.getUniqueId().toString())){        	  
					    		if (plugin.config.send && (!(plugin.lists.messagePlayers.contains(player.getUniqueId().toString())))){    			
					    			if(!((plugin.lang.prefix + " " + plugin.lang.noPickUpPlayer).equals(" "))){
					    				if(!(Piggyback.toggleCooldownPlayers.contains(player.getUniqueId()))){
					    					player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.lang.prefix + " " + (plugin.lang.noPickUpPlayer).replace("%player%", p.getDisplayName())));
					    					Piggyback.toggleCooldownPlayers.add(player.getUniqueId());
					    					new ToggleMessageCooldown(player).runTaskLater(plugin, plugin.config.messageCooldown);
					    				}
					    			}
					    		}
					    		return;
					    	}
					    }
					    //call my pickup event
					    PiggybackPickupEntityEvent pickupEnt = new PiggybackPickupEntityEvent(clicked, player);
					    Bukkit.getServer().getPluginManager().callEvent(pickupEnt);
					    if(!pickupEnt.isCancelled()){
					    	event.setDamageCancelled(true);
				        }
					}	  
				}else{
					if (plugin.config.send && (!(plugin.lists.messagePlayers.contains(player.getUniqueId().toString())))){
						if(!((plugin.lang.prefix + " " + plugin.lang.noPerms).equals(" "))){
							if(!(Piggyback.noPermsCooldownPlayers.contains(player.getUniqueId()))){
								player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.lang.prefix + " " + plugin.lang.noPerms));
								Piggyback.noPermsCooldownPlayers.add(player.getUniqueId());
								new NoPermsMessageCooldown(player).runTaskLater(plugin, plugin.config.messageCooldown);
							}
						}
					}
				}
			}else if(this.plugin.config.actionType.equals(Actions.RIDE)){				
				if(!player.getPassengers().isEmpty()){
					if(clicked.getPassengers().contains(player)){
						return;
					}
				}				
				//ride player
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
	public void checkRightClick(PlayerInteractEntityCrouchRightClickEvent event)
	{
		if(this.plugin.config.clickType.equals(Clicks.EITHER) || this.plugin.config.clickType.equals(Clicks.RIGHT)) {
			boolean perm = false;
			Player player = event.getPlayer();
			Entity clicked = event.getClickedEntity();
			if ((player.hasPermission("piggyback.use")) || (player.isOp()))
		    {
			    perm = true;
		    }
			//pickup or ride
			if(this.plugin.config.actionType.equals(Actions.PICKUP) || (this.plugin.config.actionType.equals(Actions.RIDE) && this.plugin.config.onlyRidePlayers && !(clicked instanceof Player))) {
			    if((player.isInsideVehicle()&& Objects.equals(player.getVehicle(), clicked))){
			        return;
			    }		    
				if(perm){
					if(!player.getPassengers().isEmpty()){
						if (player.getPassengers().contains(clicked))
						{
							if (plugin.config.throwRiderPickup) 
							{
				            	//call my throw event
				            	PiggybackThrowEntityEvent throwEnt = new PiggybackThrowEntityEvent(clicked, player);
				            	Bukkit.getServer().getPluginManager().callEvent(throwEnt);
				            	if(!throwEnt.isCancelled()){
				            		event.setCancelled(true);
				            	}		
							}else{
								//call my drop event
								PiggybackDropEntityEvent dropEnt = new PiggybackDropEntityEvent(clicked, player);
								Bukkit.getServer().getPluginManager().callEvent(dropEnt);
								if(!dropEnt.isCancelled()){
									event.setCancelled(true);
				            	}
							}
						}	  
					}else {
						if (plugin.config.disabledEntities.contains(clicked.getType().toString().toUpperCase()))
					    {						
					    	return;
					    }
					    if ((clicked.hasMetadata("NPC")) && (!plugin.config.pickupNPC)) {
					    	if (plugin.config.send && (!(plugin.lists.messagePlayers.contains(player.getUniqueId().toString())))){
					    		if(!((plugin.lang.prefix + " " + plugin.lang.noPickUpNPC).equals(" "))){
					    			player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.lang.prefix + " " + plugin.lang.noPickUpNPC));
					    		}
					    	}
					    	return;
					    }
					    if(plugin.config.disabledWorlds.contains(clicked.getWorld().toString().toUpperCase()))
					    {
					    	return;
					    }
					    if(plugin.config.requireEmptyHand){
							if(!Objects.equals(Piggyback.version, "pre1_9")){
								if(player.getInventory().getItemInMainHand().getType()!=Material.AIR){
									if (plugin.config.send && (!(plugin.lists.messagePlayers.contains(player.getUniqueId().toString())))){
										if(!((plugin.lang.prefix + " " + plugin.lang.emptyHand).equals(" "))){
											if(!(Piggyback.emptyHandCooldownPlayers.contains(player.getUniqueId()))){
												player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.lang.prefix + " " + plugin.lang.emptyHand));
												Piggyback.emptyHandCooldownPlayers.add(player.getUniqueId());
												new EmptyHandMessageCooldown(player).runTaskLater(plugin, plugin.config.messageCooldown);
											}
										}
									}								
									return;
								}
							}else{
								if(player.getItemInHand().getType()!=Material.AIR){
									if (plugin.config.send && (!(plugin.lists.messagePlayers.contains(player.getUniqueId().toString())))){
										if(!((plugin.lang.prefix + " " + plugin.lang.emptyHand).equals(" "))){
											if(!(Piggyback.emptyHandCooldownPlayers.contains(player.getUniqueId()))){
												player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.lang.prefix + " " + plugin.lang.emptyHand));
												Piggyback.emptyHandCooldownPlayers.add(player.getUniqueId());
												new EmptyHandMessageCooldown(player).runTaskLater(plugin, plugin.config.messageCooldown);
											}
										}
									}
									return;
								}
							}
						}
					    if(clicked instanceof Player p)
					    {
							if(plugin.lists.disabledPlayers.contains(player.getUniqueId().toString())){
					    		if(!((plugin.lang.prefix + " " + plugin.lang.noPickUpPlayerToggle).equals(" "))){
					    			player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.lang.prefix + " " + (plugin.lang.noPickUpPlayerToggle).replace("%player%", p.getDisplayName())));
					    		}
					    		return;
					    	}
					    	if(plugin.lists.disabledPlayers.contains(clicked.getUniqueId().toString())){        	  
					    		if (plugin.config.send && (!(plugin.lists.messagePlayers.contains(player.getUniqueId().toString())))){    			
					    			if(!((plugin.lang.prefix + " " + plugin.lang.noPickUpPlayer).equals(" "))){
					    				if(!(Piggyback.toggleCooldownPlayers.contains(player.getUniqueId()))){
					    					player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.lang.prefix + " " + (plugin.lang.noPickUpPlayer).replace("%player%", p.getDisplayName())));
					    					Piggyback.toggleCooldownPlayers.add(player.getUniqueId());
					    					new ToggleMessageCooldown(player).runTaskLater(plugin, plugin.config.messageCooldown);
					    				}
					    			}
					    		}
					    		return;
					    	}
					    }
					    //call my pickup event
					    PiggybackPickupEntityEvent pickupEnt = new PiggybackPickupEntityEvent(clicked, player);
					    Bukkit.getServer().getPluginManager().callEvent(pickupEnt);
					    if(!pickupEnt.isCancelled()){
					    	event.setCancelled(true);
				        }
					}	  
				}else{
					if (plugin.config.send && (!(plugin.lists.messagePlayers.contains(player.getUniqueId().toString())))){
						if(!((plugin.lang.prefix + " " + plugin.lang.noPerms).equals(" "))){
							if(!(Piggyback.noPermsCooldownPlayers.contains(player.getUniqueId()))){
								player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.lang.prefix + " " + plugin.lang.noPerms));
								Piggyback.noPermsCooldownPlayers.add(player.getUniqueId());
								new NoPermsMessageCooldown(player).runTaskLater(plugin, plugin.config.messageCooldown);
							}
						}
					}
				}
			}else {
				//Ride the Entity instead of pick them up
			}
		}
	}	 
    
}
