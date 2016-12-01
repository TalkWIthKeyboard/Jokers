package com.example.Joker.web;

import com.example.Joker.Config;
import com.example.Joker.domain.RoomDBService;
import com.example.Joker.domain.UserDBService;
import com.example.Joker.service.tool.ErrorHandler;
import com.example.Joker.service.tool.Tool;
import com.example.Joker.service.form.CreateRoomForm;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

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
            HttpServletRequest request
    ) {
        DBObject room = new BasicDBObject();
        if (!roomForm.key.equals("")) {
            roomForm.key = tool.stringToMD5(roomForm.key);
        }

        DBObject user = (DBObject) request.getSession().getAttribute("user");
        // 修改房间信息
        String userid = user.get("_id").toString();
        List<String> userList = new ArrayList<String>();
        userList.add(userid);
        room.put("isPrivate", roomForm.isPrivate);
        room.put("key", roomForm.key);
        room.put("userList", userList);
        room.put("readyNum",0);
        room.put("landlordScore", 0);
        room.put("landlordUserId", null);
        room.put("rodNumber", 0);
        String roomId = roomdb.saveData(room);
        if (roomId.equals("error")) {
            return config.getHandler("DB_SAVE_ERROR");
        } else {
            // 同步session信息
            request.getSession().setAttribute("roomId", roomId);
            // 同步修改用户的房间信息
            user.put("roomId", roomId);
            String error = userdb.updateInfo(userid, user);
            if (error == null) {
                return config.getHandler("SUCCESS");
            } else {
                return config.getHandler("DB_CHANGE_ERROR");
            }
        }
    }


    /**
     * 加入房间
     *
     * @param id
     * @param request
     * @return
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ErrorHandler joinRoom(
            @RequestParam(value = "id", required = true) String id,
            @RequestParam(value = "key", required = true) String key,
            HttpServletRequest request
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
                    DBObject user = (DBObject) request.getSession().getAttribute("user");
                    String userid = user.get("_id").toString();
                    userList.add(userid);
                    roomObj.put("userList", userList);
                    String error = roomdb.updateInfo(id, roomObj);
                    if (error == null) {
                        // 同步session信息
                        request.getSession().setAttribute("roomId", id);
                        // 输出测试
                        String testRoomId = (String) request.getSession().getAttribute("roomId");
                        System.out.println(testRoomId);
                        // 同步用户的房间信息
                        user.put("roomId", id);
                        String err = userdb.updateInfo(userid, user);
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
     * @param request
     * @return
     */
    @RequestMapping(value = "/", method = RequestMethod.PUT)
    public ErrorHandler changeRoomPwd(
            @RequestParam(value = "id", required = true) String id,
            @RequestBody CreateRoomForm roomForm,
            HttpServletRequest request
    ) {
        DBObject roomObj = roomdb.findById(id);
        if (roomObj != null) {
            List userList = (List) roomObj.get("userList");
            DBObject user = (DBObject) request.getSession().getAttribute("user");
            String userid = user.get("_id").toString();
            // 密码编码
            if (!roomForm.key.equals("")) {
                roomForm.key = tool.stringToMD5(roomForm.key);
            }

            // 判断是否是房主
            if (userList.get(0).equals(userid)) {
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
     * @param request
     * @return
     */
    @RequestMapping(value = "/userRoom", method = RequestMethod.GET)
    public ErrorHandler exitRoom(
            @RequestParam(value = "id", required = true) String id,
            HttpServletRequest request
    ) {
        DBObject roomObj = roomdb.findById(id);
        if (roomObj != null) {
            List<String> userList = (List<String>) roomObj.get("userList");
            DBObject user = (DBObject) request.getSession().getAttribute("user");
            String userid = user.get("_id").toString();
            int flag = -1;
            for (int index = 0; index < userList.size(); index++) {
                if (userList.get(index).equals(userid)) {
                    flag = index;
                    break;
                }
            }

            if (flag == -1) {
                return config.getHandler("USER_ROOM_ERROR");
            } else {
                // 先同步用户房间信息和session信息
                request.getSession().setAttribute("roomId", null);
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
}
