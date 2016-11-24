package com.example.Joker.web;

import com.example.Joker.Config;
import com.example.Joker.domain.Room;
import com.example.Joker.domain.RoomDBService;
import com.example.Joker.service.ErrorHandler;
import com.example.Joker.service.Tool;
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
        if (roomForm.password != "") {
            roomForm.password = tool.stringToMD5(roomForm.password);
        }

        DBObject user = (DBObject) request.getSession().getAttribute("user");
        String userid = user.get("_id").toString();
        List<String> userList = new ArrayList<String>();
        userList.add(userid);
        room.put("isPrivate", roomForm.isPrivate);
        room.put("key", roomForm.password);
        room.put("userList", userList);
        String error = roomdb.saveData(room);
        if (error == null) {
            return config.getHandler("SUCCESS");
        } else {
            return config.getHandler("DB_SAVE_ERROR");
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
        Room room = (Room) roomObj;
        // 密码编码
        if (key != "") {
            key = tool.stringToMD5(key);
        }

        if (roomObj != null) {
            if (room.getUserList().size() == 3) {
                return config.getHandler("USER_NUM_ERROR");
            } else {
                if (room.getIsPrivate() == 1 && !key.equals(room.getKey())) {
                    return config.getHandler("ROOM_PWD_ERROR");
                } else {
                    List<String> userList = room.getUserList();
                    DBObject user = (DBObject) request.getSession().getAttribute("user");
                    String userid = user.get("_id").toString();
                    userList.add(userid);
                    roomObj.put("userList", userList);
                    String error = roomdb.updateInfo(id, roomObj);
                    if (error == null) {
                        return config.getHandler("SUCCESS");
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
        Room room = (Room) roomObj;
        DBObject user = (DBObject) request.getSession().getAttribute("user");
        String userid = user.get("_id").toString();
        // 密码编码
        if (roomForm.password != "") {
            roomForm.password = tool.stringToMD5(roomForm.password);
        }

        if (room.getUserList().get(0).equals(userid)) {
            roomObj.put("isPrivate", roomForm.isPrivate);
            roomObj.put("key", roomForm.password);
            String error = roomdb.updateInfo(id, roomObj);
            if (error == null) {
                return config.getHandler("SUCCESS");
            } else {
                return config.getHandler("DB_UPDATE_ERROR");
            }
        } else {
            return config.getHandler("AUTHORITY_ERROR");
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
        Room room = (Room) roomObj;
        DBObject user = (DBObject) request.getSession().getAttribute("user");
        String userid = user.get("_id").toString();
        Integer flag = -1;
        List<String> userList = room.getUserList();
        for (Integer index = 0; index < userList.size(); index++) {
            if (userList.get(index).equals(userid)) {
                flag = index;
                break;
            }
        }

        if (flag == -1) {
            return config.getHandler("USER_ROOM_ERROR");
        } else {
            userList.remove(flag);
            roomObj.put("userList", userList);
            String error = roomdb.updateInfo(id, roomObj);
            if (error == null) {
                return config.getHandler("SUCCESS");
            } else {
                return config.getHandler("DB_UPDATE_ERROR");
            }
        }
    }
}
