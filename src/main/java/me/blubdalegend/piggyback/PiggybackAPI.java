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
		return plugin.lists.hasMessagesDisabled(p);
	}
	
	public static boolean hasMessagesDisabled(@NotNull UUID id) {
		return plugin.lists.hasMessagesDisabled(id);
	}
	
	public static void setMessagesDisabled(@NotNull Player p, boolean b) {
		plugin.lists.setMessagesDisabled(p, b);
	}
	
	public static void setMessagesDisabled(@NotNull UUID id, boolean b) {
		plugin.lists.setMessagesDisabled(id, b);
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
	
	public static void throwEntity(Entity ent, Player player, double throwPower) {
		ThrowEntity.throwEntity(ent, player, throwPower);
	}
	
}