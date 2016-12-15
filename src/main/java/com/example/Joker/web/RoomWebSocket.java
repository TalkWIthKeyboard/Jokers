package com.example.Joker.web;

import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by CoderSong on 16/12/3.
 */

@ServerEndpoint(value = "/RoomWebsocket/{userId}/{roomId}")
@Component
public class RoomWebSocket {
    // 连接数
    private static int onlineCount = 0;

    // WebSocket的连接池
    private static CopyOnWriteArraySet<RoomWebSocket> webSocketSet = new CopyOnWriteArraySet<>();

    private Session session;
    private String userId;

    /**
     * WebSocket的建立连接
     *
     * @param session
     * @param userId
     */
    @OnOpen
    public void onOpen(
            Session session,
            @PathParam("userId") String userId,
            // 从哪个房间退出来的
            @PathParam("roomId") String roomId
    ) throws IOException {
        this.userId = userId;
        this.session = session;
        webSocketSet.add(this);
        addOnlineCount();

        // 广播这个用户刚才的退出动作
        if (!roomId.equals("null")) {
            for (RoomWebSocket item : webSocketSet) {
                if (!item.userId.equals(userId)) {
                    item.sendMessage("exitRoom " + userId + "," + roomId);
                }
            }
        }

        System.out.println("房间 有新链接加入!当前在线人数为" + getOnlineCount());
        System.out.println("房间 新连接的用户ID为" + this.userId);
    }

    /**
     * WebSocket的断开连接
     */
    @OnClose
    public void onClose() {
        webSocketSet.remove(this);
        subOnlineCount();

        System.out.println("房间 有一链接关闭!当前在线人数为" + getOnlineCount());
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

        Pattern enterRoomPattern = Pattern.compile("^enterRoom.*");
        Matcher enterRoomMatcher = enterRoomPattern.matcher(message);
        Pattern exitRoomPattern = Pattern.compile("^exitRoom.*");
        Matcher exitRoomMatcher = exitRoomPattern.matcher(message);
        Pattern createRoomPattern = Pattern.compile("^create.*");
        Matcher createRoomMatcher = createRoomPattern.matcher(message);

        // 客户端发送准备信息
        if (enterRoomMatcher.matches()) {
            afterEnterRoom(message, this.userId);
        } else if (exitRoomMatcher.matches()) {
            afterExitRoom(message, this.userId);
        } else if (createRoomMatcher.matches()) {
            afterCreateRoom(message, this.userId);
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

    /**
     * 创建房间的API回调后续操作
     *
     * @param message
     * @throws IOException
     */
    public void afterCreateRoom(String message, String userId) throws IOException {
        String roomId = message.split("")[1];
        for (RoomWebSocket item : webSocketSet) {
            if (item.userId.equals(userId)) {
                item.sendMessage("success create room");
            } else {
                item.sendMessage("createRoom " + roomId);
            }
        }
    }


    /**
     * 进入房间的API回调后续操作
     *
     * @param message
     * @param userId
     * @throws IOException
     */
    public void afterEnterRoom(String message, String userId) throws IOException {
        String roomId = message.split(" ")[1];
        for (RoomWebSocket item : webSocketSet) {
            if (item.userId.equals(userId)) {
                item.sendMessage("success");
            } else {
                item.sendMessage("enterRoom " + userId + "," + roomId);
            }
        }
    }

    /**
     * 退出房间的API回调后续操作
     *
     * @param message
     * @param userId
     * @throws IOException
     */
    public void afterExitRoom(String message, String userId) throws IOException {
        String roomId = message.split(" ")[1];
        for (RoomWebSocket item : webSocketSet) {
            if (!item.userId.equals(userId)) {
                item.sendMessage("exitRoom " + userId + "," + roomId);
            }
        }
    }

    public static synchronized int getOnlineCount() {
        return RoomWebSocket.onlineCount;
    }

    public static synchronized void addOnlineCount() {
        RoomWebSocket.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        RoomWebSocket.onlineCount--;
    }
}
