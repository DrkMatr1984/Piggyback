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
import me.blubdalegend.piggyback.events.PiggybackRideEntityEvent;
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
	public void checkCrouchLeftClickPickup(PlayerInteractEntityCrouchLeftClickEvent event)
	{
		if(this.plugin.config.clickType.equals(Clicks.EITHER) || this.plugin.config.clickType.equals(Clicks.LEFT)) {
			boolean perm = false;
			Player player = event.getPlayer();
			Entity clicked = event.getClickedEntity();
			if ((player.hasPermission("piggyback.use")) || (player.isOp()))
			{
				perm = true;
			}
			//pickup
			if(this.plugin.config.clickAction.equals(Actions.PICKUP) || (this.plugin.config.clickAction.equals(Actions.RIDE) && this.plugin.config.pickupOnlyMobs && !(clicked instanceof Player)) || 
					(this.plugin.config.clickAction.equals(Actions.RIDE) && this.plugin.config.pickupOnlyPlayers && clicked instanceof Player)) {
				if((this.plugin.config.onlyMobs && clicked instanceof Player) || (this.plugin.config.onlyPlayers && !(clicked instanceof Player)))
					return;
				if((player.isInsideVehicle()&& Objects.equals(player.getVehicle(), clicked))){
					return;
				}
				if(perm){
					if(!player.getPassengers().isEmpty()){
						if (player.getPassengers().contains(clicked))
						{
							if (plugin.config.throwRiderAway) 
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
						if (clicked.getCustomName() != null)
							if(plugin.config.disabledCustomEntities.contains(clicked.getCustomName().toUpperCase()))
							{						
								return;
							}
					    if ((clicked.hasMetadata("NPC")) && (!plugin.config.allowNPCs)) {
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
					    if(plugin.getWgHook()!=null)
						    if(!plugin.getWgHook().canPickup(player, clicked.getLocation()))
						    	return;
					   /* if(plugin.getPlotSquared()!=null)
						    if(!plugin.getPlotSquared().canPiggyback(clicked.getLocation()))
						    	return; */
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
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority=EventPriority.HIGH, ignoreCancelled = false)
	public void checkCrouchLeftClickRide(PlayerInteractEntityCrouchLeftClickEvent event)
	{
		if(this.plugin.config.clickType.equals(Clicks.EITHER) || this.plugin.config.clickType.equals(Clicks.LEFT)) {
			boolean perm = false;
			Player player = event.getPlayer();
			Entity clicked = event.getClickedEntity();
			if ((player.hasPermission("piggyback.use")) || (player.isOp()))
			{
				perm = true;
			}
			//ride
			if(this.plugin.config.clickAction.equals(Actions.RIDE) && !this.plugin.config.pickupOnlyMobs 
	        		|| (this.plugin.config.clickAction.equals(Actions.RIDE) && this.plugin.config.pickupOnlyMobs && clicked instanceof Player)){
				if((this.plugin.config.onlyMobs && clicked instanceof Player) || (this.plugin.config.onlyPlayers && !(clicked instanceof Player)))
					return;
				if((player.isInsideVehicle()&& Objects.equals(player.getVehicle(), clicked))){
					return;
				}
				if(perm){
					if(!player.getPassengers().isEmpty()){
						if (player.getPassengers().contains(clicked))
						{
							if (plugin.config.throwRiderAway) 
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
						if (clicked.getCustomName() != null)
							if(plugin.config.disabledCustomEntities.contains(clicked.getCustomName().toUpperCase()))
							{						
								return;
							}
						if ((clicked.hasMetadata("NPC")) && (!plugin.config.allowNPCs)) {
			            	if (plugin.config.send && (!(plugin.lists.messagePlayers.contains(player.getUniqueId().toString())))){
			             		if(!((plugin.lang.prefix + " " + plugin.lang.noRideNPC).equals(" "))){
			        	    		player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.lang.prefix + " " + plugin.lang.noRideNPC));
			    	        	}
			    	        }
			         	    return;
			            }
					    if(plugin.config.disabledWorlds.contains(clicked.getWorld().toString().toUpperCase()))
					    {
					    	return;
					    }
					    if(plugin.config.requireEmptyHand){
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
					    if(clicked instanceof Player p)
			            {
			            	if(plugin.lists.disabledPlayers.contains(player.getUniqueId().toString())){
					    		if(!((plugin.lang.prefix + " " + plugin.lang.noRidePlayerToggle).equals(" "))){
					    			player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.lang.prefix + " " + (plugin.lang.noRidePlayerToggle).replace("%player%", p.getDisplayName())));
					    		}
					    		return;
					    	}
					    	if(plugin.lists.disabledPlayers.contains(p.getUniqueId().toString())){        	  
					    		if (plugin.config.send && (!(plugin.lists.messagePlayers.contains(player.getUniqueId().toString())))){    			
					    			if(!((plugin.lang.prefix + " " + plugin.lang.noRidePlayer).equals(" "))){
					    				if(!(Piggyback.toggleCooldownPlayers.contains(player.getUniqueId()))){
					    					player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.lang.prefix + " " + (plugin.lang.noRidePlayer).replace("%player%", p.getDisplayName())));
					    					Piggyback.toggleCooldownPlayers.add(player.getUniqueId());
					    					new ToggleMessageCooldown(player).runTaskLater(plugin, plugin.config.messageCooldown);
					    				}
					    			}
					    		}
					    		return;
					    	}
			            }
					   /* if(plugin.getPlotSquared()!=null)
						    if(!plugin.getPlotSquared().canPiggyback(clicked.getLocation()))
						    	return; */
					    if(plugin.getWgHook()!=null)
						    if(!plugin.getWgHook().canPickup(player, clicked.getLocation()))
						    	return;
					    //call my ride event
			            PiggybackRideEntityEvent rideEnt = new PiggybackRideEntityEvent(clicked, player);
			            Bukkit.getServer().getPluginManager().callEvent(rideEnt);
			            if(!rideEnt.isCancelled()){
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
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
	public void checkCrouchRightClickRide(PlayerInteractEntityCrouchRightClickEvent event)
	{
		if(this.plugin.config.clickType.equals(Clicks.EITHER) || this.plugin.config.clickType.equals(Clicks.RIGHT)) {
			boolean perm = false;
			Player player = event.getPlayer();
			Entity clicked = event.getClickedEntity();
			if ((player.hasPermission("piggyback.use")) || (player.isOp()))
		    {
			    perm = true;
		    }
			if(perm) {
		        if(this.plugin.config.clickAction.equals(Actions.RIDE) && !this.plugin.config.pickupOnlyMobs 
		        		|| (this.plugin.config.clickAction.equals(Actions.RIDE) && this.plugin.config.pickupOnlyMobs && clicked instanceof Player)){
		        	if((this.plugin.config.onlyMobs && clicked instanceof Player) || (this.plugin.config.onlyPlayers && !(clicked instanceof Player)))
						return;
			        if(!clicked.getPassengers().isEmpty()){
			        	if(clicked.getPassengers().contains(player)){
			    		    return;
				        }
			        }
			        if (plugin.config.disabledEntities.contains(clicked.getType().toString().toUpperCase()))
		            {						
		    	        return;
		            }
			        if (clicked.getCustomName() != null)
						if(plugin.config.disabledCustomEntities.contains(clicked.getCustomName().toUpperCase()))
						{						
							return;
						}
		            if ((clicked.hasMetadata("NPC")) && (!plugin.config.allowNPCs)) {
		            	if (plugin.config.send && (!(plugin.lists.messagePlayers.contains(player.getUniqueId().toString())))){
		             		if(!((plugin.lang.prefix + " " + plugin.lang.noRideNPC).equals(" "))){
		        	    		player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.lang.prefix + " " + plugin.lang.noRideNPC));
		    	        	}
		    	        }
		         	    return;
		            }
		            if(plugin.config.disabledWorlds.contains(clicked.getWorld().toString().toUpperCase()))
		            {
		        	    return;
		            }
		            if(plugin.config.requireEmptyHand){
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
		            if(clicked instanceof Player p)
		            {
		            	if(plugin.lists.disabledPlayers.contains(player.getUniqueId().toString())){
				    		if(!((plugin.lang.prefix + " " + plugin.lang.noRidePlayerToggle).equals(" "))){
				    			player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.lang.prefix + " " + (plugin.lang.noRidePlayerToggle).replace("%player%", p.getDisplayName())));
				    		}
				    		return;
				    	}
				    	if(plugin.lists.disabledPlayers.contains(p.getUniqueId().toString())){        	  
				    		if (plugin.config.send && (!(plugin.lists.messagePlayers.contains(player.getUniqueId().toString())))){    			
				    			if(!((plugin.lang.prefix + " " + plugin.lang.noRidePlayer).equals(" "))){
				    				if(!(Piggyback.toggleCooldownPlayers.contains(player.getUniqueId()))){
				    					player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.lang.prefix + " " + (plugin.lang.noRidePlayer).replace("%player%", p.getDisplayName())));
				    					Piggyback.toggleCooldownPlayers.add(player.getUniqueId());
				    					new ToggleMessageCooldown(player).runTaskLater(plugin, plugin.config.messageCooldown);
				    				}
				    			}
				    		}
				    		return;
				    	}
		            }
		            if(plugin.getWgHook()!=null)
					    if(!plugin.getWgHook().canPickup(player, clicked.getLocation()))
					    	return;
		            /*if(plugin.getPlotSquared()!=null)
					    if(!plugin.getPlotSquared().canPiggyback(clicked.getLocation()))
					    	return; */
		            //call my ride event
		            PiggybackRideEntityEvent rideEnt = new PiggybackRideEntityEvent(clicked, player);
		            Bukkit.getServer().getPluginManager().callEvent(rideEnt);
		            if(!rideEnt.isCancelled()){
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
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
	public void checkCrouchRightClickPickup(PlayerInteractEntityCrouchRightClickEvent event)
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
			if(this.plugin.config.clickAction.equals(Actions.PICKUP) || (this.plugin.config.clickAction.equals(Actions.RIDE) && this.plugin.config.pickupOnlyMobs && !(clicked instanceof Player)) || 
					(this.plugin.config.clickAction.equals(Actions.RIDE) && this.plugin.config.pickupOnlyPlayers && clicked instanceof Player)){
				if((this.plugin.config.onlyMobs && clicked instanceof Player) || (this.plugin.config.onlyPlayers && !(clicked instanceof Player)))
					return;
			    if((player.isInsideVehicle()&& Objects.equals(player.getVehicle(), clicked))){
			        return;
			    }		    
				if(perm){
					if(!player.getPassengers().isEmpty()){
						if (player.getPassengers().contains(clicked))
						{
							if (plugin.config.throwRiderAway) 
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
						if (clicked.getCustomName() != null)
							if(plugin.config.disabledCustomEntities.contains(clicked.getCustomName().toUpperCase()))
							{						
								return;
							}
					    if ((clicked.hasMetadata("NPC")) && (!plugin.config.allowNPCs)) {
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
					   /* if(plugin.getPlotSquared()!=null)
						    if(!plugin.getPlotSquared().canPiggyback(clicked.getLocation()))
						    	return; */
					    if(plugin.getWgHook()!=null)
						    if(!plugin.getWgHook().canPickup(player, clicked.getLocation()))
						    	return;
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
			}
		}
	}	 
    
}
