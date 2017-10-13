package me.blubdalegend.piggyback.tasks;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.blubdalegend.piggyback.Piggyback;

public class PickupClickCooldown extends BukkitRunnable {
	
	private Player player;
	
	public PickupClickCooldown(Player p)
	{
		this.player = p;
	} 
	
	@Override
	public void run() {
		if(Piggyback.clickTimerCooldownPlayers.contains(player.getUniqueId())){
			Piggyback.clickTimerCooldownPlayers.remove(player.getUniqueId());
		}		
	}
	
}