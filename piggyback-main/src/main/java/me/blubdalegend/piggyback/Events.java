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
import org.bukkit.ChatColor;
import org.bukkit.Material;

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
		  if(Piggyback.version!="pre1_9"){
			  if(event.getHand().equals(EquipmentSlot.HAND)){
				  doRightClick(player, clicked);
			  }
		  }else{
			  doRightClick(player, clicked);
		  }
		  
	  }
  }
  
  @SuppressWarnings("deprecation")
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
					  if(clicked.isInsideVehicle()){
						  if (player.getPassenger()!=null && player.getPassenger().equals(clicked))
						  {
							  clicked.leaveVehicle();
							  sendMountPacket();
							  if (plugin.config.throwMob) {
								  throwEntity(clicked, player);
								  sendMountPacket();
							  }
							  if (plugin.config.send) {
								  if(!((plugin.config.prefix + " " + plugin.config.dropMsg).equals(" "))){
									  player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.config.prefix + " " + plugin.config.dropMsg));
								  }
							  }
							  event.setDamage(0.0D);
							  event.setCancelled(true);
						  }
						  return;
					  }else {
						  if ((clicked.getType() == EntityType.PAINTING) || (clicked.getType() == EntityType.ITEM_FRAME) || (clicked.getType() == EntityType.ARROW) || (plugin.config.disabledEntities.contains(clicked.getType().toString())))
						  {
							  event.setDamage(0.0D);
							  event.setCancelled(true);
							  return;
						  }
						  if(plugin.config.disabledWorlds.contains(clicked.getWorld().toString()))
			    		  {
							  event.setDamage(0.0D);
							  event.setCancelled(true);
			    			  return;
			    		  }
						  if ((clicked.hasMetadata("NPC")) && (!plugin.config.pickupNPC)) {
							  if(plugin.config.send){
								  if(!((plugin.config.prefix + " " + plugin.config.noPickUpNPC).equals(" "))){
			    					  player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.config.prefix + " " + plugin.config.noPickUpNPC));
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
			    						  player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.config.prefix + " " + plugin.config.noPickUpPlayer));
			    					  }
								  }
								  event.setDamage(0.0D);
								  event.setCancelled(true);
								  return;
							  }
						  }	
						  player.setPassenger(clicked);
						  sendMountPacket();
						  if(player.getPassenger()!=null){
							  if(!((plugin.config.prefix + " " + plugin.config.carryMsg).equals(" "))){
		    					  player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.config.prefix + " " + plugin.config.carryMsg));
		    				  }
						  }
						  event.setDamage(0.0D);
						  event.setCancelled(true);
					  }
				  }
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
  
  @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=false)
  public void onPlayerToggleSneakEvent(PlayerToggleSneakEvent event) {
	  Player player = event.getPlayer();
	  if(player.getVehicle()!=null){
		  Events.sendMountPacket();
	  }
  }
  	
  static void sendMountPacket() {
	  Method method;
	try {
		method = Piggyback.clazz.getDeclaredMethod("ASendPacketTask", JavaPlugin.class);
		Object obj = Piggyback.clazz.newInstance();
		method.invoke(obj, ((JavaPlugin)Events.plugin));
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
  
  @SuppressWarnings("deprecation")
  private void doRightClick(Player player, Entity clicked){
	  if ((player.hasPermission("piggyback.use")) || (player.isOp()))
	  {
		  if (player.isSneaking())
		  {
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
			  if(player.getPassenger()!=null){
				  if (player.getPassenger().equals(clicked))
				  {
					  clicked.leaveVehicle();
					  sendMountPacket();
					  if (plugin.config.throwMob) 
					  {
						  throwEntity(clicked, player);
						  sendMountPacket();
					  }
					  if (plugin.config.send)
					  {
						  if(!((plugin.config.prefix + " " + plugin.config.dropMsg).equals(" "))){
							  player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.config.prefix + " " + plugin.config.dropMsg));
						  }		  
					  }
					  return;
				  }
	    	  }else {
	    		  if ((clicked.getType() == EntityType.PAINTING) || (clicked.getType() == EntityType.ITEM_FRAME) || (clicked.getType() == EntityType.ARROW) || (plugin.config.disabledEntities.contains(clicked.getType().toString())))
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
	    		  if(plugin.config.disabledWorlds.contains(clicked.getWorld().toString()))
	    		  {
	    			  return;
	    		  }
	    		  if(clicked instanceof Player)
	    		  {
	    			  if(plugin.config.disabledPlayers.contains(clicked.getUniqueId().toString())){        	  
	    				  if (plugin.config.send)
	    				  {
	    					  if(!((plugin.config.prefix + " " + plugin.config.noPickUpPlayer).equals(" "))){
	    						  player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.config.prefix + " " + plugin.config.noPickUpPlayer));
	    					  }
	    				  }
	    				  return;
	    			  }
	    		  }
	    		  player.setPassenger(clicked);
	    		  sendMountPacket();
	    		  if (plugin.config.send) 
	    		  {
	    			  if(player.getPassenger()!=null){
	    				  if(!((plugin.config.prefix + " " + plugin.config.carryMsg).equals(" "))){
	    					  player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.config.prefix + " " + plugin.config.carryMsg));
	    				  }
	    			  }
	    		  }
	    	  }
		  }
	  }else {
		  if (plugin.config.send)
		  {
			  if(!((plugin.config.prefix + " " + plugin.config.noPerms).equals(" "))){
				  player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.config.prefix + " " + plugin.config.noPerms));
			  }			  
		  }
	  }
  }
  
}
