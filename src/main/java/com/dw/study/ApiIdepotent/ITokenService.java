package com.dw.study.ApiIdepotent;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author dw
 * @ClassName ITokenService
 * @Description 创建Token的接口，主要是生成接口的唯一Token、并且校验Token
 * @Date 2021/1/30 13:10
 * @Version 1.0
 */
public interface ITokenService {

    /**
     * 创建Token
     * @return
     */
    String createToken();

    /**
     * 效验Token
     * @param request
     * @return
     */
    boolean checkToken(HttpServletRequest request);
}
