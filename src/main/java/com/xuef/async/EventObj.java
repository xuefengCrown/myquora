package com.xuef.async;

import java.util.HashMap;
import java.util.Map;

/**
 * 事件模型
 * 谁对谁在什么环境下做了什么
 * Created by moveb on 2018/10/5.
 */
public class EventObj {
    private EventType type;
    // 事件主动者
    private int triggerId;
    // 客者
    private int entityType;
    private int entityId;
    // 客者的拥有者
    private int entityOwnerId;
    // 环境
    private Map<String, String> exts = new HashMap<String, String>();

    public EventObj() {
    }

    public EventObj setExt(String key, String value) {
        exts.put(key, value);
        return this;
    }

    public EventObj(EventType type) {
        this.type = type;
    }

    public String getExt(String key) {
        return exts.get(key);
    }


    public EventType getType() {
        return type;
    }

    public EventObj setType(EventType type) {
        this.type = type;
        return this;
    }

    public int getTriggerId() {
        return triggerId;
    }

    public EventObj setTriggerId(int triggerId) {
        this.triggerId = triggerId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public EventObj setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public EventObj setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityOwnerId() {
        return entityOwnerId;
    }

    public EventObj setEntityOwnerId(int entityOwnerId) {
        this.entityOwnerId = entityOwnerId;
        return this;
    }

    public Map<String, String> getExts() {
        return exts;
    }

    public EventObj setExts(Map<String, String> exts) {
        this.exts = exts;
        return this;
    }
}
