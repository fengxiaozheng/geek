package com.lixw.web.listener;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.Set;

/**
 * @author lixw
 * @date 2021/03/24
 */
public class ComponentContextInit implements ServletContainerInitializer {
    @Override
    public void onStartup(Set<Class<?>> set, ServletContext servletContext) throws ServletException {
        servletContext.addListener(ComponentContextListener.class);
    }
}
