package me.blubdalegend.piggyback;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class Events implements org.bukkit.event.Listener
{	
  public static Piggyback plugin;
  
  public Events(Piggyback plugin){
	  Events.plugin = plugin;
  }
  
  @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=false)
  public void playerInteractEntity(PlayerInteractEntityEvent event)
  {
	  if(plugin.config.respectClaimPerms){
	      if(event.isCancelled()){
	    	  return;
	      }
	  }
	  Entity clicked = null;
	  Player player = event.getPlayer();
	  if(plugin.config.shiftRightClick){
		  clicked = event.getRightClicked();
		  if(event.getHand().equals(EquipmentSlot.HAND)){
			  if ((player.hasPermission("piggyback.use")) || (player.isOp()))
			  {
				  if (player.isSneaking())
				  {
					  if(player.getPassenger()!=null){
						  if (player.getPassenger().equals(clicked))
						  {
							  clicked.leaveVehicle();
							  sendMountPacket(player);
							  if (plugin.config.throwMob) 
							  {
								  throwEntity(clicked, player);
							  }
							  if (plugin.config.send)
							  {
								  if(!((plugin.config.prefix + " " + plugin.config.dropMsg).equals(" "))){
									  player.sendMessage(plugin.config.prefix + " " + plugin.config.dropMsg);
								  }		  
							  }
							  return;
						  }
			    	  }else {
			    		  if ((clicked.getType() == EntityType.PAINTING) || (clicked.getType() == EntityType.ITEM_FRAME) || (clicked.getType() == EntityType.ARMOR_STAND) || (clicked.getType() == EntityType.ARROW))
			    		  {
			    			  return;
			    		  }
			    		  if ((clicked.hasMetadata("NPC")) && (!plugin.config.pickupNPC)) {
			    			  if(plugin.config.send){
			    				  if(!((plugin.config.prefix + " " + plugin.config.noPickUpNPC).equals(" "))){
			    					  player.sendMessage(plugin.config.prefix + " " + plugin.config.noPickUpNPC);
			    				  }
			    			  }
			    			  return;
			    		  }
			    		  if(clicked instanceof Player)
			    		  {
			    			  if(plugin.config.disabledPlayers.contains(clicked.getUniqueId().toString())){        	  
			    				  if (plugin.config.send)
			    				  {
			    					  if(!((plugin.config.prefix + " " + plugin.config.noPickUpPlayer).equals(" "))){
			    						  player.sendMessage(plugin.config.prefix + " " + plugin.config.noPickUpPlayer);
			    					  }
			    				  }
			    				  return;
			    			  }
			    		  }
			    		  player.setPassenger(clicked);
			    		  sendMountPacket(player);
			    		  if (plugin.config.send) 
			    		  {
			    			  if(player.getPassenger()!=null){
			    				  if(!((plugin.config.prefix + " " + plugin.config.carryMsg).equals(" "))){
			    					  player.sendMessage(plugin.config.prefix + " " + plugin.config.carryMsg);
			    				  }
			    			  }
			    		  }
			    	  }
				  }
			  }else {
				  if (plugin.config.send)
				  {
					  if(!((plugin.config.prefix + " " + plugin.config.noPerms).equals(" "))){
						  player.sendMessage(plugin.config.prefix + " " + plugin.config.noPerms);
					  }			  
				  }
			  }
		  }
	  }
  }
  
  @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=false)
  public void playerLeftInteractEntity(EntityDamageByEntityEvent event)
  {
	  if(plugin.config.respectClaimPerms){
		  if(event.isCancelled()){
			  return;
		  }
	  }
	  
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
					  if(clicked.isInsideVehicle()){
						  if (player.getPassenger()!=null && player.getPassenger().equals(clicked))
						  {
							  clicked.leaveVehicle();
							  sendMountPacket(player);
							  if (plugin.config.throwMob) {
								  throwEntity(clicked, player);
							  }
							  if (plugin.config.send) {
								  if(!((plugin.config.prefix + " " + plugin.config.dropMsg).equals(" "))){
									  player.sendMessage(plugin.config.prefix + " " + plugin.config.dropMsg);
								  }
							  }
							  event.setDamage(0.0D);
							  event.setCancelled(true);
						  }
						  return;
					  }else {
						  if ((clicked.getType() == EntityType.PAINTING) || (clicked.getType() == EntityType.ITEM_FRAME) || (clicked.getType() == EntityType.ARMOR_STAND) || (clicked.getType() == EntityType.ARROW))
						  {
							  event.setDamage(0.0D);
							  event.setCancelled(true);
							  return;
						  }
						  if ((clicked.hasMetadata("NPC")) && (!plugin.config.pickupNPC)) {
							  if(plugin.config.send){
								  if(!((plugin.config.prefix + " " + plugin.config.noPickUpNPC).equals(" "))){
			    					  player.sendMessage(plugin.config.prefix + " " + plugin.config.noPickUpNPC);
			    				  }
							  }
							  event.setDamage(0.0D);
							  event.setCancelled(true);
							  return;
						  }
						  if(clicked instanceof Player){
							  if(plugin.config.disabledPlayers.contains(clicked.getUniqueId().toString())){        	  
								  if (plugin.config.send){
									  if(!((plugin.config.prefix + " " + plugin.config.noPickUpPlayer).equals(" "))){
			    						  player.sendMessage(plugin.config.prefix + " " + plugin.config.noPickUpPlayer);
			    					  }
								  }
								  event.setDamage(0.0D);
								  event.setCancelled(true);
								  return;
							  }
						  }	
						  player.setPassenger(clicked);
						  sendMountPacket(player);
						  if(player.getPassenger()!=null){
							  if(!((plugin.config.prefix + " " + plugin.config.carryMsg).equals(" "))){
		    					  player.sendMessage(plugin.config.prefix + " " + plugin.config.carryMsg);
		    				  }
						  }
						  event.setDamage(0.0D);
						  event.setCancelled(true);
					  }
				  }
			  }else {
				  if (plugin.config.send){
					  if(!((plugin.config.prefix + " " + plugin.config.noPerms).equals(" "))){
						  player.sendMessage(plugin.config.prefix + " " + plugin.config.noPerms);
					  }
				  }
			  }
	      }
	  }
  }
  
  @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=false)
  public void onPlayerToggleSneakEvent(PlayerToggleSneakEvent event) {
	  Player player = event.getPlayer();
	  if(player.getVehicle()!=null){
		  Events.sendMountPacket(player);
	  }	  
  }
  	
  static void sendMountPacket(Player player) {
	  Method method;
	try {
		method = Piggyback.clazz.getDeclaredMethod("SendPacketTask", Player.class, JavaPlugin.class);
		Object obj = Piggyback.clazz.newInstance();
		method.invoke(obj, player, ((JavaPlugin)Events.plugin));
	} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
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
