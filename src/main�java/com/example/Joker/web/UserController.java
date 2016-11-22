package com.example.Joker.web;

import com.example.Joker.Config;
import com.example.Joker.domain.User;
import com.example.Joker.form.ChangePwdForm;
import com.example.Joker.service.ErrorHandler;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.springframework.web.bind.annotation.*;
import com.example.Joker.domain.UserDBService;
import com.example.Joker.service.Tool;

import java.util.Map;


@RestController
@RequestMapping("/users")
public class UserController {

    Tool tool = new Tool();
    Config config = new Config();

    @RequestMapping("/")
    public Map findById(
            @RequestParam(value = "id", required = true) String id
    ) {
        UserDBService userdb = new UserDBService();
        DBObject user = userdb.findById(id);
        if (user != null) {
            return user.toMap();
        } else {
            // TODO 错误码返回
            return null;
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
        UserDBService userdb = new UserDBService();
        DBObject user = userdb.findById(id);
        if (user != null) {
            Map userMap = user.toMap();
            String pwdMd5 = tool.stringToMD5(changePwdForm.password);
            if (pwdMd5.equals(userMap.get("password").toString())) {
                user.put("password", tool.stringToMD5(changePwdForm.rePassword));
                String error = userdb.updateInfo(id, user);
                if (error == null){
                    return config.getHandler("SUCCESS");
                } else{
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
        UserDBService userdb = new UserDBService();
        DBObject user = new BasicDBObject();
        String password = tool.stringToMD5(UserForm.getPassword());
        user.put("account", UserForm.getAccount());
        user.put("password", password);
        user.put("username", UserForm.getUsername());
        user.put("sex", UserForm.getSex());
        String error = userdb.saveData(user);
        if (error == null){
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
            @RequestParam(value = "password", required = true) String password
    ) {
        // TODO 登录后的session操作
        UserDBService userdb = new UserDBService();
        DBObject user = userdb.findByAccount(account);
        if (user != null) {
            Map userMap = user.toMap();
            String pwdMd5 = tool.stringToMD5(password);
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
        UserDBService userdb = new UserDBService();
        DBObject user = userdb.findById(id);
        if (user != null) {
            user.put("username", changeUserForm.getUsername());
            user.put("image", changeUserForm.getImage());
            user.put("score", changeUserForm.getScore());
            user.put("sex", changeUserForm.getSex());
            userdb.updateInfo(id, user);
            return config.getHandler("SUCCESS");
        } else {
            return config.getHandler("USER_ERROR");
        }
    }

}
