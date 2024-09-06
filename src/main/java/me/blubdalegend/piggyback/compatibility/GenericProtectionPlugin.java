package me.blubdalegend.piggyback.compatibility;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class GenericProtectionPlugin {

    public static boolean canPlaceBlock(Player player) {
        // Simulate the BlockPlaceEvent
        BlockPlaceEvent event = new BlockPlaceEvent(player.getLocation().getBlock(), player.getLocation().getBlock().getState(), new Location(player.getWorld(), player.getLocation().getBlockX(),player.getLocation().getBlockY()+1, player.getLocation().getBlockZ()).getBlock(), new ItemStack(Material.STONE), player, true, null);       
        // Call the event
        player.getServer().getPluginManager().callEvent(event);
        if(event.isCancelled())
        	return false;
        else {
        	event.setCancelled(true);
        	return true;
        }
    }
    
}