package com.example.Joker.web;

import com.example.Joker.Config;
import com.example.Joker.domain.RoomDBService;
import com.example.Joker.domain.UserDBService;
import com.example.Joker.service.tool.ErrorHandler;
import com.example.Joker.service.tool.Tool;
import com.example.Joker.service.form.CreateRoomForm;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by CoderSong on 16/11/24.
 */

@RestController
@RequestMapping("/rooms")
public class RoomController {

    Tool tool = new Tool();
    Config config = new Config();
    UserDBService userdb = new UserDBService();
    RoomDBService roomdb = new RoomDBService();

    /**
     * 创建房间
     *
     * @param roomForm
     * @return
     */
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ErrorHandler createRoom(
            @RequestBody CreateRoomForm roomForm,
            @RequestParam(value = "userId", required = true) String userId
    ) {
        DBObject room = new BasicDBObject();
        if (!roomForm.key.equals("")) {
            roomForm.key = tool.stringToMD5(roomForm.key);
        }

        DBObject user = userdb.findById(userId);
        // 修改房间信息
        List<String> userList = new ArrayList<String>();
        userList.add(userId);
        room.put("isPrivate", roomForm.isPrivate);
        room.put("key", roomForm.key);
        room.put("userList", userList);
        room.put("readyNum", 0);
        room.put("landlordScore", 0);
        room.put("landlordUserId", null);
        room.put("rodNumber", 0);
        room.put("playIndex", -1);
        room.put("state", 1);
        String roomId = roomdb.saveData(room);
        if (roomId.equals("error")) {
            return config.getHandler("DB_SAVE_ERROR");
        } else {
            // 同步修改用户的房间信息
            user.put("roomId", roomId);
            String error = userdb.updateInfo(userId, user);
            if (error == null) {
                ErrorHandler success = config.getHandler("SUCCESS");
                success.setParams(room.get("_id").toString());
                return success;
            } else {
                return config.getHandler("DB_CHANGE_ERROR");
            }
        }
    }


    /**
     * 加入房间
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ErrorHandler joinRoom(
            @RequestParam(value = "userId", required = true) String userId,
            @RequestParam(value = "roomId", required = true) String id,
            @RequestParam(value = "key", required = true) String key
    ) {
        DBObject roomObj = roomdb.findById(id);
        // 密码编码
        if (!key.equals("")) {
            key = tool.stringToMD5(key);
        }

        if (roomObj != null) {
            List userList = (List) roomObj.get("userList");
            if (userList.size() == 3) {
                return config.getHandler("USER_NUM_ERROR");
            } else {
                if ((Integer) roomObj.get("isPrivate") == 1 && !key.equals((String) roomObj.get("key"))) {
                    return config.getHandler("ROOM_PWD_ERROR");
                } else {
                    DBObject user = userdb.findById(userId);
                    userList.add(userId);
                    roomObj.put("userList", userList);
                    String error = roomdb.updateInfo(id, roomObj);
                    if (error == null) {
                        // 同步用户的房间信息
                        user.put("roomId", id);
                        String err = userdb.updateInfo(userId, user);
                        if (err == null) {
                            return config.getHandler("SUCCESS");
                        } else {
                            return config.getHandler("DB_UPDATE_ERROR");
                        }
                    } else {
                        return config.getHandler("DB_UPDATE_ERROR");
                    }
                }
            }
        } else {
            return config.getHandler("ROOM_ERROR");
        }
    }


    /**
     * 修改房间的权限和密码
     *
     * @param id
     * @param roomForm
     * @return
     */
    @RequestMapping(value = "/", method = RequestMethod.PUT)
    public ErrorHandler changeRoomPwd(
            @RequestParam(value = "userId", required = true) String userId,
            @RequestParam(value = "roomId", required = true) String id,
            @RequestBody CreateRoomForm roomForm
    ) {
        DBObject roomObj = roomdb.findById(id);
        if (roomObj != null) {
            List userList = (List) roomObj.get("userList");
            // 密码编码
            if (!roomForm.key.equals("")) {
                roomForm.key = tool.stringToMD5(roomForm.key);
            }

            // 判断是否是房主
            if (userList.get(0).equals(userId)) {
                roomObj.put("isPrivate", roomForm.isPrivate);
                roomObj.put("key", roomForm.key);
                String error = roomdb.updateInfo(id, roomObj);
                if (error == null) {
                    return config.getHandler("SUCCESS");
                } else {
                    return config.getHandler("DB_UPDATE_ERROR");
                }
            } else {
                return config.getHandler("AUTHORITY_ERROR");
            }
        } else {
            return config.getHandler("ROOM_ERROR");
        }
    }


