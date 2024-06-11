package me.blubdalegend.piggyback.events;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class PlayerInteractEntityCrouchRightClickEvent extends PlayerInteractEntityBaseClickEvent{
	
	public PlayerInteractEntityCrouchRightClickEvent(Plugin plugin, Player player, Entity clickedEntity){
		super(plugin, player, clickedEntity);
	}
}