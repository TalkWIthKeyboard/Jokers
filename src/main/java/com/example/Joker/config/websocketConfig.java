package com.example.Joker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * Created by CoderSong on 16/11/29.
 * WebSocket的配置类
 */

@Configuration
public class websocketConfig {
    @Bean
    public ServerEndpointExporter serverEndpointExporter (){
        return new ServerEndpointExporter();
    }
}
