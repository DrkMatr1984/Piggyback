package me.blubdalegend.piggyback.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.Plugin;

import me.blubdalegend.piggyback.events.PlayerInteractEntityBaseClickEvent;
import me.blubdalegend.piggyback.events.PlayerInteractEntityCrouchLeftClickEvent;
import me.blubdalegend.piggyback.events.PlayerInteractEntityCrouchRightClickEvent;

public class EntityInteractListener implements Listener
{
	private Plugin plugin;
	
	public EntityInteractListener(Plugin plugin){
		this.plugin = plugin;
	}
	/*
	@EventHandler(priority=EventPriority.LOW, ignoreCancelled=true)
	public void onPlayerLeftClickEntity(EntityDamageByEntityEvent event) {
		if(event.getDamager() instanceof Player)
		{		
			if(event.getEntity() instanceof LivingEntity) {
				Player player = (Player) event.getDamager();
				PlayerInteractEntityBaseClickEvent clickEvent;
				if(player.isSneaking()) {
					clickEvent = new PlayerInteractEntityCrouchLeftClickEvent(plugin, player, (LivingEntity) event.getEntity(), false);
					Bukkit.getServer().getPluginManager().callEvent(clickEvent);
					if(clickEvent.isCancelled()) {
						event.setCancelled(true);
						return;
					}
					if(((PlayerInteractEntityCrouchLeftClickEvent) clickEvent).getDamageCancelled()) {
						event.setDamage(0.0D);
	  				  	event.setCancelled(true);
					}
				}							
			}
		}
	}
	
	@EventHandler(priority=EventPriority.LOW, ignoreCancelled=true)
	public void onPlayerRightClickEntity(PlayerInteractEntityEvent event){
		if(event.getHand().equals(EquipmentSlot.HAND)) {
			if(event.getRightClicked() instanceof LivingEntity) {
				PlayerInteractEntityBaseClickEvent clickEvent;
				Player player = event.getPlayer();
				if(player.isSneaking()) {
					clickEvent = new PlayerInteractEntityCrouchRightClickEvent(plugin, event.getPlayer(), (LivingEntity) event.getRightClicked());
					Bukkit.getServer().getPluginManager().callEvent(clickEvent);
					if(clickEvent.isCancelled()) {
						event.setCancelled(true);
						return;
					}
				}				
			}
		}	
	}*/
}