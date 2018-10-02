package com.xuef.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by moveb on 2018/10/1.
 */
@Controller
public class UserController {
    @RequestMapping(path = "/test")
    @ResponseBody
    public String test(){
        return "quora";
    }
}
