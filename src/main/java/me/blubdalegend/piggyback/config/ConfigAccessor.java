package me.blubdalegend.piggyback.config;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;

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
	public boolean send;
	public long cooldown;
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
		if(Piggyback.version!="pre1_9"){
			disabledEntities.add(EntityType.ARMOR_STAND.toString());
			disabledEntities.add(EntityType.BOAT.toString());
		}else{
			disabledEntities.add(EntityType.BOAT.toString());
		}		
		disabledWorlds = new ArrayList<String>();
		disabledWorlds.add("");
		f = plugin.getConfig();
		f.options().header("PIGGYBACK CONFIGURATION FILE");
		f.addDefault("clickType", "RIGHT");
		f.addDefault("throwRiderAway", Boolean.valueOf(true));
		f.addDefault("pickUpNPCs", Boolean.valueOf(false));
		f.addDefault("requireEmptyHand", Boolean.valueOf(true));
		f.addDefault("cancelPickup.IfAnotherPluginCancelsEvent", Boolean.valueOf(false));
		f.addDefault("messages.Send", Boolean.valueOf(true));
		f.addDefault("messages.Cooldown", Long.valueOf(30L));
		f.addDefault("blacklists.entityBlacklist", disabledEntities);
		f.addDefault("blacklists.worldBlacklist", disabledWorlds);
	    
		f.options().copyDefaults(true);
		plugin.saveConfig();
	    
		clickType = Clicks.valueOf((f.getString("clickType")).toUpperCase());
		throwRider = f.getBoolean("throwRiderAway");
		pickupNPC = f.getBoolean("pickUpNPCs");
		requireEmptyHand = f.getBoolean("requireEmptyHand");
		cancelPickupIfAnotherPlugin = f.getBoolean("cancelPickup.IfAnotherPluginCancelsEvent");		
		send = f.getBoolean("messages.Send");
		cooldown = ((f.getLong("messages.Cooldown")) * 20);
		disabledEntities = f.getStringList("blacklists.entityBlacklist");
		disabledWorlds = f.getStringList("blacklists.worldBlacklist");
	}
	
}