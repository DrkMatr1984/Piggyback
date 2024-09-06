package me.blubdalegend.piggyback.storage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.blubdalegend.piggyback.Piggyback;

public class YMLStorage
{
	private File usersFile;
	private final File dataFolder;
	private FileConfiguration users;
	private File messageFile;
	private FileConfiguration message;
	
	private Piggyback plugin;
	
	public YMLStorage(Piggyback plugin) {
		this.plugin = plugin;
		dataFolder = new File(plugin.getDataFolder().getAbsolutePath() + System.getProperty("file.separator") + "data");
		initLists();
	}
	
	public void initLists() {
	    //pickup toggle users
	   	if(!(dataFolder.exists())){
	   		dataFolder.mkdir();
	   	}
	   	if (usersFile == null) {
	   		usersFile = new File(dataFolder, "toggles.yml");
	   	}
	   	if (!usersFile.exists()) {           
	   		plugin.saveResource("data/toggles.yml", false);
	   		Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.lang.prefix + " &eSaving default toggles.yml data file..."));
	   	}
	   	// Message toggle users
	   	if (messageFile == null) {
	   		messageFile = new File(dataFolder, "messageToggles.yml");
	   	}
	   	if (!messageFile.exists()) {           
	   		plugin.saveResource("data/messageToggles.yml", false);
	   		Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.lang.prefix + " &eSaving default messageToggles.yml data file..."));
	   	}
	}
	public List<String> loadDisabledPlayers(){
		//pickup toggle users
		List<String> disabledPlayers = new ArrayList<>();
		users = YamlConfiguration.loadConfiguration(usersFile);
		if(users.getStringList("DisabledPlayers")!=null && !users.getStringList("DisabledPlayers").isEmpty())
			disabledPlayers = users.getStringList("DisabledPlayers");
		return disabledPlayers;
	}
	
	public List<String> loadMessagePlayers(){
		//message toggle users
		List<String> messagePlayers = new ArrayList<>();
		message = YamlConfiguration.loadConfiguration(messageFile);
		if(message.getStringList("DisabledPlayers") != null && !message.getStringList("DisabledPlayers").isEmpty())
			messagePlayers = message.getStringList("DisabledPlayers");
		return messagePlayers;
	}
		  
	public void saveData(List<String> disabledPlayers, List<String> messagePlayers){
		//pickup toggle users
		if(disabledPlayers!=null)
		{
			users.set("DisabledPlayers",disabledPlayers);
		}
		if(usersFile.exists())
			usersFile.delete();
		try {
			users.save(usersFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			usersFile.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//message toggle users
		if(messagePlayers!=null)
		{
			message.set("DisabledPlayers",messagePlayers);
		}
		if(messageFile.exists())
			messageFile.delete();
		try {
			message.save(messageFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			messageFile.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
}