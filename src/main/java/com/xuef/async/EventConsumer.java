package com.xuef.async;

import com.alibaba.fastjson.JSON;
import com.xuef.dao.JedisAdapter;
import com.xuef.util.RedisKeyGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by moveb on 2018/10/5.
 */
@Service
public class EventConsumer implements InitializingBean, ApplicationContextAware {
    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    // 对于每种事件，会有很多种Handler对其感兴趣，我们需要建立起这种映射!
    private Map<EventType, List<EventHandler>> eventMapping = new HashMap<EventType, List<EventHandler>>();
    private ApplicationContext applicationContext;

    @Autowired
    JedisAdapter jedisAdapter;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
    /**
     * Consumer需要根据事件类型做分派，以调用适当的Handler。
     * 所以我们得在EventConsumer初始化时，建立起 事件类型-->Handler 的映射
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        // get beans of all EventHandler
        // key: bean name,value: bean instance
        Map<String, EventHandler> handlerBeans = applicationContext.getBeansOfType(EventHandler.class);
        if(handlerBeans != null){
            for(Map.Entry<String, EventHandler> entry : handlerBeans.entrySet()){
                // e is an entry contains key && value
                EventHandler handler = entry.getValue();
                List<EventType> supportEventTypes = handler.getSupportEventTypes();
                for(EventType type : supportEventTypes){
                    if(!eventMapping.containsKey(type)){
                        eventMapping.put(type, new ArrayList<>());
                    }
                    eventMapping.get(type).add(handler);
                }
            }
        }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    String key = RedisKeyGenerator.getEventQueueKey();
                    List<String> event = jedisAdapter.brpop(0, key);
                    for (String p : event) {
                        if (!p.equals(key)) {
                            EventObj eventModel = JSON.parseObject(p, EventObj.class);
                            if (!eventMapping.containsKey(eventModel.getType())) {
                                logger.error("event not support");
                                continue;
                            }
                            for (EventHandler handler : eventMapping.get(eventModel.getType())) {
                                handler.doHandle(eventModel);
                            }
                        }
                    }
                }
            }
        });
        thread.start();
    }
}
