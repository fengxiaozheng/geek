package com.geek.lixw;

import com.geek.lixw.controller.Controller;
import com.geek.lixw.controller.PageController;
import org.apache.commons.lang.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.substringAfter;

/**
 * @author lixw
 * @date 2021/03/02
 */
public class FrontControllerServlet extends HttpServlet {

    private final Map<String, Controller> controllerMap = new HashMap<>();

    private final Map<String, HandlerMethodInfo> handlerMethodInfoMap = new HashMap<>();

    @Override
    public void init(ServletConfig config) throws ServletException {
        initMethod();
    }

    /**
     * 读取所有的 RestController 的注解元信息 @Path
     * 利用 ServiceLoader 技术（Java SPI）
     */
    private void initMethod() {
        ServiceLoader<Controller> load = ServiceLoader.load(Controller.class);
        for (Controller controller : load) {
            Class<? extends Controller> controllerClass = controller.getClass();
            //获取@Path注解信息
            Path classAnnotation = controllerClass.getAnnotation(Path.class);
            //获取请求路径
            String requestUrl = "";
            if (classAnnotation != null) {
                requestUrl = classAnnotation.value();
            }
            //获取controllerClass方法信息
            Method[] classMethods = controllerClass.getMethods();
            //处理方法支持的HTTP方法集合
            for (Method classMethod : classMethods) {
                Set<String> supportHttpMethods = findSupportHttpMethods(classMethod);
                //获取方法上的注解
                Path methodAnnotation = classMethod.getAnnotation(Path.class);
                if (methodAnnotation != null) {
                    requestUrl += methodAnnotation.value();
                }
                handlerMethodInfoMap.put(requestUrl, new HandlerMethodInfo(requestUrl, classMethod, supportHttpMethods));
            }
            controllerMap.put(requestUrl, controller);
        }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //获取请求uri
        String requestUri = req.getRequestURI();
        String contextPath = req.getContextPath();
        // 映射路径（子路径）
        String requestMappingPath = substringAfter(requestUri,
                StringUtils.replace(contextPath, "//", "/"));
        // 映射到 Controller
        Controller controller = controllerMap.get(requestMappingPath);
        if (controller == null) {
            return;
        }
        HandlerMethodInfo handlerMethodInfo = handlerMethodInfoMap.get(requestMappingPath);
        if (handlerMethodInfo == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        Set<String> supportedHttpMethods = handlerMethodInfo.getSupportedHttpMethods();
        String reqMethod = req.getMethod();
        if (!supportedHttpMethods.contains(reqMethod)) {
            resp.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }
        if (controller instanceof PageController) {
            PageController pageController = (PageController) controller;
            String viewPath;
            try {
                viewPath = pageController.execute(req, resp);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                if (throwable.getCause() instanceof IOException) {
                    throw (IOException) throwable.getCause();
                } else {
                    throw new ServletException(throwable.getCause());
                }
            }
            if (!viewPath.startsWith("/")) {
                viewPath = "/" + viewPath;
            }
            System.out.println("viewPath = " + viewPath);
            ServletContext servletContext = req.getServletContext();
            RequestDispatcher dispatcher = servletContext.getRequestDispatcher(viewPath);
            dispatcher.forward(req, resp);
        }
    }

    private Set<String> findSupportHttpMethods(Method classMethod) {
        Set<String> supportHttpMethods = new LinkedHashSet<>();
        //获取方法的注解
        for (Annotation annotation : classMethod.getAnnotations()) {
            HttpMethod httpMethod = annotation.annotationType().getAnnotation(HttpMethod.class);
            if (httpMethod != null) {
                supportHttpMethods.add(httpMethod.value());
            }
        }
        if (supportHttpMethods.isEmpty()) {
            supportHttpMethods.addAll(asList(HttpMethod.GET, HttpMethod.POST,
                    HttpMethod.PUT, HttpMethod.DELETE, HttpMethod.HEAD, HttpMethod.OPTIONS));
        }
        return supportHttpMethods;
    }
}
