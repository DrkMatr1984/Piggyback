package me.blubdalegend.piggyback.events;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class PlayerInteractEntityBaseClickEvent extends Event implements Cancellable{

	private static final HandlerList handlerList = new HandlerList();
	private boolean cancelled = false;
	private Plugin plugin;
	private Player player;
	private Entity clickedEntity;
	
	public PlayerInteractEntityBaseClickEvent(Plugin plugin, Player player, Entity clickedEntity) {
		this.plugin = plugin;
		this.player = player;
		this.clickedEntity = clickedEntity;
	}
	
	@Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList(){
        return handlerList;
    }
    
    @Override
	public boolean isCancelled() {
		return this.cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;		
	}

	public Plugin getPlugin() {
		return this.plugin;
	}
	
	public Player getPlayer(){
		return this.player;
	}
	
	public Entity getClickedEntity(){
		return this.clickedEntity;
	}
	
	public ItemStack getItemInMainHand(){
		ItemStack stack = this.player.getInventory().getItemInMainHand();
		if(!hasItemInMainHand()){
			return new ItemStack(Material.AIR, 1);
		}
		return stack;
	}
	
	public ItemStack getItemInOffHand(){
		ItemStack stack = this.player.getInventory().getItemInOffHand();
		if(!hasItemInOffHand()){
			return new ItemStack(Material.AIR, 1);
		}
		return stack;
	}
	
	public boolean hasItemInMainHand(){
		ItemStack stack = this.player.getInventory().getItemInMainHand();
		if(stack == null || stack.getType() == Material.AIR){
			return false;
		}
		return true;
	}
	
	public boolean hasItemInOffHand(){
		ItemStack stack = this.player.getInventory().getItemInOffHand();
		if(stack == null || stack.getType() == Material.AIR){
			return false;
		}
		return true;
	}
	
	public Location getClickedEntityLocation(){
		return this.clickedEntity.getLocation();
	}
	
	public World getClickedEntityWorld(){
		return this.clickedEntity.getWorld();
	}
}

