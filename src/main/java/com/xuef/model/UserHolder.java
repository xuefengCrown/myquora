package com.xuef.model;

import org.springframework.stereotype.Component;

/**
 * Created by moveb on 2018/10/2.
 */
@Component // 将对象交给 Spring来管理
public class UserHolder {
    // 相当于 Map<ThreadID, User>
    static ThreadLocal<User> users = new ThreadLocal<>();

    public User getUser() {
        return users.get();
    }

    public void setUser(User user) {
        users.set(user);
    }

    public void clear() {
        users.remove();
    }
}
