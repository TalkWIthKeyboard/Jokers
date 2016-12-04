package com.example.Joker.web;

import com.mongodb.DBObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * Created by CoderSong on 16/11/29.
 */

@Controller
public class TestController {

    /**
     * 对战页面websocket测试
     *
     * @param request
     * @param model
     * @return
     */
    @RequestMapping(value = "/playTest", method = RequestMethod.GET)
    public String playTest(
            HttpServletRequest request,
            Model model
    ) {
        DBObject user = (DBObject) request.getSession().getAttribute("user");
        String userId = user.get("_id").toString();
        String roomId = (String) request.getSession().getAttribute("roomId");
        model.addAttribute("userId", userId);
        model.addAttribute("roomId", roomId);
        return "playIndex";
    }

    /**
     * 房间页面websocket测试
     *
     * @param request
     * @param model
     * @return
     */
    @RequestMapping(value = "/roomTest", method = RequestMethod.GET)
    public String roomTest(
            @RequestParam(value = "roomId", required = true) String roomId,
            HttpServletRequest request,
            Model model
    ) {
        DBObject user = (DBObject) request.getSession().getAttribute("user");
        String userId = user.get("_id").toString();
        model.addAttribute("userId", userId);
        model.addAttribute("roomId", roomId);
        return "roomIndex";
    }
}
