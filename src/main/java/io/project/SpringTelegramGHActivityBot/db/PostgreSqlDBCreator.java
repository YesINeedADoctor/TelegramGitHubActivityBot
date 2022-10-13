package io.project.SpringTelegramGHActivityBot.db;

import io.project.SpringTelegramGHActivityBot.config.JdbcConfig;

import java.sql.*;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PostgreSqlDBCreator {

    private static final Logger LOGGER =
            Logger.getLogger(PostgreSqlDBCreator.class.getName());

    private JdbcConnection jdbcConnection;

    public PostgreSqlDBCreator(JdbcConfig jdbcConfig) {
        jdbcConnection = new JdbcConnection(jdbcConfig);
        LOGGER.log(Level.INFO, "[dbCreator] is created");
    }

    public void createDataBase() {
        Optional rawConnection = jdbcConnection.getConnection();
        Optional<Object> tableCreator = Optional.empty();
        LOGGER.log(Level.INFO, "[dbCreator] creating DataBase...");

        if (rawConnection.isPresent()) {
            Connection dbConnection = (Connection) rawConnection.get();
            String sql = """
                    CREATE DATABASE "GHTelegramActivityBot"
                        WITH
                        OWNER = root
                        ENCODING = 'UTF8'
                        LC_COLLATE = 'en_US.utf8'
                        LC_CTYPE = 'en_US.utf8'
                        TABLESPACE = pg_default
                        CONNECTION LIMIT = -1
                        IS_TEMPLATE = False;
                    """;

            try (dbConnection;
                 Statement statement = dbConnection.createStatement()) {
                int resultInt = statement.executeUpdate(sql);
                SQLWarning warn = statement.getWarnings();
                while(warn != null){
                    LOGGER.log(Level.INFO, "[dbCreator] " + warn);
                    warn = warn.getNextWarning();
                }
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, ex.getMessage());
            }
        }
    }
    public void createTables() {
        Optional rawConnection = jdbcConnection.getConnection();
        Optional<Object> tableCreator = Optional.empty();

        if (rawConnection.isPresent()) {
            Connection dbConnection = (Connection) rawConnection.get();
            String sql = """
                    CREATE TABLE IF NOT EXISTS public.telegram_chats
                    (
                        chat_id bigint NOT NULL,
                        repository_id bigint,
                        last_command character varying(50) COLLATE pg_catalog."default",
                        CONSTRAINT telegram_chats_pkey PRIMARY KEY (chat_id)
                    );
                    CREATE TABLE IF NOT EXISTS public.github_repositories
                    (
                        repository_id bigint NOT NULL,
                        name character varying(50) COLLATE pg_catalog."default",
                        fullname character varying(100) COLLATE pg_catalog."default",
                        description character varying(200) COLLATE pg_catalog."default",
                        url character varying(200) COLLATE pg_catalog."default",
                        CONSTRAINT github_repositories_pkey PRIMARY KEY (repository_id)
                    )
                    """;

            try (dbConnection;
                 Statement statement = dbConnection.createStatement()){
                int resultInt = statement.executeUpdate(sql);
                SQLWarning warn = statement.getWarnings();
                while(warn != null){
                    LOGGER.log(Level.INFO, "[dbCreator] " + warn);
                    warn = warn.getNextWarning();
                }
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, ex.getMessage());
            }
        }
    }
}