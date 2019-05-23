package com.codecool.login.dao;

import java.util.ArrayList;
import java.util.List;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.codecool.login.model.Session;

public class SessionDaoImpl implements Dao {

    @Override
    public int create(Session session) throws DaoException {
        String query = "INSERT INTO sessions "
                + "(user_name, user_password, session_id) "
                + "VALUES(?, ?, ?)";
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            setRequiredStatements(preparedStatement, session);

            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Failed to create session.\n" + e.getMessage());
        }
    }

    @Override
    public int update(Session session) throws DaoException {
        String query = "UPDATE sessions "
                + "SET user_name = ?, user_password = ?, session_id = ? "
                + "WHERE user_name = ? AND user_password = ?";
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            setRequiredStatements(preparedStatement, session);
            preparedStatement.setString(4, session.getUserName());
            preparedStatement.setString(5, session.getUserPassword());

            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Failed to update session.\n" + e.getMessage());
        }
    }

    @Override
    public List<Session> getSessions(String sessionId) throws DaoException {
        List<Session> sessions;
        String query = "SELECT id, user_name, user_password, session_id "
                + "FROM sessions WHERE session_id = ?";
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, sessionId);
            sessions = getSessionsFrom(preparedStatement);

        } catch (SQLException e) {
            throw new DaoException("Failed to get session by session id.\n" + e.getMessage());
        }
        return sessions;
    }

    @Override
    public List<Session> getSessions(String userName, String userPassword) throws DaoException {
        List<Session> sessions;
        String query = "SELECT id, user_name, user_password, session_id "
                + "FROM sessions WHERE user_name = ? AND user_password = ?";
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, userName);
            preparedStatement.setString(2, userPassword);
            sessions = getSessionsFrom(preparedStatement);

        } catch (SQLException e) {
            throw new DaoException("Failed to get session by user.\n" + e.getMessage());
        }
        return sessions;
    }

    @Override
    public int delete(String sessionId) throws DaoException {
        String query = "UPDATE sessions "
                + "SET session_id = null WHERE session_id = ?";
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, sessionId);
            return preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new DaoException("Failed to delete session id.\n" + e.getMessage());
        }
    }

    private void setRequiredStatements(PreparedStatement preparedStatement, Session session) throws SQLException {
        preparedStatement.setString(1, session.getUserName());
        preparedStatement.setString(2, session.getUserPassword());
        preparedStatement.setString(3, session.getSessionId());
    }

    private List<Session> getSessionsFrom(PreparedStatement preparedStatement) throws DaoException {
        List<Session> sessions = new ArrayList<>();
        Session session;

        String userName;
        String userPassword;
        String sessionId;
        int id;

        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                userName = resultSet.getString("user_name");
                userPassword = resultSet.getString("user_password");
                sessionId = resultSet.getString("session_id");
                id = resultSet.getInt("id");

                session = new Session.Builder()
                        .withId(id)
                        .withUserName(userName)
                        .withUserPassword(userPassword)
                        .withSessionId(sessionId)
                        .build();

                sessions.add(session);
            }
        } catch (SQLException e) {
            throw new DaoException("Failed to populate the list of sessions.\n" + e.getMessage());
        }
        return sessions;
    }
}