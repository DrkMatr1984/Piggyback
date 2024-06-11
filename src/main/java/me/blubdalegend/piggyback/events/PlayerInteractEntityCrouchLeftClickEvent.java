package me.blubdalegend.piggyback.events;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class PlayerInteractEntityCrouchLeftClickEvent extends PlayerInteractEntityBaseClickEvent{
	private boolean cancelDamage = false;
	
	public PlayerInteractEntityCrouchLeftClickEvent(Plugin plugin, Player player, Entity clickedEntity, boolean cancelDamage){
		super(plugin, player, clickedEntity);
	}
	
	public void setDamageCancelled(boolean cancelled) {
		this.cancelDamage = cancelled;
	}
	
	public boolean getDamageCancelled() {
		return this.cancelDamage;
	}
}