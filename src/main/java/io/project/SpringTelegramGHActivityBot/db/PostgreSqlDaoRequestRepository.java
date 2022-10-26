package io.project.SpringTelegramGHActivityBot.db;

import io.project.SpringTelegramGHActivityBot.config.JdbcConfig;
import io.project.SpringTelegramGHActivityBot.data.RequestRepository;
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
public class PostgreSqlDaoRequestRepository implements Dao<RequestRepository> {

    private static final Logger LOGGER = Logger.getLogger(PostgreSqlDaoRequestRepository.class.getName());

    private JdbcConnection jdbcConnection;

    @Autowired
    public PostgreSqlDaoRequestRepository(JdbcConfig jdbcConfig) {
        jdbcConnection = new JdbcConnection(jdbcConfig);
    }

    @Override
    public Optional get(Long id) {
//        Optional connection = JdbcConnection.getConnection();
//        return connection.flatMap(conn -> {});
//        Optional connection = Optional.of(new Object());

        Optional rawConnection = jdbcConnection.getConnection();
        Optional<Object> repository = Optional.empty();

        if (rawConnection.isPresent()) {
            //effectively final dbConnection will be auto-closed at the end of the try block
            Connection dbConnection = (Connection) rawConnection.get();
            String sql = "SELECT * FROM github_repositories WHERE repository_id = " + id;

            try (dbConnection; Statement statement = dbConnection.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {

                if (resultSet.next()) {
                    String name = resultSet.getString("name");
                    String fullname = resultSet.getString("fullname");
                    String url = resultSet.getString("url");
                    String description = resultSet.getString("description");

                    repository = Optional.of(new RequestRepository.Builder(id).setName(name)
                            .setFullName(fullname)
                            .setDescription(description)
                            .setHTML_URL(url)
                            .build());

                    LOGGER.log(Level.INFO, "Found {0} in database", repository.get());
                }
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
            return repository;
        } else return Optional.empty();
    }

    @Override
    public Collection getAll() {
        Optional rawConnection = jdbcConnection.getConnection();
        Collection repositories = new ArrayList<>();
        if (rawConnection.isPresent()) {
            Connection dbConnection = (Connection) rawConnection.get();
            String sql = "SELECT * FROM github_repositories";

            try (dbConnection; Statement statement = dbConnection.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {

                while (resultSet.next()) {
                    Long id = resultSet.getLong("repository_id");
                    String name = resultSet.getString("name");
                    String fullname = resultSet.getString("fullname");
                    String description = resultSet.getString("description");
                    String url = resultSet.getString("url");

                    RequestRepository repository = new RequestRepository.Builder(id).setName(name)
                            .setFullName(fullname)
                            .setDescription(description)
                            .setHTML_URL(url)
                            .build();

                    repositories.add(repository);

                    LOGGER.log(Level.INFO, "Found {0} in database", repository);
                }
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        }
        return repositories;
    }

    @Override
    public Optional save(RequestRepository requestRepository) {
        Optional rawConnection = jdbcConnection.getConnection();
        Optional<Object> generatedId = Optional.empty();
        String message = "The repository to be added should not be null";
        RequestRepository nonNullRepository = Objects.requireNonNull(requestRepository, message);

        if (rawConnection.isPresent()) {
            Connection dbConnection = (Connection) rawConnection.get();
            String sql =
                    "INSERT INTO " + "github_repositories(repository_id, name, fullname, description, url) "
                            + "VALUES(?, ?, ?, ?, ?) ON CONFLICT (repository_id) DO UPDATE SET "
                            + "name = ?, fullname = ?, description = ?, url = ?";

            try (dbConnection; PreparedStatement statement = dbConnection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                statement.setLong(1, nonNullRepository.getId());
                statement.setString(2, String.valueOf(nonNullRepository.getName()));
                statement.setString(3, String.valueOf(nonNullRepository.getFullName()));
                statement.setString(4, String.valueOf(nonNullRepository.getDescription()));
                statement.setString(5, String.valueOf(nonNullRepository.getHTML_URL()));
                statement.setString(6, String.valueOf(nonNullRepository.getName()));
                statement.setString(7, String.valueOf(nonNullRepository.getFullName()));
                statement.setString(8, String.valueOf(nonNullRepository.getDescription()));
                statement.setString(9, String.valueOf(nonNullRepository.getHTML_URL()));

                int numberOfInsertedRows = statement.executeUpdate();

//                 Retrieve the auto-generated id
                if (numberOfInsertedRows > 0) {
                    try (ResultSet resultSet = statement.getGeneratedKeys()) {
                        if (resultSet.next()) {
                            generatedId = Optional.of(resultSet.getLong(1));
                        }
                    }
                }

                LOGGER.log(Level.INFO, "{0} created successfully? {1}", new Object[]{nonNullRepository, (numberOfInsertedRows > 0)});
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        }
        return generatedId;
    }

    @Override
    public void update(RequestRepository requestRepository) {
        Optional rawConnection = jdbcConnection.getConnection();
        String message = "The repository to be updated should not be null";
        RequestRepository nonNullRepository = Objects.requireNonNull(requestRepository, message);

        if (rawConnection.isPresent()) {
            Connection dbConnection = (Connection) rawConnection.get();
            String sql = "UPDATE github_repositories "
                    + "SET "
                    + "name = ?, fullname = ?, "
                    + "description = ?, url = ? "
                    + "WHERE repository_id = ?";

            try (dbConnection; PreparedStatement statement = dbConnection.prepareStatement(sql)) {

                statement.setString(1, String.valueOf(nonNullRepository.getName()));
                statement.setString(2, String.valueOf(nonNullRepository.getFullName()));
                statement.setString(3, String.valueOf(nonNullRepository.getDescription()));
                statement.setString(4, String.valueOf(nonNullRepository.getHTML_URL()));
                statement.setLong(5, nonNullRepository.getId());

                int numberOfUpdatedRows = statement.executeUpdate();

                LOGGER.log(Level.INFO, "Was the github_repositories updated successfully? {0}", numberOfUpdatedRows > 0);

            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void delete(RequestRepository requestRepository) {
        Optional rawConnection = jdbcConnection.getConnection();
        String message = "The repository to be deleted should not be null";
        RequestRepository nonNullRepository = Objects.requireNonNull(requestRepository, message);
        String sql = "DELETE FROM github_repositories WHERE repository_id = ?";

        if (rawConnection.isPresent()) {
            Connection dbConnection = (Connection) rawConnection.get();

            try (dbConnection; PreparedStatement statement = dbConnection.prepareStatement(sql)) {

                statement.setLong(1, nonNullRepository.getId());

                int numberOfDeletedRows = statement.executeUpdate();

                LOGGER.log(Level.INFO, "Was the repository deleted successfully? {0}", numberOfDeletedRows > 0);

            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        }
    }
}