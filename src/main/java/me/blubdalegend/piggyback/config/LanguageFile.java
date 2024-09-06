package me.blubdalegend.piggyback.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.blubdalegend.piggyback.Piggyback;
import me.blubdalegend.piggyback.utils.ResourceUtils;

public class LanguageFile
{
	private final File languageFolder;
	private File languageFile;
	private FileConfiguration language;

	public String command;
	public String reloadCommand;
	public String toggleCommand; 
	public String messagesCommand;
	public String helpCommand;
	public String townCommand;
	public String nationCommand;
	public String plotCommand;
	
	public String title;
	public String prefix;      
	public String carryMsg;
	public String dropMsg;
	public String throwMsg;
	public String farThrowMsg;
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
	public String wrongCommand;
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
		languageFolder = new File(this.plugin.getDataFolder().getAbsolutePath() + System.getProperty("file.separator") + "lang");
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
			    	}
				}				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if(languageFile.exists() && YamlConfiguration.loadConfiguration(languageFile)!=null) {
			language = YamlConfiguration.loadConfiguration(languageFile);
			updateLang(language.getInt("lang-version"));
		    initLanguageFile();
		}else {
			plugin.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&e[Piggyback] &cCan't find " + languageFile.getName()));
			plugin.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&e[Piggyback] &cCheck PiggyBack/lang/ folder and make sure that"));
			plugin.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&e[Piggyback] &cyou have a filename in that folder that matches"));
			plugin.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&e[Piggyback] &cLanguage setting in config.yml"));
		}		
	}
	
	public void initLanguageFile(){
		// comands
	    language = YamlConfiguration.loadConfiguration(languageFile);
		command = (language.getString("commands.main")).toLowerCase();
		reloadCommand = (language.getString("commands.reload")).toLowerCase();
		toggleCommand = (language.getString("commands.toggle")).toLowerCase();
		messagesCommand = (language.getString("commands.messages")).toLowerCase();
		helpCommand = (language.getString("commands.help")).toLowerCase();
		townCommand = (language.getString("commands.town")).toLowerCase();
		nationCommand = (language.getString("commands.nation")).toLowerCase();
		plotCommand = (language.getString("commands.plot")).toLowerCase();
		// feedback
		title = language.getString("message.prefix");
		prefix = "&f[&r" + title + "&f]&r";      
	    carryMsg = language.getString("message.carry");
		dropMsg = language.getString("message.drop");
		throwMsg = language.getString("message.throw");
		farThrowMsg = language.getString("message.farThrow");
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
		wrongCommand = language.getString("message.wrongCommand");
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
	}
	
	private void updateLang(int langVersion) {
		FileConfiguration language = YamlConfiguration.loadConfiguration(languageFile);
        InputStream defaultConfigStream = plugin.getResource("lang/" + plugin.config.langfile);
        if (defaultConfigStream == null) {
        	Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6Piggyback&7] &flang/" + plugin.config.langfile + " &cnot found in the jar!"));
            return;
        }
        FileConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultConfigStream));
          
        int currentVersion = langVersion;
        int newVersion = defaultConfig.getInt("lang-version");

        if (newVersion > currentVersion) {
        	Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6Piggyback&7] &cOutdated &f" + plugin.config.langfile + "&c! &aUpdating &f" + plugin.config.langfile + " &ato the newest version..."));
            mergeConfigs(language, defaultConfig);
            try {
                language.save(languageFile);
                Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6Piggyback&7] &aYour &f" + plugin.config.langfile + " &ahas been updated successfully!"));
                Bukkit.getConsoleSender().sendMessage();
                Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6Piggyback&7] &7&oEven though this plugin updates its &f&o" + plugin.config.langfile + " &7&oautomagically,"));
                Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6Piggyback&7] &7&oI would check the values and make sure they're correct for your language."));
            } catch (Exception e) {
            	Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6Piggyback&7] &cCould not save updated &f" + plugin.config.langfile + "&c: "));
            	e.printStackTrace();
            }
        }
    }
	
	 private void mergeConfigs(FileConfiguration currentConfig, FileConfiguration defaultConfig) {
		 Set<String> keys = defaultConfig.getKeys(true);
		 for (String key : keys) {
			 if (!currentConfig.contains(key)) {
				 currentConfig.set(key, defaultConfig.get(key));
			 }
		 }
	 }
	
}

