package me.blubdalegend.piggyback.tasks;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.blubdalegend.piggyback.Piggyback;

public class EmptyHandMessageCooldown extends BukkitRunnable {
	
	private Player player;
	
	public EmptyHandMessageCooldown(Player p)
	{
		this.player = p;
	} 
	
	@Override
	public void run() {
		if(Piggyback.emptyHandCooldownPlayers.contains(player.getUniqueId())){
			Piggyback.emptyHandCooldownPlayers.remove(player.getUniqueId());
		}		
	}
	
}