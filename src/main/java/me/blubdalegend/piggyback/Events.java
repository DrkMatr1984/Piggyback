package me.blubdalegend.piggyback;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.util.Vector;

public class Events implements org.bukkit.event.Listener
{	
  public Piggyback plugin;
  
  public Events(Piggyback plugin){
	  this.plugin = plugin;
  }
  
  @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
  public void playerInteractEntity(PlayerInteractEntityEvent event)
  {
	Entity clicked = null;
    Player player = event.getPlayer();
    if(plugin.config.shiftRightClick){
        clicked = event.getRightClicked();
    if ((player.hasPermission("piggyback.use")) || (player.isOp()))
    {
      if (player.isSneaking())
      {
        if (clicked.isInsideVehicle())
        {
          clicked.leaveVehicle();
          if (plugin.config.throwMob) {
            throwEntity(clicked, player);
          }
          if (plugin.config.send) {
            player.sendMessage(plugin.config.prefix + " " + plugin.config.dropMsg);
          }
        }
        else {
          if ((clicked.getType() == EntityType.PAINTING) || (clicked.getType() == EntityType.ITEM_FRAME) || (clicked.getType() == EntityType.ARMOR_STAND) || (clicked.getType() == EntityType.ARROW))
          {
            event.setCancelled(true);
            return;
          }
          if ((clicked.hasMetadata("NPC")) && (!plugin.config.pickupNPC)) {
        	  if (plugin.config.send){
        		  player.sendMessage(plugin.config.prefix + " " + plugin.config.noPickUpNPC);
        	  }
        	  event.setCancelled(true);
              return;
          }
          if(clicked instanceof Player){
        	  if(plugin.config.disabledPlayers.contains(clicked.getUniqueId().toString())){
        		  if (plugin.config.send){
        			  player.sendMessage(plugin.config.prefix + " " + plugin.config.noPickUpPlayer);
        		  }
        		  event.setCancelled(true);
                  return;
        	  }
          }  
	      player.setPassenger(clicked);
	      if (plugin.config.send) {
	          player.sendMessage(plugin.config.prefix + plugin.config.carryMsg);
	      }          
        }
        }
    }else {
      if (plugin.config.send){
      player.sendMessage(plugin.config.prefix + " " + plugin.config.noPerms);
      }
    }
    }
  }
  
  @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
  public void playerLeftInteractEntity(EntityDamageByEntityEvent event)
  {
	    Entity clicked = null;
	    Player player = null;
		if(event.getDamager() instanceof Player)
		{
	        player = (Player)event.getDamager();
	        if(!plugin.config.shiftRightClick){
		        clicked = event.getEntity();
			    if ((player.hasPermission("piggyback.use")) || (player.isOp()))
			    {
			      if (player.isSneaking())
			      {
			        if (clicked.isInsideVehicle())
			        {
			          clicked.leaveVehicle();
			          if (plugin.config.throwMob) {
			            throwEntity(clicked, player);
			            event.setDamage(0.0D);
				        event.setCancelled(true);
			          }
			          if (plugin.config.send) {
			            player.sendMessage(plugin.config.prefix + plugin.config.dropMsg);
			          }
			        }
			        else {
			          if ((clicked.getType() == EntityType.PAINTING) || (clicked.getType() == EntityType.ITEM_FRAME) || (clicked.getType() == EntityType.ARMOR_STAND) || (clicked.getType() == EntityType.ARROW))
			          {
			        	event.setDamage(0.0D);
			            event.setCancelled(true);
			            return;
			          }
			          if ((clicked.hasMetadata("NPC")) && (!plugin.config.pickupNPC)) {
			            if(plugin.config.send){
			        	  player.sendMessage(plugin.config.prefix + " " + plugin.config.noPickUpNPC);
			            }
			            event.setDamage(0.0D);
				        event.setCancelled(true);
				        return;
			          }
			          if(clicked instanceof Player){
			          	  if(plugin.config.disabledPlayers.contains(clicked.getUniqueId().toString())){        	  
				          	  if (plugin.config.send){
				          		  player.sendMessage(plugin.config.prefix + " " + plugin.config.noPickUpPlayer);
				          	  }
				          	  event.setDamage(0.0D);
							  event.setCancelled(true);
				        	  return;
			        	  }
			          }
				      player.setPassenger(clicked);
				      event.setDamage(0.0D);
					  event.setCancelled(true);
				      if (plugin.config.send) {
				          player.sendMessage(plugin.config.prefix + " " + plugin.config.carryMsg);
				      }
				      return;
			          }
			      }
			    }
	        }else {
	        	if (plugin.config.send){
	        	player.sendMessage(plugin.config.prefix + " " + plugin.config.noPerms);
	        	}
	        }
		}
  	}
  
  private void throwEntity(Entity ent, Player player)
  {
    Vector vector = player.getLocation().getDirection();
    int pitch = (int)player.getLocation().getPitch();
    
    vector.setY(0.4D);
    if (pitch == 0) {
      vector.setZ(vector.getZ() + 1.5D);
    }
    if (pitch == 1) {
      vector.setX(vector.getX() - 1.5D);
    }
    if (pitch == 2) {
      vector.setZ(vector.getZ() - 1.5D);
    }
    if (pitch == 3) {
      vector.setX(vector.getX() + 1.5D);
    }
    ent.setVelocity(vector);
  }
}
