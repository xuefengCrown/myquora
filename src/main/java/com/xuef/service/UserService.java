package com.xuef.service;

import com.xuef.base.Cons;
import com.xuef.dao.LoginTicketDao;
import com.xuef.dao.UserDao;
import com.xuef.model.LoginTicket;
import com.xuef.model.User;
import com.xuef.util.QuoraUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by moveb on 2018/10/2.
 */
@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    @Autowired
    private UserDao userDao;
    @Autowired
    private LoginTicketDao loginTicketDao;

    public User getByName(String name){
        return userDao.getByName(name);
    }
    public Map<String, Object> register(String name, String password){
        Map<String, Object> res = new HashMap<>();
        // 检查 name, password 格式
        if(StringUtils.isBlank(name)){
            res.put("msg","用户名不能为空");
            return res;
        }
        if(StringUtils.isBlank(password)){
            res.put("msg","密码不能为空");
            return res;
        }
        // TODO 其他格式检查，如敏感词，特殊字符

        // 用户名是否已经存在
        if(userDao.getByName(name) != null){
            res.put("msg","用户名已经存在");
            return res;
        }
        User user = new User();
        user.setName(name);
        user.setSalt(UUID.randomUUID().toString().substring(0,6));
        user.setPassword(QuoraUtil.MD5(password + user.getSalt()));
        user.setProfile(Cons.defaultProfile);

        userDao.addUser(user);
        user.setId(userDao.getByName(name).getId());

        String ticket = addLoginTicket(user.getId());
        res.put("ticket", ticket);
        return res;
    }

    public Map<String, Object> login(String username, String password) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (StringUtils.isBlank(username)) {
            map.put("msg", "用户名不能为空");
            return map;
        }

        if (StringUtils.isBlank(password)) {
            map.put("msg", "密码不能为空");
            return map;
        }

        User user = userDao.getByName(username);

        if (user == null) {
            map.put("msg", "用户名不存在");
            return map;
        }

        if (!QuoraUtil.MD5(password+user.getSalt()).equals(user.getPassword())) {
            map.put("msg", "密码不正确");
            return map;
        }
        // 登录成功后，下发一张t票
        String ticket = addLoginTicket(user.getId());
        map.put("ticket", ticket);
        return map;
    }

    private String addLoginTicket(int userId) {
        LoginTicket ticket = new LoginTicket();
        ticket.setUserId(userId);
        Date date = new Date();
        date.setTime(date.getTime() + 1000*3600*24);
        ticket.setExpired(date);
        ticket.setStatus(0);
        ticket.setTicket(UUID.randomUUID().toString().replaceAll("-", ""));
        loginTicketDao.addTicket(ticket);
        return ticket.getTicket();
    }

    public void logout(String ticket) {
        // 用status来标志数据是否有效
        loginTicketDao.updateStatus(ticket, 1);
    }
}
