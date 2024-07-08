package me.blubdalegend.piggyback.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Vector;

import me.blubdalegend.piggyback.Piggyback;
import me.blubdalegend.piggyback.actions.ThrowEntity;
import me.blubdalegend.piggyback.config.ConfigAccessor.Actions;
import me.blubdalegend.piggyback.config.ConfigAccessor.Clicks;
import me.blubdalegend.piggyback.events.PiggybackDropEntityEvent;
import me.blubdalegend.piggyback.events.PiggybackFarThrowEntityEvent;
import me.blubdalegend.piggyback.events.PiggybackPickupEntityEvent;
import me.blubdalegend.piggyback.events.PiggybackRideEntityEvent;
import me.blubdalegend.piggyback.events.PiggybackThrowEntityEvent;
import me.blubdalegend.piggyback.events.PlayerInteractEntityCrouchLeftClickEvent;
import me.blubdalegend.piggyback.events.PlayerInteractEntityCrouchRightClickEvent;
import me.blubdalegend.piggyback.tasks.EmptyHandMessageCooldown;
import me.blubdalegend.piggyback.tasks.NoPermsMessageCooldown;
import me.blubdalegend.piggyback.tasks.ToggleMessageCooldown;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class PickupClickListener implements org.bukkit.event.Listener
{
	private final Piggyback plugin;
	private final HashMap<UUID, Entity> carriedEntities = new HashMap<>();

	public PickupClickListener(Piggyback plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		Bukkit.getLogger().info("Event is Firing");
		if(event.getHand().equals(EquipmentSlot.HAND)) {
			Bukkit.getLogger().info("Event is Firing with right hand"); 
			Player player = event.getPlayer();
		        Entity entity = event.getRightClicked();
                List<Entity> passengers = player.getPassengers();
		        if (player.isSneaking()) {
		            // Pick up the entity
		            // Try Delaying for .25 seconds maybe? (5 ticks)
		            carriedEntities.put(player.getUniqueId(), entity);
		            player.setPassenger(entity);
		            player.sendMessage("You picked up the entity!");
		            event.setCancelled(true);
		        }
		}   
    }
	
	@EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
		Bukkit.getLogger().info("PlayerInteractAtEntityEvent is Firing");
		Bukkit.getLogger().info(event.getRightClicked().getName());
	}
	
	/////////////////// new throw method !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!  Remove other Throw methods and merge them with the one
	@EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		if(!p.getPassengers().isEmpty())
			if(carriedEntities.containsKey(p.getUniqueId()) && isLookingUp(p) && p.isSneaking()) {
				if(event.getClickedBlock()==null) {
					Entity carried = carriedEntities.get(p.getUniqueId());
                    carriedEntities.remove(p.getUniqueId());
                    p.removePassenger(carried);
                    ThrowEntity.throwEntity(carried, p);
                    p.sendMessage("You threw the entity!");
				}
			}
	}
	
	public boolean isLookingUp(Player player) {
		float pitch = player.getLocation().getPitch();
	    return pitch <= -50 && pitch >= -90;
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
			if((this.plugin.config.onlyMobs && clicked instanceof Player) || (this.plugin.config.onlyPlayers && !(clicked instanceof Player)))
				return;
			if(this.plugin.config.clickAction.equals(Actions.PICKUP) || (this.plugin.config.clickAction.equals(Actions.RIDE) && this.plugin.config.pickupOnlyMobs && !(clicked instanceof Player)) || 
					(this.plugin.config.clickAction.equals(Actions.RIDE) && this.plugin.config.pickupOnlyPlayers && clicked instanceof Player)) {
				if((player.isInsideVehicle()&& Objects.equals(player.getVehicle(), clicked))){
					return;
				}
				if(perm){
					if(!player.getPassengers().isEmpty()){
						if (player.getPassengers().contains(clicked))
						{
							if (plugin.config.throwRiderAway) 
							{
								if(plugin.config.farThrowRider && player.isSprinting()) {
									//call my  far throw event
					            	PiggybackFarThrowEntityEvent farThrowEnt = new PiggybackFarThrowEntityEvent(clicked, player);
					            	Bukkit.getServer().getPluginManager().callEvent(farThrowEnt);
					            	if(!farThrowEnt.isCancelled()){
					            		event.setDamageCancelled(true);
					            	}
								}else {
									//call my throw event
					            	PiggybackThrowEntityEvent throwEnt = new PiggybackThrowEntityEvent(clicked, player);
					            	Bukkit.getServer().getPluginManager().callEvent(throwEnt);
					            	if(!throwEnt.isCancelled()){
					            		event.setDamageCancelled(true);
					            	}
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
						Bukkit.getServer().getLogger().info("Trying to Pickup...");
						if(!plugin.config.disabledWorlds.isEmpty()) {
					    	if(plugin.config.disabledWorlds.contains(clicked.getWorld().toString().toUpperCase()) && !plugin.config.whitelistWorlds)
						    {
						    	return;
						    }
					    	if(!plugin.config.disabledWorlds.contains(clicked.getWorld().toString().toUpperCase()) && plugin.config.whitelistWorlds)
						    {
						    	return;
						    }
					    }
						if(!plugin.config.disabledEntities.isEmpty()) {
							if (plugin.config.disabledEntities.contains(clicked.getType().toString().toUpperCase()) && !plugin.config.whitelistEntities)
						    {			
								return;
						    }
							if (!plugin.config.disabledEntities.contains(clicked.getType().toString().toUpperCase()) && plugin.config.whitelistEntities)
						    {			
								return;
						    }
						}					
						if (clicked.getCustomName() != null) {
							if(!plugin.config.disabledCustomEntities.isEmpty()) {
								if(plugin.config.disabledCustomEntities.contains(clicked.getCustomName().toUpperCase()) && !plugin.config.whitelistCustomEntities)
								{						
									return;
								}
								if(!plugin.config.disabledCustomEntities.contains(clicked.getCustomName().toUpperCase()) && plugin.config.whitelistCustomEntities)
								{						
									return;
								}
							}
						}
					    if ((clicked.hasMetadata("NPC")) && (!plugin.config.allowNPCs)) {
					    	if (plugin.config.send && (!(plugin.lists.hasMessagesDisabled(player)))){
					    		if(!((plugin.lang.prefix + " " + plugin.lang.noPickUpNPC).equals(" "))){
					    			player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.lang.prefix + " " + plugin.lang.noPickUpNPC));
					    		}
					    	}
					    	return;
					    }
					    if(plugin.config.requireEmptyHand){
							if(player.getItemInHand().getType()!=Material.AIR){
								if (plugin.config.send && (!(plugin.lists.hasMessagesDisabled(player)))){
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
					    if(plugin.config.requireItem!=null){
							if(player.getItemInHand().getType()!=Material.AIR){
								if(plugin.config.requireItem!=player.getItemInHand().getType()) {
									if(!((plugin.lang.prefix + " " + plugin.lang.requireItem).equals(" "))){
										if(!(Piggyback.emptyHandCooldownPlayers.contains(player.getUniqueId()))){
											player.sendMessage(ChatColor.translateAlternateColorCodes('&', (plugin.lang.prefix + " " + plugin.lang.requireItem).replace("%requireItem%", plugin.config.requireItem.toString())));
											Piggyback.emptyHandCooldownPlayers.add(player.getUniqueId());
											new EmptyHandMessageCooldown(player).runTaskLater(plugin, plugin.config.messageCooldown);
										}
									}
									return;
								}
							}
						}
					    if(clicked instanceof Player p)
					    	
					    {
					    	if(p.hasPermission("piggyback.no")) {
								return;
							}
							if(plugin.lists.isDisabled(player)){
					    		if(!((plugin.lang.prefix + " " + plugin.lang.noPickUpPlayerToggle).equals(" "))){
					    			player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.lang.prefix + " " + (plugin.lang.noPickUpPlayerToggle).replace("%player%", p.getDisplayName())));
					    		}
					    		return;
					    	}
					    	if(plugin.lists.isDisabled(clicked.getUniqueId())){        	  
					    		if (plugin.config.send && (!(plugin.lists.hasMessagesDisabled(player)))){    			
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
					    if(Piggyback.piggybackPickupCooldownPlayers.containsKey(player.getUniqueId()) && !player.hasPermission("piggyback.cooldown.bypass")){
							DecimalFormat df = new DecimalFormat("#.##");
							double time = ((plugin.config.pickupCooldown/20.0) -
									(((Long)System.currentTimeMillis() - 
									Piggyback.piggybackPickupCooldownPlayers.get(player.getUniqueId()))
									/1000.0));
							String timeLeft;
							timeLeft = df.format(time);
							if(!Objects.equals(timeLeft, "0")){
								if(Piggyback.piggybackPickupCooldownPlayers.containsKey(player.getUniqueId())){
									if (plugin.config.send && (!(plugin.lists.hasMessagesDisabled(player)))) {
										if(!((plugin.lang.prefix + " " + ((plugin.lang.pickupCooldown))).equals(" "))){
										    player.sendMessage(ChatColor.translateAlternateColorCodes('&',plugin.lang.prefix + " " + ((plugin.lang.pickupCooldown).replace("%time%", timeLeft))));
										}
								    }
								}				
							}
							return;
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
					if (plugin.config.send && (!(plugin.lists.hasMessagesDisabled(player)))){
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
			if((this.plugin.config.onlyMobs && clicked instanceof Player) || (this.plugin.config.onlyPlayers && !(clicked instanceof Player)))
				return;
			if(this.plugin.config.clickAction.equals(Actions.RIDE) && !this.plugin.config.pickupOnlyMobs 
	        		|| (this.plugin.config.clickAction.equals(Actions.RIDE) && this.plugin.config.pickupOnlyMobs && clicked instanceof Player)){
				if((player.isInsideVehicle()&& Objects.equals(player.getVehicle(), clicked))){
					return;
				}
				if(perm){
					if(!plugin.config.disabledWorlds.isEmpty()) {
				    	if(plugin.config.disabledWorlds.contains(clicked.getWorld().toString().toUpperCase()) && !plugin.config.whitelistWorlds)
					    {
					    	return;
					    }
				    	if(!plugin.config.disabledWorlds.contains(clicked.getWorld().toString().toUpperCase()) && plugin.config.whitelistWorlds)
					    {
					    	return;
					    }
				    }
					if(!plugin.config.disabledEntities.isEmpty()) {
						if (plugin.config.disabledEntities.contains(clicked.getType().toString().toUpperCase()) && !plugin.config.whitelistEntities)
					    {			
							return;
					    }
						if (!plugin.config.disabledEntities.contains(clicked.getType().toString().toUpperCase()) && plugin.config.whitelistEntities)
					    {			
							return;
					    }
					}					
					if (clicked.getCustomName() != null) {
						if(!plugin.config.disabledCustomEntities.isEmpty()) {
							if(plugin.config.disabledCustomEntities.contains(clicked.getCustomName().toUpperCase()) && !plugin.config.whitelistCustomEntities)
							{						
								return;
							}
							if(!plugin.config.disabledCustomEntities.contains(clicked.getCustomName().toUpperCase()) && plugin.config.whitelistCustomEntities)
							{						
								return;
							}
						}
					}
					if ((clicked.hasMetadata("NPC")) && (!plugin.config.allowNPCs)) {
			           	if (plugin.config.send && (!(plugin.lists.hasMessagesDisabled(player)))){
			           		if(!((plugin.lang.prefix + " " + plugin.lang.noRideNPC).equals(" "))){
			            		player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.lang.prefix + " " + plugin.lang.noRideNPC));
			    	       	}
			    	    }
			        return;
			        }
					if(plugin.config.requireEmptyHand){
					    if(player.getItemInHand().getType()!=Material.AIR){
					    	if (plugin.config.send && (!(plugin.lists.hasMessagesDisabled(player)))){
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
					if(plugin.config.requireItem!=null){
						if(player.getItemInHand().getType()!=Material.AIR){
							if(plugin.config.requireItem!=player.getItemInHand().getType()) {
								if(!((plugin.lang.prefix + " " + plugin.lang.requireItem).equals(" "))){
									if(!(Piggyback.emptyHandCooldownPlayers.contains(player.getUniqueId()))){
										player.sendMessage(ChatColor.translateAlternateColorCodes('&', (plugin.lang.prefix + " " + plugin.lang.requireItem).replace("%requireItem%", plugin.config.requireItem.toString())));
										Piggyback.emptyHandCooldownPlayers.add(player.getUniqueId());
										new EmptyHandMessageCooldown(player).runTaskLater(plugin, plugin.config.messageCooldown);
									}
								}
								return;
							}
						}
					}
					if(clicked instanceof Player p)
			        {
						if(p.hasPermission("piggyback.no")) {
							return;
						}
			           	if(plugin.lists.isDisabled(player)){
					   		if(!((plugin.lang.prefix + " " + plugin.lang.noRidePlayerToggle).equals(" "))){
					   			player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.lang.prefix + " " + (plugin.lang.noRidePlayerToggle).replace("%player%", p.getDisplayName())));
					   		}
					   		return;
					   	}
					   	if(plugin.lists.isDisabled(p)){        	  
					   		if (plugin.config.send && (!(plugin.lists.hasMessagesDisabled(player)))){    			
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
					if(Piggyback.piggybackRideCooldownPlayers.containsKey(player.getUniqueId()) && !player.hasPermission("piggyback.cooldown.bypass")){
						DecimalFormat df = new DecimalFormat("#.##");
						double time = ((plugin.config.rideCooldown/20.0) -
								(((Long)System.currentTimeMillis() - 
								Piggyback.piggybackRideCooldownPlayers.get(player.getUniqueId()))
								/1000.0));
						String timeLeft;
						timeLeft = df.format(time);
						if(!Objects.equals(timeLeft, "0")){
							if(Piggyback.piggybackRideCooldownPlayers.containsKey(player.getUniqueId())){
								if (plugin.config.send && (!(plugin.lists.hasMessagesDisabled(player)))) {
									if(!((plugin.lang.prefix + " " + ((plugin.lang.rideCooldown))).equals(" "))){
									    player.sendMessage(ChatColor.translateAlternateColorCodes('&',plugin.lang.prefix + " " + ((plugin.lang.rideCooldown).replace("%time%", timeLeft))));
									}
							    }
							}				
						}
						return;
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
				}else{
					if (plugin.config.send && (!(plugin.lists.hasMessagesDisabled(player)))){
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
			if((this.plugin.config.onlyMobs && clicked instanceof Player) || (this.plugin.config.onlyPlayers && !(clicked instanceof Player)))
				return;
			if(perm) {
		        if(this.plugin.config.clickAction.equals(Actions.RIDE) && !this.plugin.config.pickupOnlyMobs 
		        		|| (this.plugin.config.clickAction.equals(Actions.RIDE) && this.plugin.config.pickupOnlyMobs && clicked instanceof Player)){
			        if(!clicked.getPassengers().isEmpty()){
			        	if(clicked.getPassengers().contains(player)){
			    		    return;
				        }
			        }
			        if(!plugin.config.disabledWorlds.isEmpty()) {
				    	if(plugin.config.disabledWorlds.contains(clicked.getWorld().toString().toUpperCase()) && !plugin.config.whitelistWorlds)
					    {
					    	return;
					    }
				    	if(!plugin.config.disabledWorlds.contains(clicked.getWorld().toString().toUpperCase()) && plugin.config.whitelistWorlds)
					    {
					    	return;
					    }
				    }
					if(!plugin.config.disabledEntities.isEmpty()) {
						if (plugin.config.disabledEntities.contains(clicked.getType().toString().toUpperCase()) && !plugin.config.whitelistEntities)
					    {			
							return;
					    }
						if (!plugin.config.disabledEntities.contains(clicked.getType().toString().toUpperCase()) && plugin.config.whitelistEntities)
					    {			
							return;
					    }
					}					
					if (clicked.getCustomName() != null) {
						if(!plugin.config.disabledCustomEntities.isEmpty()) {
							if(plugin.config.disabledCustomEntities.contains(clicked.getCustomName().toUpperCase()) && !plugin.config.whitelistCustomEntities)
							{						
								return;
							}
							if(!plugin.config.disabledCustomEntities.contains(clicked.getCustomName().toUpperCase()) && plugin.config.whitelistCustomEntities)
							{						
								return;
							}
						}
					}
		            if ((clicked.hasMetadata("NPC")) && (!plugin.config.allowNPCs)) {
		            	if (plugin.config.send && (!(plugin.lists.hasMessagesDisabled(player)))){
		             		if(!((plugin.lang.prefix + " " + plugin.lang.noRideNPC).equals(" "))){
		        	    		player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.lang.prefix + " " + plugin.lang.noRideNPC));
		    	        	}
		    	        }
		         	    return;
		            }
		            if(plugin.config.requireEmptyHand){
				        if(player.getItemInHand().getType()!=Material.AIR){
					        if (plugin.config.send && (!(plugin.lists.hasMessagesDisabled(player)))){
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
		            if(plugin.config.requireItem!=null){
						if(player.getItemInHand().getType()!=Material.AIR){
							if(plugin.config.requireItem!=player.getItemInHand().getType()) {
								if(!((plugin.lang.prefix + " " + plugin.lang.requireItem).equals(" "))){
									if(!(Piggyback.emptyHandCooldownPlayers.contains(player.getUniqueId()))){
										player.sendMessage(ChatColor.translateAlternateColorCodes('&', (plugin.lang.prefix + " " + plugin.lang.requireItem).replace("%requireItem%", plugin.config.requireItem.toString())));
										Piggyback.emptyHandCooldownPlayers.add(player.getUniqueId());
										new EmptyHandMessageCooldown(player).runTaskLater(plugin, plugin.config.messageCooldown);
									}
								}
								return;
							}
						}
					}
		            if(clicked instanceof Player p)
		            {
		            	if(p.hasPermission("piggyback.no")) {
							return;
						}
		            	if(plugin.lists.isDisabled(player)){
				    		if(!((plugin.lang.prefix + " " + plugin.lang.noRidePlayerToggle).equals(" "))){
				    			player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.lang.prefix + " " + (plugin.lang.noRidePlayerToggle).replace("%player%", p.getDisplayName())));
				    		}
				    		return;
				    	}
				    	if(plugin.lists.isDisabled(p)){        	  
				    		if (plugin.config.send && (!(plugin.lists.hasMessagesDisabled(player)))){    			
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
		            if(Piggyback.piggybackRideCooldownPlayers.containsKey(player.getUniqueId()) && !player.hasPermission("piggyback.cooldown.bypass")){
						DecimalFormat df = new DecimalFormat("#.##");
						double time = ((plugin.config.rideCooldown/20.0) -
								(((Long)System.currentTimeMillis() - 
								Piggyback.piggybackRideCooldownPlayers.get(player.getUniqueId()))
								/1000.0));
						String timeLeft;
						timeLeft = df.format(time);
						if(!Objects.equals(timeLeft, "0")){
							if(Piggyback.piggybackRideCooldownPlayers.containsKey(player.getUniqueId())){
								if (plugin.config.send && (!(plugin.lists.hasMessagesDisabled(player)))) {
									if(!((plugin.lang.prefix + " " + ((plugin.lang.rideCooldown))).equals(" "))){
									    player.sendMessage(ChatColor.translateAlternateColorCodes('&',plugin.lang.prefix + " " + ((plugin.lang.rideCooldown).replace("%time%", timeLeft))));
									}
							    }
							}				
						}
						return;
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
				if (plugin.config.send && (!(plugin.lists.hasMessagesDisabled(player)))){
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
		Bukkit.broadcastMessage("EVENT IS FIRING");
		if(this.plugin.config.clickType.equals(Clicks.EITHER) || this.plugin.config.clickType.equals(Clicks.RIGHT)) {
			boolean perm = false;
			Player player = event.getPlayer();
			Entity clicked = event.getClickedEntity();
			if ((player.hasPermission("piggyback.use")) || (player.isOp()))
		    {
			    perm = true;
		    }
			//pickup
			if((this.plugin.config.onlyMobs && clicked instanceof Player) || (this.plugin.config.onlyPlayers && !(clicked instanceof Player)))
				return;
			if(this.plugin.config.clickAction.equals(Actions.PICKUP) || (this.plugin.config.clickAction.equals(Actions.RIDE) && this.plugin.config.pickupOnlyMobs && !(clicked instanceof Player)) || 
					(this.plugin.config.clickAction.equals(Actions.RIDE) && this.plugin.config.pickupOnlyPlayers && clicked instanceof Player)){
			    if((player.isInsideVehicle()&& Objects.equals(player.getVehicle(), clicked))){
			        return;
			    }		    
				if(perm){
					if(!player.getPassengers().isEmpty()){
						Bukkit.broadcastMessage("Passengers is not empty.");
						if (player.getPassengers().contains(clicked))
						{
							Bukkit.broadcastMessage("player clicked on passenger...");
							if (plugin.config.throwRiderAway) 
							{
								Bukkit.broadcastMessage("Trying to throw");
								if(plugin.config.farThrowRider && player.isSprinting()) {
									//call my  far throw event
					            	PiggybackFarThrowEntityEvent farThrowEnt = new PiggybackFarThrowEntityEvent(clicked, player);
					            	Bukkit.getServer().getPluginManager().callEvent(farThrowEnt);
					            	if(!farThrowEnt.isCancelled()){
					            		event.setCancelled(true);
					            	}
								}else {
									//call my throw event
					            	PiggybackThrowEntityEvent throwEnt = new PiggybackThrowEntityEvent(clicked, player);
					            	Bukkit.getServer().getPluginManager().callEvent(throwEnt);
					            	if(!throwEnt.isCancelled()){
					            		event.setCancelled(true);
					            	}
								}		
							}else{
								Bukkit.broadcastMessage("Trying to drop");
								//call my drop event
								PiggybackDropEntityEvent dropEnt = new PiggybackDropEntityEvent(clicked, player);
								Bukkit.getServer().getPluginManager().callEvent(dropEnt);
								if(!dropEnt.isCancelled()){
									event.setCancelled(true);
				            	}
							}
						}	  
					}else {
						Bukkit.getServer().getLogger().info("Trying to Pickup...");
						if(!plugin.config.disabledWorlds.isEmpty()) {
					    	if(plugin.config.disabledWorlds.contains(clicked.getWorld().toString().toUpperCase()) && !plugin.config.whitelistWorlds)
						    {
						    	return;
						    }
					    	if(!plugin.config.disabledWorlds.contains(clicked.getWorld().toString().toUpperCase()) && plugin.config.whitelistWorlds)
						    {
						    	return;
						    }
					    }
						if(!plugin.config.disabledEntities.isEmpty()) {
							if (plugin.config.disabledEntities.contains(clicked.getType().toString().toUpperCase()) && !plugin.config.whitelistEntities)
						    {			
								return;
						    }
							if (!plugin.config.disabledEntities.contains(clicked.getType().toString().toUpperCase()) && plugin.config.whitelistEntities)
						    {			
								return;
						    }
						}					
						if (clicked.getCustomName() != null) {
							if(!plugin.config.disabledCustomEntities.isEmpty()) {
								if(plugin.config.disabledCustomEntities.contains(clicked.getCustomName().toUpperCase()) && !plugin.config.whitelistCustomEntities)
								{						
									return;
								}
								if(!plugin.config.disabledCustomEntities.contains(clicked.getCustomName().toUpperCase()) && plugin.config.whitelistCustomEntities)
								{						
									return;
								}
							}
						}
					    if ((clicked.hasMetadata("NPC")) && (!plugin.config.allowNPCs)) {
					    	if (plugin.config.send && (!(plugin.lists.hasMessagesDisabled(player)))){
					    		if(!((plugin.lang.prefix + " " + plugin.lang.noPickUpNPC).equals(" "))){
					    			player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.lang.prefix + " " + plugin.lang.noPickUpNPC));
					    		}
					    	}
					    	return;
					    }
					    if(plugin.config.requireEmptyHand){
							if(player.getItemInHand().getType()!=Material.AIR){
								if (plugin.config.send && (!(plugin.lists.hasMessagesDisabled(player)))){
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
					    if(plugin.config.requireItem!=null){
							if(player.getItemInHand().getType()!=Material.AIR){
								if(plugin.config.requireItem!=player.getItemInHand().getType()) {
									if(!((plugin.lang.prefix + " " + plugin.lang.requireItem).equals(" "))){
										if(!(Piggyback.emptyHandCooldownPlayers.contains(player.getUniqueId()))){
											player.sendMessage(ChatColor.translateAlternateColorCodes('&', (plugin.lang.prefix + " " + plugin.lang.requireItem).replace("%requireItem%", plugin.config.requireItem.toString())));
											Piggyback.emptyHandCooldownPlayers.add(player.getUniqueId());
											new EmptyHandMessageCooldown(player).runTaskLater(plugin, plugin.config.messageCooldown);
										}
									}
									return;
								}
							}
						}
					    if(clicked instanceof Player p)
					    {
					    	if(p.hasPermission("piggyback.no")) {
								return;
							}
							if(plugin.lists.isDisabled(player)){
					    		if(!((plugin.lang.prefix + " " + plugin.lang.noPickUpPlayerToggle).equals(" "))){
					    			player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.lang.prefix + " " + (plugin.lang.noPickUpPlayerToggle).replace("%player%", p.getDisplayName())));
					    		}
					    		return;
					    	}
					    	if(plugin.lists.isDisabled(clicked.getUniqueId())){        	  
					    		if (plugin.config.send && (!(plugin.lists.hasMessagesDisabled(player)))){    			
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
					    if(Piggyback.piggybackPickupCooldownPlayers.containsKey(player.getUniqueId()) && !player.hasPermission("piggyback.cooldown.bypass")){
							DecimalFormat df = new DecimalFormat("#.##");
							double time = ((plugin.config.pickupCooldown/20.0) -
									(((Long)System.currentTimeMillis() - 
									Piggyback.piggybackPickupCooldownPlayers.get(player.getUniqueId()))
									/1000.0));
							String timeLeft;
							timeLeft = df.format(time);
							if(!Objects.equals(timeLeft, "0")){
								if(Piggyback.piggybackPickupCooldownPlayers.containsKey(player.getUniqueId())){
									if (plugin.config.send && (!(plugin.lists.hasMessagesDisabled(player)))) {
										if(!((plugin.lang.prefix + " " + ((plugin.lang.pickupCooldown))).equals(" "))){
										    player.sendMessage(ChatColor.translateAlternateColorCodes('&',plugin.lang.prefix + " " + ((plugin.lang.pickupCooldown).replace("%time%", timeLeft))));
										}
								    }
								}				
							}
							return;
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
					if (plugin.config.send && (!(plugin.lists.hasMessagesDisabled(player)))){
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
