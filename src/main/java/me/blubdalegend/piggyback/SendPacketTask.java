package me.blubdalegend.piggyback;

import org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import net.minecraft.server.v1_9_R1.PacketPlayOutMount;

public class SendPacketTask extends BukkitRunnable{
	
	private Player player;
	
	public SendPacketTask(Player player){
		this.player = player;
	}

	@Override
	public void run() {
		PacketPlayOutMount packet = new PacketPlayOutMount(((CraftPlayer) player).getHandle());
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
	}
	
}