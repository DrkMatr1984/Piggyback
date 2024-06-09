package me.blubdalegend.piggyback.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.blubdalegend.piggyback.Piggyback;

public class ConfigAccessor
{	
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
	public Actions clickAction;
	public boolean requireEmptyHand;
	public Material requireItem;
	public boolean onlyPlayers;
	public boolean onlyMobs;
	public boolean allowNPCs;

	public long pickupCooldown;
	public boolean throwRiderAway;
	public boolean farThrowRider;
	
	public long rideCooldown;
	public boolean pickupOnlyMobs;
	public boolean pickupOnlyPlayers;
	
	public String langfile;
	public boolean send;
	public long messageCooldown;
	
	public boolean whitelistEntities;
	public List<String> disabledEntities;
	public boolean whitelistCustomEntities;
	public List<String> disabledCustomEntities;
	public boolean whitelistWorlds;
	public List<String> disabledWorlds;
	
	public String storageType;
	public long saveTimer;
	public String sqliteFilename = "pback.db";
	public String url;
	public String database;
	public boolean useSSL;
	public boolean autoReconnect;
	public String username;
	public String password;
	
	public boolean bStats;
	
	public ConfigAccessor(Piggyback plugin){
		this.plugin = plugin;
		this.dataFolder = new File(this.plugin.getDataFolder().toString());
	    if (!this.dataFolder.exists())
	        this.dataFolder.mkdir(); 
		if (this.file == null)
	        this.file = new File(this.dataFolder, "config.yml"); 
	      if (!this.file.exists())
	        this.plugin.saveResource("config.yml", false);
	    initConfig();
	}
		    
	public void initConfig()
	{
		this.config = YamlConfiguration.loadConfiguration(file);
		// main config section
		clickAction = Actions.valueOf((Objects.requireNonNull(config.getString("main.clickAction"))).toUpperCase()); //DONE
		clickType = Clicks.valueOf((Objects.requireNonNull(config.getString("main.clickType"))).toUpperCase());	// DONE
		requireEmptyHand = config.getBoolean("main.requireEmptyHand"); //DONE
		requireItem = Material.matchMaterial(config.getString(("main.requireItem").toUpperCase())); //is null if cannot match
		onlyPlayers = config.getBoolean("main.onlyPlayers"); // DONE
		onlyMobs = config.getBoolean("main.onlyMobs"); // DONE
		allowNPCs = config.getBoolean("main.allowNPCs"); // DONE
		
		// pickup configuration
		pickupCooldown = ((config.getLong("pickup.cooldown")) * 20);
		throwRiderAway = config.getBoolean("pickup.throwRiderAway");
		farThrowRider = config.getBoolean("pickup.farThrowRider");
		
		// ride
		rideCooldown = ((config.getLong("ride.cooldown")) * 20);
		pickupOnlyMobs = config.getBoolean("ride.pickupOnlyMobs");
		pickupOnlyPlayers = config.getBoolean("ride.pickupOnlyPlayers");
		
		// messages
		langfile = ((config.getString("messages.language")) + ".yml");
		send = config.getBoolean("messages.send");
		messageCooldown = ((config.getLong("messages.cooldown")) * 20);
		
		// blacklists
		whitelistEntities = config.getBoolean("blacklists.entities.whitelist");
		disabledEntities = uppercaseStringList(config.getStringList("blacklists.entities.entityBlacklist"));
		whitelistCustomEntities = config.getBoolean("blacklists.customEntities.whitelist");
		disabledCustomEntities = uppercaseStringList(config.getStringList("blacklists.customEntities.customEntityBlacklist"));
		whitelistWorlds = config.getBoolean("blacklists.worldBlacklist.whitelist");
		disabledWorlds = uppercaseStringList(config.getStringList("blacklists.worldBlacklist.worlds"));
		
		// storage
		storageType = config.getString("storage.type");
		saveTimer = ((config.getLong("storage.saveTimer")) * 20);
		sqliteFilename = config.getString("storage.sqliteFilename");
		url = config.getString("storage.url");
		database = config.getString("storage.database");
		useSSL = config.getBoolean("storage.useSSL");
		autoReconnect = config.getBoolean("storage.autoReconnect");
		username = config.getString("storage.username");
		password = config.getString("storage.password");
		
		bStats = config.getBoolean("general.bStatsMetrics");
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