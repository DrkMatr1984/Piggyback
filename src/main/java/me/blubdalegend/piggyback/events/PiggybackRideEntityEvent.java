package me.blubdalegend.piggyback.events;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PiggybackRideEntityEvent extends Event implements Cancellable
{

	private static final HandlerList handlerList = new HandlerList();
    private final Entity entity;
    private final Player player;
    private boolean cancelled = false;
    
    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList(){
        return handlerList;
    }
	
    public PiggybackRideEntityEvent(Entity e, Player p){
    	entity = e;
    	player = p;
    }
    
    public Entity getClicked(){
    	return entity;
    }
    
    public Player getPlayer(){
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