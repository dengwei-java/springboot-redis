package com.dw.study.utils;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
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
    public boolean del(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                return redisTemplate.delete(key[0]);
            } else {
                Long delete = redisTemplate.delete(Arrays.asList(key));
                if (delete > 0) {
                    return true;
                }
                return false;
            }
        }
        return false;
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
    public long getKeyExpire(String key) {
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
     *
     * @param key
     * @param field
     * @return
     */
    public boolean hashExists(String key, String field) {
        return redisTemplate.opsForHash().hasKey(key, field);
    }

    /**
     * 给哈希表key中的指定字段的整数值加上增量increment
     *
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
     *
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
     *
     * @param mapName map名字
     * @return
     */
    public Set<Object> hashKeys(String mapName) {
        return redisTemplate.opsForHash().keys(mapName);
    }

    /**
     * 获取hash表中存在的所有的Value
     *
     * @param mapName map名字
     * @return
     */
    public List<Object> hashValues(String mapName) {
        return redisTemplate.opsForHash().values(mapName);
    }

    /**
     * 获取hash表的大小
     *
     * @param mapName
     * @return
     */
    public Long hashSize(String mapName) {
        return redisTemplate.opsForHash().size(mapName);
    }

    // ##########################【操作List类型】#####################################################

    /**
     * 设置值到List中的头部
     *
     * @param key
     * @param value
     * @return
     */
    public Boolean listAddInHead(String key, Object value) {
        try {
            redisTemplate.opsForList().leftPush(key, value);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    /**
     * 批量设置值到List中的头部
     *
     * @param key    List名字
     * @param values
     * @return
     */
    public Boolean listAddAllInHead(String key, Collection<Object> values) {
        try {
            redisTemplate.opsForList().leftPushAll(key, values);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    /**
     * 如果存在List->key, 则设置值到List中的头部
     *
     * @param key   List名字
     * @param value
     * @return
     */
    public Boolean listAddIfPresent(String key, Object value) {
        try {
            redisTemplate.opsForList().leftPushIfPresent(key, value);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    /**
     * 设置值到List中的尾部
     *
     * @param key   List名字
     * @param value
     * @return
     */
    public Boolean listAddInEnd(String key, Object value) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    /**
     * 批量设置值到List中的尾部
     *
     * @param key    List名字
     * @param values
     * @return
     */
    public Boolean listAddAllInEnd(String key, Collection<Object> values) {
        try {
            redisTemplate.opsForList().rightPushAll(key, values);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    /**
     * 通过索引去设置List->key中的值
     *
     * @param key
     * @param index
     * @param value
     * @return
     */
    public Boolean listAddByIndex(String key, long index, Object value) {
        try {
            redisTemplate.opsForList().set(key, index, value);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }


    /**
     * 根据索引获取list中的值
     *
     * @param key   list名字
     * @param index
     * @return
     */
    public Object listGetByIndex(String key, long index) {
        return redisTemplate.opsForList().index(key, index);
    }

    /**
     * 根据索引范围获取list中的值
     *
     * @param key   list名字
     * @param start
     * @param end
     * @return
     */
    public List<Object> listGetByRange(String key, long start, long end) {
        return redisTemplate.opsForList().range(key, start, end);
    }

    /**
     * 移除并获取列表中第一个元素(如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止)
     *
     * @param key list名字
     * @return
     */
    public Object listLeftPop(String key) {
        return redisTemplate.opsForList().leftPop(key);
    }

    /**
     * 移除并获取列表中最后一个元素(如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止)
     *
     * @param key list名字
     * @return
     */
    public Object listRightPop(String key) {
        return redisTemplate.opsForList().rightPop(key);
    }

    /**
     * 删除集合中值等于value的元素(
     * index=0, 删除所有值等于value的元素;
     * index>0, 从头部开始删除第一个值等于value的元素;
     * index<0, 从尾部开始删除第一个值等于value的元素)
     *
     * @param key
     * @param index
     * @param value
     * @return
     */
    public Long listRemove(String key, long index, Object value) {
        Long removeNum = redisTemplate.opsForList().remove(key, index, value);
        return removeNum;
    }

// ##########################【操作Set类型】#####################################################

    /**
     * 设置值到Set集合(支持批量)
     *
     * @param key
     * @param value
     * @return
     */
    public Boolean setAdd(String key, Object... value) {
        try {
            redisTemplate.opsForSet().add(key, value);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    /**
     * 移除Set集合中的值，支持批量
     *
     * @param key
     * @param values
     * @return 移除的数量
     */
    public long setRemove(String key, Object... values) {
        return redisTemplate.opsForSet().remove(key, values);
    }

    /**
     * 判断Set中是否存在value
     *
     * @param key
     * @param value
     * @return
     */
    public boolean setIsExist(String key, Object value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }

    // ##########################【操作经纬度】#####################################################

    /***
     * 将指定的地理空间位置（纬度、经度、名称）添加到指定的key中(添加单个经纬度)。
     * 命令行命令： GEOADD key longitude latitude member [longitude latitude member ...]
     * @param key redis的key
     * @param longitude   经度
     * @param latitude   纬度
     * @param member  该坐标的名称（标识）
     * @return
     */
    public Long geoAdd(String key, double longitude, double latitude, String member) {
//        Long addedNum = redisTemplate.opsForGeo().add("city", new Point(116.405285, 39.904989), "北京");
        return redisTemplate.opsForGeo().add(key, new Point(longitude, latitude), member);
    }

    /***
     * 将指定的地理空间位置（纬度、经度、名称）添加到指定的key中(批量添加经纬度)。
     * @param key redis的key
     * @param geoLocationList  批量添加的经纬度
     * @return
     */
    public Long geoBatchAdd(String key, List<BizGeoLocation> geoLocationList) {
        if (CollectionUtils.isEmpty(geoLocationList)) {
            return 0L;
        }
        List<RedisGeoCommands.GeoLocation<Object>> locations = new ArrayList<>(geoLocationList.size());
        for (BizGeoLocation geoLocation : geoLocationList) {
            locations.add(new RedisGeoCommands.GeoLocation<Object>(geoLocation.getBizKey(), new Point(geoLocation.getLongitude(), geoLocation.getLatitude())));
        }
        return redisTemplate.opsForGeo().add(key, locations);
    }


    /***
     * 从key里返回所有给定位置元素的位置（经度和纬度）。
     * Redis命令： GEOPOS key member [member ...]
     * @param key redis的key
     * @param memberList 坐标名称（标识）的集合
     */
    public List<Point> geoGet(String key, List<String> memberList) {
        return redisTemplate.opsForGeo().position(key, memberList);
    }


    /***
     * 【获取两个坐标之间的距离】
     * 根据redis中键名（key）中，名字为 name1 和 name2 两个坐标的距离
     * Redis命令： GEODIST key member1 member2 [unit]
     * unit:
     * - m：表示单位为米
     * - km：表示单位为千米
     * - mi：表示单位为英里
     * - ft：表示单位为英尺
     * @param key redis的key
     * @param name1 坐标名称(标识)1
     * @param name2 坐标名称（标识）2
     * @return distance(单位米)
     */
    public double geoGetDistance(String key, String name1, String name2) {
        return redisTemplate.opsForGeo()
                .distance(key, name1, name2, RedisGeoCommands.DistanceUnit.METERS).getValue();
    }


    /***
     * 【获取指定范围内的坐标】
     * 以给定的经纬度为中心画圆， 返回键（key）包含的位置元素当中，
     * 与中心的距离不超过给定最大距离的所有位置元素，并给出所有位置元素与中心的平均距离。
     * Redis命令： GEORADIUS key longitude latitude radius m|km|ft|mi [WITHCOORD] [WITHDIST] [WITHHASH] [ASC|DESC] [COUNT count]
     * - key longitude latitude： 是前置条件，给定的经纬度信息，以及我要搜索的key
     * - radius ：距离半径，指的搜索的范围
     * - m|km|ft|mi： 为给定的距离单位，有 米、千米、英尺、英里 4种
     * - [WITHCOORD] [WITHDIST] [WITHHASH]： 为返回的信息类型
     * - WITHDIST： 在返回位置元素的同时， 将位置元素与中心之间的距离也一并返回。距离的单位和用户给定的范围单位保持一致。
     * - WITHCOORD： 将位置元素的经度和维度也一并返回。
     * - WITHHASH： 以 52 位有符号整数的形式， 返回位置元素经过原始 geohash 编码的有序集合分值。这个选项主要用于底层应用或者调试， 实际中的作用并不大。
     * - ASC|DESC ：可选参数，按照距离升序或者降序排列，即 由近到远（asc） 还是 由远到近（desc）
     * - COUNT count：取数数量，避免获取到太多的信息，返回太多信息
     * @param key redis的key
     * @param longitude   经度
     * @param latitude   纬度
     * @param distance 距离(单位：米)
     * @param count 如果 count > 0 则最多返回count个坐标， 否则返回所有
     * @return
     */
    public GeoResults<RedisGeoCommands.GeoLocation<Object>> geoGetCoordinatesWithinRange(String key,
                                                                                         double longitude,
                                                                                         double latitude,
                                                                                         Integer distance,
                                                                                         Integer count) {
        //以当前坐标为中心画圆，标识当前坐标覆盖的distance的范围， Point(经度, 纬度) Distance(距离量, 距离单位)
        Circle circle = new Circle(new Point(longitude, latitude), new Distance(distance, RedisGeoCommands.DistanceUnit.METERS));
        // 从redis获取的信息包含：距离中心坐标的距离、当前的坐标、并且升序排序，如果count > 0 则只取count个坐标，否则返回所有
        RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs
                .newGeoRadiusArgs().includeDistance().includeCoordinates().sortAscending();
        if (count > 0) {
            args.limit(count);
        }
        return redisTemplate.opsForGeo().radius(key, circle, args);
    }

    /***
     * 【获取指定范围内的坐标】
     * 以给定的键（key）中的坐标名字（标识）name为中心画圆， 返回键包含的位置元素当中，
     * 与中心的距离不超过给定最大距离的所有位置元素，并给出所有位置元素与中心的平均距离。
     * @param key redis的key
     * @param name 坐标名称(标识)
     * @param distance 距离
     * @param count 如果 count > 0 则最多返回count个坐标， 否则返回所有
     * @return
     */
    public GeoResults<RedisGeoCommands.GeoLocation<Object>> geoGetCoordinatesWithinRange(String key,
                                                                                         String name,
                                                                                         Integer distance,
                                                                                         Integer count) {
        // 创建距离对象
        Distance distances = new Distance(distance, RedisGeoCommands.DistanceUnit.METERS);
        // 需要从redis获取的参数
        RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs
                .newGeoRadiusArgs().includeDistance().includeCoordinates().sortAscending();
        if (count > 0) {
            args.limit(count);
        }
        return redisTemplate.opsForGeo().radius(key, name, distances, args);
    }

    /**
     * 经纬度范围业务数据
     */
    @Data
    private static class BizGeoLocation {
        /**
         * 业务唯一标识
         */
        private String bizKey;
        /**
         * 经度
         */
        private Double longitude;
        /**
         * 纬度
         */
        private Double latitude;
    }

}
