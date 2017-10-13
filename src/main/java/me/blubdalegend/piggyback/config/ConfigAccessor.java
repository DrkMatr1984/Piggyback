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
		  RIGHT, LEFT
	}
	
	public Clicks clickType; // true for use shift-right-click, false to use shift-left-click
	public boolean throwRider; // Throw Rider instead of just dropping them
	public boolean pickupNPC;
	public long pickupCooldown;
	public boolean send;
	public long messageCooldown;
	public boolean cancelPickupIfAnotherPlugin;
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
		f.addDefault("general.throwRiderAway", Boolean.valueOf(true));
		f.addDefault("general.pickUpNPCs", Boolean.valueOf(false));
		f.addDefault("general.requireEmptyHand", Boolean.valueOf(true));
		f.addDefault("general.pickUp.Cooldown", Long.valueOf(10L));
		f.addDefault("cancelPickup.IfAnotherPluginCancelsEvent", Boolean.valueOf(false));
		f.addDefault("messages.Send", Boolean.valueOf(true));
		f.addDefault("messages.Cooldown", Long.valueOf(30L));
		f.addDefault("blacklists.entityBlacklist", disabledEntities);
		f.addDefault("blacklists.worldBlacklist", disabledWorlds);
	    
		f.options().copyDefaults(true);
		plugin.saveConfig();
	    
		clickType = Clicks.valueOf((f.getString("general.clickType")).toUpperCase());
		throwRider = f.getBoolean("general.throwRiderAway");
		pickupNPC = f.getBoolean("general.pickUpNPCs");
		pickupCooldown = ((f.getLong("general.pickUp.Cooldown")) * 20);
		requireEmptyHand = f.getBoolean("general.requireEmptyHand");
		cancelPickupIfAnotherPlugin = f.getBoolean("cancelPickup.IfAnotherPluginCancelsEvent");		
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