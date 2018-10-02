package com.xuef.interceptor;

import com.xuef.dao.LoginTicketDao;
import com.xuef.dao.UserDao;
import com.xuef.model.LoginTicket;
import com.xuef.model.User;
import com.xuef.model.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * 检查cookie的拦截器
 */
@Component
public class PassportInterceptor implements HandlerInterceptor {

    @Autowired
    private LoginTicketDao loginTicketDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserHolder userHolder;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest,
                             HttpServletResponse httpServletResponse,
                             Object o) throws Exception {
        String ticket = null;
        if (httpServletRequest.getCookies() != null) {
            for (Cookie cookie : httpServletRequest.getCookies()) {
                if (cookie.getName().equals("ticket")) {
                    ticket = cookie.getValue();
                    break;
                }
            }
        }
        // 每个已登录user会关联到一张 ticket(记录在数据库 并 写到客户端浏览器的 cookie中)
        if (ticket != null) {
            LoginTicket loginTicket = loginTicketDao.selectByTicket(ticket);
            if (loginTicket == null ||
                    loginTicket.getExpired().before(new Date()) ||
                    loginTicket.getStatus() != 0) {
                return true;
            }

            User user = userDao.getById(loginTicket.getUserId());
            userHolder.setUser(user);
        }
        return true;
    }

    // 页面渲染前
    @Override
    public void postHandle(HttpServletRequest httpServletRequest,
                           HttpServletResponse httpServletResponse,
                           Object o,
                           ModelAndView modelAndView) throws Exception {
        if (modelAndView != null && userHolder.getUser() != null) {
            modelAndView.addObject("user", userHolder.getUser());
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest,
                                HttpServletResponse httpServletResponse,
                                Object o,
                                Exception e) throws Exception {
        userHolder.clear();
    }
}
