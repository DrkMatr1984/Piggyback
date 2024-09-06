package me.blubdalegend.piggyback.storage;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import javax.sql.DataSource;
import org.sqlite.SQLiteDataSource;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import me.blubdalegend.piggyback.Piggyback;
import me.blubdalegend.piggyback.utils.DownloadDrivers;
import me.blubdalegend.piggyback.utils.ResourceUtils;

public class MySQLStorage {
	
	protected static DataSource dataSource;
    private HikariConfig config;
    protected String hostname = "";
    protected String username = "";
    protected String password = "";
    protected String url = "";
    protected Properties props;
    protected Driver driver;
    private static Connection conn = null;
    private File dataFolder;
    private File libFolder;
    private Piggyback plugin;


    @SuppressWarnings({"deprecation"})
	public MySQLStorage(Piggyback plugin) throws SQLException {
    	this.plugin = plugin;
    	this.dataFolder = new File(plugin.getDataFolder().getAbsolutePath() + System.getProperty("file.separator") + "data");
        this.username = plugin.config.username;
        this.password = plugin.config.password;
        if(plugin.config.storageType.equalsIgnoreCase("sqlite")){
        	Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6Piggyback&7] &eConnecting to SQLite Database..."));
        	if(!(dataFolder.exists())){
           		dataFolder.mkdir();
           	}
        	this.hostname = "jdbc:sqlite:" + plugin.getDataFolder().getAbsolutePath() + System.getProperty("file.separator") + "data" + System.getProperty("file.separator") + plugin.config.sqliteFilename;		
        	dataSource = new SQLiteDataSource();
        	((SQLiteDataSource)dataSource).setUrl(this.hostname);
        	MySQLStorage.conn = getConnection();         
            if (conn==null) {
                throw new SQLException("Couldn't connect to the database");
            }
            createTable("disabledPlayers");
            createTable("messagePlayers");
        }else{
          	if(plugin.config.storageType.equalsIgnoreCase("mysql")) {
          		Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6Piggyback&7] &eConnecting to MySQL Database..."));
           		if(plugin.config.autoReconnect)
               		this.hostname = "jdbc:mysql://" + plugin.config.hostname + ":" + plugin.config.port + "/" + plugin.config.database + "?useSSL=" + plugin.config.useSSL + "&autoReconnect=true";
               	else
               		this.hostname = "jdbc:mysql://" + plugin.config.hostname + ":" + plugin.config.port + "/" + plugin.config.database + "?useSSL=" + plugin.config.useSSL;         		
               	config = new HikariConfig();
               	config.setJdbcUrl(this.hostname);
                config.setUsername(this.username);
                config.setPassword(this.password);
                config.setMaximumPoolSize(10);
                config.setMaxLifetime(360000);
                config.setValidationTimeout(60000);
                config.setDriverClassName("com.mysql.cj.jdbc.Driver");
                config.addDataSourceProperty("cachePrepStmts", "true");
                config.addDataSourceProperty("prepStmtCacheSize", "250");
                config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
                dataSource = new HikariDataSource(config);
          	}else if (plugin.config.storageType.equalsIgnoreCase("h2")) {
          		Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6Piggyback&7] &eConnecting to H2 Database..."));
          		File h2Jar = null;
          		URL jarUrl = null;
          		this.libFolder = new File(plugin.getDataFolder().getAbsolutePath() + System.getProperty("file.separator") + "lib");
          		if(!(libFolder.exists())){
               		libFolder.mkdir();
               	}
          		if(ResourceUtils.listLibFiles(plugin).isEmpty()) {
          			if(plugin.config.autoDownloadLibs) {
          				DownloadDrivers.downloadDriver(plugin.config.storageType, libFolder);
          			}else {
          				Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6Piggyback&7]  &cERROR: &eTo use H2 database you need to place your H2 java driver jar inside of PiggyBack/lib before"));
              			Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6Piggyback&7]  &estarting the server. Please download the jar, place it in " + libFolder.toString() + ", and restart the server,"));
              			Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6Piggyback&7]  &eor enable ** autoDownloadLibs: true ** in the Storage Configuration section to automatically download the libs from the"));
              			Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6Piggyback&7]  &eMaven Central Repository"));
              		}
          		}else {
          			boolean contains = false;
          			for(String s : ResourceUtils.listLibFiles(plugin)) {      				
          				if(s.contains("h2-")) {
          					contains = true;
          				}
          			}
          			if(!contains) {
          				if(plugin.config.autoDownloadLibs) {
              				DownloadDrivers.downloadDriver(plugin.config.storageType, libFolder);
              			}else {
              				Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6Piggyback&7]  &cERROR: &eTo use H2 database you need to place your H2 java driver jar inside of PiggyBack/lib before"));
                  			Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6Piggyback&7]  &estarting the server. Please download the jar, place it in " + libFolder.toString() + ", and restart the server,"));
                  			Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6Piggyback&7]  &eor enable ** autoDownloadLibs: true ** in the Storage Configuration section to automatically download the libs from the"));
                  			Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6Piggyback&7]  &eMaven Central Repository"));
                  		}
      			    }      			
          		}	
          		
          		if(!ResourceUtils.listLibFiles(plugin).isEmpty()) {
          			for(String s : ResourceUtils.listLibFiles(plugin))
          			    if(s.contains("h2-")) 
          				    h2Jar = new File(libFolder.getAbsolutePath() + System.getProperty("file.separator") + s); 
          		}         		  			
          		if(h2Jar != null) {
					try {
						jarUrl = h2Jar.toURI().toURL();
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}				
          		}
          		if(jarUrl != null) {
          			URLClassLoader classLoader = new URLClassLoader(new URL[]{jarUrl}, MySQLStorage.class.getClassLoader());
          			try {
          				driver = (Driver) Class.forName("org.h2.Driver", true, classLoader).newInstance();
						plugin.getLogger().info("Loaded: " + h2Jar.getName());						
					} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
						plugin.getLogger().info("Failed to Load: " + h2Jar.getName());
						e.printStackTrace();
					}
                    	props = new Properties();
                        url = "jdbc:h2:tcp://" + plugin.config.hostname + ":" + plugin.config.port + "/~/" + plugin.config.database;
                        if(this.username!=null)
                        	props.setProperty("user", this.username);
                        if(this.password!=null)
                        	props.setProperty("password", this.password);
                        props.setProperty("MODE", "MYSQL");
          		}          		
         	}else if (plugin.config.storageType.equalsIgnoreCase("postgre")) {
         		Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6Piggyback&7] &eConnecting to PostgreSQL Database..."));
         		File postgreJar = null;
          		URL jarUrl = null;
          		this.libFolder = new File(plugin.getDataFolder().getAbsolutePath() + System.getProperty("file.separator") + "lib");
          		if(!(libFolder.exists())){
               		libFolder.mkdir();
               	}
          		if(ResourceUtils.listLibFiles(plugin).isEmpty()) {
          			if(plugin.config.autoDownloadLibs) {
          				DownloadDrivers.downloadDriver(plugin.config.storageType, libFolder);
          			}else {
          				Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6Piggyback&7] &cERROR: &eTo use PostgreSQL database you need to place your PostgreSQL java driver jar inside of PiggyBack/lib before"));
              			Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6Piggyback&7] &estarting the server. Please download the jar, place it in " + libFolder.toString() + ", and restart the server,"));
              			Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6Piggyback&7] &eor enable ** autoDownloadLibs: true ** in the Storage Configuration section to automatically download the libs from the"));
              			Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6Piggyback&7] &eMaven Central Repository"));
              		}
          		}else {
          			boolean contains = false;
          			for(String s : ResourceUtils.listLibFiles(plugin)) {      				
          				if(s.contains("postgresql-")) {
          					contains = true;
          				}
          			}
          			if(!contains) {
          				if(plugin.config.autoDownloadLibs) {
              				DownloadDrivers.downloadDriver(plugin.config.storageType, libFolder);
              			}else {
              				Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6Piggyback&7] &cERROR: &eTo use PostgreSQL database you need to place your PostgreSQL java driver jar inside of PiggyBack/lib before"));
                  			Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6Piggyback&7] &estarting the server. Please download the jar, place it in " + libFolder.toString() + ", and restart the server,"));
                  			Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6Piggyback&7] &eor enable ** autoDownloadLibs: true ** in the Storage Configuration section to automatically download the libs from the"));
                  			Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6Piggyback&7] &eMaven Central Repository"));
                  		}
      			    }      			    
          		}
          		if(!ResourceUtils.listLibFiles(plugin).isEmpty()) {
          			for(String s : ResourceUtils.listLibFiles(plugin))
          			    if(s.contains("postgresql-")) 
          				    postgreJar = new File(libFolder.getAbsolutePath() + System.getProperty("file.separator") + s); 
          		}         		  			
          		if(postgreJar != null) {
					try {
						jarUrl = postgreJar.toURI().toURL();
					} catch (MalformedURLException e) {
						Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6Piggyback&7]  &cERROR: &ePath is not right for postgreSQL jar file"));
						e.printStackTrace();
					}				
          		}
          		
          		if(jarUrl != null) {
          			URLClassLoader classLoader = new URLClassLoader(new URL[]{jarUrl}, MySQLStorage.class.getClassLoader());
          			try {
          				driver = (Driver) Class.forName("org.postgresql.Driver", true, classLoader).newInstance();
          				Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6Piggyback&7] &aLoaded: &f" + postgreJar.getName()));						
					} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
						Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6Piggyback&7] &cFailed to Load: " + postgreJar.getName()));
						e.printStackTrace();
					}
                    	props = new Properties();
                        if(plugin.config.useSSL) {
                   			Path path = Paths.get(plugin.getDataFolder().getAbsolutePath() + System.getProperty("file.separator") + "data" + System.getProperty("file.separator") + "ca-certificate.crt");
                       		String cert = null;
                       		try
                       		{
                       		    cert = Files.readString( path , StandardCharsets.UTF_8 );
                       		    plugin.getLogger().info("Loading: " + cert + " for PostgreSQL...");
                       		}
                       		catch ( IOException ex )
                       		{
                       		    throw new IllegalStateException( "Unable to load the TLS certificate needed to make database connections for PostgreSQL." );
                       		}
                       		if(cert!=null) {
                       		    if ( cert.isEmpty() )
                       		      	throw new IllegalStateException( "Failed to load TLS cert." );
                       		} else {
                       			throw new IllegalStateException( "Failed to load TLS cert." );
                       		}
                       		props.setProperty("ssl", "true");
                            props.setProperty("sslmode", "verify-full");
                       		props.setProperty("sslrootcert", path.toString());
                   		}
                        url = "jdbc:postgresql://" + plugin.config.hostname + ":" + plugin.config.port + "/" + plugin.config.database;
                        if(this.username!=null)
                        	if(!this.username.isBlank())
                        		props.setProperty("user", this.username);
                        if(this.password!=null)
                        	if(!this.password.isBlank())
                        		props.setProperty("password", this.password);
          		}
         		
           	}else {
           		Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6Piggyback&7] &cERROR: &eDatabase needs a type set. Possible values: YML, H2, MYSQL, POSTGRE, SQLITE"));
           	}
        
          	try {
				conn = getConnection();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            if (conn==null) {
            	throw new SQLException("Couldn't connect to the database");
            }
            if (plugin.config.storageType.equalsIgnoreCase("postgre")) {
              	PreparedStatement stmt = getStatement("CREATE SCHEMA IF NOT EXISTS piggyback");
                stmt.executeUpdate();
            }
            try
            {
               	createTable("piggybackDisabledPlayers");
               	createTable("piggybackMessagePlayers");
            }catch (Exception e) {
            	try
                {
                    conn.rollback();
                }
                catch (SQLException e1)
                {
                    e1.printStackTrace();
                }
                e.printStackTrace();
                return;
            }
            Bukkit.getScheduler().runTaskTimerAsynchronously(plugin,() -> keepAlive(), 20*60*60*7, 20*60*60*7);
        }
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6Piggyback&7] &aSuccessfully connected to &f" + plugin.config.storageType.toUpperCase() +" &aDatabase!"));
    }
    
    public List<String> loadDisabledPlayers(){
		//pickup toggle users
		List<String> disabledPlayers = new ArrayList<>();
		String tableName;
		if(plugin.config.storageType.equalsIgnoreCase("sqlite")){
			tableName = "disabledPlayers";
			disabledPlayers = getAllKeys(tableName);
		}else {
			tableName = "piggybackDisabledPlayers";
			disabledPlayers = getAllKeys(tableName);
		}		
		return disabledPlayers;	
	}
    
	public List<String> loadMessagePlayers(){
		//message toggle users
		List<String> messagePlayers = new ArrayList<>();
		String tableName;
		if(plugin.config.storageType.equalsIgnoreCase("sqlite")){
			tableName = "messagePlayers";
			messagePlayers = getAllKeys(tableName);
		}else {
			tableName = "piggybackMessagePlayers";
			messagePlayers = getAllKeys(tableName);
		}
		return messagePlayers;
	}    

	//SQLite Saving
	public void saveData(List<String> disabledPlayers, List<String> messagePlayers) {
        clearTable("disabledPlayers");
        try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(disabledPlayers!=null) {
	  		if(!disabledPlayers.isEmpty()) {
				for(String id : disabledPlayers) {
					try {
							createTable("disabledPlayers");
							PreparedStatement ps = getStatement("REPLACE INTO disabledPlayers (uuid) VALUES (?)");
						    ps.setString(1, id);
						    ps.executeUpdate();
						    ps.close();
				        } catch (Exception ex) {
				        	ex.printStackTrace();
				        }
				}
	  		}
		}
		clearTable("messagePlayers");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(messagePlayers!=null) {
	  		if(!messagePlayers.isEmpty()) {
				for(String s : messagePlayers) {
					try {
							createTable("messagePlayers");
							PreparedStatement ps = getStatement("REPLACE INTO messagePlayers (uuid) VALUES (?)");
							ps.setString(1, s);
							ps.executeUpdate();
							ps.close();
				      } catch (Exception ex) {
				    	  ex.printStackTrace();
				      }
		  		}
	  		}
		}				
	}
	
	public boolean isDisabled(String uuid) {
		if(plugin.config.storageType.equalsIgnoreCase("postgre")) {
			if(getAllKeys("piggyback.piggybackDisabledPlayers").contains(uuid))
				return true;
		}else {
			if(getAllKeys("piggybackDisabledPlayers").contains(uuid))
				return true;
		}
		return false;
	}
	
	public boolean isDisabled(UUID uuid) {
		return isDisabled(uuid.toString());
	}
	
	public boolean isDisabled(Player p) {
		return isDisabled(p.getUniqueId());
	}
	
	public void setDisabled(String uuid, boolean b) {
		if(!isDisabled(uuid) && b==true) {
			if(plugin.config.storageType.equalsIgnoreCase("postgre")) {
				addToggle("piggyback.piggybackDisabledPlayers", uuid);
			}else {
				addToggle("piggybackDisabledPlayers", uuid);
			}			
		}
		if(isDisabled(uuid) && b==false) {
			if(plugin.config.storageType.equalsIgnoreCase("postgre")) {
				removeToggle("piggyback.piggybackDisabledPlayers", uuid);
			}else {
				removeToggle("piggybackDisabledPlayers", uuid);
			}			
		}
	}
	
	public void setDisabled(UUID uuid, boolean b) {
		setDisabled(uuid.toString(), b);
	}
	
	public void setDisabled(Player p, boolean b) {
		setDisabled(p.getUniqueId(), b);
	}
	
	public boolean hasMessagesDisabled(String uuid) {
		if(plugin.config.storageType.equalsIgnoreCase("postgre")) {
			if(getAllKeys("piggyback.piggybackMessagePlayers").contains(uuid))
				return true;
		}else {
			if(getAllKeys("piggybackMessagePlayers").contains(uuid))
				return true;
		}		
		return false;
	}
	
	public boolean hasMessagesDisabled(UUID uuid) {
		return hasMessagesDisabled(uuid.toString());
	}
	
	public boolean hasMessagesDisabled(Player p) {
		return hasMessagesDisabled(p.getUniqueId());
	}
	
	public void setMessagesDisabled(String uuid, boolean b) {
		if(!hasMessagesDisabled(uuid) && b==true) {
			if(plugin.config.storageType.equalsIgnoreCase("postgre")) {
				addToggle("piggyback.piggybackMessagePlayers", uuid);
			}else {
				addToggle("piggybackMessagePlayers", uuid);
			}		
		}
		if(hasMessagesDisabled(uuid) && b==false) {
			if(plugin.config.storageType.equalsIgnoreCase("postgre")) {
				removeToggle("piggyback.piggybackMessagePlayers", uuid);
			}else {
				removeToggle("piggybackMessagePlayers", uuid);
			}			
		}
	}
	
	public void setMessagesDisabled(UUID uuid, boolean b) {
		setMessagesDisabled(uuid.toString(), b);
	}
	
	public void setMessagesDisabled(Player p, boolean b) {
		setMessagesDisabled(p.getUniqueId(), b);
	}
	
	private void addToggle(String tableName, String uuid){
		String qry = "";
		if(plugin.config.storageType.equalsIgnoreCase("postgre")) {
			qry = "INSERT INTO piggyback." + tableName + " (uuid) " + 
		        "VALUES (?)";
		}else {
			qry = "REPLACE INTO " + tableName + " (uuid) VALUES (?)";
		}	
		PreparedStatement ps = getStatement(qry);
    	try {
    		createTable(tableName);
		    ps.setString(1, uuid);
		    ps.executeUpdate();
		    ps.close();
	    } catch (Exception ex) {
	    	 ex.printStackTrace();
		}
    }
    
	///////This doesn't actually seem to do anything different than the expression above it.
    private void removeToggle(String tableName, String uuid) {
    	String qry = "";
    	if(plugin.config.storageType.equalsIgnoreCase("postgre")) {
    		qry = "DELETE FROM piggyback." + tableName + " WHERE uuid = ?";
    	} else {
    		qry = "DELETE FROM " + tableName + " WHERE uuid = ?";
    	}
    	PreparedStatement ps = getStatement(qry);
    	try {
    		createTable(tableName);
			ps.setString(1, uuid);
			 // Execute the delete statement
            ps.executeUpdate();
		    ps.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public static void closeConnection() throws SQLException{
    	if(dataSource!=null) {
    		if(dataSource instanceof HikariDataSource) {
        		if(!((HikariDataSource) dataSource).isClosed()) {
        			((HikariDataSource) dataSource).close();
        		}
        	}
    	}
    	if (conn != null && !conn.isClosed()) conn.close();  	 
    } 
    private Connection getConnection() throws SQLException{
    	if (isConnected())
    	      try {
    	        conn.createStatement().execute("SELECT 1;");
    	      } catch (SQLException sqlException) {
    	        if (sqlException.getSQLState().equals("08S01"))
    	          try {
    	            conn.close();
    	          } catch (SQLException sQLException) {
    	        	  
    	          } 
    	      }
        if (conn == null || conn.isClosed() || !conn.isValid(60)) {
            //maybe change to lower than 60, like 4 seconds?
        	if(dataSource  instanceof HikariDataSource || dataSource instanceof SQLiteDataSource) {
                try {
				    conn = dataSource.getConnection();
				} catch (SQLException e) {
					Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6Piggyback&7] &cCannot connect to Hikari/SQLite Database! Check your settings in config.yml!"));
					e.printStackTrace();
				}           	
        	}else {
        		if(driver!=null) {
        			try {
        				conn = driver.connect(url, props);
        			}catch (Exception e) {
        				//hopefully this catches the exception and cleans up the error log
        			}      			
        		}
        		    
        	}      
        }
        // The connection could be null here (!)
        return conn;
    }
    
    private void keepAlive() {
        try {
            conn.isValid(0);
        } catch (SQLException e) {
            e.printStackTrace();
        }              
    }
    
    private boolean isConnected() {
  		return (conn != null);
  	}
    
    private void createTable(String tableName) throws SQLException {
    	try
    	{
    		MySQLStorage.conn = getConnection();
            if (conn==null) {
                throw new SQLException("Couldn't connect to the database");
            }
            String qry = "";
        	if(plugin.config.storageType.equalsIgnoreCase("postgre")) {
        		qry = "CREATE TABLE IF NOT EXISTS piggyback." + tableName + " (" +
                        "id SERIAL PRIMARY KEY, " +
                        "uuid VARCHAR(64) UNIQUE NOT NULL" +
                        ")";
        	}else {
        		qry = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "uuid VARCHAR(64) NOT NULL" +
                        ")";
        	}          
            PreparedStatement stmt = getStatement(qry);
            stmt.executeUpdate();
    	}catch (Exception e) {
    		try
            {
                    conn.rollback();
            }
            catch (SQLException e1)
            {
                    e1.printStackTrace();
            }
            e.printStackTrace();
            return;
    	}   
    }
    
    private void deleteTable(String tableName){
    	if (isConnected()) {
			try {
				PreparedStatement statement = conn.prepareStatement("DROP TABLE IF EXISTS " + tableName);
				statement.executeUpdate();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}         
    	}
    }
    
    private void clearTable(String tableName) {
        try {
            // Delete the table
            deleteTable(tableName);
            // Wait for a second
            Thread.sleep(1000);
            // Recreate the table
            createTable(tableName);
        } catch (SQLException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    // Function to get all keys from a table
    private List<String> getAllKeys(String tableName) {
    	List<String> keys = new ArrayList<>();
    	if (isConnected()) {  		
            String sql = "SELECT uuid FROM " + tableName;
            PreparedStatement pstmt = getStatement(sql);
            ResultSet rs;
    		try {
    			rs = pstmt.executeQuery();
    			while (rs.next()) {
                    keys.add(rs.getString("uuid"));
                }
    		} catch (SQLException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}            
    	}else {
    		Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6Piggyback&7] &cERROR: Not connected to database. This is a serious error and PiggyBack data WILL be lost. Please"
    				+ " &cnotify the plugin author"));
    	}
    	return keys;
    }
    
    private PreparedStatement getStatement(String sql) {
  		if (isConnected())
  			try {
  				PreparedStatement ps = conn.prepareStatement(sql);
  				return ps;
  			} catch (SQLException e) {
  				e.printStackTrace();
  			}  
  		return null;
  	}

}
