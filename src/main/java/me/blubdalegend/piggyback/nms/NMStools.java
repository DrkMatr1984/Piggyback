package me.blubdalegend.piggyback.nms;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class NMStools
{  
	public static String getNmsVersion()
	{
		return Bukkit.getServer().getClass().getPackage().getName().replace("org.bukkit.craftbukkit.", "");
	}
	
	static Class<?> getNmsClass(String nmsClassName) throws ClassNotFoundException {
		return Class.forName("net.minecraft.server." + Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + "." + nmsClassName);
	}
  
	static Class<?> getCraftBukkitClass(String nmsClassName) throws ClassNotFoundException {
		return Class.forName("org.bukkit.craftbukkit." + Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + "." + nmsClassName);
	}
	
	public static void sendMountPacket() {
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
			//Only needed for some Verions. Do nothing.
		} 
	}
}