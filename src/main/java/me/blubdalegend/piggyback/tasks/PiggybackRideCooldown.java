package me.blubdalegend.piggyback.tasks;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.blubdalegend.piggyback.Piggyback;

public class PiggybackRideCooldown extends BukkitRunnable {
	
	private Player player;
	
	public PiggybackRideCooldown(Player p)
	{
		this.player = p;
	} 
	
	@Override
	public void run() {
		if(Piggyback.piggybackRideCooldownPlayers.containsKey(player.getUniqueId())){
			Piggyback.piggybackRideCooldownPlayers.remove(player.getUniqueId());
		}		
	}
	
}