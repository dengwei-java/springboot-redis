package com.dw.study.ApiIdepotent.MyIntercepters;

import com.dw.study.ApiIdepotent.MyAnnotations.ApiIdempotent;
import com.dw.study.ApiIdepotent.service.TokenServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @Author dw
 * @ClassName ApiIdempotentInterceptor
 * @Description 接口幂等性拦截器
 * @Date 2021/1/30 21:52
 * @Version 1.0
 */
@Component
public class ApiIdempotentInterceptor implements HandlerInterceptor {


    @Autowired
    private TokenServiceImpl tokenService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        ApiIdempotent apiIdempotent = method.getAnnotation(ApiIdempotent.class);
        if (apiIdempotent != null) {
            // 幂等性校验，通过则放行；失败抛出异常，统一异常处理返回友好提示
            tokenService.checkToken(request);
        }
        // 这里必须返回true，否则会拦截一切请求
        return true;
    }

}
