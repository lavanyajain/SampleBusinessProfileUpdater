package com.intuit.interview.businessprofile.library;

import com.intuit.interview.businessprofile.exception.QueryExecutionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.*;

@Component
public class QueryExecutor {
    static private Connection connection;
    static private Statement statement;
    @Value("${business.profile.validator.database.driver}")
    private String JDBC_DRIVER;
    @Value("${business.profile.validator.database.url}")
    private String DB_URL;
    @Value("${business.profile.validator.database.username}")
    private String USER_NAME;
    @Value("${business.profile.validator.database.password}")
    private String PASSWORD;

    QueryExecutor(@Value("${business.profile.validator.database.driver}")
                          String JDBC_DRIVER, @Value("${business.profile.validator.database.url}")
                          String DB_URL, @Value("${business.profile.validator.database.username}")
                          String USER_NAME, @Value("${business.profile.validator.database.password}")
                          String PASSWORD) {
        try {
            connection = DriverManager.getConnection(DB_URL, USER_NAME, PASSWORD);
            statement = connection.createStatement();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public static Connection getConnection() {
        return connection;
    }

    public static Statement getStatement() {
        return statement;
    }

    static public ResultSet executeQuery(String query) {
        try {
            return statement.executeQuery(query);
        } catch (SQLException exception) {
            throw new QueryExecutionException(exception.getMessage());
        }
    }

    static public Integer executeUpdate(String query) {
        try {
            return statement.executeUpdate(query);
        } catch (SQLException exception) {
            throw new QueryExecutionException(exception.getMessage());
        }
    }
}
