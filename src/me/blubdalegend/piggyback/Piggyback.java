package me.blubdalegend.piggyback;

import java.io.IOException;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Piggyback extends JavaPlugin {

	private Logger log = getLogger();
	private PluginManager pm = getServer().getPluginManager();

	@Override
	public void onEnable() {
		initConfig();
		initMetrics(this);

		pm.registerEvents(new Events(this), this);
		log.info("Piggyback enabled!");
	}

	private void initConfig() {
		FileConfiguration conf = getConfig();

		conf.options().header("PIGGYBACK CONFIGURATION FILE");

		conf.addDefault("metrics", true);
		
		conf.addDefault("message.send", true);
		conf.addDefault("message.prefix", "[" + ChatColor.GOLD + "Piggyback"
				+ ChatColor.WHITE + "] ");
		conf.addDefault("message.carry", "Picked up the mob.");
		conf.addDefault("message.drop", "Dropped the mob.");

		conf.options().copyDefaults(true);
		saveConfig();
	}

	private void initMetrics(Plugin pl) {
		if (getConfig().getBoolean("metrics") == true) {
			try {
				Metrics metrics = new Metrics(pl);
				metrics.start();
				log.info("Metrics enabled!");
			} catch (IOException e) {
				log.warning("Failed to submit stats to MCStats.org.");
				log.warning("If this keeps happening please contact BlubdaLegend on DBO.");
				e.printStackTrace();
			}
		}
	}

}
