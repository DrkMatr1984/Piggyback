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
	public boolean respectClaimPerms;
	public boolean requireEmptyHand;
	public List<String> disabledEntities;
	public List<String> disabledWorlds;
	
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
		  
		  prefix = language.getString("message.prefix");      
	      carryMsg = language.getString("message.carry");
		  dropMsg = language.getString("message.drop");
		  toggleOn = language.getString("message.toggleOn");
		  toggleOff = language.getString("message.toggleOff");
		  noPickUpNPC = language.getString("message.noPickUpNPC");
		  noPerms = language.getString("message.noPerms");
		  notAPlayer = language.getString("message.notAPlayer");
		  noPickUpPlayer = language.getString("message.noPickUpPlayer");
		  error = language.getString("message.error");
	  }
	  
	  public void initConfig()
	  {
		disabledEntities = new ArrayList<String>();
		disabledEntities.add("");
		disabledWorlds = new ArrayList<String>();
		disabledWorlds.add("");
		f = plugin.getConfig();
	    f.options().header("PIGGYBACK CONFIGURATION FILE");
	    f.addDefault("shiftRightClick", Boolean.valueOf(true));
	    f.addDefault("respectClaimPerms", Boolean.valueOf(true));
	    f.addDefault("throwMobAway", Boolean.valueOf(true));
	    f.addDefault("PickUpNPCs", Boolean.valueOf(false));
	    f.addDefault("requireEmptyHand", Boolean.valueOf(true));
	    f.addDefault("message.send", Boolean.valueOf(true));
	    f.addDefault("entityBlacklist", disabledEntities);
	    f.addDefault("worldBlacklist", disabledWorlds);
	    
	    f.options().copyDefaults(true);
	    plugin.saveConfig();
	    
	    shiftRightClick = f.getBoolean("shiftRightClick");
	    respectClaimPerms = f.getBoolean("respectClaimPerms");
		throwMob = f.getBoolean("throwMobAway");
		pickupNPC = f.getBoolean("PickUpNPCs");
		requireEmptyHand = f.getBoolean("requireEmptyHand");
		send = f.getBoolean("message.send");
		disabledEntities = f.getStringList("entityBlacklist");
		disabledWorlds = f.getStringList("worldBlacklist");
	    
	    saveDefaultUserList();
	    loadUserList();
	    saveDefaultLanguageFile();
	    loadLanguageFile();
	  }
}