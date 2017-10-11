package me.blubdalegend.piggyback.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

import me.blubdalegend.piggyback.Piggyback;
import me.blubdalegend.piggyback.events.PiggybackDropEntityEvent;
import me.blubdalegend.piggyback.events.PiggybackPickupEntityEvent;
import me.blubdalegend.piggyback.events.PiggybackThrowEntityEvent;

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
		if (player.isSneaking())
		{
			if(plugin.config.shiftRightClick){
				if ((player.hasPermission("piggyback.use")) || (player.isOp()))
				{
					if(Piggyback.version!="pre1_9"){
						if(event.getHand().equals(EquipmentSlot.HAND)){
							perm = true;
						}
					}else{
						perm = true;
					}
				}
			}
			if(perm)
			{
				if(plugin.config.requireEmptyHand){
					if(Piggyback.version!="pre1_9"){
						if(player.getInventory().getItemInMainHand().getType()!=Material.AIR){
							return;
						}
					}else{
						if(player.getItemInHand().getType()!=Material.AIR){
							return;
						}
					}
				}
				if(!(player.isEmpty())){
					if (player.getPassenger().equals(clicked))
					{
						if(player.isInsideVehicle()&&player.getVehicle().equals(clicked)){
							return;
				        }
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
					if((player.isInsideVehicle()&&player.getVehicle().equals(clicked))||(clicked.isInsideVehicle()&&clicked.getVehicle().equals(player))){
						return;
			        }
				    if ((clicked.getType() == EntityType.PAINTING) || (clicked.getType() == EntityType.ITEM_FRAME) || (clicked.getType() == EntityType.ARROW) || (plugin.config.disabledEntities.contains(clicked.getType().toString())))
				    {
				    	return;
				    }
				    if ((clicked.hasMetadata("NPC")) && (!plugin.config.pickupNPC)) {
				    	if(plugin.config.send){
				    		if(!((plugin.config.prefix + " " + plugin.config.noPickUpNPC).equals(" "))){
				    			player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.config.prefix + " " + plugin.config.noPickUpNPC));
				    		}
				    	}
				    	return;
				    }
				    if(plugin.config.disabledWorlds.contains(clicked.getWorld().toString()))
				    {
				    	return;
				    }
				    if(clicked instanceof Player)
				    {
				    	if(plugin.config.disabledPlayers.contains(clicked.getUniqueId().toString())){        	  
				    		if (plugin.config.send)
				    		{
				    			if(!((plugin.config.prefix + " " + plugin.config.noPickUpPlayer).equals(" "))){
				    				player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.config.prefix + " " + plugin.config.noPickUpPlayer));
				    			}
				    		}
				    		return;
				    	}
				    }
				    //call my pickup event
				    PiggybackPickupEntityEvent pickupEnt = new PiggybackPickupEntityEvent(clicked, player);
				    Bukkit.getServer().getPluginManager().callEvent(pickupEnt);				    
				}
			}else{
				if (plugin.config.send)
				{
					if(!((plugin.config.prefix + " " + plugin.config.noPerms).equals(" "))){
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.config.prefix + " " + plugin.config.noPerms));
					}			  
				}
			}
			
		}
	}	
}