package me.blubdalegend.piggyback.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import me.blubdalegend.piggyback.Piggyback;
import me.blubdalegend.piggyback.actions.ThrowEntity;
import me.blubdalegend.piggyback.events.PiggybackDropEntityEvent;
import me.blubdalegend.piggyback.events.PiggybackPickupEntityEvent;
import me.blubdalegend.piggyback.events.PiggybackThrowEntityEvent;
import me.blubdalegend.piggyback.nms.NMStools;
import me.blubdalegend.piggyback.tasks.PickupClickCooldown;
import me.blubdalegend.piggyback.tasks.PiggybackPickupCooldown;

import java.text.DecimalFormat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class PiggybackEventsListener implements org.bukkit.event.Listener
{	
	private Piggyback plugin;
  
	public PiggybackEventsListener(Piggyback plugin){
		this.plugin = plugin;
	}
  
  	@EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
	public void onEntityThrow(PiggybackThrowEntityEvent event)
	{		
  		event.getPlayer().removePassenger(event.getEntity());
		ThrowEntity.throwEntity(event.getEntity(), event.getPlayer());
		if(Piggyback.version!="pre1_9"){
			try{
				NMStools.sendMountPacket();
			}catch(IllegalStateException e){
		    	return;
		    } 		
		}
		if (plugin.config.send && (!(plugin.lists.messagePlayers.contains(event.getPlayer().getUniqueId().toString()))))
		{
			if(!((plugin.lang.prefix + " " + plugin.lang.throwMsg).equals(" "))){
				 event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.lang.prefix + " " + (plugin.lang.throwMsg).replace("%passenger%", getEntityName(event.getEntity()))));
			}		  
		}
	}
	
	@EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
	public void onEntityDrop(PiggybackDropEntityEvent event)
	{		
		event.getPlayer().removePassenger(event.getEntity());		            		
		if(Piggyback.version!="pre1_9"){
			try{
				NMStools.sendMountPacket();
			}catch(IllegalStateException e){
		    	return;
		    } 		
		}
		if (plugin.config.send && (!(plugin.lists.messagePlayers.contains(event.getPlayer().getUniqueId().toString()))))
		{
			if(!((plugin.lang.prefix + " " + plugin.lang.dropMsg).equals(" "))){
				event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.lang.prefix + " " + (plugin.lang.dropMsg).replace("%passenger%", getEntityName(event.getEntity()))));
			}		  
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
	public void onEntityPickup(PiggybackPickupEntityEvent event)
	{
		Player player = event.getPlayer();
		Entity entity = event.getEntity();
		if(Piggyback.piggybackPickupCooldownPlayers.containsKey(player.getUniqueId())){
			DecimalFormat df = new DecimalFormat("#.##");
			double time = ((plugin.config.pickupCooldown/20) - 
					(((Long)System.currentTimeMillis() - 
					Piggyback.piggybackPickupCooldownPlayers.get(player.getUniqueId()))
					/1000));
			String timeLeft = "";
			timeLeft = df.format(time);
			if(timeLeft!="0"){
				if(!Piggyback.clickTimerCooldownPlayers.contains(player.getUniqueId())){
					if(!((plugin.lang.prefix + " " + ((plugin.lang.pickupCooldown).replace("%time%", timeLeft))).equals(" "))){
						player.sendMessage(ChatColor.translateAlternateColorCodes('&',plugin.lang.prefix + " " + ((plugin.lang.pickupCooldown).replace("%time%", timeLeft))));
					}					
					Piggyback.clickTimerCooldownPlayers.add(player.getUniqueId());
					Bukkit.getServer().getScheduler().runTaskLater(plugin, new PickupClickCooldown(player), 20);
				}				
			}
		}
		if(player.hasPermission("piggyback.cooldown.bypass") || !(Piggyback.piggybackPickupCooldownPlayers.containsKey(player.getUniqueId())) || plugin.config.pickupCooldown==0){
			if(!(player.hasPermission("piggyback.cooldown.bypass")) || plugin.config.pickupCooldown!=0){
				Piggyback.piggybackPickupCooldownPlayers.put(player.getUniqueId(), System.currentTimeMillis());
				Bukkit.getServer().getScheduler().runTaskLater(plugin, new PiggybackPickupCooldown(player), plugin.config.pickupCooldown);
			}
			player.addPassenger(entity);
		    if(Piggyback.version!="pre1_9"){
				try{
					NMStools.sendMountPacket();
				}catch(IllegalStateException e){
			    	return;
			    } 		
			}
		    if (plugin.config.send && (!(plugin.lists.messagePlayers.contains(player.getUniqueId().toString())))) 
		    {
		    	if(player.getPassengers().contains(entity)){
		    		if(!((plugin.lang.prefix + " " + plugin.lang.carryMsg).equals(" "))){    			
		    			player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.lang.prefix + " " + (plugin.lang.carryMsg).replace("%passenger%", getEntityName(entity))));
		    		}
		    	}
		    }
		}		
	}
	
	public String getEntityName(Entity entity)
	{
		if(entity.getCustomName()!=null){
			return entity.getCustomName();
		}else if(entity.getName()!=null){
			return entity.getName();
		}else{
			return (entity.getType().toString()).toLowerCase();
		}
	} 
}
