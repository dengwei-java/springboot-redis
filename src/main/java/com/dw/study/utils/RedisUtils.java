package com.dw.study.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Author
 * @ClassName RedisUtils
 * @Description
 * @Date 2021/1/20 12:22
 * @Version 1.0
 */
@Component
public class RedisUtils {

    private final static Logger log = LoggerFactory.getLogger(RedisUtils.class);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


// ##########################【操作String类型】#####################################################

    /**
     * 设置缓存
     *
     * @param key
     * @param value
     * @return
     */
    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    /**
     * 设置值并设置过期时间（单位秒）
     *
     * @param key
     * @param value
     * @param time  过期时间
     * @return
     */
    public boolean set(String key, Object value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    /**
     * 设置一个已经存在的key的值，并返回旧值
     *
     * @param key
     * @param value
     * @return
     */
    public Object getAndSet(String key, Object value) {
        try {
            Object andSet = redisTemplate.opsForValue().getAndSet(key, value);
            return andSet;
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    /**
     * 如果不存在则设置值value，返回true。 否则返回false
     *
     * @param key
     * @param value
     * @return
     */
    public boolean setIfAbsent(String key, String value) {
        try {
            return redisTemplate.opsForValue().setIfAbsent(key, value);
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    /**
     * 批量设置 k->v 到 redis
     *
     * @param valueMap
     * @return
     */
    public boolean multiSet(HashMap valueMap) {
        try {
            redisTemplate.opsForValue().multiSet(valueMap);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    /**
     * 如果不存在对应的Map，则批量设置 k->v 到 redis
     *
     * @param valueMap
     * @return
     */
    public boolean multiSetIfAbsent(HashMap valueMap) {
        try {
            redisTemplate.opsForValue().multiSetIfAbsent(valueMap);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }


    /**
     * 在原有的值基础上新增字符串到末尾
     *
     * @param key
     * @param value
     * @return
     */
    public boolean append(String key, String value) {
        try {
            redisTemplate.opsForValue().append(key, value);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }


    /**
     * 获取value
     *
     * @param key
     * @return
     */
    public Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     * 批量获取值
     *
     * @param keys
     * @return
     */
    public List<Object> multiGet(Collection<String> keys) {
        if (CollectionUtils.isEmpty(keys)) {
            return null;
        }
        return redisTemplate.opsForValue().multiGet(keys);
    }


    /**
     * 删除缓存，支持批量删除
     *
     * @param key
     */
    public void del(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                redisTemplate.delete(Arrays.asList(key));
            }
        }
    }

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    public boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    /**
     * 根据key 获取key的过期时间
     *
     * @param key 键 不能为null
     * @return 时间(秒) 返回-1, 代表为永久有效
     */
    public long getkeyExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间(秒)
     * @return
     */
    public boolean expireKey(String key, long time) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    /**
     * 通过increment(K key, long increment)方法以增量方式存储long值（正值则自增，负值则自减）
     *
     * @param key
     * @param increment
     * @return
     */
    public void increment(String key, long increment) {
        redisTemplate.opsForValue().increment(key, increment);
    }

    /**
     * 通过increment(K key, double increment)方法以增量方式存储double值（正值则自增，负值则自减）
     *
     * @param key
     * @param increment
     * @return
     */
    public void increment(String key, double increment) {
        redisTemplate.opsForValue().increment(key, increment);
    }

    /**
     * 修改redis中key的名称
     *
     * @param oldKey
     * @param newKey
     */
    public void renameKey(String oldKey, String newKey) {
        redisTemplate.rename(oldKey, newKey);
    }

    /**
     * 如果旧值key存在时，将旧值改为新值
     *
     * @param oldKey
     * @param newKey
     * @return
     */
    public Boolean renameOldKeyIfAbsent(String oldKey, String newKey) {
        return redisTemplate.renameIfAbsent(oldKey, newKey);
    }

    // ##########################【操作Hash类型】#####################################################

    /**
     * 批量添加Map中的键值对
     *
     * @param mapName map名字
     * @param maps
     */
    public boolean hashPutAll(String mapName, Map<String, String> maps) {
        try {
            redisTemplate.opsForHash().putAll(mapName, maps);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }


    /**
     * 添加一个键值对
     *
     * @param mapName
     * @param key
     * @param value
     */
    public boolean hashPutOne(String mapName, String key, String value) {
        try {
            redisTemplate.opsForHash().put(mapName, key, value);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    /**
     * 添加一个键值对,仅当hashKey不存在时才设置
     *
     * @param mapName
     * @param hashKey
     * @param value
     */
    public boolean hashPutOneIfAbsent(String mapName, String hashKey, String value) {
        try {
            redisTemplate.opsForHash().putIfAbsent(mapName, hashKey, value);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    /**
     * 获取mapName中的所有的键值对
     *
     * @param mapName Map名字
     * @return
     */
    public Object hashGetOne(String mapName, Object hashKey) {
        return redisTemplate.opsForHash().get(mapName, hashKey);
    }

    /**
     * 获取mapName中的所有的键值对
     *
     * @param mapName Map名字
     * @return
     */
    public Map<Object, Object> hashGetAll(String mapName) {
        return redisTemplate.opsForHash().entries(mapName);
    }


    /**
     * 删除一个或者多个hash表字段
     *
     * @param key
     * @param fields
     * @return
     */
    public Long hashDelete(String key, Object... fields) {
        return redisTemplate.opsForHash().delete(key, fields);
    }

    /**
     * 查看hash表中指定字段是否存在
     * @param key
     * @param field
     * @return
     */
    public boolean hashExists(String key, String field) {
        return redisTemplate.opsForHash().hasKey(key, field);
    }

    /**
     * 给哈希表key中的指定字段的整数值加上增量increment
     * @param key
     * @param field
     * @param increment
     * @return
     */
    public Long hashIncrementByLong(String key, Object field, long increment) {
        return redisTemplate.opsForHash().increment(key, field, increment);
    }

    /**
     * 给哈希表key中的指定字段的double加上增量increment
     * @param key
     * @param field
     * @param delta
     * @return
     */
    public Double hashIncrementByDouble(String key, Object field, double delta) {
        return redisTemplate.opsForHash().increment(key, field, delta);
    }

    /**
     * 获取hash表中存在的所有的key
     * @param mapName map名字
     * @return
     */
    public Set<Object> hashKeys(String mapName) {
        return redisTemplate.opsForHash().keys(mapName);
    }

    /**
     * 获取hash表中存在的所有的Value
     * @param mapName map名字
     * @return
     */
    public List<Object> hashValues(String mapName) {
        return redisTemplate.opsForHash().values(mapName);
    }

    /**
     * 获取hash表的大小
     * @param mapName
     * @return
     */
    public Long hashSize(String mapName) {
        return redisTemplate.opsForHash().size(mapName);
    }

    // ##########################【操作List类型】#####################################################





}
