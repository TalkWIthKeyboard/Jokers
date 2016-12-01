package com.example.Joker.web;

import com.mongodb.DBObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * Created by CoderSong on 16/11/29.
 */

@Controller
public class TestController {

    @RequestMapping("/test")
    public String index(
            HttpServletRequest request,
            Model model
    ) {
        DBObject user = (DBObject) request.getSession().getAttribute("user");
        String userId = user.get("_id").toString();
        String roomId = (String) request.getSession().getAttribute("roomId");
        System.out.println("test " + userId);
        System.out.println("test " + roomId);
        model.addAttribute("userId", userId);
        model.addAttribute("roomId", roomId);
        return "index";
    }
}
