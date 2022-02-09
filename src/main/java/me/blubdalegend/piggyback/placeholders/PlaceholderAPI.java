package me.blubdalegend.piggyback.placeholders;

import java.text.DecimalFormat;

import org.bukkit.entity.Player;

import me.blubdalegend.piggyback.Piggyback;
import me.blubdalegend.piggyback.PiggybackAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.jetbrains.annotations.NotNull;

public class PlaceholderAPI extends PlaceholderExpansion {
	
	private final Piggyback plugin;
	
	public PlaceholderAPI(Piggyback piggy) {
		plugin = piggy;
	}
	
    @Override
    public boolean persist(){
        return true;
    }  

   @Override
   public boolean canRegister(){
       return true;
   }

   @Override
   public String getAuthor(){
       return plugin.getDescription().getAuthors().toString();
   }

	@Override
	public String getIdentifier(){
		return "Piggyback";
	}

	@Override
	public String getVersion(){
		return plugin.getDescription().getVersion();
	}

	@Override
	public String onPlaceholderRequest(@NotNull Player player, String identifier){
		if(identifier.equals("is_disabled")){
            return Boolean.toString(PiggybackAPI.isDisabled(player));
		}
		if(identifier.equals("is_messages_disabled")){
            return Boolean.toString(PiggybackAPI.hasMessagesDisabled(player));
		}
		if(identifier.equals("cooldown_integer")){
            return Integer.toString((int) Math.round(PiggybackAPI.getCurrentPickupCooldown(player)));
		}
		if(identifier.equals("cooldown_decimal")){
			DecimalFormat df = new DecimalFormat("#.##");
            return df.format(PiggybackAPI.getCurrentPickupCooldown(player));
		}
		return null;
	}
	
}