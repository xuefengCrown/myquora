package com.xuef.service;

import com.xuef.dao.JedisAdapter;
import com.xuef.util.RedisKeyGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by moveb on 2018/10/6.
 */
@Service
public class FollowingService {
    @Autowired
    JedisAdapter jedisAdapter;

    public boolean follow(int userId, int entityType, int entityId){
        // 用户关注的集合 的key
        String followingSetKey = RedisKeyGenerator.getFollowingSetKey(userId, entityType);
        // 实体的fans set 的key
        String fansSetKey = RedisKeyGenerator.getFansSetKey(entityType, entityId);
        Date date = new Date();

        Jedis jedis = jedisAdapter.getJedis();
        Transaction tx = jedisAdapter.multi(jedis);
        // 更新实体的粉丝列表(将关注者加入实体的粉丝集合中)
        tx.zadd(fansSetKey, date.getTime(), String.valueOf(userId));
        // 更新用户的关注列表
        tx.zadd(followingSetKey, date.getTime(), String.valueOf(entityId));
        List<Object> ret = jedisAdapter.exec(tx, jedis);
        return ret.size() == 2 && (Long) ret.get(0) > 0 && (Long) ret.get(1) > 0;
    }

    public boolean unfollow(int userId, int entityType, int entityId){
        // 用户关注的集合 的key
        String followingSetKey = RedisKeyGenerator.getFollowingSetKey(userId, entityType);
        // 实体的fans set 的key
        String fansSetKey = RedisKeyGenerator.getFansSetKey(entityType, entityId);
        Jedis jedis = jedisAdapter.getJedis();
        Transaction tx = jedisAdapter.multi(jedis);
        // 更新实体的粉丝列表(将关注者从实体的粉丝集合中删除)
        tx.zrem(fansSetKey, String.valueOf(userId));
        // 更新用户的关注列表
        tx.zrem(followingSetKey, String.valueOf(entityId));

        List<Object> ret = jedisAdapter.exec(tx, jedis);
        return ret.size() == 2 && (Long) ret.get(0) > 0 && (Long) ret.get(1) > 0;
    }

    public List<Integer> getFans(int entityType, int entityId, int count) {
        String fansSetKey = RedisKeyGenerator.getFansSetKey(entityType, entityId);
        return set2list(jedisAdapter.zrevrange(fansSetKey, 0, count));
    }

    public List<Integer> getFans(int entityType, int entityId, int offset, int count) {
        String followerKey = RedisKeyGenerator.getFansSetKey(entityType, entityId);
        return set2list(jedisAdapter.zrevrange(followerKey, offset, offset+count));
    }

    public List<Integer> getFollowings(int userId, int entityType, int count) {
        String followeeKey = RedisKeyGenerator.getFollowingSetKey(userId, entityType);
        return set2list(jedisAdapter.zrevrange(followeeKey, 0, count));
    }

    public List<Integer> getFollowings(int userId, int entityType, int offset, int count) {
        String followeeKey = RedisKeyGenerator.getFollowingSetKey(userId, entityType);
        return set2list(jedisAdapter.zrevrange(followeeKey, offset, offset+count));
    }

    public long getFansCount(int entityType, int entityId) {
        String followerKey = RedisKeyGenerator.getFansSetKey(entityType, entityId);
        return jedisAdapter.zcard(followerKey);
    }

    public long getFollowingCount(int userId, int entityType) {
        String followeeKey = RedisKeyGenerator.getFollowingSetKey(userId, entityType);
        return jedisAdapter.zcard(followeeKey);
    }

    private List<Integer> set2list(Set<String> set) {
        List<Integer> ids = new ArrayList<>();
        for (String str : set) {
            ids.add(Integer.parseInt(str));
        }
        return ids;
    }

    /**
     *  用户是否关注了某实体
     * @param userId
     * @param entityType
     * @param entityId
     * @return
     */
    public boolean isFans(int userId, int entityType, int entityId) {
        String followerKey = RedisKeyGenerator.getFansSetKey(entityType, entityId);
        return jedisAdapter.zscore(followerKey, String.valueOf(userId)) != null;
    }
}
