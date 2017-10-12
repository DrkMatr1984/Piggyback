package me.blubdalegend.piggyback.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import me.blubdalegend.piggyback.Piggyback;
import me.blubdalegend.piggyback.nms.NMStools;

public class BukkitListeners implements org.bukkit.event.Listener{
	
	public BukkitListeners() {
	}
	
	@EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
	public void cancelVehiclePassengerDamage(EntityDamageByEntityEvent event)
	{
		//disable damage from Vehicle(Player) to Rider 
		if(event.getDamager() instanceof Player){
			Player player = (Player)event.getDamager();
			Entity clicked = event.getEntity();
			if(player.getPassengers().contains(clicked)){
				event.setDamage(0.0D);
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=false)
	public void onPlayerToggleSneakEvent(PlayerToggleSneakEvent event) {
		Player player = event.getPlayer();
		if(Piggyback.version!="pre1_9"){
			if(player.getVehicle()!=null){
				NMStools.sendMountPacket();
			} 
		}  
	}
}
