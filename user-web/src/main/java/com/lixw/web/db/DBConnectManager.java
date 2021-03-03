package com.lixw.web.db;

/**
 * @author lixw
 * @date 2021/03/02
 */
public class DBConnectManager {

    public static final String DROP_TABLE_USERS = "DROP TABLE users";
    public static final String CREATE_TABLE_USERS = "CREATE TABLE users(" +
            "id INT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), " +
            "name VARCHAR(16) NOT NULL, " +
            "password VARCHAR(64) NOT NULL, " +
            "email VARCHAR(64) NOT NULL, " +
            "phoneNumber VARCHAR(64) NOT NULL" +
            ")";

}
