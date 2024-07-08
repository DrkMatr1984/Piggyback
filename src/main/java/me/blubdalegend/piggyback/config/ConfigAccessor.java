package me.blubdalegend.piggyback.config;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.blubdalegend.piggyback.Piggyback;

public class ConfigAccessor
{	
	private File file = null;
	private File example = null;
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
	public String hostname;
	public int port;
	public String database;
	public boolean useSSL;
	public boolean autoReconnect;
	public String username;
	public String password;
	public boolean autoDownloadLibs;
	
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
	    if (this.example == null)
	        this.example = new File(this.dataFolder, "deluxe-menus-example.yml"); 
	    if (!this.example.exists())
	        this.plugin.saveResource("deluxe-menus-example.yml", false);
	    this.config = YamlConfiguration.loadConfiguration(file);
	    updateConfig(config.getInt("config-version"));
	    initConfig();
	}
		    
	public void initConfig()
	{	
		// main config section
		clickAction = Actions.valueOf((Objects.requireNonNull(config.getString("main.clickAction"))).toUpperCase());
		clickType = Clicks.valueOf((Objects.requireNonNull(config.getString("main.clickType"))).toUpperCase());
		requireEmptyHand = config.getBoolean("main.requireEmptyHand");
	    if(config.getString(("main.requireItem").toUpperCase())=="NONE")
	    	requireItem = null;
	    try {
	    	requireItem = Material.matchMaterial(config.getString(("main.requireItem").toUpperCase()));
	    }catch(IllegalArgumentException e) {
	    	requireItem = null;
	    }		
		onlyPlayers = config.getBoolean("main.onlyPlayers");
		onlyMobs = config.getBoolean("main.onlyMobs");
		allowNPCs = config.getBoolean("main.allowNPCs");
		
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
		disabledEntities = new ArrayList<String>();
		if(config.getStringList("blacklists.entities.entityBlacklist")!=null)
		    disabledEntities = uppercaseStringList(config.getStringList("blacklists.entities.entityBlacklist"));
		whitelistCustomEntities = config.getBoolean("blacklists.customEntities.whitelist");
		disabledCustomEntities = new ArrayList<String>();
		if(config.getStringList("blacklists.customEntities.customEntityBlacklist")!=null)
		    disabledCustomEntities = uppercaseStringList(config.getStringList("blacklists.customEntities.customEntityBlacklist"));
		whitelistWorlds = config.getBoolean("blacklists.worldBlacklist.whitelist");
		disabledWorlds = new ArrayList<String>();
		if(config.getStringList("blacklists.worldBlacklist.worlds")!=null)
		    disabledWorlds = uppercaseStringList(config.getStringList("blacklists.worldBlacklist.worlds"));
		
		// storage
		storageType = config.getString("storage.type");
		saveTimer = ((config.getLong("storage.saveTimer")) * 20);
		sqliteFilename = config.getString("storage.sqliteFilename");
		hostname = config.getString("storage.hostname");
		port = Integer.parseInt(config.getString("storage.port"));
		database = config.getString("storage.database");
		useSSL = config.getBoolean("storage.useSSL");
		autoReconnect = config.getBoolean("storage.autoReconnect");
		username = config.getString("storage.username");
		password = config.getString("storage.password");
		autoDownloadLibs = config.getBoolean("storage.autoDownloadLibs");
		
		// bStats
		bStats = config.getBoolean("metrics.bStatsMetrics");
	}
	
	private List<String> uppercaseStringList(List<String> list)
	{
		List<String> newList = new ArrayList<>();
		for(String s : list){
			newList.add(s.toUpperCase());
		}
		return newList;
	}
	
	public void updateConfig(int configVersion) {
        InputStream defaultConfigStream = plugin.getResource("config.yml");
        if (defaultConfigStream == null) {
        	Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6Piggyback&7] &cDefault config.yml not found in the jar!"));
            return;
        }
        FileConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultConfigStream));
          
        int currentVersion = configVersion;
        int newVersion = defaultConfig.getInt("config-version");

        if (newVersion > currentVersion) {
        	Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6Piggyback&7] &cOutdated &fconfig.yml&c! &aUpdating &fconfig.yml &ato the newest version..."));
            mergeConfigs(config, defaultConfig);
            try {
                config.save(file);
                Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6Piggyback&7] &aYour &fconfig.yml &ahas been updated successfully!"));
                Bukkit.getConsoleSender().sendMessage();
                Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6Piggyback&7] &7&oEven though this plugin updates its &f&oconfig.yml &7&oautomagically,"));
                Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6Piggyback&7] &7&oI would consider renaming your current updated &f&oconfig.yml &7&oand"));
                Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6Piggyback&7] &7&oletting a new one generate. Then manually copy over your old settings."));
                Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6Piggyback&7] &7&oA fresh &f&oconfig.yml &7&owill have comments thoroughly explaining each"));
                Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6Piggyback&7] &7&onew setting. Without reading these, &c&ounintended results may occur&7&o."));
            } catch (Exception e) {
            	Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6Piggyback&7] &cCould not save updated config.yml: "));
            	e.printStackTrace();
            }
        }
    }
	
	 private void mergeConfigs(FileConfiguration currentConfig, FileConfiguration defaultConfig) {
		 Set<String> keys = defaultConfig.getKeys(true);
		 for (String key : keys) {
			 if (!currentConfig.contains(key)) {
				 currentConfig.set(key, defaultConfig.get(key));
			 }
		 }
	 }
	
}