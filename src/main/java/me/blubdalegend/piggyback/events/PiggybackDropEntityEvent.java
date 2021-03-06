package me.blubdalegend.piggyback.events;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PiggybackDropEntityEvent extends Event implements Cancellable
{

	private static final HandlerList handlerList = new HandlerList();
    private Entity entity = null;
    private Player player = null;
    private boolean cancelled = false;
    
    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList(){
        return handlerList;
    }
	
    public PiggybackDropEntityEvent(Entity e, Player p){
    	entity = e;
    	player = p;
    }
    
    public Entity getEntity(){
    	return this.entity;
    }
    
    public Player getPlayer(){
    	return this.player;
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
