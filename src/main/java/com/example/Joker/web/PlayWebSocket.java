package com.example.Joker.web;

import com.example.Joker.Config;
import com.example.Joker.domain.RoomDBService;
import com.example.Joker.domain.UserDBService;
import com.example.Joker.service.Poker;
import com.example.Joker.service.Sands;
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

@ServerEndpoint(value = "/PlayWebsocket/{userId}/{roomId}")
@Component
public class PlayWebSocket {
    // 连接数
    private static int onlineCount = 0;

    // WebSocket的连接池
    private static CopyOnWriteArraySet<PlayWebSocket> webSocketSet = new CopyOnWriteArraySet<>();

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

        System.out.println("对战 有新链接加入!当前在线人数为" + getOnlineCount());
        System.out.println("对战 新连接的用户ID为" + this.userId);
        System.out.println("对战 新连接的用户房间ID为" + this.roomId);
    }


    /**
     * WebSocket的断开连接
     */
    @OnClose
    public void onClose() {
        webSocketSet.remove(this);
        subOnlineCount();

        System.out.println("对战 有一链接关闭!当前在线人数为" + getOnlineCount());
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
            for (PlayWebSocket item : webSocketSet) {
                item.sendMessage("用户 " + this.userId + "在房间 " + this.roomId + "准备");
            }
            // 如果3个人都准备了就发牌开始
            if (readyNumber(this.roomId) == 1) {
                afterAllReady(this.roomId);
            }
        } else if (message.equals("userClearReady")) {
            // 取消准备
            userReadyOrNot(this.roomId, -1);
        } else if (message.equals("gameOver")) {
            // 游戏结束
            finishGame(this.roomId, this.userId);
        } else {
            Pattern playPokersPattern = Pattern.compile("^playPokers.*");
            Matcher playPokersMatcher = playPokersPattern.matcher(message);
            Pattern robPattern = Pattern.compile("^rob.*");
            Matcher robMatcher = robPattern.matcher(message);
            // 出牌
            if (playPokersMatcher.matches()) {
                playPoker(message, this.userId, this.roomId);
            } else if (robMatcher.matches()) {
                robLandlord(message, this.roomId, this.userId);
            } else {

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
        return PlayWebSocket.onlineCount;
    }

    public static synchronized void addOnlineCount() {
        PlayWebSocket.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        PlayWebSocket.onlineCount--;
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
     * 所有人都准备了的后置动作
     *
     * @throws IOException
     */
    public void afterAllReady(String roomId) throws IOException {
        // 修改房间状态
        RoomDBService roomdb = new RoomDBService();
        DBObject room = roomdb.findById(roomId);
        room.put("state", 2);
        roomdb.updateInfo(roomId, room);

        // 发牌
        Sands sand = new Sands();
        sand.addPokers();
        sand.washPokers();
        List<List<Poker>> players = sand.getPlayers();

        // 领牌
        for (PlayWebSocket item : webSocketSet) {
            String itemUserId = item.userId;
            String itemRoomId = item.roomId;
            item.userPokers = players.get(sendPoker(itemUserId, itemRoomId));
            item.sendMessage(printPokers(item.userPokers));
        }
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
            print += config.getColorHandler().get(userPokers.get(i).getColor()) + "/" +
                    config.getPointHandler().get(userPokers.get(i).getPoint()) + ",";
        }
        return print;
    }


    /**
     * 处理客户端的出牌消息
     *
     * @param message
     */
    public void playPoker(String message, String userId, String roomId) throws IOException {
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

        // 出的是炸弹，处理一下分数
        if (isBoom(pokerObjList)) {
            RoomDBService roomdb = new RoomDBService();
            DBObject roomObj = roomdb.findById(roomId);
            roomObj.put("landlordScore", (Integer) roomObj.get("landlordScore") * 2);
            roomdb.updateInfo(roomId, roomObj);
        }

        if (this.userPokers.size() > 0) {
            // 给前端发消息
            this.sendMessage("你现在手上还有" + printPokers(this.userPokers));
            for (PlayWebSocket item : webSocketSet) {
                if (roomId.equals(item.roomId)) {
                    item.sendMessage("玩家 " + userId + "出了" + printPokers(pokerObjList));
                }
            }
        } else {
            // 牌出完了,游戏结束
            finishGame(roomId, userId);
        }
    }


    /**
     * 处理客户端抢地主的消息
     *
     * @param message
     * @throws IOException
     */
    public void robLandlord(String message, String roomId, String userId) throws IOException {
        // 对前端的抢地主请求进行转换
        Integer robNumber = Integer.parseInt(message.split(" ")[1]);

        RoomDBService roomdb = new RoomDBService();
        DBObject roomObj = roomdb.findById(roomId);
        if (robNumber > (Integer) roomObj.get("landlordScore")) {
            roomObj.put("landlordUserId", userId);
            roomObj.put("landlordScore", robNumber);
        }
        roomObj.put("rodNumber", (Integer) roomObj.get("rodNumber") + 1);
        roomdb.updateInfo(roomId, roomObj);

        if (robNumber == 3 || (Integer) roomObj.get("rodNumber") == 3) {
            // 修改房间状态
            roomObj.put("state", 3);
            roomdb.updateInfo(roomId, roomObj);

            for (PlayWebSocket item : webSocketSet) {
                if (item.roomId.equals(roomId)) {
                    item.sendMessage("玩家 " + roomObj.get("landlordUserId") + "抢到了地主");
                }
            }
        }
    }


    /**
     * 游戏结束
     */
    public void finishGame(String roomId, String userId) throws IOException {
        // 修改房间状态
        RoomDBService roomdb = new RoomDBService();
        DBObject room = roomdb.findById(roomId);
        room.put("state", 4);
        roomdb.updateInfo(roomId, room);

        UserDBService userdb = new UserDBService();

        // 构造通知信息，同步玩家数据库分数
        List<String> userList = (List<String>) room.get("userList");
        String message = new String();
        Integer score = (Integer) room.get("rodNumber");
        String landlordUserId = (String) room.get("landlordUserId");

        for (int i = 0; i < userList.size(); i++) {
            Integer thisScore = score;
            // 这个人是地主
            if (userList.get(i).equals(landlordUserId)) {
                thisScore *= 2;
            }

            DBObject user = userdb.findById(userList.get(i));
            // 这个人是赢家
            if (userList.get(i).equals(userId)) {
                message += userId + " win " + thisScore.toString() + " score/n";
                user.put("score", (Integer) user.get("score") + thisScore);
                userdb.updateInfo(userList.get(i), user);
            } else {
                // 这个人是输家
                message += userList.get(i) + " lose " + thisScore.toString() + "score/n";
                user.put("score", (Integer) user.get("score") - thisScore);
                userdb.updateInfo(userList.get(i), user);
            }
        }

        // 通知房间内的所有用户
        for (PlayWebSocket item : webSocketSet) {
            if (this.roomId.equals(item.roomId)) {
                item.sendMessage("玩家 " + userId + "赢了!/n" + message);
            }
        }
    }


    /**
     * 判断是不是炸弹
     *
     * @return
     */
    public static synchronized Boolean isBoom(List<Poker> pokers) {

        if (pokers.size() != 4 && pokers.size() != 2) {
            return false;
        } else {
            // 双王
            if (pokers.size() == 2 && pokers.get(0).getPoint() + pokers.get(1).getPoint() == 33) {
                return true;
            } else if (pokers.size() == 4) {
                // 普通炸弹
                int point = pokers.get(0).getPoint();
                for (int i = 1; i < pokers.size(); i++) {
                    if (pokers.get(i).getPoint() != point) {
                        return false;
                    }
                }
                return true;
            } else {
                return false;
            }
        }
    }
}
