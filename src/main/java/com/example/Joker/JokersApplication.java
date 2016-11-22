package com.example.Joker;

/**
 * Created by CoderSong on 16/11/21.
 */

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.example.Joker.service.filter.LoginCheckFilter;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@EnableRedisHttpSession
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
     * 注册过滤器
     *
     * @return
     */
//    @Bean
//    public FilterRegistrationBean filterRegistrationBean() {
//        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
//
//        // 给/users/*路由注册登录检测过滤器
//        LoginCheckFilter loginCheckFilter = new LoginCheckFilter();
//        registrationBean.setFilter(loginCheckFilter);
//        List<String> urlPatterns = new ArrayList<String>();
//        urlPatterns.add("/users/*");
//        registrationBean.setUrlPatterns(urlPatterns);
//
//        return registrationBean;
//    }

    /**
     * 注册拦截链
     */
    //    @Override
    //    public void addInterceptors(InterceptorRegistry registry) {
    //        registry.addInterceptor(new LoginCheck());
    //    }
}
