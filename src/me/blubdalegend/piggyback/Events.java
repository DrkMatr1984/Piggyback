package me.blubdalegend.piggyback;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.util.Vector;

public class Events implements Listener {

	public Piggyback plugin;

	public Events(Piggyback instance) {
		plugin = instance;
	}

	@EventHandler
	public void playerInteractEntity(PlayerInteractEntityEvent event) {
		Player player = event.getPlayer();
		Entity clicked = event.getRightClicked();

		if (player.hasPermission("piggyback.use") || player.isOp()) {
			if (player.isSneaking()) {
				boolean throwMob = plugin.getConfig()
						.getBoolean("throwMobAway");
				boolean send = plugin.getConfig().getBoolean("message.send");
				String prefix = plugin.getConfig().getString("message.prefix");
				String carryMsg = plugin.getConfig().getString("message.carry");
				String dropMsg = plugin.getConfig().getString("message.drop");

				if (clicked.isInsideVehicle()) {

					clicked.leaveVehicle();
					if (throwMob) {
						throwEntity(clicked, player);
					}
					if (send) {
						player.sendMessage(prefix + dropMsg);
					}
				} else {
					player.setPassenger(clicked);
					if (send) {
						player.sendMessage(prefix + carryMsg);
					}
				}
			}
		} else {
			player.sendMessage(ChatColor.RED
					+ "Sorry, you have no permission to carry stuff. :(");
		}
	}

	private void throwEntity(Entity ent, Player player) {
		Vector vector = player.getLocation().getDirection();
		int pitch = (int) player.getLocation().getPitch();
		
		vector.setY(0.4);
		
		if(pitch == 0) {
			vector.setZ(vector.getZ() + 1.5);
		}
		if(pitch == 1) {
			vector.setX(vector.getX() - 1.5);
		}
		if(pitch == 2) {
			vector.setZ(vector.getZ() - 1.5);
		}
		if(pitch == 3) {
			vector.setX(vector.getX() + 1.5);
		}
		ent.setVelocity(vector);
	}

}
