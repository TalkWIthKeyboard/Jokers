package com.example.Joker.service.filter;

import com.mongodb.DBObject;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by CoderSong on 16/12/1.
 */

public class EnterRoomFilter implements Filter {

    @Override
    public void destroy() {
        System.out.println("是否进入房间过滤器销毁");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(true);
        String roomId = (String) session.getAttribute("roomId");
        if (roomId != null && !roomId.equals("")) {
            chain.doFilter(request, response);
        } else {
            httpResponse.setContentType("text/html;charset=utf-8");
            PrintWriter out = httpResponse.getWriter();
            out.print("413");
        }
    }

    @Override
    public void init(FilterConfig config) throws ServletException {
        System.out.println("是否进入房间过滤器初始化");
    }
}