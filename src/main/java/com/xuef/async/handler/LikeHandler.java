package com.xuef.async.handler;

import com.xuef.async.EventHandler;
import com.xuef.async.EventObj;
import com.xuef.async.EventType;
import com.xuef.model.User;
import com.xuef.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Created by moveb on 2018/10/5.
 */
@Component // 交给Spring管理
public class LikeHandler implements EventHandler {
    private static final Logger logger = LoggerFactory.getLogger(LikeHandler.class);
    @Autowired
    UserService userService;

    @Override
    public void doHandle(EventObj eventObj) {
        User user = userService.getUserById(eventObj.getTriggerId());
        logger.info(user.getName() + "刚刚对" + eventObj.getEntityId() + "号实体点赞了");
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        // 目前对点赞事件感兴趣
        return Arrays.asList(EventType.LIKE);
    }
}
