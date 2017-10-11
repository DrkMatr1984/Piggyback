package me.blubdalegend.piggyback.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import me.blubdalegend.piggyback.Piggyback;
import me.blubdalegend.piggyback.actions.ThrowEntity;
import me.blubdalegend.piggyback.events.PiggybackDropEntityEvent;
import me.blubdalegend.piggyback.events.PiggybackPickupEntityEvent;
import me.blubdalegend.piggyback.events.PiggybackThrowEntityEvent;
import me.blubdalegend.piggyback.nms.NMStools;

import org.bukkit.ChatColor;
import org.bukkit.Material;

public class CustomEventsListener implements org.bukkit.event.Listener
{	
  private Piggyback plugin;
  
  public CustomEventsListener(Piggyback plugin){
	  this.plugin = plugin;
  }
  
  @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=false)
  public void checkLeftClick(EntityDamageByEntityEvent event)
  {
	  if(plugin.config.cancelPickupIfAnotherPlugin){
		  if(event.isCancelled()){
			  return;
		  }
	  }
	  Player player = null;
	  if(event.getDamager() instanceof Player)
	  {
		  player = (Player)event.getDamager();
		  if(player.isSneaking())
		  {
			  if(!plugin.config.shiftRightClick){
				  if ((player.hasPermission("piggyback.use")) || (player.isOp()))
				  {
					  Entity clicked = null;
					  clicked = event.getEntity();
					  doLeftClick(player, clicked, event);
				  }else {
					  if (plugin.config.send){
						  if(!((plugin.config.prefix + " " + plugin.config.noPerms).equals(" "))){
							  player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.config.prefix + " " + plugin.config.noPerms));
						  }
					  }
				  }
			  }
		  }
	  }
	  return;
   }
   
  
  @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=false)
  public void onPlayerToggleSneakEvent(PlayerToggleSneakEvent event) {
	  Player player = event.getPlayer();
	  if(Piggyback.version!="pre1_9"){
		  if(player.getVehicle()!=null){
			  NMStools.sendMountPacket();
		  } 
	  }
	  
  }
  
  @SuppressWarnings("deprecation")
  private void doLeftClick(Player player, Entity clicked, EntityDamageByEntityEvent event){
	 if(!(player.isEmpty())){
		  if (player.getPassenger()!=null && player.getPassenger().equals(clicked))
		  {   
			  if(player.isInsideVehicle()&&player.getVehicle().equals(clicked)){
				  event.setDamage(0.0D);
				  event.setCancelled(true);
	           	  return;
	          }
			  try{
				  clicked.leaveVehicle();
				  if(Piggyback.version!="pre1_9"){
					  NMStools.sendMountPacket();
				  }
				  if (plugin.config.throwRider) {
					  ThrowEntity.throwEntity(clicked, player);
					  if(Piggyback.version!="pre1_9"){
						  NMStools.sendMountPacket();
					  }
				  }
				  if (plugin.config.send) {
					  if(!((plugin.config.prefix + " " + plugin.config.dropMsg).equals(" "))){
						  player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.config.prefix + " " + plugin.config.dropMsg));
					  }
				  }
				  event.setDamage(0.0D);
				  event.setCancelled(true);
			  }catch(IllegalStateException e){
				  return;
			  }
		  }
	  }else {
		  if((player.isInsideVehicle()&&player.getVehicle().equals(clicked))||(clicked.isInsideVehicle()&&clicked.getVehicle().equals(player))){
				return;
	      }
		  if ((clicked.getType() == EntityType.PAINTING) || (clicked.getType() == EntityType.ITEM_FRAME) || (clicked.getType() == EntityType.ARROW) || (plugin.config.disabledEntities.contains(clicked.getType().toString())))
		  {
			  return;
		  }
		  if(plugin.config.disabledWorlds.contains(clicked.getWorld().toString()))
	   	  {
	   		  return;
	   	  }
		  if ((clicked.hasMetadata("NPC")) && (!plugin.config.pickupNPC)) {
			  if(plugin.config.send){
				  if(!((plugin.config.prefix + " " + plugin.config.noPickUpNPC).equals(" "))){
	   				  player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.config.prefix + " " + plugin.config.noPickUpNPC));
	   			  }
			  }
			  return;
		  }
		  if(clicked instanceof Player){
			  if(plugin.config.disabledPlayers.contains(clicked.getUniqueId().toString())){        	  
				  if (plugin.config.send){
					  if(!((plugin.config.prefix + " " + plugin.config.noPickUpPlayer).equals(" "))){
	   					  player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.config.prefix + " " + plugin.config.noPickUpPlayer));
	   				  }
				  }
				  event.setDamage(0.0D);
				  event.setCancelled(true);
				  return;
			  }
		  }
		  if(plugin.config.requireEmptyHand){
				 if(Piggyback.version!="pre1_9"){
					 if(player.getInventory().getItemInMainHand().getType()!=Material.AIR){
						 return;
					 }
			     }else{
				     if(player.getItemInHand().getType()!=Material.AIR){
				    	 return;
				     }
			     }
		  }
		  try{	
			  player.setPassenger(clicked);
			  if(Piggyback.version!="pre1_9"){
				  NMStools.sendMountPacket();
			  }
			  if(player.getPassenger()!=null){
				  if(!((plugin.config.prefix + " " + plugin.config.carryMsg).equals(" "))){
	   				  player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.config.prefix + " " + plugin.config.carryMsg));
	   			  }
			  }
			  event.setDamage(0.0D);
			  event.setCancelled(true);
		  }catch(IllegalStateException e){
		      return;
	      }
	  }
  }
  
  	@EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
	public void onEntityThrow(PiggybackThrowEntityEvent event)
	{		
		event.getEntity().leaveVehicle();		            		
		ThrowEntity.throwEntity(event.getEntity(), event.getPlayer());
		if(Piggyback.version!="pre1_9"){
			try{
				NMStools.sendMountPacket();
			}catch(IllegalStateException e){
		    	return;
		    } 		
		}
		if (plugin.config.send)
		{
			if(!((plugin.config.prefix + " " + plugin.config.dropMsg).equals(" "))){
				 event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.config.prefix + " " + plugin.config.dropMsg));
			}		  
		}
	}
	
	@EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
	public void onEntityDrop(PiggybackDropEntityEvent event)
	{		
		event.getEntity().leaveVehicle();		            		
		if(Piggyback.version!="pre1_9"){
			try{
				NMStools.sendMountPacket();
			}catch(IllegalStateException e){
		    	return;
		    } 		
		}
		if (plugin.config.send)
		{
			if(!((plugin.config.prefix + " " + plugin.config.dropMsg).equals(" "))){
				event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.config.prefix + " " + plugin.config.dropMsg));
			}		  
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
	public void onEntityPickup(PiggybackPickupEntityEvent event)
	{		
	    event.getPlayer().setPassenger(event.getEntity());
	    if(Piggyback.version!="pre1_9"){
			try{
				NMStools.sendMountPacket();
			}catch(IllegalStateException e){
		    	return;
		    } 		
		}
	    if (plugin.config.send) 
	    {
	    	if(event.getPlayer().getPassenger()!=null){
	    		if(!((plugin.config.prefix + " " + plugin.config.carryMsg).equals(" "))){
	    			event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.config.prefix + " " + plugin.config.carryMsg));
	    		}
	    	}
	    }		
	}
  
}
