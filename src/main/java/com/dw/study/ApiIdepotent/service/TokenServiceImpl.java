package com.dw.study.ApiIdepotent.service;

import com.dw.study.ApiIdepotent.ITokenService;
import com.dw.study.utils.RedisUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

/**
 * @Author dw
 * @ClassName TokenServiceImpl
 * @Description
 * @Date 2021/1/30 13:14
 * @Version 1.0
 */
@Service
public class TokenServiceImpl implements ITokenService {

    @Autowired
    RedisUtils redisUtils;

    @Override
    public String createToken() {
        String token = UUID.randomUUID().toString().replaceAll("-", "");
        redisUtils.set(token, token, 10000L);
        return token;
    }

    @Override
    public boolean checkToken(HttpServletRequest request) {
        // 检查请求信息是否携带Token
        String token = request.getHeader("token");
        if (StringUtils.isEmpty(token)) {
            // head中不存在检查parameter
            token = request.getParameter("token");
            if(StringUtils.isEmpty(token)){
                throw new RuntimeException("没有获取到访问接口的Token字段");
            }
        }
        // 检查redis中是否存在key，如果不存在说明之前该接口已经请求过了
        if (!redisUtils.hasKey(token)) {
            throw new RuntimeException("重复请求");
        }
        // 效验成功，删除Token
        boolean remove = redisUtils.del(token);
        /**
         * 这里要注意：不能单纯的直接删除token而不校验token是否删除成功，会出现并发安全问题
         * 在多线程情况下，此时token还未被删除，继续向下执行
         */
        if (!remove) {
            throw new RuntimeException("token delete fail");
        }
        return true;
    }

}
