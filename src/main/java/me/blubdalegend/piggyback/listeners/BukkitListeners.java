package me.blubdalegend.piggyback.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDismountEvent;

import me.blubdalegend.piggyback.Piggyback;

public class BukkitListeners implements Listener{
	
	public BukkitListeners() {
	}
	
	@EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
	public void cancelVehiclePassengerDamage(EntityDamageByEntityEvent event)
	{
		//disable damage from Vehicle(Player) to Rider 
		if(event.getDamager() instanceof Player player){
			Entity clicked = event.getEntity();
			if(player.getPassengers().contains(clicked)){
				event.setDamage(0.0D);
				event.setCancelled(true);
			}
		}
	}
	
	//prevent player from dropping carried passengers underwater
	@EventHandler(priority=EventPriority.HIGHEST)
	public void preventDismountUnderWater(EntityDismountEvent event) {
		if(Piggyback.passengers.containsKey(event.getDismounted().getUniqueId())) {
			if(Piggyback.passengers.get(event.getDismounted().getUniqueId()) == event.getEntity()) {
				if(event.getEntity().getLocation().getBlock().getType()==Material.WATER) {
					event.getDismounted().addPassenger(event.getEntity());
			    }				
			}
		}	
	}
	
}
