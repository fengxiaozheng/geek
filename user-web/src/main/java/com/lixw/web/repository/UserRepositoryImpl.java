package com.lixw.web.repository;

import com.lixw.web.db.DBConnectManager;
import com.lixw.web.domain.User;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

/**
 * @author lixw
 * @date 2021/03/02
 */
public class UserRepositoryImpl implements UserRepository {

    private final DataSource dataSource;
    private Statement statement;
    private Connection connection;

    public UserRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public UserRepositoryImpl() {
        this.dataSource = initDataSource();
        if (dataSource != null) {
            try {
                connection = dataSource.getConnection();
                statement = connection.createStatement();
            } catch (SQLException troubles) {
                troubles.printStackTrace();
            }
        }
    }

    private DataSource initDataSource() {
        try {
            Context context = new InitialContext();
            return (DataSource) context.lookup("java:comp/env/jdbc/UserPlatformDB");
        } catch (NamingException e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public User findUserByName(String username) {
        ResultSet resultSet;
        try {
            resultSet = statement.executeQuery("SELECT name FROM users WHERE name = " + username);
            if (resultSet.next()) {

            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
            return null;
        }
        return null;
    }

    @Override
    public boolean save(User user) {
        try {
            int i = statement.executeUpdate("INSERT INTO users(name, password, email, phoneNumber) VALUES ("+user.getName()
                    +", "+ user.getPassword() +", " + user.getEmail() +", " + user.getPhoneNumber() + ")");
            return i > 0;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }

    @Override
    public void drop() {
        try {
            statement.execute(DBConnectManager.DROP_TABLE_USERS);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public void createTable() {
        try {
            statement.execute(DBConnectManager.CREATE_TABLE_USERS);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
