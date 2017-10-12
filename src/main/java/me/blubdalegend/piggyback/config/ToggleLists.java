package me.blubdalegend.piggyback.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.blubdalegend.piggyback.Piggyback;

public class ToggleLists{
	
	private File usersFile;
	private FileConfiguration users;
	private File messageFile;
	private FileConfiguration message;
	
	public List<String> disabledPlayers = new ArrayList<String>();
	public List<String> messagePlayers = new ArrayList<String>();
	
	private Piggyback plugin;
	
	public ToggleLists(Piggyback plugin){
		this.plugin = plugin;
	}
		
	public void initLists(){
		saveDefaultUserList();
		loadUserList();
	}
	
    ////////////////////////////////////////////////////////////
	public void saveDefaultUserList() {
		//pickup toggle users
	    if (usersFile == null) {
	        usersFile = new File(plugin.getDataFolder(), "toggles.yml");
	    }
	    if (!usersFile.exists()) {           
	        plugin.saveResource("toggles.yml", false);
	    }
	    // Message toggle users
	    if (messageFile == null) {
	        messageFile = new File(plugin.getDataFolder(), "messageToggles.yml");
	    }
	    if (!messageFile.exists()) {           
	        plugin.saveResource("messageToggles.yml", false);
	    }
    }
	  
	public void loadUserList(){
		//pickup toggle users
		users = YamlConfiguration.loadConfiguration(usersFile);
		disabledPlayers = users.getStringList("DisabledPlayers");
		//message toggle users
		message = YamlConfiguration.loadConfiguration(messageFile);
		messagePlayers = users.getStringList("DisabledPlayers");
	}
	  
	public void saveUserList(){
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
			message.set("DisabledPlayers",disabledPlayers);
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

