package me.blubdalegend.piggyback.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

import me.blubdalegend.piggyback.Piggyback;
import me.blubdalegend.piggyback.events.PiggybackDropEntityEvent;
import me.blubdalegend.piggyback.events.PiggybackPickupEntityEvent;
import me.blubdalegend.piggyback.events.PiggybackThrowEntityEvent;
import me.blubdalegend.piggyback.tasks.EmptyHandMessageCooldown;
import me.blubdalegend.piggyback.tasks.NoPermsMessageCooldown;
import me.blubdalegend.piggyback.tasks.ToggleMessageCooldown;

public class RightClickListener implements org.bukkit.event.Listener
{
	private Piggyback plugin;
	
	public RightClickListener(Piggyback plugin)
	{
		this.plugin = plugin;
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority=EventPriority.HIGH, ignoreCancelled=false)
	public void checkRightClick(PlayerInteractEntityEvent event){
		if(plugin.config.cancelPickupIfAnotherPlugin){
			if(event.isCancelled()){
				return;
			}
		}
		Entity clicked = event.getRightClicked();
		Player player = event.getPlayer();
		boolean perm = false;
		if(event.getHand().equals(EquipmentSlot.HAND)){
			if (player.isSneaking())
			{
				if((player.isInsideVehicle()&&player.getVehicle().equals(clicked))){
					return;
		        }
				if ((player.hasPermission("piggyback.use")) || (player.isOp()))
				{
					perm = true;
				}
				if(perm)
				{
					if(!player.getPassengers().isEmpty()){
						if (player.getPassengers().contains(clicked))
						{
							if (plugin.config.throwRider) 
							{
				            	//call my throw event
				            	PiggybackThrowEntityEvent throwEnt = new PiggybackThrowEntityEvent(clicked, player);
				            	Bukkit.getServer().getPluginManager().callEvent(throwEnt);	
							}else{
								//call my drop event
								PiggybackDropEntityEvent dropEnt = new PiggybackDropEntityEvent(clicked, player);
								Bukkit.getServer().getPluginManager().callEvent(dropEnt);
							}
							return;
						}
					}else{
						if (!(clicked instanceof LivingEntity) || plugin.config.disabledEntities.contains(clicked.getType().toString().toUpperCase()))
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
							if(Piggyback.version!="pre1_9"){
								if(player.getInventory().getItemInMainHand().getType()!=Material.AIR){
									if (plugin.config.send && (!(plugin.lists.messagePlayers.contains(player.getUniqueId().toString())))){
										if(!((plugin.lang.prefix + " " + plugin.lang.emptyHand).equals(" "))){
											if(!(Piggyback.emptyHandCooldownPlayers.contains(player.getUniqueId()))){
												player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.lang.prefix + " " + plugin.lang.emptyHand));
												Piggyback.emptyHandCooldownPlayers.add(player.getUniqueId());
												Bukkit.getServer().getScheduler().runTaskLater(plugin, new EmptyHandMessageCooldown(player), plugin.config.messageCooldown);
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
												Bukkit.getServer().getScheduler().runTaskLater(plugin, new EmptyHandMessageCooldown(player), plugin.config.messageCooldown);
											}
											
										}
									}
									return;
								}
							}
						}
					    if(clicked instanceof Player)
					    {
					    	Player p = (Player)clicked;
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
					    					Bukkit.getServer().getScheduler().runTaskLater(plugin, new ToggleMessageCooldown(player), plugin.config.messageCooldown);
					    				}
					    			}
					    		}
					    		return;
					    	}
					    }
					    //call my pickup event
					    PiggybackPickupEntityEvent pickupEnt = new PiggybackPickupEntityEvent(clicked, player);
					    Bukkit.getServer().getPluginManager().callEvent(pickupEnt);
					    return;
					}
				}else{
					if (plugin.config.send && (!(plugin.lists.messagePlayers.contains(player.getUniqueId().toString())))){
						if(!((plugin.lang.prefix + " " + plugin.lang.noPerms).equals(" "))){
							if(!(Piggyback.noPermsCooldownPlayers.contains(player.getUniqueId()))){
								player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.lang.prefix + " " + plugin.lang.noPerms));
								Piggyback.noPermsCooldownPlayers.add(player.getUniqueId());
								Bukkit.getServer().getScheduler().runTaskLater(plugin, new NoPermsMessageCooldown(player), plugin.config.messageCooldown);
							}
						}			  
					}
				}
				
			}
		}		
	}	
}