package com.xuef.controller;

import com.xuef.base.ApiResponse;
import com.xuef.model.User;
import com.xuef.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by nowcoder on 2016/7/2.
 */
@Controller
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    UserService userService;

    @RequestMapping(path = {"/reg"}, method = {RequestMethod.POST})
    public String reg(Model model, @RequestParam("username") String username,
                      @RequestParam("password") String password,
                      HttpServletResponse response) {
        try {
            Map<String, Object> map = userService.register(username, password);
            // 注册成功，跳到主页; 否则重新转至注册页面
            if (map.containsKey("ticket")) {
                Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
                cookie.setPath("/");
                response.addCookie(cookie);
                return "redirect:/";
            } else {
                model.addAttribute("msg", map.get("msg"));
                return "register";
            }
        } catch (Exception e) {
            logger.error("注册异常" + e.getMessage());
            model.addAttribute("msg", "服务器错误");
            return "register";
        }
    }

    @RequestMapping(path = {"/check"}, method = {RequestMethod.POST})
    @ResponseBody
    public ApiResponse check(Model model, @RequestParam("username") String username,
                             HttpServletResponse response) {
        try {
            User user = userService.getByName(username);
            if (user == null){
                return ApiResponse.ofSuccess("username not exist");
            } else {
                return ApiResponse.ofMessage(300, "username exists");
            }
        } catch (Exception e) {
            return ApiResponse.ofMessage(500, "server error");
        }
    }

    @RequestMapping(path = {"/reg"}, method = {RequestMethod.GET})
    public String regloginPage(Model model) {
        return "register";
    }

    @RequestMapping(path = {"/"}, method = {RequestMethod.GET})
    public String index() {
        return "index";
    }

    /**
     *
     * @param model
     * @param username
     * @param password
     * @param target 登录成功后的跳转目标
     * @param rememberme
     * @param response
     * @return
     */
    @RequestMapping(path = {"/login"}, method = {RequestMethod.POST})
    public String login(Model model, @RequestParam("username") String username,
                        @RequestParam("password") String password,
                        @RequestParam(value = "target", required = false) String target,
                        @RequestParam(value="rememberme", defaultValue = "false") boolean rememberme,
                        HttpServletResponse response) {
        try {
            Map<String, Object> map = userService.login(username, password);
            if (map.containsKey("ticket")) {
                Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
                cookie.setPath("/");
                if (rememberme) {
                    cookie.setMaxAge(3600*24*5);
                }
                response.addCookie(cookie);
                if (StringUtils.isNotBlank(target)) {
                    return "redirect:" + target;
                }
                return "redirect:/";
            } else {
                model.addAttribute("msg", map.get("msg"));
                return "login";
            }

        } catch (Exception e) {
            logger.error("登陆异常" + e.getMessage());
            return "login";
        }
    }

    @RequestMapping(path = {"/login"}, method = {RequestMethod.GET})
    public String loginPage(Model model,
                            @RequestParam(value = "target", required = false) String target) {
        model.addAttribute("target", target);
        return "login";
    }

    @RequestMapping(path = {"/logout"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String logout(@CookieValue("ticket") String ticket) {
        userService.logout(ticket);
        return "redirect:/";
    }

}
