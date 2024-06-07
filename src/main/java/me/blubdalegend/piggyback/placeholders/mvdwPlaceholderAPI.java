package me.blubdalegend.piggyback.placeholders;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import be.maximvdw.placeholderapi.PlaceholderReplacer;
import me.blubdalegend.piggyback.Piggyback;
import me.blubdalegend.piggyback.PiggybackAPI;
import java.text.DecimalFormat;

import org.bukkit.plugin.Plugin;

public class mvdwPlaceholderAPI {
    public mvdwPlaceholderAPI() {
        PlaceholderReplacer isDisabled = event -> {
            if (event.getPlayer() == null) return "";
            return Boolean.toString(PiggybackAPI.isDisabled(event.getPlayer()));
        };
        PlaceholderAPI.registerPlaceholder((Plugin)Piggyback.getPlugin(), "is_disabled", (PlaceholderReplacer)isDisabled);
        
        PlaceholderReplacer hasMessagesDisabled = event -> {
            if (event.getPlayer() == null) return "";
            return Boolean.toString(PiggybackAPI.hasMessagesDisabled(event.getPlayer()));
        };
        PlaceholderAPI.registerPlaceholder((Plugin)Piggyback.getPlugin(), "is_messages_disabled", (PlaceholderReplacer)hasMessagesDisabled);
        
        PlaceholderReplacer cooldownInteger = event -> {
            if (event.getPlayer() == null) return "";
            return Integer.toString((int) Math.round(PiggybackAPI.getCurrentPickupCooldown(event.getPlayer())));
        };
        PlaceholderAPI.registerPlaceholder((Plugin)Piggyback.getPlugin(), "cooldown_integer", (PlaceholderReplacer)cooldownInteger);
        
        PlaceholderReplacer cooldownDouble = event -> {
            if (event.getPlayer() == null) return "";
            DecimalFormat df = new DecimalFormat("#.##");
            return df.format(PiggybackAPI.getCurrentPickupCooldown(event.getPlayer()));
        };
        PlaceholderAPI.registerPlaceholder((Plugin)Piggyback.getPlugin(), "cooldown_decimal", (PlaceholderReplacer)cooldownDouble);
    }
    
}