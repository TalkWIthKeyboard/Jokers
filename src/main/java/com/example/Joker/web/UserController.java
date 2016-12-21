package com.example.Joker.web;

import com.example.Joker.Config;
import com.example.Joker.domain.ReceiveScore;
import com.example.Joker.domain.ReceiveScoreDBService;
import com.example.Joker.domain.User;
import com.example.Joker.service.form.ChangePwdForm;
import com.example.Joker.service.tool.ErrorHandler;
import com.example.Joker.service.form.LoginForm;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import com.example.Joker.domain.UserDBService;
import com.example.Joker.service.tool.Tool;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    Tool tool = new Tool();
    Config config = new Config();
    UserDBService userdb = new UserDBService();
    ReceiveScoreDBService receiveScoreDB = new ReceiveScoreDBService();

    /**
     * 获取用户的基本信息
     *
     * @return
     */
    @RequestMapping(value = "/user/userInfo", method = RequestMethod.GET)
    public ErrorHandler getUserInfo(
            @RequestParam(value = "userId", required = true) String id
    ) {
        DBObject user = userdb.findById(id);
        if (user != null) {
            ErrorHandler success = config.getHandler("SUCCESS");
            success.setParams(user);
            return success;
        } else {
            return config.getHandler("INSIDE_ERROR");
        }
    }

    /**
     * 用户修改密码
     *
     * @param changePwdForm
     * @return
     */
    @RequestMapping(value = "/user/password", method = RequestMethod.PUT)
    public ErrorHandler changePassword(
            @RequestParam(value = "userId", required = true) String id,
            @RequestBody ChangePwdForm changePwdForm
    ) {
        DBObject user = userdb.findById(id);
        String userId = user.get("_id").toString();
        if (user != null) {
            Map userMap = user.toMap();
            String pwdMd5 = tool.stringToMD5(changePwdForm.password);
            if (pwdMd5.equals(userMap.get("password").toString())) {
                user.put("password", tool.stringToMD5(changePwdForm.rePassword));
                String error = userdb.updateInfo(userId, user);
                if (error == null) {
                    return config.getHandler("SUCCESS");
                } else {
                    return config.getHandler("DB_UPDATE_ERROR");
                }
            } else {
                return config.getHandler("PWD_ERROR");
            }
        } else {
            return config.getHandler("USER_ERROR");
        }
    }

    /**
     * 创建用户
     *
     * @param UserForm
     * @return
     */
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ErrorHandler saveUser(
            @RequestBody User UserForm
    ) {
        DBObject user = new BasicDBObject();
        String password = tool.stringToMD5(UserForm.getPassword());
        user.put("account", UserForm.getAccount());
        user.put("password", password);
        user.put("username", UserForm.getUsername());
        user.put("sex", UserForm.getSex());
        user.put("image", UserForm.getImage());
        user.put("roomId", null);
        user.put("score",10);
        String error = userdb.saveData(user);
        if (error == null) {
            ErrorHandler success = config.getHandler("SUCCESS");
            success.setParams(user.get("_id").toString());
            return success;
        } else {
            return config.getHandler("DB_SAVE_ERROR");
        }
    }

    /**
     * 用户登录
     *
     * @param login
     * @return
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ErrorHandler login(
            @RequestBody LoginForm login
    ) {
        DBObject user = userdb.findByAccount(login.account);
        if (user != null) {
            Map userMap = user.toMap();
            String pwdMd5 = tool.stringToMD5(login.password);
            if (pwdMd5.equals(userMap.get("password").toString())) {
                // 登录后存入session
//                request.getSession(true).setAttribute("user", user);
//                System.out.println("userId: " + user.get("_id"));
//                System.out.println("sessionId: " + request.getSession().getId());
//                Cookie cookie = new Cookie("sessionId", request.getSession().getId());
//                cookie.setMaxAge(1200);
//                response.addCookie(cookie);
                ErrorHandler success = config.getHandler("SUCCESS");
                success.setParams(user.get("_id").toString());
                return success;
            } else {
                return config.getHandler("PWD_ERROR");
            }
        } else {
            return config.getHandler("USER_ERROR");
        }
    }

    /**
     * 修改用户信息
     *
     * @param changeUserForm
     * @return
     */
    @RequestMapping(value = "/user", method = RequestMethod.PUT)
    public ErrorHandler changeInfo(
            @RequestParam(value = "userId", required = true) String id,
            @RequestBody User changeUserForm
    ) {
        DBObject user = userdb.findById(id);
        String userId = user.get("_id").toString();
        if (user != null) {
            user.put("username", changeUserForm.getUsername());
            user.put("image", changeUserForm.getImage());
            user.put("sex", changeUserForm.getSex());
            String error = userdb.updateInfo(userId, user);
            if (error == null) {
                return config.getHandler("SUCCESS");
            } else {
                return config.getHandler("DB_UPDATE_ERROR");
            }
        } else {
            return config.getHandler("USER_ERROR");
        }
    }

    /**
     * 查询积分榜前6位
     *
     * @return
     */
    @RequestMapping(value = "/score/all", method = RequestMethod.GET)
    public ErrorHandler getAllScore(
    ) {
        List<DBObject> userList = userdb.getTopUserScore();
        if (userList != null) {
            ErrorHandler success = config.getHandler("SUCCESS");
            success.setParams(userList);
            return success;
        } else {
            return config.getHandler("INSIDE_ERROR");
        }
    }


    /**
     * 查询用户今天的领取积分情况
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/score/receive", method = RequestMethod.GET)
    public ErrorHandler canReceiveScore(
            @RequestParam(value = "userId", required = true) String id
    ){
        Integer count = receiveScoreDB.findByDateAndUserId(id);
        DBObject user = userdb.findById(id);

        if ((Integer) user.get("score") > 10) {
            return config.getHandler("USER_SCORE_ERROR");
        } else {
            if (count == -1) {
                return config.getHandler("INSIDE_ERROR");
            } else {
                ErrorHandler success = config.getHandler("SUCCESS");
                success.setParams(count.toString());
                return success;
            }
        }
    }


    /**
     * 给用户领取积分
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/score/add", method = RequestMethod.GET)
    public ErrorHandler receiveScore(
            @RequestParam(value = "userId", required = true) String id
    ){
        DBObject user = userdb.findById(id);
        if (user != null) {
            // 修改用户积分
            user.put("score", (Integer) user.get("score") + 50);
            String error = userdb.updateInfo(id, user);
            if (error == null) {
                // 添加本条记录
                DBObject receiveScore = new BasicDBObject();
                receiveScore.put("userId", id);
                String receiveScoreDBerror = receiveScoreDB.saveData(receiveScore);
                if (receiveScoreDBerror == null) {
                    return config.getHandler("SUCCESS");
                } else {
                    return config.getHandler("DB_SAVE_ERROR");
                }
            } else {
                return config.getHandler("DB_UPDATE_ERROR");
            }
        } else {
            return config.getHandler("USER_ERROR");
        }
    }
}
