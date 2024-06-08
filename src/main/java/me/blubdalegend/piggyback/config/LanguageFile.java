package me.blubdalegend.piggyback.config;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.blubdalegend.piggyback.Piggyback;

public class LanguageFile
{
	private final File languageFolder;
	private File languageFile;

	public String command;
	public String reloadCommand;
	public String toggleCommand; 
	public String messagesCommand;
	public String helpCommand;
	
	public String title;
	public String prefix;      
	public String carryMsg;
	public String dropMsg;
	public String throwMsg;
	public String rideMsg;
	public String pickupCooldown;
	public String rideCooldown;
	public String noPickUpNPC;
	public String noRideNPC;
	public String emptyHand;
	public String noPerms;
	public String notAPlayer;
	public String noPickUpPlayer;
	public String noPickUpPlayerToggle;
	public String noRidePlayer;
	public String noRidePlayerToggle;
	public String toggleOn;
	public String toggleOff;
	public String toggleOnOther;
	public String toggleOffOther;
	public String messageOn;
	public String messageOff;
	public String messageOnOther;
	public String messageOffOther;
	public String error;
	public String hasNotPlayed;
	public String reload;
	public String helpReload;
	
	public String help;
	public String helpMain;
	public String helpToggle;
	public String helpToggleOther;
	public String helpMessageToggle;
	public String helpMessageToggleOther;
	
	private final Piggyback plugin;
	
	public LanguageFile(Piggyback plugin){
		this.plugin = plugin;
		languageFolder = new File(this.plugin.getDataFolder() +"/lang");
		if(!(languageFolder.exists())){
			languageFolder.mkdir();
	   	}
		if (languageFile == null) {
			languageFile = new File(languageFolder, plugin.config.langfile);
	    }
	    if (!languageFile.exists()) {           
	    	plugin.saveResource(("lang/"+plugin.config.langfile), false);
	    }
	    initLanguageFile();
	}
	
	public void initLanguageFile(){
		FileConfiguration language = YamlConfiguration.loadConfiguration(languageFile);
		// Commands - make them all "to lowercase"
		command = (language.getString("commands.main")).toLowerCase();
		reloadCommand = (language.getString("commands.reload")).toLowerCase();
		toggleCommand = (language.getString("commands.toggle")).toLowerCase();
		messagesCommand = (language.getString("commands.main")).toLowerCase();
		helpCommand = (language.getString("commands.help")).toLowerCase();
		
		title = language.getString("message.prefix");
		prefix = "&f[&r" + title + "&f]&r";      
	    carryMsg = language.getString("message.carry");
		dropMsg = language.getString("message.drop");
		throwMsg = language.getString("message.throw");
		rideMsg = language.getString("message.ride");
		pickupCooldown = language.getString("message.pickupCD");
		rideCooldown = language.getString("message.rideCD");
		toggleOn = language.getString("message.toggleOn");
		toggleOff = language.getString("message.toggleOff");
		toggleOnOther = language.getString("message.toggleOnOther");
		toggleOffOther = language.getString("message.toggleOffOther");
		messageOn = language.getString("message.messageOn");
		messageOff = language.getString("message.messageOff");
		messageOnOther = language.getString("message.messageOnOther");
		messageOffOther = language.getString("message.messageOffOther");
		noPickUpNPC = language.getString("message.noPickUpNPC");
		noRideNPC = language.getString("message.noRideNPC");
		emptyHand = language.getString("message.emptyHand");
		noPerms = language.getString("message.noPerms");
		notAPlayer = language.getString("message.notAPlayer");
		noPickUpPlayer = language.getString("message.noPickUpPlayer");
		noPickUpPlayerToggle = language.getString("message.noPickUpPlayerToggle");
		noRidePlayer = language.getString("message.noRidePlayer");
		noRidePlayerToggle = language.getString("message.noRidePlayerToggle");
		error = language.getString("message.error");
		hasNotPlayed = language.getString("message.hasNotPlayed");
		reload = language.getString("message.reload");
		help = language.getString("help.help");
		helpMain = language.getString("help.main");
		helpToggle = language.getString("help.toggle");
		helpToggleOther = language.getString("help.toggleOther");
		helpMessageToggle = language.getString("help.messageToggle");
		helpMessageToggleOther = language.getString("help.messageToggleOther");
		helpReload = language.getString("help.reload");
	}
	
}

