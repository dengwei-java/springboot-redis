package com.dw.study.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author dw
 * @ClassName TestCacheController
 * @Description 测试基于SpringCache做方法级别的缓存
 * @Date 2021/1/23 21:16
 * @Version 1.0
 */
@RestController
@Slf4j
public class TestCacheController {


    /**
     * @Cacheable 标注在方法上，如果该方法结果存在缓存则使用缓存，否则执行方法并将结果缓存
     * @Cacheable 参数：cacheNames 被缓存的时候的命名空间
     * key	这里的key的优先级是最高的，可以覆盖掉全局配置的key，如果不配置的话使用的就是全局的key
     * keyGenerator	指定的缓存的key的生成器，如果没有在RedisConfig配置默认没有
     * cacheManager	 指定要使用哪个缓存管理器。默认是底层自动配置的管理器（这里在RedisConfig中配置的RedisCacheManager）
     * condition 满足什么条件会进行缓存，里面可以写简单的表达式进行逻辑判断: condition = "#count = 1"
     * unless	满足什么条件不进行缓存，里面可以写简单的表达式进行逻辑判断
     * sync	加入缓存的这个操作是否是同步的
     * value 指定将方法的返回结果放在哪个缓存中，可以指定多个，用大括号保存
     * @return
     */
    @RequestMapping("addCache1")
    @Cacheable(value = "cacheName1")
    public String testAddCache(Integer count){
        log.info("如果我被打印，并且count=2,说明没有走缓存");
        count += 1;
        return "count==" + count;
    }

    @RequestMapping("addCache2")
    @Cacheable(value = "cacheName2")
    public String testAddCache2(Integer count){
        log.info("如果我被打印，并且count=2,说明没有走缓存");
        count += 1;
        return "count==" + count;
    }

    @RequestMapping("addCache3")
    @Cacheable(value = "cacheName3")
    public String testAddCache3(Integer count){
        log.info("如果我被打印，并且count=2,说明没有走缓存");
        count += 1;
        return "count==" + count;
    }

    /**
     *  @CacheEvict 清除缓存，
     *  beforeInvocation 在执行删除方法前就执行清空缓存操作，默认是false，如果删除方法执行报错该注解则不执行
     * allEntries	是否删除该命名空间下面的全部缓存，默认是false
     * @param count
     * @return
     */
    @RequestMapping("removeCache")
    @CacheEvict
    public String testRemoveCache(Integer count){
        log.info("缓存已被删除");
        count += 1;
        return "缓存已被删除count==" + count;
    }

    


}
