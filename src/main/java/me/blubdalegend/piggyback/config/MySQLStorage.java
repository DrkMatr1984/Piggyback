package me.blubdalegend.piggyback.config;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import javax.sql.DataSource;

import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteDataSource;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import me.blubdalegend.piggyback.Piggyback;

public class MySQLStorage {
	
	protected DataSource dataSource;
    protected DatabaseType driver;
    private HikariConfig config;
    protected String url = "";
    protected String username = "";
    protected String password = "";
    private Connection conn = null;
    private File dataFolder;
    
    //fix to work with piggyback
    
    public MySQLStorage(Piggyback plugin) throws SQLException {
    	this.dataFolder = new File(plugin.getDataFolder() +"/data");
        this.driver = DatabaseType.match(plugin.config.storageType);
        if(this.driver!=null) {
        	if(this.driver.equals(DatabaseType.SQLITE)){	
        		if(!(dataFolder.exists())){
        	   		dataFolder.mkdir();
        	   	}
        		this.url = "jdbc:sqlite:" + plugin.getDataFolder().getAbsolutePath() + System.getProperty("file.separator") + "data" + System.getProperty("file.separator") + plugin.config.sqliteFilename;		
        		/*try
            	{*/
        			dataSource = new SQLiteDataSource();
        			((SQLiteDataSource)dataSource).setUrl(this.url);
        			this.conn = getConnection();         
                    if (conn==null) {
                         throw new SQLException("Couldn't connect to the database");
                    }
                 	createTable("disabledPlayers");
                 	createTable("messagePlayers");
            	/*}catch (Exception e) {
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
                }*/
        			/*
                    if (!loadDriver()) {
                        throw new SQLException("Couldn't load driver");
                    }
                    this.conn = getConnection();           
                
                	if (conn==null) {
                        throw new SQLException("Couldn't connect to the database");
                    }
                	createTable("disabledPlayers");
                	createTable("messagePlayers");
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
                    */     	             
            }else{
            	if(plugin.config.storageType.equalsIgnoreCase("mysql")) {
            		if(plugin.config.autoReconnect)
                		this.url = "jdbc:" + plugin.config.url + plugin.config.database + "?useSSL=" + plugin.config.useSSL + "&autoReconnect=true";
                	else
                		this.url = "jdbc:" + plugin.config.url + plugin.config.database + "?useSSL=" + plugin.config.useSSL;
            	}
            	this.username = plugin.config.username;
                this.password = plugin.config.password;
            	config = new HikariConfig();
            	config.setJdbcUrl(this.url);
                config.setUsername(this.username);
                config.setPassword(this.password);
                config.setMaximumPoolSize(10);
                config.setMaxLifetime(360000);
                config.setValidationTimeout(60000);
                config.setDriverClassName(this.driver.driver);
                config.addDataSourceProperty("cachePrepStmts", "true");
                config.addDataSourceProperty("prepStmtCacheSize", "250");
                config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
                dataSource = new HikariDataSource(config);
                try {
					this.conn = getConnection();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                if (conn==null) {
                    throw new SQLException("Couldn't connect to the database");
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
            }
        	//Start "keepAlive" task to keep connection active
        	Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> keepAlive(), 20*60*60*7, 20*60*60*7);
        }else {
        	plugin.getLogger().info("Database needs a type set. Possible values: H2, MYSQL, POSTGRE, SQLITE");
        }      
    }
    
    public List<String> loadDisabledPlayers(){
		//pickup toggle users
		List<String> disabledPlayers = new ArrayList<>();
		String tableName;
		if(this.driver.equals(DatabaseType.SQLITE)){
			tableName = "disabledPlayers";
			disabledPlayers = getSQLiteColumnNames(tableName);
		}else {
			tableName = "piggybackDisabledPlayers";
			try {
				disabledPlayers = getTableColumnNames(tableName);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return disabledPlayers;	
	}
    
	public List<String> loadMessagePlayers(){
		//message toggle users
		List<String> messagePlayers = new ArrayList<>();
		String tableName;
		if(this.driver.equals(DatabaseType.SQLITE)){
			tableName = "messagePlayers";
			messagePlayers = getSQLiteColumnNames(tableName);
		}else {
			tableName = "piggybackMessagePlayers";
			try {
				messagePlayers = getTableColumnNames(tableName);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return messagePlayers;
	}    

	//SQLite Saving
	public void saveData(List<String> disabledPlayers, List<String> messagePlayers) {
        clearTable("disabledPlayers");
		if(disabledPlayers!=null) {
	  		if(!disabledPlayers.isEmpty()) {
				for(String s : disabledPlayers) {
					try {
							createTable("disabledPlayers");
							PreparedStatement ps = getStatement("REPLACE INTO " + "disabledPlayers" + " SET uuid= ?");
						    ps.setString(1, s);
						    ps.executeUpdate();
						    ps.close();
				        } catch (Exception ex) {
				    	  //put error message here
				        }
				}
	  		}
		}
		clearTable("messagePlayers");
		if(messagePlayers!=null) {
	  		if(!messagePlayers.isEmpty()) {
				for(String s : messagePlayers) {
					try {
							createTable("messagePlayers");
							PreparedStatement ps = getStatement("REPLACE INTO " + "messagePlayers" + " SET uuid= ?");
							ps.setString(1, s);
							ps.executeUpdate();
							ps.close();
				      } catch (Exception ex) {
				    	  //put error message here
				      }
		  		}
	  		}
		}				
	}
	
	//MySQL saving
	public void saveData() {
		List<String> disabledPlayers = loadDisabledPlayers();
		if(disabledPlayers!=null) {
  			if(!disabledPlayers.isEmpty()) {
  				clearTable("piggybackDisabledPlayers");
	  			for(String s : disabledPlayers) {
	  				try {
	  					createTable("piggybackDisabledPlayers");
	  			        PreparedStatement ps = getStatement("REPLACE INTO piggybackDisabledPlayers SET uuid= ?");
	  			        ps.setString(1, s);
	  			        ps.executeUpdate();
	  			        ps.close();
	  			      } catch (Exception ex) {
	  			    	  //put error message here
	  			      }
	  	  		}
  			}
		}
		
		List<String> messagePlayers = loadMessagePlayers();
		if(messagePlayers!=null) {
  			if(!messagePlayers.isEmpty()) {
  				clearTable("piggybackMessagePlayers");
	  			for(String s : messagePlayers) {
	  				try {
	  					createTable("piggybackMessagePlayers");
	  			        PreparedStatement ps = getStatement("REPLACE INTO piggybackMessagePlayers SET uuid= ?");
	  			        ps.setString(1, s);
	  			        ps.executeUpdate();
	  			        ps.close();
	  			      } catch (Exception ex) {
	  			    	  //put error message here
	  			      }
	  	  		}
  			}
		}
	}
	
	public boolean isDisabled(String uuid) {
		try {
			if(getTableColumnNames("piggybackDisabledPlayers").contains(uuid))
				return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			addToggle("piggybackDisabledPlayers", uuid);
		}
		if(isDisabled(uuid) && b==false) {
			removeToggle("piggybackDisabledPlayers", uuid);
		}
	}
	
	public void setDisabled(UUID uuid, boolean b) {
		setDisabled(uuid.toString(), b);
	}
	
	public void setDisabled(Player p, boolean b) {
		setDisabled(p.getUniqueId(), b);
	}
	
	public boolean hasMessagesDisabled(String uuid) {
		try {
			if(getTableColumnNames("piggybackMessagePlayers").contains(uuid))
				return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			addToggle("piggybackMessagePlayers", uuid);
		}
		if(hasMessagesDisabled(uuid) && b==false) {
			removeToggle("piggybackMessagePlayers", uuid);
		}
	}
	
	public void setMessagesDisabled(UUID uuid, boolean b) {
		setMessagesDisabled(uuid.toString(), b);
	}
	
	public void setMessagesDisabled(Player p, boolean b) {
		setMessagesDisabled(p.getUniqueId(), b);
	}
	
	private void addToggle(String tableName, String uuid){
    	PreparedStatement ps = getStatement("REPLACE INTO " + tableName + " SET uuid= ?");
    	try {
    		createTable(tableName);
		    ps.setString(1, uuid);
		    ps.executeUpdate();
		    ps.close();
	    } catch (Exception ex) {
		  	  //put error message here
		}
    }
    
    private void removeToggle(String tableName, String uuid) {
    	PreparedStatement ps = getStatement("DELETE FROM " + tableName + " WHERE uuid= ?");
    	try {
			ps.setString(1, uuid);
			ps.executeUpdate();
		    ps.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void closeConnection() throws SQLException{
    	if(dataSource instanceof HikariDataSource) {
    		if(!((HikariDataSource) dataSource).isClosed()) {
    			((HikariDataSource) dataSource).close();
    		}
    	}else {
    		if (conn != null && !conn.isClosed()) conn.close();
    	}  	 
    }
    
    private boolean loadDriver()
    {
        try {
            this.getClass().getClassLoader().loadClass(this.driver.driver).newInstance();
            return true;
        } catch (IllegalAccessException e) {
            // Constructor is private, OK for DriverManager contract
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Connection getConnection() throws SQLException{
    	if (conn != null)
    	      try {
    	        conn.createStatement().execute("SELECT 1;");
    	      } catch (SQLException sqlException) {
    	        if (sqlException.getSQLState().equals("08S01"))
    	          try {
    	            conn.close();
    	          } catch (SQLException sQLException) {
    	        	  
    	          } 
    	      }
    	Bukkit.getServer().getLogger().info("!!!!!!!!!!!!!!!!!!!! Got to getConnection. Is somewhere throwing a fit in here");
        if (conn == null || conn.isClosed() || !conn.isValid(60)) { //maybe change to lower than 60, like 4 seconds?
        	if(dataSource  instanceof HikariDataSource || dataSource instanceof SQLiteDataSource) {
                if((username.isEmpty() && password.isEmpty())) {
                	Bukkit.getServer().getLogger().info("!!!!!!!!!!!!!!!!!!!! Trying to connect to SQLite");
                	try {
						conn = dataSource.getConnection();
					} catch (SQLException e) {
						Bukkit.getServer().getLogger().info("!!!!!!!!!!!!!!!!!!!! dataSource.getConnection() is throwing a fit");
					}
                } else {
                	Bukkit.getServer().getLogger().info("!!!!!!!!!!!!!!!!!!!! Trying to connect to MySQL");
                	conn = dataSource.getConnection(username, password);
                }              	
        	}else {
        		conn = (username.isEmpty() && password.isEmpty()) ? DriverManager.getConnection(url) : DriverManager.getConnection(url, username, password);
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
    		this.conn = dataSource.getConnection();
            if (conn==null) {
                throw new SQLException("Couldn't connect to the database");
            }
            String qry = "CREATE TABLE IF NOT EXISTS `" + tableName + "` (`uuid` VARCHAR(64) NOT NULL PRIMARY KEY);";
            Statement stmt = this.conn.createStatement();
            stmt.execute(qry);
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
    	/*
 		if (isConnected()) {
 			try {
 	        	PreparedStatement ps = conn.prepareStatement("CREATE TABLE IF NOT EXISTS " + tableName + " (`uuid` VARCHAR(64) NOT NULL PRIMARY KEY");
 		 			ps.executeUpdate();
 	        } catch (SQLException e) {
 	        	Bukkit.getServer().getLogger().info("It's throwing E on the create table catch");
 	        	throw e;
 	        }
 		}*/      
    }
    
    private void deleteTable(String tableName){
    	if (isConnected()) {
			try {
				PreparedStatement statement = conn.prepareStatement("DROP TABLE IF EXISTS " + tableName);
				statement.executeUpdate();
				System.out.println(tableName + " table deleted successfully.");
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
    
    private List<String> getTableColumnNames(String tableName) throws SQLException {
    	if (isConnected()) {
    		try {
                DatabaseMetaData metaData = conn.getMetaData();

                // Get columns of the specified table
                ResultSet resultSet = metaData.getColumns(null, null, tableName, null);
                resultSet.last();
                int columnCount = resultSet.getRow();
                resultSet.beforeFirst();

                if (columnCount > 0) {
                    String[] columnNames = new String[columnCount];
                    int index = 0;

                    while (resultSet.next()) {
                        columnNames[index++] = resultSet.getString("COLUMN_NAME");
                    }

                    return new ArrayList<String>(Arrays.asList(columnNames));
                } else {
                    return null; // Table not found or no columns
                }
            } catch (SQLException e) {
                throw e;
            }
    	}
        return null;
    }
    
    private List<String> getSQLiteColumnNames(String tableName) {
        List<String> columnNames = new ArrayList<>();
        // SQL query to retrieve column names
        String query = "PRAGMA table_info(" + tableName + ")";
        if(isConnected()) {
        	try {
            	Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                while (rs.next()) {
                    String columnName = rs.getString("name");
                    columnNames.add(columnName);
                }
            }catch (SQLException e) {
            	Bukkit.getServer().getLogger().info("!!!!!!!!!!!!!!!!!!!! Got to getConnection. Is somewhere throwing a fit in getSQLiteColumNames");
            }
        }             
        return columnNames;
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
