package com.example.Joker.service.interceptor;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.Joker.Config;

import java.io.PrintWriter;


/**
 * Created by CoderSong on 16/11/22.
 */
public class LoginCheck extends HandlerInterceptorAdapter {

    /**
     * 前置检查
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        Object user = request.getSession().getAttribute("user");
        if (user != null) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 后置动作
     *
     * @param request
     * @param response
     * @param obj
     * @param mav
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object obj, ModelAndView mav) throws Exception {

//        Config errorHandler = new Config();
//        response.setContentType("text/html;charset=utf-8");
//        PrintWriter out = response.getWriter();
//        out.print(errorHandler.getHandler("LIVE_ERROR"));
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response, Object obj, Exception err) throws Exception {
        Config errorHandler = new Config();
        response.setContentType("text/html;charset=utf-8");
        PrintWriter out = response.getWriter();
        out.print(errorHandler.getHandler("LIVE_ERROR"));
    }
}
