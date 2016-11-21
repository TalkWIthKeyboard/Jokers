package com.example.Joker.web;
import com.example.Joker.domain.User;
import com.example.Joker.form.ChangePwdForm;
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

    @RequestMapping("/")
    public Map findById(
            @RequestParam(value = "id", required = true) String id
    ) {
        UserDBService userdb = new UserDBService();
        DBObject user = userdb.findById(id);
        if (user != null){
            return user.toMap();
        } else {
            // TODO 错误码返回
            return null;
        }
    }

    /**
     * 用户修改密码
     * @param changePwdForm
     * @return
     */
    @RequestMapping(value = "/password",method = RequestMethod.POST)
    public Map changePassword(
            @RequestBody ChangePwdForm changePwdForm
    ){
        UserDBService userdb = new UserDBService();
        DBObject user = userdb.findByAccount(changePwdForm.account);
        if (user != null){
            Map userMap = user.toMap();
            String pwdMd5 = tool.stringToMD5(changePwdForm.password);
            if (pwdMd5.equals(userMap.get("password").toString())){
                user.put("password",tool.stringToMD5(changePwdForm.rePassword));
                userdb.updatePwd(changePwdForm,user);
                // TODO 成功码返回
                return null;
            } else {
                // TODO 错误码返回
                return null;
            }
        } else {
            // TODO 错误码返回
            return null;
        }
    }

    /**
     * 创建用户
     * @param UserForm
     * @return
     */
    @RequestMapping(value = "/",method = RequestMethod.POST)
    public Map saveUser(
            @RequestBody User UserForm
    ){
        UserDBService userdb = new UserDBService();
        DBObject user = new BasicDBObject();
        String password = tool.stringToMD5(UserForm.getPassword());
        user.put("account", UserForm.getAccount());
        user.put("password", password);
        user.put("username", UserForm.getUsername());
        user.put("sex", UserForm.getSex());
        userdb.saveData(user);
        // TODO 异常检测，完整表单信息
        return null;
    }
}
