package me.blubdalegend.piggyback;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
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
import org.bukkit.util.Vector;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class Events implements org.bukkit.event.Listener
{	
  public static Piggyback plugin;
  
  public Events(Piggyback plugin){
	  Events.plugin = plugin;
  }
  
  
  @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=false)
  public void checkRightClick(PlayerInteractEntityEvent event)
  {
	  if(plugin.config.cancelPickupIfAnotherPlugin){
	      if(event.isCancelled()){
	    	  return;
	      }
	  }
	  Entity clicked = event.getRightClicked();
	  Player player = event.getPlayer();
	  if (player.isSneaking())
	  {
		  if(plugin.config.shiftRightClick){
			  if ((player.hasPermission("piggyback.use")) || (player.isOp()))
			  {
				  if(Piggyback.version!="pre1_9"){
					  if(event.getHand().equals(EquipmentSlot.HAND)){
						  doRightClick(player, clicked);
					  }
				  }else{
					  doRightClick(player, clicked);
				  }
			  }else{
				  if (plugin.config.send)
				  {
					  if(!((plugin.config.prefix + " " + plugin.config.noPerms).equals(" "))){
						  player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.config.prefix + " " + plugin.config.noPerms));
					  }			  
				  }
			  }
		  }
	  }
	  return;
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
			  Events.sendMountPacket();
		  } 
	  }
	  
  }
  	
  static void sendMountPacket() {
	Class<?> eentity;
	Class<?> mountPacket;
	try {
		eentity = getNmsClass("Entity");
		mountPacket = getNmsClass("PacketPlayOutMount");
		Constructor<?> mPacketConstructor = mountPacket.getConstructor(eentity);
		for(Player player : Bukkit.getServer().getOnlinePlayers()){
			Method getHandle = player.getClass().getMethod("getHandle");
			Object nmsPlayer = getHandle.invoke(player);					
			Field conField = nmsPlayer.getClass().getField("playerConnection");
		    Object con = conField.get(nmsPlayer);
		    Object packet = mPacketConstructor.newInstance(nmsPlayer);
		    Method sendPacket = getNmsClass("PlayerConnection").getMethod("sendPacket", getNmsClass("Packet"));
		    sendPacket.invoke(con, packet);
		}
	} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchFieldException | InstantiationException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		Bukkit.getServer().getLogger().info("Can't Assemble Mount Packet");
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
  private void doLeftClick(Player player, Entity clicked, EntityDamageByEntityEvent event){
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
	 if(!(player.isEmpty())){
		  if (player.getPassenger()!=null && player.getPassenger().equals(clicked))
		  {   
			  if(player.isInsideVehicle()&&player.getVehicle().equals(clicked)){
	           	  return;
	          }
			  try{
				  clicked.leaveVehicle();
				  if(Piggyback.version!="pre1_9"){
					  sendMountPacket();
				  }
				  if (plugin.config.throwRider) {
					  throwEntity(clicked, player);
					  if(Piggyback.version!="pre1_9"){
						  sendMountPacket();
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
		  return;
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
		  if(player.isInsideVehicle()&&player.getVehicle().equals(clicked)){
           	  return;
          }
		  try{	
			  player.setPassenger(clicked);
			  if(Piggyback.version!="pre1_9"){
				  sendMountPacket();
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
  
  @SuppressWarnings("deprecation")
  private void doRightClick(Player player, Entity clicked){
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
	if(!(player.isEmpty())){
		if (player.getPassenger().equals(clicked))
		{
			if(player.isInsideVehicle()&&player.getVehicle().equals(clicked)){
				return;
	        }
            try{
            	clicked.leaveVehicle();
            	if(Piggyback.version!="pre1_9"){
					  sendMountPacket();
				  }
            	if (plugin.config.throwRider) 
				{
					 throwEntity(clicked, player);
					 if(Piggyback.version!="pre1_9"){
						  sendMountPacket();
					  }
				}
				if (plugin.config.send)
				{
					if(!((plugin.config.prefix + " " + plugin.config.dropMsg).equals(" "))){
						 player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.config.prefix + " " + plugin.config.dropMsg));
					}		  
				}
			    return;
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
	    try{
	    	player.setPassenger(clicked);
	    	if(Piggyback.version!="pre1_9"){
				  sendMountPacket();
			  }
	    	if (plugin.config.send) 
	    	{
	    		if(player.getPassenger()!=null){
	    			if(!((plugin.config.prefix + " " + plugin.config.carryMsg).equals(" "))){
	    				player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.config.prefix + " " + plugin.config.carryMsg));
	    			}
	    		}
	    	}
	    }catch(IllegalStateException e){
	    	return;
	    }
	 }	
  }
  
  static Class<?> getNmsClass(String nmsClassName) throws ClassNotFoundException {
	    return Class.forName("net.minecraft.server." + Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + "." + nmsClassName);
  }
  
  static Class<?> getCraftBukkitClass(String nmsClassName) throws ClassNotFoundException {
	    return Class.forName("org.bukkit.craftbukkit." + Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + "." + nmsClassName);
  }
  
}