    /**
     * 用户退出房间
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/userRoom", method = RequestMethod.GET)
    public ErrorHandler exitRoom(
            @RequestParam(value = "userId", required = true) String userId,
            @RequestParam(value = "roomId", required = true) String id
    ) {
        DBObject roomObj = roomdb.findById(id);
        if (roomObj != null) {
            List<String> userList = (List<String>) roomObj.get("userList");
            DBObject user = userdb.findById(userId);
            int flag = -1;
            for (int index = 0; index < userList.size(); index++) {
                if (userList.get(index).equals(userId)) {
                    flag = index;
                    break;
                }
            }

            if (flag == -1) {
                return config.getHandler("USER_ROOM_ERROR");
            } else {
                user.put("roomId", null);
                String err = userdb.updateInfo(id, user);
                if (err == null) {
                    // 如果人数等于1人还退出房间，那就把房间删了
                    if (userList.size() > 1) {
                        userList.remove(flag);
                        roomObj.put("userList", userList);
                        String error = roomdb.updateInfo(id, roomObj);
                        if (error == null) {
                            return config.getHandler("SUCCESS");
                        } else {
                            return config.getHandler("DB_UPDATE_ERROR");
                        }
                    } else {
                        String error = roomdb.removeById(id);
                        if (error == null) {
                            return config.getHandler("SUCCESS");
                        } else {
                            return config.getHandler("DB_REMOVE_ERROR");
                        }
                    }
                } else {
                    return config.getHandler("DB_UPDATE_ERROR");
                }
            }
        } else {
            return config.getHandler("ROOM_ERROR");
        }
    }


    /**
     * 获得一个房间内所有的用户信息
     *
     * @return
     */
    @RequestMapping(value = "/room/userInfo", method = RequestMethod.GET)
    public ErrorHandler getRoomUserInfo(
            @RequestParam(value = "roomId", required = true) String id
    ) {
        List<DBObject> userInfoList = userdb.getRoomUserInfo(id);

        if (userInfoList != null) {
            ErrorHandler success = config.getHandler("SUCCESS");
            success.setParams(userInfoList);
            return success;
        } else {
            return config.getHandler("INSIDE_ERROR");
        }
    }

    /**
     * 获得所有房间的信息
     *
     * @return
     */
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public ErrorHandler getAllRooms() {
        List<DBObject> rooms = roomdb.findAll();

        if (rooms != null) {
            // 用户的图片加到返回的数据里
            for (int index = 0; index < rooms.size(); index++) {
                List<String> imgList = new ArrayList<>();
                DBObject roomObj = (DBObject) rooms.get(index);
                List userList = (List) roomObj.get("userList");
                for (int userIndex = 0; userIndex < userList.size(); userIndex++) {
                    DBObject userObj = userdb.findById((String) userList.get(userIndex));
                    if (userObj != null) {
                        imgList.add(userObj.get("image").toString());
                    }
                }
                roomObj.put("imgList",imgList);
            }

            // 构造数据
            ErrorHandler success = config.getHandler("SUCCESS");
            success.setParams(rooms);
            return success;
        } else {
            return config.getHandler("INSIDE_ERROR");
        }
    }
}
