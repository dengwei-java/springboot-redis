package com.dw.study.controller;

import com.dw.study.utils.RedisLockUtil;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Author dw
 * @ClassName TestRedisLocalController
 * @Description
 * @Date 2022/6/13 9:16
 * @Version 1.0
 */
@RestController
public class TestRedisLocalController {

    @Autowired
    private RedisLockUtil redisLockUtil;
    private static final  String key = UUID.randomUUID().toString();

    @RequestMapping("testLocal")
    public String testLocal() {
        System.out.println("开始获取锁。。。。。");
        String lockKey = "test:" + key;
        boolean b = redisLockUtil.tryLock(lockKey);
        if (!b) {
            throw new RuntimeException("获取分布式锁失败！");
        }
        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            redisLockUtil.unlock(lockKey);
            System.out.println("释放锁。。。。。");
        }
        return "操作成功";
    }
}
