package me.blubdalegend.piggyback.config;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.blubdalegend.piggyback.Piggyback;

public class LanguageFile
{
	private File languageFile;
	private FileConfiguration language;
	
	public String title;
	public String prefix;      
	public String carryMsg;
	public String dropMsg;
	public String throwMsg;
	public String pickupCooldown;
	public String noPickUpNPC;
	public String emptyHand;
	public String noPerms;
	public String notAPlayer;
	public String noPickUpPlayer;
	public String toggleOn;
	public String toggleOff;
	public String messageOn;
	public String messageOff;
	public String error;
	
	public String help;
	public String helpMain;
	public String helpToggle;
	public String helpMessageToggle;
	
	private Piggyback plugin;
	
	public LanguageFile(Piggyback plugin){
		this.plugin = plugin;
	}
	
	public void initLanguageFile(){
		saveDefaultLanguageFile();
		loadLanguageFile();
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
		title = language.getString("message.prefix");
		prefix = "&f[&r" + title + "&f]&r";      
	    carryMsg = language.getString("message.carry");
		dropMsg = language.getString("message.drop");
		throwMsg = language.getString("message.throw");
		pickupCooldown = language.getString("message.pickupCD");
		toggleOn = language.getString("message.toggleOn");
		toggleOff = language.getString("message.toggleOff");
		messageOn = language.getString("message.messageOn");
		messageOff = language.getString("message.messageOff");
		noPickUpNPC = language.getString("message.noPickUpNPC");
		emptyHand = language.getString("message.emptyHand");
		noPerms = language.getString("message.noPerms");
		notAPlayer = language.getString("message.notAPlayer");
		noPickUpPlayer = language.getString("message.noPickUpPlayer");
		error = language.getString("message.error");
		
		help = language.getString("help.help");
		helpMain = language.getString("help.main");
		helpToggle = language.getString("help.toggle");
		helpMessageToggle = language.getString("help.messageToggle");
	}
}

