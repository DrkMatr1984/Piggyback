package me.blubdalegend.piggyback.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import me.blubdalegend.piggyback.Piggyback;
import me.blubdalegend.piggyback.actions.ThrowEntity;
import me.blubdalegend.piggyback.events.PiggybackDropEntityEvent;
import me.blubdalegend.piggyback.events.PiggybackPickupEntityEvent;
import me.blubdalegend.piggyback.events.PiggybackThrowEntityEvent;
import me.blubdalegend.piggyback.nms.NMStools;

import org.bukkit.ChatColor;

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
		if (plugin.config.send)
		{
			if(!((plugin.config.prefix + " " + plugin.config.throwMsg).equals(" "))){
				 event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.config.prefix + " " + plugin.config.throwMsg));
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
		if (plugin.config.send)
		{
			if(!((plugin.config.prefix + " " + plugin.config.dropMsg).equals(" "))){
				event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.config.prefix + " " + plugin.config.dropMsg));
			}		  
		}
	}
	
	@EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
	public void onEntityPickup(PiggybackPickupEntityEvent event)
	{		
	    event.getPlayer().addPassenger(event.getEntity());
	    if(Piggyback.version!="pre1_9"){
			try{
				NMStools.sendMountPacket();
			}catch(IllegalStateException e){
		    	return;
		    } 		
		}
	    if (plugin.config.send) 
	    {
	    	if(event.getPlayer().getPassengers().contains(event.getEntity())){
	    		if(!((plugin.config.prefix + " " + plugin.config.carryMsg).equals(" "))){
	    			event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.config.prefix + " " + plugin.config.carryMsg));
	    		}
	    	}
	    }		
	}
  
}
