package me.blubdalegend.piggyback.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.scheduler.BukkitRunnable;

import me.blubdalegend.piggyback.Piggyback;
import me.blubdalegend.piggyback.actions.ThrowEntity;
import me.blubdalegend.piggyback.events.PiggybackDropEntityEvent;
import me.blubdalegend.piggyback.events.PiggybackFarThrowEntityEvent;
import me.blubdalegend.piggyback.events.PiggybackPickupEntityEvent;
import me.blubdalegend.piggyback.events.PiggybackRideEntityEvent;
import me.blubdalegend.piggyback.events.PiggybackThrowEntityEvent;
import me.blubdalegend.piggyback.tasks.PiggybackPickupCooldown;
import me.blubdalegend.piggyback.tasks.PiggybackRideCooldown;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.bukkit.event.entity.EntityDismountEvent;

public class PiggybackEventsListener implements org.bukkit.event.Listener
{	
	private final Piggyback plugin;
	private List<Player> playersRiding = new ArrayList<Player>();
  
	public PiggybackEventsListener(Piggyback plugin){
		this.plugin = plugin;
	}
  
  	@EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
	public void onEntityThrow(PiggybackThrowEntityEvent event)
	{		
  		Player player = event.getPlayer();
  		player.sendMessage("Got to PiggybackThrowEntityEvent");
  		player.removePassenger(event.getEntity());
  		List<Entity> riders = new ArrayList<>();
		if(Piggyback.passengers.containsKey(player.getUniqueId())) {
			riders = Piggyback.passengers.get(player.getUniqueId());
		}
		riders.remove(event.getEntity());
		Piggyback.passengers.put(player.getUniqueId(),riders);
		ThrowEntity.throwEntity(event.getEntity(), event.getPlayer());
		if (plugin.config.send && (!(plugin.lists.messagePlayers.contains(event.getPlayer().getUniqueId().toString()))))
		{
			if(!((plugin.lang.prefix + " " + plugin.lang.throwMsg).equals(" "))){
				 event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.lang.prefix + " " + (plugin.lang.throwMsg).replace("%passenger%", getEntityName(event.getEntity()))));
			}		  
		}
	}
  	
  	@EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
	public void onEntityFarThrow(PiggybackFarThrowEntityEvent event)
	{		
  		Player player = event.getPlayer();
  		player.sendMessage("Got to PiggybackFarThrowEntityEvent");
  		player.removePassenger(event.getEntity());
  		List<Entity> riders = new ArrayList<>();
		if(Piggyback.passengers.containsKey(player.getUniqueId())) {
			riders = Piggyback.passengers.get(player.getUniqueId());
		}
		riders.remove(event.getEntity());
		Piggyback.passengers.put(player.getUniqueId(),riders);
		ThrowEntity.farThrowEntity(event.getEntity(), event.getPlayer());
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
		player.sendMessage("Got to PiggyBackDropEntityEvent");
		player.removePassenger(event.getEntity());
		List<Entity> riders = new ArrayList<>();
		if(Piggyback.passengers.containsKey(player.getUniqueId())) {
			riders = Piggyback.passengers.get(player.getUniqueId());
		}
		riders.remove(event.getEntity());
		Piggyback.passengers.put(player.getUniqueId(),riders);
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
		if(player.hasPermission("piggyback.cooldown.bypass") || !(Piggyback.piggybackPickupCooldownPlayers.containsKey(player.getUniqueId())) || plugin.config.pickupCooldown==0){
			if(!(player.hasPermission("piggyback.cooldown.bypass"))){
				Piggyback.piggybackPickupCooldownPlayers.put(player.getUniqueId(), System.currentTimeMillis());
				new PiggybackPickupCooldown(player).runTaskLater(plugin, plugin.config.pickupCooldown);
			}
	    }
		player.addPassenger(entity);
		List<Entity> riders = new ArrayList<>();
		if(Piggyback.passengers.containsKey(player.getUniqueId())) {
			riders = Piggyback.passengers.get(player.getUniqueId());
		}
		if(!riders.contains(entity))
			riders.add(entity);
		Piggyback.passengers.put(player.getUniqueId(),riders);
		if (plugin.config.send && (!(plugin.lists.messagePlayers.contains(player.getUniqueId().toString())))) 
		{
		    if(player.getPassengers().contains(entity)){
		    	if(!((plugin.lang.prefix + " " + plugin.lang.carryMsg).equals(" "))){    			
		  			player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.lang.prefix + " " + (plugin.lang.carryMsg).replace("%passenger%", getEntityName(entity))));
		   		}
		   	}
		}		
	}
	
	@EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
	public void onEntityRide(PiggybackRideEntityEvent event)
	{
		Player player = event.getPlayer();
		Entity clicked = event.getClicked();
		if(player.hasPermission("piggyback.cooldown.bypass") || !(Piggyback.piggybackRideCooldownPlayers.containsKey(player.getUniqueId())) || plugin.config.rideCooldown==0){
			if(!(player.hasPermission("piggyback.cooldown.bypass"))){
				Piggyback.piggybackRideCooldownPlayers.put(player.getUniqueId(), System.currentTimeMillis());
				new PiggybackRideCooldown(player).runTaskLater(plugin, plugin.config.rideCooldown);
			}
	    }
		clicked.addPassenger(player);
		List<Entity> riders = new ArrayList<>();
		if(Piggyback.passengers.containsKey(clicked.getUniqueId())) {
			riders = Piggyback.passengers.get(clicked.getUniqueId());
		}
		if(!riders.contains(player))
			riders.add(player);
		Piggyback.passengers.put(clicked.getUniqueId(),riders);
		
		//Disallow dismount for a time so riding players don't fall right off
		playersRiding.add(player);
		new BukkitRunnable() {
			@Override
			public void run() {
				if(playersRiding.contains(player))
					playersRiding.remove(player);		
			}}.runTaskLater(plugin, 25);
				
		if (plugin.config.send && (!(plugin.lists.messagePlayers.contains(player.getUniqueId().toString())))) 
		{
		  	if(clicked.getPassengers().contains(player)){
		   		if(!((plugin.lang.prefix + " " + plugin.lang.rideMsg).equals(" "))){    			
		   			player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.lang.prefix + " " + (plugin.lang.rideMsg).replace("%clicked%", getEntityName(clicked))));
		   		}
		   	}
		}
		
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onEntityRideDismount(EntityDismountEvent event)
	{
		if(event.getEntity() instanceof Player) {
			if(!playersRiding.isEmpty())
				if(playersRiding.contains((Player) event.getEntity()))
					event.setCancelled(true);
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
