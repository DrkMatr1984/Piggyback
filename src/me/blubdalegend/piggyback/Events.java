package me.blubdalegend.piggyback;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

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
				boolean send = plugin.getConfig().getBoolean("message.send");
				String prefix = plugin.getConfig().getString("message.prefix");
				String carryMsg = plugin.getConfig().getString("message.carry");
				String dropMsg = plugin.getConfig().getString("message.drop");

				if (clicked.isInsideVehicle()) {
					clicked.leaveVehicle();
					if (send == true) {
						player.sendMessage(prefix + dropMsg);
					}
				} else {
					player.setPassenger(clicked);
					if (send == true) {
						player.sendMessage(prefix + carryMsg);
					}
				}
			}
		} else {
			player.sendMessage(ChatColor.RED
					+ "Sorry, you have no permission to carry stuff. :(");
		}
	}
}
