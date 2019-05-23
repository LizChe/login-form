package com.codecool.login.dao;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.SQLException;

class DatabaseConnector {

    private static BasicDataSource dataSource = new BasicDataSource();

    private DatabaseConnector() {

    }

    static {
        dataSource.setUrl("jdbc:postgresql://localhost:5432/login");
        dataSource.setUsername("postgres");
        dataSource.setPassword("123");

        dataSource.setMinIdle(5);
        dataSource.setMaxIdle(10);
        dataSource.setMaxOpenPreparedStatements(100);
    }

    static Connection getConnection() throws SQLException {
        return  dataSource.getConnection();
    }
}