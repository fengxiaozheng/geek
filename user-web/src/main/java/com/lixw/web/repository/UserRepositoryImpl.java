package com.lixw.web.repository;

import com.lixw.web.db.DBConnectManager;
import com.lixw.web.domain.User;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * @author lixw
 * @date 2021/03/02
 */
public class UserRepositoryImpl implements UserRepository {

    private  DataSource dataSource;
    private Statement statement;
    private Connection connection;


    public UserRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public UserRepositoryImpl() {
//        this.dataSource = initDataSource();
//        if (dataSource != null) {
//            try {
//                connection = dataSource.getConnection();
//                statement = connection.createStatement();
//            } catch (SQLException troubles) {
//                troubles.printStackTrace();
//            }



        String databaseUrl = "jdbc:derby:/Users/lixw/opt/db/user-platform;create=true";
        try {


//            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
//            Driver driver = DriverManager.getDriver("jdbc:derby:/Users/lixw/opt/db/user-platform;create=true");
//            connection = driver.connect("jdbc:derby:/Users/lixw/opt/db/user-platform;create=true", new Properties());

            connection = DriverManager.getConnection(databaseUrl);
            statement = connection.createStatement();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
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
            String sql = "SELECT id, name, password, email, phoneNumber FROM users WHERE name = '" + username + "'";
            System.out.println("sql = " + sql);
            resultSet = statement.executeQuery(sql);
            BeanInfo beanInfo = null;
            try {
                 beanInfo = Introspector.getBeanInfo(User.class, Object.class);
            } catch (IntrospectionException e) {
                e.printStackTrace();
            }
            if (resultSet.next()) {
                if (beanInfo == null) {
                    String name = resultSet.getString("name");
                    String password = resultSet.getString("password");
                    String email = resultSet.getString("email");
                    String phone = resultSet.getString("phoneNumber");
                    Long id = resultSet.getLong("id");

                    User user = new User();
                    user.setId(id);
                    user.setName(name);
                    user.setPassword(password);
                    user.setEmail(email);
                    user.setPhoneNumber(phone);
                    System.out.println("user = " + user);
                    return user;
                } else {
                    User user = new User();
                    for (PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors()) {
                        String propertyName = propertyDescriptor.getName();
                        Class<?> propertyType = propertyDescriptor.getPropertyType();
                        //根据类型获取方法名
                        String methodName = typeMethodMappings.get(propertyType);
                        Method method = ResultSet.class.getMethod(methodName, String.class);
                        // 通过放射调用 getXXX(String) 方法
                        Object resultValue = method.invoke(resultSet, propertyName);
                        // 获取 User 类 Setter方法
                        // PropertyDescriptor ReadMethod 等于 Getter 方法
                        // PropertyDescriptor WriteMethod 等于 Setter 方法
                        Method writeMethod = propertyDescriptor.getWriteMethod();
                        //姜值写入user
                        writeMethod.invoke(user, resultValue);
                    }
                    System.out.println(" invoke user = " + user);
                    return user;
                }
            }
        } catch (Exception throwable) {
            throwable.printStackTrace();
            return null;
        }
        return null;
    }

    @Override
    public boolean save(User user) {
        try {
            String sql = "INSERT INTO users(name, password, email, phoneNumber) VALUES ('"+user.getName()
                    +"', '"+ user.getPassword() +"', '" + user.getEmail() +"', '" + user.getPhoneNumber() + "')";
            System.out.println("sql = " + sql);
            int i = statement.executeUpdate(sql);
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

    /**
     * 数据类型与 ResultSet 方法名映射
     */
    static Map<Class, String> typeMethodMappings = new HashMap<>();

    static {
        typeMethodMappings.put(Long.class, "getLong");
        typeMethodMappings.put(String.class, "getString");
    }
}
