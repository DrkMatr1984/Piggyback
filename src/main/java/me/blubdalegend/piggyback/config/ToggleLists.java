package me.blubdalegend.piggyback.config;

import java.sql.SQLException;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.bukkit.entity.Player;

import me.blubdalegend.piggyback.Piggyback;
import org.jetbrains.annotations.NotNull;

public class ToggleLists{
	public Queue<String> disabledPlayers = new ConcurrentLinkedQueue<String>();
	public Queue<String> messagePlayers = new ConcurrentLinkedQueue<String>();
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
				ymlStorage = new YMLStorage(plugin);
				disabledPlayers = listToQueue(ymlStorage.loadDisabledPlayers());
				messagePlayers = listToQueue(ymlStorage.loadMessagePlayers());
			}else {   ///MYSQL type Storages
				if(DatabaseType.match(this.storageType)!=null) {
			        if(isSQLite()) {
			        	mysqlStorage = new MySQLStorage(plugin);
			        	disabledPlayers = listToQueue(mysqlStorage.loadDisabledPlayers());
			        	messagePlayers = listToQueue(mysqlStorage.loadMessagePlayers());
			        }else {
			        	mysqlStorage = new MySQLStorage(plugin);
			        }
				}else {
					//Someone did not specify YML, H2, MYSQL, POSTGRE, SQLITE
					//Fallback to yml
					plugin.getLogger().info("Database needs a type set. Possible values: YML, H2, MYSQL, POSTGRE, SQLITE");
					plugin.getLogger().info("Falling back to yml.");
					ymlStorage = new YMLStorage(plugin);
					disabledPlayers = listToQueue(ymlStorage.loadDisabledPlayers());
					messagePlayers = listToQueue(ymlStorage.loadMessagePlayers());
					this.storageType = "yml";
				}
			}
		}	
	}
	
	public void saveData() {
		if(isYML()) {
			ymlStorage.saveData(disabledPlayers, messagePlayers);
		}else if(isSQLite()){   ///MYSQL type Storages
			mysqlStorage.saveData(disabledPlayers, messagePlayers);
		}else {
			mysqlStorage.saveData();
		}
	}
	
	public void closeMySQLConnection() {
		if(!isYML())
			try {
				mysqlStorage.closeConnection();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	public boolean isYML() {
		if(this.storageType.equalsIgnoreCase("yml"))
			return true;
		return false;
	}
	
	public boolean isSQLite() {
		if(DatabaseType.match(this.storageType)!=null) {
			if(DatabaseType.match(this.storageType)==DatabaseType.SQLITE){
				return true;
			}
		}
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
			mysqlStorage.setDisabled(id, b);
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
			mysqlStorage.setMessagesDisabled(id, b);
		}	
	}
	
	public void setMessagesDisabled(@NotNull UUID id, boolean b) {
		setMessagesDisabled(id.toString(), b);
	}
	
	public void setMessagesDisabled(@NotNull Player p, boolean b) {
		setMessagesDisabled(p.getUniqueId(), b);
	}
	
	private Queue<String> listToQueue(List<String> list) {
		Queue<String> queue = new ConcurrentLinkedQueue<String>();
		for(String s : list)
			queue.add(s);
		return queue;
	}
	
}
