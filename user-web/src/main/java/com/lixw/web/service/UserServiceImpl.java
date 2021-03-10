package com.lixw.web.service;

import com.lixw.web.domain.User;
import com.lixw.web.orm.jpa.DelegatingEntityManager;
import org.apache.derby.jdbc.EmbeddedDataSource;

import javax.annotation.Resource;
import javax.sql.DataSource;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author lixw
 * @date 2021/03/03
 */
public class UserServiceImpl implements UserService {

    @Override
    public boolean save(User user) {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        Set<ConstraintViolation<User>> violations = validatorFactory.getValidator().validate(user);
        violations.forEach(userConstraintViolation -> {
            throw new InvalidParameterException(userConstraintViolation.getMessage());
        });
        DelegatingEntityManager entityManager = new DelegatingEntityManager();
        entityManager.setPersistenceUnitName("emf");
        entityManager.setPersistenceLocation("jpa.datasource.properties");
        entityManager.init();
        entityManager.persist(user);

        User findResult = entityManager.find(User.class, 1L);
        System.out.println("findResult = " + findResult);
        entityManager.onDestroy();
        return true;

//        UserRepository userRepository = new UserRepositoryImpl();
//         userRepository.drop();
//        userRepository.createTable();
//        boolean b = userRepository.save(user);
//        if (b) {
//            userRepository.findUserByName(user.getName());
//            return true;
//        }
//        return false;
    }
}
