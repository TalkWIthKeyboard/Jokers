package com.example.Joker.service;

import com.example.Joker.Config;
import com.example.Joker.domain.RoomDBService;
import com.mongodb.DBObject;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by CoderSong on 16/11/29.
 */

@ServerEndpoint(value = "/websocket/{userId}/{roomId}", configurator = GetHttpSessionConfigurator.class)
@Component
public class WebSocket {
    // 连接数
    private static int onlineCount = 0;

    // WebSocket的连接池
    private static CopyOnWriteArraySet<WebSocket> webSocketSet = new CopyOnWriteArraySet<>();

    private Session session;

    private String userId;
    private String roomId;

    private List<Poker> userPokers;

    /**
     * WebSocket的建立连接
     *
     * @param session
     * @param userId
     * @param roomId
     */
    @OnOpen
    public void onOpen(
            Session session,
            @PathParam("userId") String userId,
            @PathParam("roomId") String roomId
    ) {
        this.userId = userId;
        this.roomId = roomId;
        this.session = session;
        webSocketSet.add(this);
        addOnlineCount();

        System.out.println("有新链接加入!当前在线人数为" + getOnlineCount());
        System.out.println("新连接的用户ID为" + this.userId);
        System.out.println("新连接的用户房间ID为" + this.roomId);
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


        // 客户端发送准备信息
        if (message.equals("userReady")) {
            userReadyOrNot(this.roomId, 1);
            // 通知所有人他准备了（测试用）
            for (WebSocket item : webSocketSet) {
                String thisUserId = item.userId;
                String thisRoomId = item.roomId;
                item.sendMessage("用户 " + thisUserId + "在房间 " + thisRoomId + "准备");
            }
            // 如果3个人都准备了就发牌开始
            if (readyNumber(this.roomId) == 1) {
                // 发牌
                Sands sand = new Sands();
                sand.addPokers();
                sand.washPokers();
                List<List<Poker>> players = sand.getPlayers();

                // 领牌
                for (WebSocket item : webSocketSet) {
                    String userId = item.userId;
                    String roomId = item.roomId;
                    this.userPokers = players.get(sendPoker(userId, roomId));
                    item.sendMessage(printPokers(this.userPokers));
                }
            }
        } else if (message.equals("userClearReady")) {
            // 取消准备
            userReadyOrNot(this.roomId, -1);
        } else {
            Pattern pattern = Pattern.compile("^playPokers.*");
            Matcher playPokersMatcher = pattern.matcher(message);
            // 出牌
            if (playPokersMatcher.matches()) {
                playPoker(message);
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

    /**
     * 用户准备和取消准备动作
     *
     * @param roomId
     * @param isReady -1为取消准备， 1为准备
     */
    public static synchronized void userReadyOrNot(String roomId, int isReady) {
        RoomDBService roomdb = new RoomDBService();
        DBObject room = roomdb.findById(roomId);
        Integer readyNum = (Integer) room.get("readyNum");
        room.put("readyNum", readyNum + isReady * 1);
        roomdb.updateInfo(roomId, room);
    }


    /**
     * 获取房间准备人数
     *
     * @param roomId
     * @return
     */
    public static synchronized Integer readyNumber(String roomId) {
        RoomDBService roomdb = new RoomDBService();
        DBObject room = roomdb.findById(roomId);
        Integer readyNum = (Integer) room.get("readyNum");
        return readyNum;
    }

    /**
     * 按照序号发牌
     *
     * @return
     */
    public static synchronized int sendPoker(String userId, String roomId) {
        RoomDBService roomdb = new RoomDBService();
        DBObject room = roomdb.findById(roomId);
        List<String> userList = (List<String>) room.get("userList");
        int index = userList.indexOf(userId);
        return index;
    }

    /**
     * 把牌打印下来，（测试用）
     *
     * @param userPokers
     */
    public static synchronized String printPokers(List<Poker> userPokers) {
        Config config = new Config();
        String print = new String();
        for (int i = 0; i < userPokers.size(); i++) {
            print += config.getColorHandler().get(userPokers.get(i).getColor()) +
                    config.getPointHandler().get(userPokers.get(i).getPoint()) + " ";
        }
        return print;
    }


    /**
     * 处理客户端的出牌消息
     *
     * @param message
     */
    public void playPoker(String message) throws IOException {
        // 对前端的出牌请求进行转换
        String pokers = message.split(" ")[1];
        String[] pokerList = pokers.split(",");
        List<Poker> pokerObjList = new ArrayList<>();
        Config config = new Config();
        for (int i = 0; i < pokerList.length; i++) {
            String[] poker = pokerList[i].split("/");
            Poker pokerObj = new Poker(
                    config.getRePointHandler().get(poker[1]),
                    config.getReColorHandler().get(poker[0])
            );
            pokerObjList.add(pokerObj);
        }

        // 后端同步出牌操作
        for (int i = 0; i < pokerObjList.size(); i++) {
            for (int j = 0; j < this.userPokers.size(); j++) {
                if (pokerObjList.get(i).getColor() == this.userPokers.get(j).getColor() &&
                        pokerObjList.get(i).getPoint() == this.userPokers.get(j).getPoint()) {
                    this.userPokers.remove(j);
                    break;
                }
            }
        }

        if (this.userPokers.size() > 0) {
            // 给前端发消息
            this.sendMessage(printPokers(this.userPokers));
        } else {
            // 牌出完了,游戏结束
            this.sendMessage("game over!");
        }
    }
}
