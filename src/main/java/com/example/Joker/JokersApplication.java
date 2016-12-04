package com.example.Joker;

/**
 * Created by CoderSong on 16/11/21.
 */

import com.example.Joker.service.filter.EnterRoomFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.example.Joker.service.filter.LoginCheckFilter;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class JokersApplication extends WebMvcConfigurerAdapter {

    /**
     * 项目入口
     *
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(JokersApplication.class, args);
    }

    /**
     * 注册是否登录过滤器
     *
     * @return
     */
    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();

        // 给/users/*路由注册登录检测过滤器
        LoginCheckFilter loginCheckFilter = new LoginCheckFilter();
        registrationBean.setFilter(loginCheckFilter);
        List<String> urlPatterns = new ArrayList<String>();
        urlPatterns.add("/users/user/*");
        urlPatterns.add("/rooms/*");
        urlPatterns.add("/playTest");
        urlPatterns.add("/roomTest");
        registrationBean.setUrlPatterns(urlPatterns);

        return registrationBean;
    }

    /**
     * 注册是否进入房间过滤器
     *
     * @return
     */
    @Bean
    public FilterRegistrationBean filterJoinRoomBean() {
        FilterRegistrationBean joinRoomBean = new FilterRegistrationBean();

        // 给/users/*路由注册登录检测过滤器
        EnterRoomFilter enterRoomCheckFilter = new EnterRoomFilter();
        joinRoomBean.setFilter(enterRoomCheckFilter);
        List<String> urlPatterns = new ArrayList<String>();
        urlPatterns.add("/test");
        joinRoomBean.setUrlPatterns(urlPatterns);

        return joinRoomBean;
    }

    /**
     * 注册拦截链
     */
    //    @Override
    //    public void addInterceptors(InterceptorRegistry registry) {
    //        registry.addInterceptor(new LoginCheck());
    //    }
}
