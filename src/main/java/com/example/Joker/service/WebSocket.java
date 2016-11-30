package com.example.Joker.service;

import com.mongodb.DBObject;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by CoderSong on 16/11/29.
 */

@ServerEndpoint(value = "/websocket", configurator = GetHttpSessionConfigurator.class)
@Component
public class WebSocket {
    // 连接数
    private static int onlineCount = 0;

    // WebSocket的连接池
    private static CopyOnWriteArraySet<WebSocket> webSocketSet = new CopyOnWriteArraySet<>();

    private Session session;

    private HttpSession httpSession;

    /**
     * WebSocket的连接
     *
     * @param session
     * @param config
     */
    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        this.httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
        this.session = session;
        webSocketSet.add(this);
        addOnlineCount();

        // 连接状态持久化保存(可能后面要改为redis)
        DBObject user = (DBObject) httpSession.getAttribute("user");
        System.out.println("有新链接加入!当前在线人数为" + getOnlineCount());
        System.out.println("新连接的用户ID为" + user.get("_id"));
        System.out.println("新连接的用户房间ID为" + httpSession.getAttribute("roomId"));
    }

    /**
     * WebSocket的断开连接
     */
    @OnClose
    public void onClose() {
        webSocketSet.remove(this);
        subOnlineCount();

        System.out.println("有一链接关闭!当前在线人数为" + getOnlineCount());

    }

    /**
     * WebSocket监听到客户端的信息后的方法
     *
     * @param message
     * @param session
     * @throws IOException
     */
    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        System.out.println("来自客户端的消息:" + message);
        // 群发消息
        if (message.equals("ready")){
            for (WebSocket item : webSocketSet) {
                DBObject user = (DBObject) item.httpSession.getAttribute("user");
                String room = item.httpSession.getAttribute("roomId").toString();
                item.sendMessage("用户 "+ user.get("_id") + "在房间 " + room + "准备");
            }
        }
    }

    /**
     * 给客户端发送信息
     *
     * @param message
     * @throws IOException
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    public static synchronized int getOnlineCount() {
        return WebSocket.onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocket.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocket.onlineCount--;
    }
}
