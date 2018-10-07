package com.xuef.async;

import com.alibaba.fastjson.JSON;
import com.xuef.dao.JedisAdapter;
import com.xuef.util.RedisKeyGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by moveb on 2018/10/5.
 */
@Service
public class EventProducer {
    private static final Logger logger = LoggerFactory.getLogger(EventProducer.class);

    @Autowired
    JedisAdapter jedisAdapter;

    public void fireEvent(EventObj eventObj){
        try {
            // 将事件序列化后丢入事件队列
            String jsonObj = JSON.toJSONString(eventObj);
            jedisAdapter.lpush(RedisKeyGenerator.getEventQueueKey(), jsonObj);
        }catch (Exception e) {
            logger.error("事件入队失败: " + e.getMessage());
        }
    }
}
