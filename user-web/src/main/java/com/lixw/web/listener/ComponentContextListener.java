package com.lixw.web.listener;

import com.lixw.di.ComponentContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @author lixw
 * @date 2021/03/24
 */
public class ComponentContextListener implements ServletContextListener {

    private ServletContext servletContext;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        this.servletContext = servletContextEvent.getServletContext();
        ComponentContext.getInstance().init(servletContext);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
