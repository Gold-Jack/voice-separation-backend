package com.voice.separation.config.interceptor;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.voice.separation.config.security.TokenManager;
import com.voice.separation.pojo.User;
import com.voice.separation.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.management.ServiceNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

/**
 * jwt拦截器，用token验证方式，拦截非正常登陆的token持有者
 * @author GoldJack
 * @since 2022/7/14
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Resource
    private UserRepository userRepository;
    @Autowired
    private TokenManager tokenManager;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        // 从请求头获取token
        String token = request.getHeader("token");

        // 如果handler不一致，直接返回true
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        // 是否有token
        if (token == null) {
            throw new RuntimeException("未找到token，请获取token后再进行登陆操作");
        }

        // 是否能从token中正确解析出userId
        String userId;
        try {
            userId = JWT.decode(token).getAudience().get(0);
//            System.out.println(employeeId);
        } catch (JWTDecodeException j) {
            throw new RuntimeException("token解析错误，请重新登陆");
        }

        // 判断从数据库中是否能根据userId取出对应的User信息
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent())
            throw new ServiceNotFoundException("用户不存在，请重新登陆");

        // 密码加签验证 token
        JWTVerifier jwtVerifier = JWT.require(
                Algorithm.HMAC256(tokenManager.getTokenSignKey())).build();
        try {
            jwtVerifier.verify(token);
        } catch (JWTVerificationException e) {
            throw new ServiceNotFoundException("token加签验证失败，请重新登陆");
        }
        return true;
    }

}
