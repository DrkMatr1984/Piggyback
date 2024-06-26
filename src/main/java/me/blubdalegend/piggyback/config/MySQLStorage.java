package me.blubdalegend.piggyback.config;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import javax.sql.DataSource;
import org.postgresql.ds.PGSimpleDataSource;

import org.sqlite.SQLiteDataSource;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import me.blubdalegend.piggyback.Piggyback;

public class MySQLStorage {
	
	protected static DataSource dataSource;
    private HikariConfig config;
    protected String hostname = "";
    protected String username = "";
    protected String password = "";
    private static Connection conn = null;
    private File dataFolder;
    private Piggyback plugin;


    public MySQLStorage(Piggyback plugin) throws SQLException {
    	this.plugin = plugin;
    	this.dataFolder = new File(plugin.getDataFolder() +"/data");
        this.username = plugin.config.username;
        this.password = plugin.config.password;
        if(plugin.config.storageType.equalsIgnoreCase("sqlite")){
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
           		this.hostname = "jdbc:h2:tcp://" + plugin.config.hostname + ":" + plugin.config.port + "/~/" + plugin.config.database;
           		config = new HikariConfig();
           		config.setJdbcUrl(this.hostname);
                config.setUsername(this.username); // Replace with your username
                config.setPassword(this.password); // Replace with your password
                config.setDriverClassName("org.h2.Driver");
                config.setMaximumPoolSize(10);
                config.setMinimumIdle(2);
                config.setIdleTimeout(30000);
                config.setConnectionTimeout(30000);
                config.setMaxLifetime(1800000);
                dataSource = new HikariDataSource(config);
         	}else if (plugin.config.storageType.equalsIgnoreCase("postgre")) {
           		dataSource = new PGSimpleDataSource();
           		if(plugin.config.useSSL) {
           			Path path = Paths.get(plugin.getDataFolder().getAbsolutePath() + System.getProperty("file.separator") + "data" + System.getProperty("file.separator") + "ca-certificate.crt");
               		String cert = null;
               		try
               		{
               		    cert = Files.readString( path , StandardCharsets.UTF_8 );
               		    System.out.println( "cert = " + cert );
               		}
               		catch ( IOException ex )
               		{
               		    throw new IllegalStateException( "Unable to load the TLS certificate needed to make database connections." );
               		}
               		Objects.requireNonNull(cert);
               		if ( cert.isEmpty() )
               		{
               			throw new IllegalStateException( "Failed to load TLS cert." );
               		}
               	    ((PGSimpleDataSource)dataSource).setSslCert(cert);
           		}         		
           	    ((PGSimpleDataSource)dataSource).setServerNames(new String[]{plugin.config.hostname});
           	    ((PGSimpleDataSource)dataSource).setDatabaseName(plugin.config.database);
          	    ((PGSimpleDataSource)dataSource).setUser(this.username);
           	    ((PGSimpleDataSource)dataSource).setPassword(this.password);
           	    ((PGSimpleDataSource)dataSource).setPortNumbers(new int[] {plugin.config.port});
           	}else {
           		plugin.getLogger().info("ERROR: Database needs a type set. Possible values: YML, H2, MYSQL, POSTGRE, SQLITE");
           	}
        
          	try {
				MySQLStorage.conn = getConnection();
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
    	if(dataSource instanceof HikariDataSource) {
    		if(!((HikariDataSource) dataSource).isClosed()) {
    			((HikariDataSource) dataSource).close();
    		}
    	}else {
    		if (conn != null && !conn.isClosed()) conn.close();
    	}  	 
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
        if (conn == null || conn.isClosed() || !conn.isValid(60)) { //maybe change to lower than 60, like 4 seconds?
        	if(dataSource  instanceof HikariDataSource || dataSource instanceof SQLiteDataSource || dataSource instanceof PGSimpleDataSource) {
                try {
				    conn = dataSource.getConnection();
				} catch (SQLException e) {
					Bukkit.getServer().getLogger().info("!!!!!!!!!!!!!!!!!!!! dataSource.getConnection() is throwing a fit");
					e.printStackTrace();
				}           	
        	}else {
        		conn = (username.isEmpty() && password.isEmpty()) ? DriverManager.getConnection(hostname) : DriverManager.getConnection(hostname, username, password);
        		Bukkit.getServer().getLogger().info("!!!!!!!!!!!!!!!!!!!! Trying to connect to Other Database");
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
    		MySQLStorage.conn = dataSource.getConnection();
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
    		Bukkit.getServer().getLogger().info("ERROR: Not connected to database. This is a serious error and PiggyBack data WILL be lost. Please"
    				+ " notify the plugin author");
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
