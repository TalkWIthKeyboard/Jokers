package com.example.Joker.service.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by CoderSong on 16/11/22.
 */

public class LoginCheckFilter implements Filter {

    @Override
    public void destroy() {
        System.out.println("过滤器销毁");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(true);
        Object user = session.getAttribute("user");
        if (user != null) {
            chain.doFilter(request, response);
        } else {
            httpResponse.setContentType("text/html;charset=utf-8");
            PrintWriter out = httpResponse.getWriter();
            out.print("412");
        }
    }

    @Override
    public void init(FilterConfig config) throws ServletException {
        System.out.println("过滤器初始化");
    }

}
