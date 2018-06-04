package me.blubdalegend.piggyback.config;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import me.blubdalegend.piggyback.Piggyback;

public class ConfigAccessor
{	
	public FileConfiguration f;
	
	public Piggyback plugin;
	
	public static enum Clicks{
		  RIGHT, LEFT, EITHER
	}
	
	public static enum Actions{
		  RIDE, PICKUP
	}
	
	public Clicks clickType;
	public Actions actionType;
	public boolean onlyRidePlayers;
	public boolean throwRiderRide; // Throw Rider instead of just dropping them
	public boolean throwRiderPickup; // Throw Rider instead of just dropping them
	public boolean rideNPC;
	public boolean pickupNPC;
	public long pickupCooldown;
	public boolean send;
	public long messageCooldown;
	public boolean requireEmptyHand;
	public List<String> disabledEntities;
	public List<String> disabledWorlds;
	
	public ConfigAccessor(Piggyback plugin){
		this.plugin = plugin;
	}
		    
	public void initConfig()
	{
		disabledEntities = new ArrayList<String>();
		disabledEntities.add("");
	
		disabledWorlds = new ArrayList<String>();
		disabledWorlds.add("");
		
		f = plugin.getConfig();
		f.options().header("PIGGYBACK CONFIGURATION FILE");
		f.addDefault("general.clickType", "RIGHT");
		f.addDefault("general.requireEmptyHand", Boolean.valueOf(true));
		f.addDefault("general.pickUp.Cooldown", Long.valueOf(10L));
		f.addDefault("general.clickAction", "PICKUP");	
		//Pickup
		f.addDefault("pickup.throwRiderAway", Boolean.valueOf(true));
		f.addDefault("pickup.pickUpNPCs", Boolean.valueOf(false));		
		//Ride
		f.addDefault("ride.onlyRidePlayers", Boolean.valueOf(true));
		f.addDefault("ride.throwRiderAway", Boolean.valueOf(false));
		f.addDefault("ride.rideNPC", Boolean.valueOf(false));
		//messages
		f.addDefault("messages.Send", Boolean.valueOf(true));
		f.addDefault("messages.Cooldown", Long.valueOf(30L));
		f.addDefault("blacklists.entityBlacklist", disabledEntities);
		f.addDefault("blacklists.worldBlacklist", disabledWorlds);
	    
		f.options().copyDefaults(true);
		plugin.saveConfig();
	    
		clickType = Clicks.valueOf((f.getString("general.clickType")).toUpperCase());
		requireEmptyHand = f.getBoolean("general.requireEmptyHand");
		pickupCooldown = ((f.getLong("general.pickUp.Cooldown")) * 20);
		actionType = Actions.valueOf((f.getString("general.clickAction")).toUpperCase());
		//pickup
		throwRiderPickup = f.getBoolean("pickup.throwRiderAway");
		pickupNPC = f.getBoolean("pickup.pickUpNPCs");
		//ride
		onlyRidePlayers = f.getBoolean("ride.onlyRidePlayers");
		throwRiderRide = f.getBoolean("ride.throwRiderAway");
		rideNPC = f.getBoolean("ride.rideNPC");
		//messages
		send = f.getBoolean("messages.Send");
		messageCooldown = ((f.getLong("messages.Cooldown")) * 20);
		disabledEntities = uppercaseStringList(f.getStringList("blacklists.entityBlacklist"));
		disabledWorlds = uppercaseStringList(f.getStringList("blacklists.worldBlacklist"));
	}
	
	public List<String> uppercaseStringList(List<String> list)
	{
		List<String> newList = new ArrayList<String>();
		for(String s : list){
			newList.add(s.toUpperCase());
		}
		return newList;
	}
	
}