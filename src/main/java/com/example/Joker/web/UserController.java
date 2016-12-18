package com.example.Joker.web;

import com.example.Joker.Config;
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

import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    Tool tool = new Tool();
    Config config = new Config();
    UserDBService userdb = new UserDBService();

    /**
     * 获取用户的基本信息
     *
     * @return
     */
    @RequestMapping(value = "/user/userInfo", method = RequestMethod.GET)
    public ErrorHandler getUserInfo(
            HttpServletRequest request
    ) {
        DBObject user = (DBObject) request.getSession().getAttribute("user");
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
            @RequestBody ChangePwdForm changePwdForm,
            javax.servlet.http.HttpServletRequest request
    ) {
        DBObject user = (DBObject) request.getSession().getAttribute("user");
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
        user.put("roomId", null);
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
     * @param request
     * @return
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ErrorHandler login(
            @RequestBody LoginForm login,
            HttpServletRequest request
    ) {
        DBObject user = userdb.findByAccount(login.account);
        if (user != null) {
            Map userMap = user.toMap();
            String pwdMd5 = tool.stringToMD5(login.password);
            if (pwdMd5.equals(userMap.get("password").toString())) {
                // 登录后存入session
                request.getSession().setAttribute("user", user);
                DBObject userSession = (DBObject) request.getSession().getAttribute("user");
                String userId = userSession.get("_id").toString();
                System.out.println("success load session userId: " + userId + " sessionId: " + request.getSession().getId());
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
            @RequestBody User changeUserForm,
            javax.servlet.http.HttpServletRequest request
    ) {
        DBObject user = (DBObject) request.getSession().getAttribute("user");
        String userId = user.get("_id").toString();
        if (user != null) {
            user.put("username", changeUserForm.getUsername());
            user.put("image", changeUserForm.getImage());
            user.put("score", changeUserForm.getScore());
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
}
