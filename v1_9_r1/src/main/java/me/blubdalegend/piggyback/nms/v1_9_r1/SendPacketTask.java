package me.blubdalegend.piggyback.nms.v1_9_r1;

import org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.blubdalegend.piggyback.AbstractPacketTask;
import net.minecraft.server.v1_9_R1.PacketPlayOutMount;

public class SendPacketTask implements AbstractPacketTask{
	
	private JavaPlugin plugin;
	
	public void SendPacketTask(Player p, JavaPlugin plugin){
		final Player player = p;
		this.plugin = plugin;
		new BukkitRunnable() {
			@Override
			public void run() {
				PacketPlayOutMount packet = new PacketPlayOutMount(((CraftPlayer) player).getHandle());
		        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
			}
        }.runTaskLater(this.plugin, 2L);
	}
	
}