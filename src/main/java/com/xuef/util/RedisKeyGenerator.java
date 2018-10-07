package com.xuef.util;

/**
 * Created by moveb on 2018/10/4.
 */
public class RedisKeyGenerator {
    private static String PROJECT_NAME = "quora";
    private static String SPLIT = ":";
    private static String BIZ_LIKE = "LIKE";
    private static String BIZ_DISLIKE = "DISLIKE";
    private static String BIZ_EVENTQUEUE = "EVENT_QUEUE";
    private static String SET_FOLLOWING = "FOLLOWING";
    private static String SET_FANS = "FANS";

    public static String getLikeKey(int entityType, int entityId) {
        return PROJECT_NAME + SPLIT + BIZ_LIKE + SPLIT + String.valueOf(entityType) + SPLIT + String.valueOf(entityId);
    }

    public static String getDisLikeKey(int entityType, int entityId) {
        return PROJECT_NAME + SPLIT + BIZ_DISLIKE + SPLIT + String.valueOf(entityType) + SPLIT + String.valueOf(entityId);
    }

    public static String getEventQueueKey() {
        return PROJECT_NAME + SPLIT + BIZ_EVENTQUEUE;
    }

    /**
     * 用户对某类实体的关注集合的 key
     * (目前的潜在的关注者只可能是 用户)
     * @param userId
     * @return
     */
    public static String getFollowingSetKey(int userId, int entityType){
        return PROJECT_NAME + SPLIT + SET_FOLLOWING + SPLIT + String.valueOf(userId) + SPLIT + String.valueOf(entityType);
    }
    public static String getFansSetKey(int entityType, int entityId){
        return PROJECT_NAME + SPLIT + SET_FANS + SPLIT + String.valueOf(entityType) + SPLIT + String.valueOf(entityId);
    }
}
