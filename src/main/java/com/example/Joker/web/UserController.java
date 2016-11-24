package com.example.Joker.web;

import com.example.Joker.Config;
import com.example.Joker.domain.User;
import com.example.Joker.service.form.ChangePwdForm;
import com.example.Joker.service.ErrorHandler;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import com.example.Joker.domain.UserDBService;
import com.example.Joker.service.Tool;

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
     * @param id
     * @return
     */
    @RequestMapping(value = "/userInfo", method = RequestMethod.GET)
    public ErrorHandler getUserInfo(
            @RequestParam(value = "id", required = true) String id
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
     * @param id
     * @param changePwdForm
     * @return
     */
    @RequestMapping(value = "/password", method = RequestMethod.PUT)
    public ErrorHandler changePassword(
            @RequestParam(value = "id", required = true) String id,
            @RequestBody ChangePwdForm changePwdForm
    ) {
        DBObject user = userdb.findById(id);
        if (user != null) {
            Map userMap = user.toMap();
            String pwdMd5 = tool.stringToMD5(changePwdForm.password);
            if (pwdMd5.equals(userMap.get("password").toString())) {
                user.put("password", tool.stringToMD5(changePwdForm.rePassword));
                String error = userdb.updateInfo(id, user);
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
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ErrorHandler saveUser(
            @RequestBody User UserForm
    ) {
        DBObject user = new BasicDBObject();
        String password = tool.stringToMD5(UserForm.getPassword());
        user.put("account", UserForm.getAccount());
        user.put("password", password);
        user.put("username", UserForm.getUsername());
        user.put("sex", UserForm.getSex());
        String error = userdb.saveData(user);
        if (error == null) {
            return config.getHandler("SUCCESS");
        } else {
            return config.getHandler("DB_SAVE_ERROR");
        }
    }

    /**
     * 用户登录
     *
     * @param account
     * @param password
     * @return
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ErrorHandler login(
            @RequestParam(value = "account", required = true) String account,
            @RequestParam(value = "password", required = true) String password,
            HttpServletRequest request
    ) {
        DBObject user = userdb.findByAccount(account);
        if (user != null) {
            Map userMap = user.toMap();
            String pwdMd5 = tool.stringToMD5(password);
            // 登录后存入session
            request.getSession().setAttribute("user", user);
            if (pwdMd5.equals(userMap.get("password").toString())) {
                return config.getHandler("SUCCESS");
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
     * @param id
     * @param changeUserForm
     * @return
     */
    @RequestMapping(value = "/", method = RequestMethod.PUT)
    public ErrorHandler changeInfo(
            @RequestParam(value = "id", required = true) String id,
            @RequestBody User changeUserForm
    ) {
        DBObject user = userdb.findById(id);
        if (user != null) {
            user.put("username", changeUserForm.getUsername());
            user.put("image", changeUserForm.getImage());
            user.put("score", changeUserForm.getScore());
            user.put("sex", changeUserForm.getSex());
            String error = userdb.updateInfo(id, user);
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
