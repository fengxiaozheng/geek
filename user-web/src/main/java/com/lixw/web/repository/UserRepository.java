package com.lixw.web.repository;

import com.lixw.web.domain.User;

/**
 * 委派实现（静态aop实现）
 * @author lixw
 * @date 2021/03/02
 */
public interface UserRepository {

    User findUserByName(String username);

    boolean save(User user);

    void drop();

    void createTable();
}
