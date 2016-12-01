package com.example.Joker.service;

import javax.servlet.http.HttpSession;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

/**
 * Created by CoderSong on 16/11/30.
 */

/**
 * 在websocket还是发送http包的时候获取http协议的session
 */
public class GetHttpSessionConfigurator extends ServerEndpointConfig.Configurator {

    @Override
    public void modifyHandshake(ServerEndpointConfig config,
                                HandshakeRequest request,
                                HandshakeResponse response)
    {
        HttpSession httpSession = (HttpSession)request.getHttpSession();
        System.out.println("http session roomId!" + httpSession.getAttribute("roomId"));
        config.getUserProperties().put(HttpSession.class.getName(),httpSession);
    }
}
