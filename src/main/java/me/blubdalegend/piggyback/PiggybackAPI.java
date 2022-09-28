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
	
	public static void setDisabled(Player p) {
		plugin.lists.setDisabled(p);
	}
	
	public static void setDisabled(UUID id) {
		plugin.lists.setDisabled(id);
	}
	
	public static boolean hasMessagesDisabled(@NotNull Player p) {
		return plugin.lists.messagePlayers.contains(p.getUniqueId().toString());
	}
	
	public static boolean hasMessagesDisabled(@NotNull UUID id) {
		return plugin.lists.messagePlayers.contains(id.toString());
	}
	
	public static void setMessagesDisabled(@NotNull Player p) {
		plugin.lists.messagePlayers.add(p.getUniqueId().toString());
	}
	
	public static void setMessagesDisabled(@NotNull UUID id) {
		plugin.lists.messagePlayers.add(id.toString());
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