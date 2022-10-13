package io.project.SpringTelegramGHActivityBot.db;

import io.project.SpringTelegramGHActivityBot.config.JdbcConfig;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;
import java.util.logging.Logger;

public class JdbcConnection {
    private static final Logger LOGGER = Logger.getLogger(JdbcConnection.class.getName());
    private Optional connection;

    private JdbcConfig config;

    public JdbcConnection(JdbcConfig config){
        this.config = config;
    }

    public Optional getConnection() {
        String url = config.getDbURL();
        String user = config.getDbUser();
        String password = config.getDbPassword();
        connection = Optional.empty();
        try {
            connection = Optional.ofNullable(
                    DriverManager.getConnection(url, user, password)
            );
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return connection;
    }
}