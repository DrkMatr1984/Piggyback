package me.blubdalegend.piggyback.events;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PiggybackFarThrowEntityEvent extends Event implements Cancellable
{

	private static final HandlerList handlerList = new HandlerList();
    private Entity entity;
    private Player player;
    private boolean cancelled = false;
    
    public PiggybackFarThrowEntityEvent(Entity e, Player p){
    	entity = e;
    	this.player = p;
    }
    
    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList(){
        return handlerList;
    }
	  
    public Entity getEntity(){
    	return entity;
    }

	public Player getPlayer() {
		return player;
	}

	@Override
	public boolean isCancelled() {
		return this.cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;		
	}
}
