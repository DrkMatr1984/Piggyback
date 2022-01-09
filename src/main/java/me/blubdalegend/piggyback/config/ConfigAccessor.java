package me.blubdalegend.piggyback.config;

import java.util.ArrayList;
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
	public boolean onlyRidePlayers;
	public boolean throwRiderRide; // Throw Rider instead of just dropping them
	public boolean throwRiderPickup; // Throw Rider instead of just dropping them
	public boolean rideNPC;
	public boolean pickupNPC;
	public long pickupCooldown;
	public boolean send;
	public long messageCooldown;
	public boolean requireEmptyHand;
	public List<String> disabledEntities = new ArrayList<>();
	public List<String> disabledWorlds;
	
	public ConfigAccessor(Piggyback plugin){
		this.plugin = plugin;
	}
		    
	public void initConfig()
	{
		disabledEntities = new ArrayList<>();
		disabledEntities.add("");
	
		disabledWorlds = new ArrayList<>();
		disabledWorlds.add("");
		
		f = plugin.getConfig();
		f.options().header("PIGGYBACK CONFIGURATION FILE");
		
		f.setComments("general.clickType", disabledEntities);
		f.addDefault("general.clickType", "RIGHT");
		f.addDefault("general.requireEmptyHand", Boolean.TRUE);
		f.addDefault("general.pickUp.Cooldown", 10L);
		f.addDefault("general.clickAction", "PICKUP");	
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
		f.addDefault("blacklists.worldBlacklist", disabledWorlds);
	    
		f.options().copyDefaults(true);
		plugin.saveConfig();
	    
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
	}
	
	public List<String> uppercaseStringList(List<String> list)
	{
		List<String> newList = new ArrayList<>();
		for(String s : list){
			newList.add(s.toUpperCase());
		}
		return newList;
	}
	
}