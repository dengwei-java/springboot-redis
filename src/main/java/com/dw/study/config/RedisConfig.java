package com.dw.study.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.lang.reflect.Method;
import java.time.Duration;

/**
 * @Author
 * @ClassName RedisConfig
 * @Description redis配置： 如果需要使用SpringCache 方法级别的缓存需要加上@EnableCaching开启对缓存的支持，
 * 并且需要继承CachingConfigurerSupport以配置基于方法级别的缓存配置，如果只是单纯的使用Redis做缓存，不基于SpringCache框架，则不用配置
 * 只需要配置Redis的序列化方式
 * @Date 2021/1/20 19:43
 * @Version 1.0
 */
@Configuration
//@EnableCaching开启对SpringCache的支持（提供基于方法级别的缓存）
@EnableCaching
@Slf4j
public class RedisConfig extends CachingConfigurerSupport{

    /**
     * 设置Redis序列化方式，默认使用的JDKSerializer的序列化方式，效率低，这里我们使用 FastJsonRedisSerializer
     * @param redisConnectionFactory
     * @return
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        // key序列化
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        // value序列化
        redisTemplate.setValueSerializer(new FastJsonRedisSerializer<>(Object.class));
        // Hash key序列化
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        // Hash value序列化
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }


    /**
     * 配置SpringCache基于方法级别的缓存，key的生成策略的配置: 类名+方法名+参数列表的类型+参数值 再做 哈希散列 作为key
     *
     * @return KeyGenerator
     */
    @Bean
    @Override
    public KeyGenerator keyGenerator() {
        log.info("RedisCacheConfig.keyGenerator()");
        return new KeyGenerator() {
            @Override
            public Object generate(Object o, Method method, Object... objects) {
                StringBuilder sb = new StringBuilder();
                sb.append(o.getClass().getSimpleName())
                        .append(":")
                        .append(method.getName())
                        .append(":");
                for (Object obj : objects) {
                    if (null != obj) {// 替换字符串
                        String objKey = JSON.toJSONString(obj);
                        objKey = objKey.replace(":", "=");
                        sb.append(objKey);
                    }
                }
                return sb.toString();
            }
        };
    }


    /**
     * 配置SpringCache基于方法级别的缓存的过期时间、key-value的序列化方式
     * @param connectionFactory
     * @return
     */
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        //redis缓存配置
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                //缓存生存时间60秒
                .entryTtl(Duration.ofSeconds(60))
                // 配置Key序列化（解决乱码的问题）
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                // 配置Value序列化
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new FastJsonRedisSerializer<>(Object.class)))
                // 不缓存空值
                .disableCachingNullValues();
        //根据redis缓存配置和redis连接工厂生成redis缓存管理器
        RedisCacheManager redisCacheManager = RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .build();
        log.debug("自定义RedisCacheManager加载完成");
        return redisCacheManager;
    }



}
