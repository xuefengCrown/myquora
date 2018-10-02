package com.xuef.service;

import com.xuef.constant.Cons;
import com.xuef.dao.UserDao;
import com.xuef.model.User;
import com.xuef.util.WendaUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by moveb on 2018/10/2.
 */
@Service
public class UserService {
    @Autowired
    private UserDao userDao;

    public Map<String, String> register(String name, String password){
        Map<String, String> res = new HashMap<>();
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
        user.setPassword(WendaUtil.MD5(password + user.getSalt()));
        user.setProfile(Cons.defaultProfile);
        userDao.addUser(user);
        return res;
    }


}
