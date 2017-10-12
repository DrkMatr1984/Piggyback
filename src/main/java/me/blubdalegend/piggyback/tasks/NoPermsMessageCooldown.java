package me.blubdalegend.piggyback.tasks;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.blubdalegend.piggyback.Piggyback;

public class NoPermsMessageCooldown extends BukkitRunnable {
	
	private Player player;
	
	public NoPermsMessageCooldown(Player p)
	{
		this.player = p;
	} 
	
	@Override
	public void run() {
		if(Piggyback.noPermsCooldownPlayers.contains(player.getUniqueId())){
			Piggyback.noPermsCooldownPlayers.remove(player.getUniqueId());
		}		
	}
	
}