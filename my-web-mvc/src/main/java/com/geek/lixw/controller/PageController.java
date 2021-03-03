package com.geek.lixw.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author lixw
 * @date 2021/03/02
 */
public interface PageController extends Controller {

    String execute(HttpServletRequest request, HttpServletResponse response) throws Throwable;
}
