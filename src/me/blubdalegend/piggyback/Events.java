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
				if (clicked.isInsideVehicle()) {
					clicked.leaveVehicle();
					player.sendMessage(ChatColor.GREEN + "No longer carrying "
							+ clicked.getType());
				} else {
					player.setPassenger(clicked);
					player.sendMessage(ChatColor.GREEN
							+ "You are now carrying " + clicked.getType());
				}
			}
		} else {
			player.sendMessage(ChatColor.RED
					+ "Sorry, you have no permission to carry stuff. :(");
		}
	}
}
