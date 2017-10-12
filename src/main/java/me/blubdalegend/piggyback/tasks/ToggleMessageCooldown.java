package me.blubdalegend.piggyback.tasks;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.blubdalegend.piggyback.Piggyback;

public class ToggleMessageCooldown extends BukkitRunnable {
	
	private Player player;
	
	public ToggleMessageCooldown(Player p)
	{
		this.player = p;
	} 
	
	@Override
	public void run() {
		if(Piggyback.toggleCooldownPlayers.contains(player.getUniqueId())){
			Piggyback.toggleCooldownPlayers.remove(player.getUniqueId());
		}		
	}
	
}