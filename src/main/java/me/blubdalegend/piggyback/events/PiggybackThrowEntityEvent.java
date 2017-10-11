package me.blubdalegend.piggyback.events;

import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PiggybackThrowEntityEvent extends Event
{

	private static final HandlerList handlerList = new HandlerList();
    private Entity entity;
    
    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList(){
        return handlerList;
    }
	
    public PiggybackThrowEntityEvent(Entity e){
    	entity = e;
    }
    
    public Entity getEntity(){
    	return entity;
    }
}
