package me.blubdalegend.piggyback.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import me.blubdalegend.piggyback.Piggyback;
import me.blubdalegend.piggyback.actions.ThrowEntity;
import me.blubdalegend.piggyback.events.PiggybackDropEntityEvent;
import me.blubdalegend.piggyback.events.PiggybackPickupEntityEvent;
import me.blubdalegend.piggyback.events.PiggybackRideEntityEvent;
import me.blubdalegend.piggyback.events.PiggybackThrowEntityEvent;
import me.blubdalegend.piggyback.nms.NMStools;
import me.blubdalegend.piggyback.tasks.PickupClickCooldown;
import me.blubdalegend.piggyback.tasks.PiggybackPickupCooldown;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PiggybackEventsListener implements org.bukkit.event.Listener
{	
	private final Piggyback plugin;
  
	public PiggybackEventsListener(Piggyback plugin){
		this.plugin = plugin;
	}
  
  	@EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
	public void onEntityThrow(PiggybackThrowEntityEvent event)
	{		
  		Player player = event.getPlayer();
  		player.removePassenger(event.getEntity());
  		List<Entity> riders = new ArrayList<>();
		if(Piggyback.passengers.containsKey(player.getUniqueId())) {
			riders = Piggyback.passengers.get(player.getUniqueId());
		}
		riders.remove(event.getEntity());
		Piggyback.passengers.put(player.getUniqueId(),riders);
		ThrowEntity.throwEntity(event.getEntity(), event.getPlayer());
		if(!Objects.equals(Piggyback.version, "pre1_9")){
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
		Player player = event.getPlayer();
		player.removePassenger(event.getEntity());
		List<Entity> riders = new ArrayList<>();
		if(Piggyback.passengers.containsKey(player.getUniqueId())) {
			riders = Piggyback.passengers.get(player.getUniqueId());
		}
		riders.remove(event.getEntity());
		Piggyback.passengers.put(player.getUniqueId(),riders);
		if(!Objects.equals(Piggyback.version, "pre1_9")){
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
	
	@EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
	public void onEntityPickup(PiggybackPickupEntityEvent event)
	{
		Player player = event.getPlayer();
		Entity entity = event.getClicked();
		if(Piggyback.piggybackPickupCooldownPlayers.containsKey(player.getUniqueId()) && !player.hasPermission("piggyback.cooldown.bypass")){
			DecimalFormat df = new DecimalFormat("#.##");
			double time = ((plugin.config.pickupCooldown/20.0) -
					(((Long)System.currentTimeMillis() - 
					Piggyback.piggybackPickupCooldownPlayers.get(player.getUniqueId()))
					/1000.0));
			String timeLeft;
			timeLeft = df.format(time);
			if(!Objects.equals(timeLeft, "0")){
				if(!Piggyback.clickTimerCooldownPlayers.contains(player.getUniqueId())){
					if (plugin.config.send && (!(plugin.lists.messagePlayers.contains(player.getUniqueId().toString())))) {
						if(!((plugin.lang.prefix + " " + ((plugin.lang.pickupCooldown).replace("%time%", timeLeft))).equals(" "))){
						    player.sendMessage(ChatColor.translateAlternateColorCodes('&',plugin.lang.prefix + " " + ((plugin.lang.pickupCooldown).replace("%time%", timeLeft))));
						}
				    }
					Piggyback.clickTimerCooldownPlayers.add(player.getUniqueId());
					new PickupClickCooldown(player).runTaskLater(plugin, 20);
				}				
			}
		}
		if(player.hasPermission("piggyback.cooldown.bypass") || !(Piggyback.piggybackPickupCooldownPlayers.containsKey(player.getUniqueId())) || plugin.config.pickupCooldown==0){
			if(!(player.hasPermission("piggyback.cooldown.bypass")) || plugin.config.pickupCooldown!=0){
				Piggyback.piggybackPickupCooldownPlayers.put(player.getUniqueId(), System.currentTimeMillis());
				new PiggybackPickupCooldown(player).runTaskLater(plugin, plugin.config.pickupCooldown);
			}
			player.addPassenger(entity);
			List<Entity> riders = new ArrayList<>();
			if(Piggyback.passengers.containsKey(player.getUniqueId())) {
				riders = Piggyback.passengers.get(player.getUniqueId());
			}
			if(!riders.contains(entity))
				riders.add(entity);
			Piggyback.passengers.put(player.getUniqueId(),riders);
		    if(!Objects.equals(Piggyback.version, "pre1_9")){
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
	
	@EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
	public void onEntityRide(PiggybackRideEntityEvent event)
	{
		Player player = event.getPlayer();
		Entity clicked = event.getClicked();
		if(Piggyback.piggybackPickupCooldownPlayers.containsKey(player.getUniqueId()) && !player.hasPermission("piggyback.cooldown.bypass")){
			DecimalFormat df = new DecimalFormat("#.##");
			double time = ((plugin.config.pickupCooldown/20.0) -
					(((Long)System.currentTimeMillis() - 
					Piggyback.piggybackPickupCooldownPlayers.get(player.getUniqueId()))
					/1000.0));
			String timeLeft;
			timeLeft = df.format(time);
			if(!Objects.equals(timeLeft, "0")){
				if(!Piggyback.clickTimerCooldownPlayers.contains(player.getUniqueId())){
					if (plugin.config.send && (!(plugin.lists.messagePlayers.contains(player.getUniqueId().toString())))) {
					    if(!((plugin.lang.prefix + " " + ((plugin.lang.rideCooldown).replace("%time%", timeLeft))).equals(" "))){
						    player.sendMessage(ChatColor.translateAlternateColorCodes('&',plugin.lang.prefix + " " + ((plugin.lang.rideCooldown).replace("%time%", timeLeft))));
					    }
					}
					Piggyback.clickTimerCooldownPlayers.add(player.getUniqueId());
					new PickupClickCooldown(player).runTaskLater(plugin, 20);
				}				
			}
		}
		if(player.hasPermission("piggyback.cooldown.bypass") || !(Piggyback.piggybackPickupCooldownPlayers.containsKey(player.getUniqueId())) || plugin.config.pickupCooldown==0){
			if(!(player.hasPermission("piggyback.cooldown.bypass")) || plugin.config.pickupCooldown!=0){
				Piggyback.piggybackPickupCooldownPlayers.put(player.getUniqueId(), System.currentTimeMillis());
				new PiggybackPickupCooldown(player).runTaskLater(plugin, plugin.config.pickupCooldown);
			}
			clicked.addPassenger(player);
			List<Entity> riders = new ArrayList<>();
			if(Piggyback.passengers.containsKey(clicked.getUniqueId())) {
				riders = Piggyback.passengers.get(clicked.getUniqueId());
			}
			if(!riders.contains(player))
				riders.add(player);
			Piggyback.passengers.put(clicked.getUniqueId(),riders);
		    if(!Objects.equals(Piggyback.version, "pre1_9")){
				try{
					NMStools.sendMountPacket();
				}catch(IllegalStateException e){
			    	return;
			    } 		
			}
		    if (plugin.config.send && (!(plugin.lists.messagePlayers.contains(player.getUniqueId().toString())))) 
		    {
		    	if(clicked.getPassengers().contains(player)){
		    		if(!((plugin.lang.prefix + " " + plugin.lang.rideMsg).equals(" "))){    			
		    			player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.lang.prefix + " " + (plugin.lang.rideMsg).replace("%clicked%", getEntityName(clicked))));
		    		}
		    	}
		    }
		}
	}
	
	public String getEntityName(@NotNull Entity entity)
	{
		if(entity.getCustomName()!=null){
			return entity.getCustomName();
		}else {
			return entity.getName();
		}
	} 
}
