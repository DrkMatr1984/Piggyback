package me.blubdalegend.piggyback.config;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import me.blubdalegend.piggyback.Piggyback;
import org.jetbrains.annotations.NotNull;

public class ToggleLists{
	
	private Set<String> disabledPlayers = Collections.newSetFromMap(new ConcurrentHashMap<>());
	private Set<String> messagePlayers = Collections.newSetFromMap(new ConcurrentHashMap<>());
	private YMLStorage ymlStorage;
	private MySQLStorage mysqlStorage;
	
	private final Piggyback plugin;
	private String storageType;
	
	public ToggleLists(Piggyback plugin){		
		this.plugin = plugin;
		this.storageType = plugin.config.storageType;
		initLists();
	}
		
	private void initLists(){
		if(this.storageType!=null) {
			if(this.storageType.equalsIgnoreCase("yml")) {
				Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.lang.prefix + " &aLoading .yml data files..."));
				ymlStorage = new YMLStorage(plugin);
				disabledPlayers = listToSet(ymlStorage.loadDisabledPlayers());
				messagePlayers = listToSet(ymlStorage.loadMessagePlayers());
			}else {   ///MYSQL type Storages
				if(this.storageType!=null) {
			        if(isSQLite()) {
			        	try {
							mysqlStorage = new MySQLStorage(plugin);
						} catch (Exception e) {
							Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.lang.prefix + " &eThere has been an error with the &f" + plugin.config.storageType.toUpperCase() + " &edatabase. Check your settings and try again."));
							Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.lang.prefix + " &eIf you are sure everything is correct please report the following error to &fhttps://github.com/DrkMatr1984/Piggyback/issues."));
							Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.lang.prefix + " &cPiggyBack will now shutdown..."));
							e.printStackTrace();
			            	Bukkit.getServer().getPluginManager().disablePlugin(plugin);
						}
			        	disabledPlayers = listToSet(mysqlStorage.loadDisabledPlayers());
			        	messagePlayers = listToSet(mysqlStorage.loadMessagePlayers());
			        }else {
			        	try {
							mysqlStorage = new MySQLStorage(plugin);
						} catch (Exception e) {
							Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.lang.prefix + " &eThere has been an error with the &f" + plugin.config.storageType.toUpperCase() + " &edatabase. Check your settings and try again."));
							Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.lang.prefix + " &eIf you are sure everything is correct please report the following error to &fhttps://github.com/DrkMatr1984/Piggyback/issues."));
							Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.lang.prefix + " &cPiggyBack will now shutdown..."));
							e.printStackTrace();
			            	Bukkit.getServer().getPluginManager().disablePlugin(plugin);
						}
			        }
				}else {
					//Someone did not specify YML, H2, MYSQL, POSTGRE, SQLITE
					//Fallback to yml
					plugin.getLogger().info("ERROR: Database needs a type set. Possible values: YML, H2, MYSQL, POSTGRE, SQLITE");
					plugin.getLogger().info("Falling back to yml.");
					ymlStorage = new YMLStorage(plugin);
					disabledPlayers = listToSet(ymlStorage.loadDisabledPlayers());
					messagePlayers = listToSet(ymlStorage.loadMessagePlayers());
					this.storageType = "yml";
				}
			}
			if(isYML() || isSQLite()) {
				Bukkit.getScheduler().runTaskTimerAsynchronously(plugin,new Runnable() {
					public void run() {
						saveData();
					}
				}, 20*plugin.config.saveTimer, 20*plugin.config.saveTimer);
			}
		}else {
			//Someone did not specify YML, H2, MYSQL, POSTGRE, SQLITE
			//Fallback to yml
			plugin.getLogger().info("Database needs a type set. Possible values: YML, H2, MYSQL, POSTGRE, SQLITE");
			plugin.getLogger().info("Falling back to yml.");
			ymlStorage = new YMLStorage(plugin);
			disabledPlayers = listToSet(ymlStorage.loadDisabledPlayers());
			messagePlayers = listToSet(ymlStorage.loadMessagePlayers());
			this.storageType = "yml";
		}
	}
	
	public void saveData() {
		if(isYML()) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.lang.prefix + " &aSaving data..."));
			ymlStorage.saveData(setToList(disabledPlayers),setToList(messagePlayers));
			Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.lang.prefix + " &aData saved successfully to YML!"));
		}else if(isSQLite()){   ///MYSQL type Storages
			Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.lang.prefix + " &aSaving data..."));
			mysqlStorage.saveData(setToList(disabledPlayers), setToList(messagePlayers));
			Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.lang.prefix + " &aData saved successfully to SQLite!"));
		}
	}
	
	@SuppressWarnings("static-access")
	public void closeMySQLConnection() {
		if(!isYML())
			try {
				mysqlStorage.closeConnection();
			} catch (SQLException e) {
				Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.lang.prefix + " Could not properly close MySQL-like connection."));
			}
	}
	
	public boolean isYML() {
		if(this.storageType.equalsIgnoreCase("yml"))
			return true;
		return false;
	}
	
	public boolean isSQLite() {
		if(this.storageType.equalsIgnoreCase("sqlite"))
			return true;
		return false;
	}
	
	public boolean isDisabled(@NotNull String id) {
		if(isYML() || isSQLite())
		    return this.disabledPlayers.contains(id);
		else {
			return mysqlStorage.isDisabled(id);
		}
	}
	
	public boolean isDisabled(@NotNull UUID id) {
		return isDisabled(id.toString());
	}
	
	public boolean isDisabled(@NotNull Player p) {
		return isDisabled(p.getUniqueId());
	}
	
	public void setDisabled(@NotNull String id, boolean b) {
		if(isYML() || isSQLite()) {
			if(b) {
				if(!this.disabledPlayers.contains(id))
			        this.disabledPlayers.add(id);
			}else {
				if(this.disabledPlayers.contains(id))
					this.disabledPlayers.remove(id);
			}
		}else {
			plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> mysqlStorage.setDisabled(id, b));			
		}	
	}
	
	public void setDisabled(@NotNull Player p, boolean b) {
		setDisabled(p.getUniqueId(), b);
	}
	
	public void setDisabled(@NotNull UUID id, boolean b) {
		setDisabled(id.toString(), b);
	}
	
	public boolean hasMessagesDisabled(@NotNull String id) {
		if(isYML() || isSQLite())
		    return this.messagePlayers.contains(id);
		else
			return mysqlStorage.hasMessagesDisabled(id);
	}
	
	public boolean hasMessagesDisabled(@NotNull UUID id) {
		return hasMessagesDisabled(id.toString());
	}
	
	public boolean hasMessagesDisabled(@NotNull Player p) {
		return hasMessagesDisabled(p.getUniqueId());
	}
	
	public void setMessagesDisabled(@NotNull String id, boolean b) {
		if(isYML() || isSQLite()) {
			if(b) {
				if(!plugin.lists.messagePlayers.contains(id))
			        this.messagePlayers.add(id);
			}else {
				if(plugin.lists.messagePlayers.contains(id))
			        this.messagePlayers.remove(id);
			}
		}else {
			plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> mysqlStorage.setMessagesDisabled(id, b));
		}	
	}
	
	public void setMessagesDisabled(@NotNull UUID id, boolean b) {
		setMessagesDisabled(id.toString(), b);
	}
	
	public void setMessagesDisabled(@NotNull Player p, boolean b) {
		setMessagesDisabled(p.getUniqueId(), b);
	}
	
	private Set<String> listToSet(List<String> list) {
		Set<String> set = Collections.newSetFromMap(new ConcurrentHashMap<>());
		for(String s : list)
			set.add(s);
		return set;
	}
	
	public List<String> setToList(Set<String> set) {
		List<String> list = new ArrayList<String>();
		for(String s : set)
			list.add(s);
		return list;
	}
}
