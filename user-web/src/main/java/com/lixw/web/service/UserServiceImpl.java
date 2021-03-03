package com.lixw.web.service;

import com.lixw.web.db.DBConnectManager;
import com.lixw.web.domain.User;
import com.lixw.web.repository.UserRepository;
import com.lixw.web.repository.UserRepositoryImpl;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.sql.*;
import java.util.Properties;

/**
 * @author lixw
 * @date 2021/03/03
 */
public class UserServiceImpl implements UserService {

    @Override
    public boolean save(User user) {
        UserRepository userRepository = new UserRepositoryImpl();
        userRepository.drop();
        userRepository.createTable();
        boolean b = userRepository.save(user);
        if (b) {
            userRepository.findUserByName(user.getName());
            return true;
        }
        return false;
    }
}
