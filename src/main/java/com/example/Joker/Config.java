package com.example.Joker;

/**
 * Created by CoderSong on 16/11/21.
 */

import com.example.Joker.service.tool.ErrorHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Config {

    // 默认分页的每页个数
    private Integer pageSize;
    // 统一错误处理器
    private List<ErrorHandler> errorHandler;
    // 花色处理器
    private Map<Integer, String> colorHandler;
    // 反花色处理器
    private Map<String, Integer> reColorHandler;
    // 点数处理器
    private Map<Integer, String> pointHandler;
    // 反点数处理器
    private Map<String, Integer> rePointHandler;
    // 房间状态处理器
    private Map<Integer, String> roomStateHandler;


    public Config() {
        this.pageSize = 10;

        // 初始化错误处理器
        List<ErrorHandler> list = new ArrayList<ErrorHandler>();
        ErrorHandler success = new ErrorHandler(200, "SUCCESS", "操作成功"),
                userError = new ErrorHandler(410, "USER_ERROR", "不存在这个用户"),
                pwdError = new ErrorHandler(411, "PWD_ERROR", "用户密码错误"),
                liveError = new ErrorHandler(412, "LIVE_ERROR", "用户没有登录"),
                roomError = new ErrorHandler(413, "ROOM_ERROR", "没有找到这个房间"),
                roomPwdError = new ErrorHandler(414, "ROOM_PWD_ERROR", "房间密码错误"),
                authorityError = new ErrorHandler(415, "AUTHORITY_ERROR", "没有房主权限"),
                userNumError = new ErrorHandler(416, "USER_NUM_ERROR", "房间已经满员"),
                userRoomError = new ErrorHandler(417, "USER_ROOM_ERROR", "该用户不在这个房间"),
                userScoreError = new ErrorHandler(417, "USER_SCORE_ERROR", "该用户积分超过了10分"),
                insideError = new ErrorHandler(500, "INSIDE_ERROR", "方法内部错误"),
                dbSaveError = new ErrorHandler(420, "DB_SAVE_ERROR", "数据库的存储错误"),
                dbUpdateError = new ErrorHandler(421, "DB_UPDATE_ERROR", "数据库的更新错误"),
                dbRemoveError = new ErrorHandler(422, "DB_REMOVE_ERROR", "数据库的删除错误");

        list.add(success);
        list.add(userError);
        list.add(pwdError);
        list.add(insideError);
        list.add(liveError);
        list.add(roomError);
        list.add(roomPwdError);
        list.add(authorityError);
        list.add(userNumError);
        list.add(userRoomError);
        list.add(userScoreError);
        list.add(dbSaveError);
        list.add(dbUpdateError);
        list.add(dbRemoveError);
        this.errorHandler = list;

        // 初始化花色处理器
        Map<Integer, String> color = new HashMap<>();
        color.put(1, "clubs"); // 梅花
        color.put(2, "diamonds"); // 方块
        color.put(3, "hearts"); // 红桃
        color.put(4, "spades"); // 黑桃
        this.colorHandler = color;

        // 初始化反花色处理器
        Map<String, Integer> reColor = new HashMap<>();
        reColor.put("clubs", 1);
        reColor.put("diamonds", 2);
        reColor.put("hearts", 3);
        reColor.put("spades", 4);
        this.reColorHandler = reColor;

        // 初始化点数处理器
        Map<Integer, String> point = new HashMap<>();
        point.put(3, "3");
        point.put(4, "4");
        point.put(5, "5");
        point.put(6, "6");
        point.put(7, "7");
        point.put(8, "8");
        point.put(9, "9");
        point.put(10, "10");
        point.put(11, "J");
        point.put(12, "Q");
        point.put(13, "K");
        point.put(14, "A");
        point.put(15, "2");
        point.put(16, "sk"); // 小王
        point.put(17, "bk"); // 大王
        this.pointHandler = point;

        // 初始化反点数处理器
        Map<String, Integer> rePoint = new HashMap<>();
        rePoint.put("3", 3);
        rePoint.put("4", 4);
        rePoint.put("5", 5);
        rePoint.put("6", 6);
        rePoint.put("7", 7);
        rePoint.put("8", 8);
        rePoint.put("9", 9);
        rePoint.put("10", 10);
        rePoint.put("J", 11);
        rePoint.put("Q", 12);
        rePoint.put("K", 13);
        rePoint.put("A", 14);
        rePoint.put("2", 15);
        rePoint.put("sk", 16); // 小王
        rePoint.put("bk", 17); // 大王
        this.rePointHandler = rePoint;

        // 初始化房间状态处理器
        Map<Integer, String> roomState = new HashMap<>();
        roomState.put(1, "WaitPlayersReady");
        roomState.put(2, "RobLandlord");
        roomState.put(3, "PlayPokers");
        roomState.put(4, "GameOver");
        this.roomStateHandler = roomState;
    }

    public ErrorHandler getHandler(String key) {

        for (int i = 0; i < this.errorHandler.size(); i++) {
            ErrorHandler error = this.errorHandler.get(i);
            if (error.getErrorValue().containsKey(key)) {
                return error;
            }
        }

        return null;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public List<ErrorHandler> getErrorHandler() {
        return errorHandler;
    }

    public void setErrorHandler(List<ErrorHandler> errorHandler) {
        this.errorHandler = errorHandler;
    }

    public Map<Integer, String> getColorHandler() {
        return colorHandler;
    }

    public Map<Integer, String> getPointHandler() {
        return pointHandler;
    }

    public Map<String, Integer> getReColorHandler() {
        return reColorHandler;
    }

    public Map<String, Integer> getRePointHandler() {
        return rePointHandler;
    }

    public Map<Integer, String> getRoomStateHandler() {
        return roomStateHandler;
    }
}
