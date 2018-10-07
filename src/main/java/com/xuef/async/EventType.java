package com.xuef.async;

/**
 * 事件类型
 * Created by moveb on 2018/10/5.
 */
public enum EventType {
    LOGIN(1),
    COMMENT(2),
    LIKE(3);

    private int val;
    EventType(int val) { this.val = val; }
    public int getVal() { return val; }
}
