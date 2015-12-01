package me.blubdalegend.piggyback;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigAccessor
{
	private File usersFile;
	private FileConfiguration users;
	private File languageFile;
	private FileConfiguration language;
	public List<String> disabledPlayers = new ArrayList<String>();
	public FileConfiguration f;
	
	public Piggyback plugin;
	
	public boolean shiftRightClick;
	public boolean throwMob;
	public boolean pickupNPC;
	public boolean send;
	
	public String prefix;      
	public String carryMsg;
	public String dropMsg;
	public String noPickUpNPC;
	public String noPerms;
	public String notAPlayer;
	public String noPickUpPlayer;
	public String toggleOn;
	public String toggleOff;
	public String error;
	
	public ConfigAccessor(Piggyback plugin){
		this.plugin = plugin;
	}
	
	public void saveDefaultUserList() {
	      if (usersFile == null) {
	          usersFile = new File(plugin.getDataFolder(), "toggles.yml");
	      }
	      if (!usersFile.exists()) {           
	    	  plugin.saveResource("toggles.yml", false);
	      }   
	  }
	  
	  public void loadUserList(){
		  users = YamlConfiguration.loadConfiguration(usersFile);
		  disabledPlayers = users.getStringList("DisabledPlayers");
	  }
	  
	  public void saveUserList(){
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
	  }
	  
	  public void saveDefaultLanguageFile() {
	      if (languageFile == null) {
	          languageFile = new File(plugin.getDataFolder(), "language.yml");
	      }
	      if (!languageFile.exists()) {           
	    	  plugin.saveResource("language.yml", false);
	      }   
	  }
	  
	  public void loadLanguageFile(){
		  language = YamlConfiguration.loadConfiguration(languageFile);
		  
		  prefix = formatColor(language.getString("message.prefix"));      
	      carryMsg = formatColor(language.getString("message.carry"));
		  dropMsg = formatColor(language.getString("message.drop"));
		  toggleOn = formatColor(language.getString("message.toggleOn"));
		  toggleOff = formatColor(language.getString("message.toggleOff"));
		  noPickUpNPC = formatColor(language.getString("message.noPickUpNPC"));
		  noPerms = formatColor(language.getString("message.noPerms"));
		  notAPlayer = formatColor(language.getString("message.notAPlayer"));
		  noPickUpPlayer = formatColor(language.getString("message.noPickUpPlayer"));
		  error = formatColor(language.getString("message.error"));
	  }
	  
	  public void initConfig()
	  {
		f = plugin.getConfig();
	    f.options().header("PIGGYBACK CONFIGURATION FILE");
	    f.addDefault("shiftRightClick", Boolean.valueOf(true));
	    f.addDefault("throwMobAway", Boolean.valueOf(true));
	    f.addDefault("PickUpNPCs", Boolean.valueOf(false));
	    f.addDefault("message.send", Boolean.valueOf(true)); 
	    f.options().copyDefaults(true);
	    plugin.saveConfig();
	    
	    shiftRightClick = f.getBoolean("shiftRightClick");
		throwMob = f.getBoolean("throwMobAway");
		pickupNPC = f.getBoolean("PickUpNPCs");
		send = f.getBoolean("message.send");
	    
	    saveDefaultUserList();
	    loadUserList();
	    saveDefaultLanguageFile();
	    loadLanguageFile();
	  }
	  
	  private String formatColor(String msg){
		  String temp = msg;
		  temp = temp.replaceAll("&", "§");
		  return temp;
	  }
}