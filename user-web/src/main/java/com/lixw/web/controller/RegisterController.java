package com.lixw.web.controller;

import com.geek.lixw.controller.PageController;
import com.lixw.di.ComponentContext;
import com.lixw.web.domain.User;
import com.lixw.web.service.UserService;
import com.lixw.web.service.UserServiceImpl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

/**
 * @author lixw
 * @date 2021/03/02
 */
public class RegisterController implements PageController {

    @Path("/signUp")
    @POST
    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws Throwable {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");

//        String databaseUrl = "jdbc:derby:/db/user-platform;create=true";
//        Connection connection = DriverManager.getConnection(databaseUrl);
//        Statement statement = connection.createStatement();
////        ResultSet resultSet = statement.executeQuery("");
//        Introspector.getBeanInfo(User.class, Object.class);

        ComponentContext componentContext = ComponentContext.getInstance();
        componentContext.init(request.getServletContext());
        UserService userService = new UserServiceImpl();
        User user = new User();
        user.setName(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setPhoneNumber(phone);
        userService.save(user);
        return "success.jsp";
    }
}
