package me.blubdalegend.piggyback.config;

public enum DatabaseType
{
    H2("org.h2.Driver"),  
    MYSQL("com.mysql.cj.jdbc.Driver"),  
    POSTGRE("org.postgresql.Driver"),  
    SQLITE("org.sqlite.JDBC");
    
    public final String driver;
    //private boolean loaded = false;

    DatabaseType(String driver) {
        this.driver = driver;
    }

    public static DatabaseType match(String driver) {
        for (DatabaseType type : DatabaseType.values()) {
            if (type.name().equalsIgnoreCase(driver)) {
                return type;
            }
        }
        return null;
    }
}
