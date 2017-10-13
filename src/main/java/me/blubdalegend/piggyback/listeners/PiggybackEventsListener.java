package me.blubdalegend.piggyback.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import me.blubdalegend.piggyback.Piggyback;
import me.blubdalegend.piggyback.actions.ThrowEntity;
import me.blubdalegend.piggyback.events.PiggybackDropEntityEvent;
import me.blubdalegend.piggyback.events.PiggybackPickupEntityEvent;
import me.blubdalegend.piggyback.events.PiggybackThrowEntityEvent;
import me.blubdalegend.piggyback.nms.NMStools;
import me.blubdalegend.piggyback.tasks.PiggybackPickupCooldown;

import java.text.DecimalFormat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;

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
		if(event.getPlayer().hasPermission("piggyback.cooldown.bypass") || !(Piggyback.piggybackPickupCooldownPlayers.containsKey(event.getPlayer().getUniqueId())) || plugin.config.pickupCooldown==0){
			if(!(event.getPlayer().hasPermission("piggyback.cooldown.bypass")) || plugin.config.pickupCooldown!=0){
				Piggyback.piggybackPickupCooldownPlayers.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
				Bukkit.getServer().getScheduler().runTaskLater(plugin, new PiggybackPickupCooldown(event.getPlayer()), plugin.config.pickupCooldown);
			}
			event.getPlayer().addPassenger(event.getEntity());
		    if(Piggyback.version!="pre1_9"){
				try{
					NMStools.sendMountPacket();
				}catch(IllegalStateException e){
			    	return;
			    } 		
			}
		    if (plugin.config.send && (!(plugin.lists.messagePlayers.contains(event.getPlayer().getUniqueId().toString())))) 
		    {
		    	if(event.getPlayer().getPassengers().contains(event.getEntity())){
		    		if(!((plugin.lang.prefix + " " + plugin.lang.carryMsg).equals(" "))){    			
		    			event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.lang.prefix + " " + (plugin.lang.carryMsg).replace("%passenger%", getEntityName(event.getEntity()))));
		    		}
		    	}
		    }
		}
		if(Piggyback.piggybackPickupCooldownPlayers.containsKey(event.getPlayer().getUniqueId())){
			DecimalFormat df = new DecimalFormat("#.##");
			double time = ((plugin.config.pickupCooldown/20) - 
					(((Long)System.currentTimeMillis() - 
					Piggyback.piggybackPickupCooldownPlayers.get(event.getPlayer().getUniqueId()))
					/1000));
			String timeLeft = df.format(time);
			//Make spam click cooldown task to prevent too many spam clicks here, maybe one click per second(20 ticks)
			event.getPlayer().sendMessage("Time Left:: " + timeLeft + " seconds"); //Make this prettier and add to language file
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
