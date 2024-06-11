package me.blubdalegend.piggyback.tasks;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.blubdalegend.piggyback.Piggyback;

public class PiggybackPickupCooldown extends BukkitRunnable {
	
	private Player player;
	
	public PiggybackPickupCooldown(Player p)
	{
		this.player = p;
	} 
	
	@Override
	public void run() {
		if(Piggyback.piggybackPickupCooldownPlayers.containsKey(player.getUniqueId())){
			Piggyback.piggybackPickupCooldownPlayers.remove(player.getUniqueId());
		}		
	}
	
}