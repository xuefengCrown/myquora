package com.xuef.async;

import java.util.List;

/**
 * Created by moveb on 2018/10/5.
 */
public interface EventHandler {
    void doHandle(EventObj eventObj);
    List<EventType> getSupportEventTypes();
}
