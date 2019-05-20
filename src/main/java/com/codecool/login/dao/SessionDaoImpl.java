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
                + "WHERE id = ?";
        try (Connection connection = DatabaseConnector.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            setRequiredStatements(preparedStatement, session);
            preparedStatement.setInt(4, session.getId());

            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Failed to update session.\n" + e.getMessage());
        }
    }

    @Override
    public List<Session> getSessions() throws DaoException {
        List<Session> sessions;
        String query = "SELECT id, user_name, user_password, session_id "
                + "FROM sessions";
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            sessions = getSessionsFrom(preparedStatement);

        } catch (SQLException e) {
            throw  new DaoException("Failed to get sessions.\n" + e.getMessage());
        }
        return sessions;
    }

    private void setRequiredStatements(PreparedStatement preparedStatement, Session session) throws  SQLException {
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
            while(resultSet.next()) {
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