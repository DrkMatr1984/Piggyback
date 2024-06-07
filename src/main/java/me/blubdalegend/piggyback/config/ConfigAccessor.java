package me.blubdalegend.piggyback.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.blubdalegend.piggyback.Piggyback;

public class ConfigAccessor
{	
	private FileConfiguration f;
	private File file = null;
	private File dataFolder;
	private FileConfiguration config;
	
	private Piggyback plugin;
	
	public enum Clicks{
		  RIGHT, LEFT, EITHER
	}
	
	public enum Actions{
		  RIDE, PICKUP
	}
	
	public Clicks clickType;
	public Actions actionType;
	public boolean bStats;
	public boolean onlyRidePlayers;
	public boolean throwRiderPickup; // Throw Rider instead of just dropping them
	public boolean rideNPC;
	public boolean pickupNPC;
	public long pickupCooldown;
	public boolean send;
	public long messageCooldown;
	public boolean requireEmptyHand;
	public List<String> disabledEntities;
	public List<String> disabledCustomEntities;
	public List<String> disabledWorlds;
	public String storageType;
	public String sqliteFilename = "pback.db";
	
	public ConfigAccessor(Piggyback plugin){
		this.plugin = plugin;
		this.dataFolder = new File(this.plugin.getDataFolder().toString());
	    if (!this.dataFolder.exists())
	        this.dataFolder.mkdir(); 
		if (this.file == null)
	        this.file = new File(this.dataFolder, "config.yml"); 
	      if (!this.file.exists())
	        this.plugin.saveResource("config.yml", false);
	    this.config = YamlConfiguration.loadConfiguration(file);
	    initConfig();
	}
		    
	private void initConfig()
	{
		bStats = f.getBoolean("general.bStatsMetrics");
		clickType = Clicks.valueOf((Objects.requireNonNull(f.getString("general.clickType"))).toUpperCase());
		requireEmptyHand = f.getBoolean("general.requireEmptyHand");
		pickupCooldown = ((f.getLong("general.pickUp.Cooldown")) * 20);
		actionType = Actions.valueOf((Objects.requireNonNull(f.getString("general.clickAction"))).toUpperCase());
		//pickup
		throwRiderPickup = f.getBoolean("pickup.throwRiderAway");
		pickupNPC = f.getBoolean("pickup.pickUpNPCs");
		//ride
		onlyRidePlayers = f.getBoolean("ride.onlyRidePlayers");
		rideNPC = f.getBoolean("ride.rideNPC");
		//messages
		send = f.getBoolean("messages.Send");
		messageCooldown = ((f.getLong("messages.Cooldown")) * 20);
		disabledEntities = uppercaseStringList(f.getStringList("blacklists.entityBlacklist"));
		disabledWorlds = uppercaseStringList(f.getStringList("blacklists.worldBlacklist"));
		disabledCustomEntities = uppercaseStringList(f.getStringList("blacklists.customEntityBlacklist"));		
	}
	
	private List<String> uppercaseStringList(List<String> list)
	{
		List<String> newList = new ArrayList<>();
		for(String s : list){
			newList.add(s.toUpperCase());
		}
		return newList;
	}
	
}