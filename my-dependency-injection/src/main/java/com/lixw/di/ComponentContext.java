package com.lixw.di;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.naming.*;
import javax.servlet.ServletContext;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * @author lixw
 * @date 2021/03/24
 */
public class ComponentContext {

    public static final String CONTEXT_NAME = ComponentContext.class.getSimpleName();

    private static final String JNDI_ENV_CONTEXT_NAME = "java:comp/env";

    private static final Logger logger = Logger.getLogger(ComponentContext.class.getSimpleName());

    private static ServletContext servletContext;

    private static ComponentContext componentContext;

    private Context envContext;

    private ClassLoader classLoader;

    private final Map<String, Object> componentMaps = new LinkedHashMap<>();

    public static ComponentContext getInstance() {
        return componentContext == null ? new ComponentContext() : componentContext;
    }

    private ComponentContext() {
        if (componentContext == null) {
            componentContext = this;
        }
    }

    public void init(ServletContext servletContext) {
        ComponentContext.servletContext = servletContext;
        // 获取当前 ServletContext（WebApp）ClassLoader
        this.classLoader = servletContext.getClassLoader();
        initEnvContext();
        instantiateComponents();
        initializeComponents();
    }

    /**
     * 初始化组件
     */
    private void initializeComponents() {
        componentMaps.values().forEach(component -> {
            Class<?> componentClass = component.getClass();
            //注入组件，@Resource
            injectComponents(component, componentClass);
            //初始化组件，@PostConstruct
            processPostConstruct(component, componentClass);
        });
    }

    private void processPostConstruct(Object component, Class<?> componentClass) {
        //获取方法
        Stream.of(componentClass.getMethods())
                .filter(method -> !Modifier.isStatic(method.getModifiers())//非静态方法
                        && method.getParameterCount() == 0 //无参
                        && method.isAnnotationPresent(PostConstruct.class) //方法标有@PostConstruct注解
                ).forEach(method -> {
            // 执行目标方法
            try {
                method.invoke(component);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        });
    }

    private void injectComponents(Object component, Class<?> componentClass) {
        Arrays.stream(componentClass.getDeclaredFields())//获取所有字段
                .filter(field -> {
                    int modifiers = field.getModifiers();//获取字段的修饰符（public、private）
                    return !Modifier.isStatic(modifiers)//非静态
                            && field.isAnnotationPresent(Resource.class);//resource注解的字段
                }).forEach(field -> {
            Resource resource = field.getAnnotation(Resource.class);
            String resourceName = resource.name();
            //查找
            Object injectedObject = lookupComponent(resourceName);
            field.setAccessible(true);
            try {
                //注入
                field.set(component, injectedObject);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
    }


    /**
     * 组件实例化
     */
    private void instantiateComponents() {
        List<String> componentNames = listAllComponentNames();
        // 通过依赖查找，实例化对象（ Tomcat BeanFactory setter 方法的执行，仅支持简单类型）
        componentNames.forEach(s -> componentMaps.put(s, lookupComponent(s)));
    }

    public  <C> C lookupComponent(String name) {
        try {
            return (C) envContext.lookup(name);
        } catch (NamingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<String> listAllComponentNames() {
        return listAllComponentNames("/");
    }

    private List<String> listAllComponentNames(String name) {
        NamingEnumeration<NameClassPair> list = null;
        try {
            list = envContext.list(name);
        } catch (NamingException e) {
            e.printStackTrace();
        }
        // 目录 - Context
        // 节点 -
        if (list == null) {// 当前 JNDI 名称下没有子节点
            return  Collections.emptyList();
        }
        List<String> fullNames = new LinkedList<>();
        while (list.hasMoreElements()) {
            NameClassPair nameClassPair = list.nextElement();
            String className = nameClassPair.getClassName();
            Class<?> aClass;
            try {
                aClass = classLoader.loadClass(className);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
            if (Context.class.isAssignableFrom(aClass)) {
                // 如果当前名称是目录（Context 实现类）的话，递归查找
                fullNames.addAll(listAllComponentNames(nameClassPair.getName()));
            } else {
                // 否则，当前名称绑定目标类型的话话，添加该名称到集合中
                String fullName = name.startsWith("/") ?
                        nameClassPair.getName() : name + "/" + nameClassPair.getName();
                fullNames.add(fullName);
            }

        }
        return fullNames;
    }


    /**
     * 初始化JNDI上下文环境
     */
    private void initEnvContext() {
        if (envContext != null) {
            return;
        }
        try {
            Context context = new InitialContext();
            envContext = (Context) context.lookup(JNDI_ENV_CONTEXT_NAME);
        } catch (NamingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void destroyComponents() {
        if (servletContext != null) {
            servletContext = null;
        }
    }
}
