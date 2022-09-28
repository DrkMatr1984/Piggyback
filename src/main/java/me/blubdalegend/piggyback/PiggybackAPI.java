package me.blubdalegend.piggyback;

import java.util.UUID;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import me.blubdalegend.piggyback.actions.ThrowEntity;
import org.jetbrains.annotations.NotNull;

public class PiggybackAPI
{
	private static Piggyback plugin;
	
	public PiggybackAPI(Piggyback pb) {
		plugin = pb;
	}
	
	public static boolean isDisabled(Player p) {
		return plugin.lists.isDisabled(p);
	}
	
	public static boolean isDisabled(UUID id) {
		return plugin.lists.isDisabled(id);
	}
	
	public static void setDisabled(Player p, boolean b) {
		plugin.lists.setDisabled(p, b);
	}
	
	public static void setDisabled(UUID id, boolean b) {
		plugin.lists.setDisabled(id, b);
	}
	
	public static boolean hasMessagesDisabled(@NotNull Player p) {
		return plugin.lists.messagePlayers.contains(p.getUniqueId().toString());
	}
	
	public static boolean hasMessagesDisabled(@NotNull UUID id) {
		return plugin.lists.messagePlayers.contains(id.toString());
	}
	
	public static void setMessagesDisabled(@NotNull Player p, boolean b) {
		if(b) {
			if(!plugin.lists.messagePlayers.contains(p.getUniqueId().toString()))
		        plugin.lists.messagePlayers.add(p.getUniqueId().toString());
		}else {
			if(plugin.lists.messagePlayers.contains(p.getUniqueId().toString()))
		        plugin.lists.messagePlayers.remove(p.getUniqueId().toString());
		}
	}
	
	public static void setMessagesDisabled(@NotNull UUID id, boolean b) {
		if(b) {
			if(!plugin.lists.messagePlayers.contains(id.toString()))
		        plugin.lists.messagePlayers.add(id.toString());
		}else {
			if(plugin.lists.messagePlayers.contains(id.toString()))
		        plugin.lists.messagePlayers.remove(id.toString());
		}
	}
	
	public static double getCurrentPickupCooldown(UUID id) {
		if(Piggyback.piggybackPickupCooldownPlayers.containsKey(id)) {
			return ((plugin.config.pickupCooldown/20.0) -
					(((Long)System.currentTimeMillis() -
					Piggyback.piggybackPickupCooldownPlayers.get(id))
					/1000.0));
		}
		return 0;
	}
	
	public static double getCurrentPickupCooldown(@NotNull Player p) {
		if(Piggyback.piggybackPickupCooldownPlayers.containsKey(p.getUniqueId())) {
			return ((plugin.config.pickupCooldown/20.0) -
					 (((Long)System.currentTimeMillis() -
					Piggyback.piggybackPickupCooldownPlayers.get(p.getUniqueId()))
					 /1000.0));
		}
		return 0;
	}
	
	public static void throwEntity(Entity ent, Player player) {
		ThrowEntity.throwEntity(ent, player);
	}
	
}