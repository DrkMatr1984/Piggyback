package me.blubdalegend.piggyback.nms.v1_10_r1;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.blubdalegend.piggyback.AbstractPacketTask;
import net.minecraft.server.v1_10_R1.PacketPlayOutMount;

public class SendPacketTask implements AbstractPacketTask{
	
	private JavaPlugin plugin;
	
	public void ASendPacketTask(JavaPlugin plugin){
		this.plugin = plugin;
		new BukkitRunnable() {
			@Override
			public void run() {
				PacketPlayOutMount packet;
				for(Player player : Bukkit.getServer().getOnlinePlayers()){
					packet = new PacketPlayOutMount(((CraftPlayer) player).getHandle());
			        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
				}
				
			}
        }.runTaskLater(this.plugin, 5L);
	}
	
}