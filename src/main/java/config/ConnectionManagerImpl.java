package config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

public class ConnectionManagerImpl implements ConnectionManager {

    private static final HikariConfig config = new HikariConfig("/db.properties");
    private static HikariDataSource ds;
    private static ConnectionManager instance;

    private static HikariDataSource remoteDs;
    private static boolean isRemote = false;

    private ConnectionManagerImpl() {
        if (!isRemote) {
            ds  = new HikariDataSource(config);
        }
    }

    public static ConnectionManager getInstance() {
        if (instance == null) {
            instance = new ConnectionManagerImpl();
        }
        return instance;
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (isRemote) {
            return remoteDs.getConnection();
        }
        return ds.getConnection();
    }

    public static void setRemote(String url) {
        HikariConfig config = new HikariConfig("/db.properties");
        if (Objects.nonNull(url) && !url.isEmpty()) {
            config.setJdbcUrl(url);
        }
        remoteDs = new HikariDataSource(config);
        isRemote = true;
    }

    public static void setLocal() {
        isRemote = false;
    }
}
