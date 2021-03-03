package com.lixw.web.service;

import com.lixw.web.domain.User;
import com.lixw.web.repository.UserRepository;
import com.lixw.web.repository.UserRepositoryImpl;

/**
 * @author lixw
 * @date 2021/03/03
 */
public class UserServiceImpl implements UserService {

    @Override
    public boolean save(User user) {
        UserRepository userRepository = new UserRepositoryImpl();
        userRepository.createTable();
        return userRepository.save(user);
    }
}
