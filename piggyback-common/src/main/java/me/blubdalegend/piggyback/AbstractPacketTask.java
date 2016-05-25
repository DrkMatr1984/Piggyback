package me.blubdalegend.piggyback;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public interface AbstractPacketTask {
	public void SendPacketTask(Player p, JavaPlugin plugin);
}