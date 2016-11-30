package com.example.Joker.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * Created by CoderSong on 16/11/29.
 */

@Controller
public class TestController {

    @RequestMapping("/")
    public String index(
            HttpServletRequest request
    ) {
        Object session = request.getSession();
        return "index";
    }
}
