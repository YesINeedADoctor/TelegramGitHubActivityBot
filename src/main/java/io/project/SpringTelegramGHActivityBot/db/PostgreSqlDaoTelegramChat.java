package io.project.SpringTelegramGHActivityBot.db;

import io.project.SpringTelegramGHActivityBot.config.JdbcConfig;
import io.project.SpringTelegramGHActivityBot.data.Command;
import io.project.SpringTelegramGHActivityBot.data.TelegramChat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class PostgreSqlDaoTelegramChat implements Dao<TelegramChat> {
    private static final Logger LOGGER =
            Logger.getLogger(PostgreSqlDaoTelegramChat.class.getName());

    private JdbcConnection jdbcConnection;

    @Autowired
    public PostgreSqlDaoTelegramChat(JdbcConfig jdbcConfig) {
        jdbcConnection = new JdbcConnection(jdbcConfig);
    }

    @Override
    public Optional get(Long id) {
        Optional rawConnection = jdbcConnection.getConnection();
        Optional<Object> telegramChat = Optional.empty();

        if (rawConnection.isPresent()) {
            Connection dbConnection = (Connection) rawConnection.get();
            String sql = "SELECT * FROM telegram_chats WHERE chat_id = " + id;

            try (dbConnection;
                 Statement statement = dbConnection.createStatement();
                 ResultSet resultSet = statement.executeQuery(sql)) {

                if (resultSet.next()) {
                    Long repositoryId = resultSet.getLong("repository_id");
                    repositoryId = resultSet.wasNull() ? null : repositoryId;
                    String lastCommand = resultSet.getString("last_command");
                    telegramChat = Optional.of(
                            new TelegramChat.Builder(id)
                                    .setRepositoryId(repositoryId)
                                    .setLastCommand(Command.valueOf(lastCommand))
                                    .build());

                    LOGGER.log(Level.INFO, "Found {0} in database", telegramChat.get());
                }
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        }
        return telegramChat;
    }

    @Override
    public Collection getAll() {
        Optional rawConnection = jdbcConnection.getConnection();
        Collection telegramChats = new ArrayList<>();

        if (rawConnection.isPresent()) {
            Connection dbConnection = (Connection) rawConnection.get();
            String sql = "SELECT * FROM telegram_chats";

            try (dbConnection;
                 Statement statement = dbConnection.createStatement();
                 ResultSet resultSet = statement.executeQuery(sql)) {

                while (resultSet.next()) {
                    Long id = resultSet.getLong("chat_id");
                    Long repositoryId = resultSet.getLong("repository_id");

                    TelegramChat telegramChat = new TelegramChat.Builder(id).setRepositoryId(repositoryId).build();

                    telegramChats.add(telegramChat);

                    LOGGER.log(Level.INFO, "Found {0} in database", telegramChat);
                }
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        }
        return telegramChats;
    }

    @Override
    public Optional save(TelegramChat telegramChat) {
        Optional rawConnection = jdbcConnection.getConnection();
        Optional<Object> generatedId = Optional.empty();
        String message = "The telegramChat to be added should not be null";
        TelegramChat nonNullTelegramChat = Objects.requireNonNull(telegramChat, message);

        if (rawConnection.isPresent()) {
            Connection dbConnection = (Connection) rawConnection.get();
            String sql = "INSERT INTO "
                    + "telegram_chats(chat_id, repository_id, last_command) "
                    + "VALUES(?, ?, ?) ON CONFLICT (chat_id) DO UPDATE SET "
                    + "repository_id = ?, "
                    + "last_command = ?";

            try (dbConnection;
                 PreparedStatement statement =
                         dbConnection.prepareStatement(
                                 sql,
                                 Statement.RETURN_GENERATED_KEYS)) {

                statement.setLong(1, nonNullTelegramChat.getId());
                statement.setObject(2, nonNullTelegramChat.getRepositoryId());
                statement.setString(3, nonNullTelegramChat.getLastCommand().toString());
                statement.setObject(4, nonNullTelegramChat.getRepositoryId());
                statement.setString(5, nonNullTelegramChat.getLastCommand().toString());
                int numberOfInsertedRows = statement.executeUpdate();

//                 Retrieve the auto-generated id
                if (numberOfInsertedRows > 0) {
                    try (ResultSet resultSet = statement.getGeneratedKeys()) {
                        if (resultSet.next()) {
                            generatedId = Optional.of(resultSet.getLong(1));
                        }
                    }
                }

                LOGGER.log(
                        Level.INFO,
                        "{0} created successfully? {1}",
                        new Object[]{nonNullTelegramChat,
                                (numberOfInsertedRows > 0)});
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        }
        return generatedId;
    }

    @Override
    public void update(TelegramChat telegramChat) {
        Optional rawConnection = jdbcConnection.getConnection();
        String message = "The repository to be updated should not be null";
        TelegramChat nonNullTelegramChat = Objects.requireNonNull(telegramChat, message);

        if (rawConnection.isPresent()) {
            Connection dbConnection = (Connection) rawConnection.get();
            String sql = "UPDATE telegram_chats "
                    + "SET "
                    + "repository_id = ?, "
                    + "last_command = ? "
                    + "WHERE "
                    + "chat_id = ?";

            try (dbConnection;
                 PreparedStatement statement = dbConnection.prepareStatement(sql)) {

                statement.setObject(1, nonNullTelegramChat.getRepositoryId());
                statement.setString(2, nonNullTelegramChat.getLastCommand().toString());
                statement.setLong(3, nonNullTelegramChat.getId());

                int numberOfUpdatedRows = statement.executeUpdate();

                LOGGER.log(Level.INFO, "Was the telegram_chats updated successfully? {0}",
                        numberOfUpdatedRows > 0);

            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void delete(TelegramChat telegramChat) {
        Optional rawConnection = jdbcConnection.getConnection();
        String message = "The telegramChat to be deleted should not be null";
        TelegramChat nonNullTelegramChat = Objects.requireNonNull(telegramChat, message);

        if (rawConnection.isPresent()) {
            Connection dbConnection = (Connection) rawConnection.get();
            String sql = "DELETE FROM telegram_chats WHERE chat_id = ?";

            try (dbConnection;
                 PreparedStatement statement = dbConnection.prepareStatement(sql)) {

                statement.setLong(1, nonNullTelegramChat.getId());

                int numberOfDeletedRows = statement.executeUpdate();

                LOGGER.log(Level.INFO, "Was the telegramChat deleted successfully? {0}",
                        numberOfDeletedRows > 0);

            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        }
    }
}