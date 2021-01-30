package com.dw.study.controller;

import com.dw.study.ApiIdepotent.ITokenService;
import com.dw.study.ApiIdepotent.MyAnnotations.ApiIdempotent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Author dw
 * @ClassName TestApiIdempotentController
 * @Description  测试接口幂等性
 * @Date 2021/1/30 22:21
 * @Version 1.0
 */
@RestController
public class TestApiIdempotentController {

    @Resource
    private ITokenService tokenService;

    @GetMapping("/getToken")
    public String getToken() {
        String token = tokenService.createToken();
        return token;
    }


    @ApiIdempotent
    @PostMapping("/testIdempotent")
    public String testIdempotent() {
        try {
            // 模拟业务执行耗时
            Thread.sleep(500);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "SUCCESS";
    }

}
