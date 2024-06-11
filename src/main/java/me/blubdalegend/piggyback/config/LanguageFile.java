package me.blubdalegend.piggyback.config;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.blubdalegend.piggyback.Piggyback;
import me.blubdalegend.piggyback.compatibility.ResourceUtils;

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
	public String requireItem;
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
	public String helpClickType;
	public String helpRequireItem;
	
	private final Piggyback plugin;
	
	//Credit goes to alexey-va for some of the code for saving multiple lang files
	
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
			plugin.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&e[Piggyback] &aSaving default language files..."));
	    	List<String> files;
			try {
				files = ResourceUtils.listFiles(plugin.getClass(), "/lang");
				if(files!=null) {
					for(String file : files) {
						plugin.saveResource("lang/" + file, false);
						plugin.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&e[Piggyback] " + "&aPiggyBack/lang/" + file + " saved"));
			    	}
				}				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}		
	    initLanguageFile();
	}
	
	public void initLanguageFile(){
		if(languageFile.exists() && YamlConfiguration.loadConfiguration(languageFile)!=null) {
			FileConfiguration language = YamlConfiguration.loadConfiguration(languageFile);
			// comands
			command = (language.getString("commands.main")).toLowerCase();
			reloadCommand = (language.getString("commands.reload")).toLowerCase();
			toggleCommand = (language.getString("commands.toggle")).toLowerCase();
			messagesCommand = (language.getString("commands.main")).toLowerCase();
			helpCommand = (language.getString("commands.help")).toLowerCase();
			// feedback
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
			requireItem = language.getString("message.requireItem");
			noPerms = language.getString("message.noPerms");
			notAPlayer = language.getString("message.notAPlayer");
			noPickUpPlayer = language.getString("message.noPickUpPlayer");
			noPickUpPlayerToggle = language.getString("message.noPickUpPlayerToggle");
			noRidePlayer = language.getString("message.noRidePlayer");
			noRidePlayerToggle = language.getString("message.noRidePlayerToggle");
			error = language.getString("message.error");
			hasNotPlayed = language.getString("message.hasNotPlayed");
			reload = language.getString("message.reload");
			// help
			help = language.getString("help.help");
			helpMain = language.getString("help.main");
			helpToggle = language.getString("help.toggle");
			helpToggleOther = language.getString("help.toggleOther");
			helpMessageToggle = language.getString("help.messageToggle");
			helpMessageToggleOther = language.getString("help.messageToggleOther");
			helpReload = language.getString("help.reload");
			helpClickType = language.getString("help.clickType");
			helpRequireItem = language.getString("help.requireItem");
		}else {
			plugin.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&e[Piggyback] &cCan't find " + languageFile.getName()));
			plugin.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&e[Piggyback] &cCheck PiggyBack/lang/ folder and make sure that"));
			plugin.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&e[Piggyback] &cyou have a filename in that folder that matches"));
			plugin.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&e[Piggyback] &cLanguage setting in config.yml"));
		}
	
	}
	
}

