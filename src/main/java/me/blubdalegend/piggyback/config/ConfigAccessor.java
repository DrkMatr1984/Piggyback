package me.blubdalegend.piggyback.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.bukkit.configuration.file.FileConfiguration;
import me.blubdalegend.piggyback.Piggyback;

public class ConfigAccessor
{	
	public FileConfiguration f;
	
	public Piggyback plugin;
	
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
	
	public ConfigAccessor(Piggyback plugin){
		this.plugin = plugin;
	}
		    
	@SuppressWarnings("deprecation")
	public void initConfig()
	{
		disabledEntities = new ArrayList<>();
		disabledEntities.add("");
		disabledCustomEntities = new ArrayList<>();
		disabledCustomEntities.add("");
		disabledWorlds = new ArrayList<>();
		disabledWorlds.add("");
		
		f = plugin.getConfig();
		f.options().header("PIGGYBACK CONFIGURATION FILE");	
		f.addDefault("general.bStatsMetrics", Boolean.TRUE);
		f.setComments("general.bStatsMetrics", Arrays.asList("Enable bStats metrics"));
		f.addDefault("general.clickType", "RIGHT");
		f.setComments("general.clickType", Arrays.asList("Possible options are RIGHT, LEFT, or EITHER"));
		f.addDefault("general.requireEmptyHand", Boolean.TRUE);
		f.addDefault("general.pickUp.Cooldown", 10L);
		f.addDefault("general.clickAction", "PICKUP");
		f.setComments("general.clickAction", Arrays.asList("Possible options are PICKUP or RIDE"));
		//Pickup
		f.addDefault("pickup.throwRiderAway", Boolean.TRUE);
		f.addDefault("pickup.pickUpNPCs", Boolean.FALSE);
		//Ride
		f.addDefault("ride.onlyRidePlayers", Boolean.TRUE);
		f.addDefault("ride.rideNPC", Boolean.FALSE);
		//messages
		f.addDefault("messages.Send", Boolean.TRUE);
		f.addDefault("messages.Cooldown", 30L);
		f.addDefault("blacklists.entityBlacklist", disabledEntities);
		
		f.addDefault("blacklists.customEntityBlacklist", disabledCustomEntities);
		
		f.addDefault("blacklists.worldBlacklist", disabledWorlds);
	    
		f.options().copyDefaults(true);
		plugin.saveConfig();
		
	    loadConfig();		
	}
	
	public void loadConfig() {
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