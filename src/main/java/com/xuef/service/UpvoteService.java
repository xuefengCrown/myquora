package com.xuef.service;

import com.xuef.dao.JedisAdapter;
import com.xuef.util.RedisKeyGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by moveb on 2018/10/4.
 */
@Service
public class UpvoteService {
    @Autowired
    JedisAdapter jedisAdapter;

    public long getLikeCount(int entityType, int entityId) {
        String likeSetKey = RedisKeyGenerator.getLikeKey(entityType, entityId);
        return jedisAdapter.scard(likeSetKey);
    }

    /**
     *
     * @param userId
     * @param entityType
     * @param entityId
     * @return 1: like; -1: dislike; 0: don't care;
     */
    public int getLikeStatus(int userId, int entityType, int entityId) {
        String likeSetKey = RedisKeyGenerator.getLikeKey(entityType, entityId);
        if (jedisAdapter.sismember(likeSetKey, String.valueOf(userId))) {
            return 1;
        }
        String disLikeKey = RedisKeyGenerator.getDisLikeKey(entityType, entityId);
        return jedisAdapter.sismember(disLikeKey, String.valueOf(userId)) ? -1 : 0;
    }

    /**
     * 点赞
     * @param userId
     * @param entityType
     * @param entityId
     * @return 目前喜欢该实体的总人数
     */
    public long like(int userId, int entityType, int entityId) {
        String likeSetKey = RedisKeyGenerator.getLikeKey(entityType, entityId);
        jedisAdapter.sadd(likeSetKey, String.valueOf(userId));

        String disLikeKey = RedisKeyGenerator.getDisLikeKey(entityType, entityId);
        jedisAdapter.srem(disLikeKey, String.valueOf(userId));

        return jedisAdapter.scard(likeSetKey);
    }

    public long disLike(int userId, int entityType, int entityId) {
        String disLikeKey = RedisKeyGenerator.getDisLikeKey(entityType, entityId);
        jedisAdapter.sadd(disLikeKey, String.valueOf(userId));

        String likeSetKey = RedisKeyGenerator.getLikeKey(entityType, entityId);
        jedisAdapter.srem(likeSetKey, String.valueOf(userId));

        return jedisAdapter.scard(likeSetKey);
    }
}
