package com.codecool.login.dao;

import com.codecool.login.model.Session;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SessionDao implements Dao {

    @Override
    public int create(Session session) {
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
    public int update(Session session) {
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

    private void setRequiredStatements(PreparedStatement preparedStatement, Session session) throws  SQLException {
        preparedStatement.setString(1, session.getUserName());
        preparedStatement.setString(2, session.getUserPassword());
        preparedStatement.setString(3, session.getSessionId());
    }
}