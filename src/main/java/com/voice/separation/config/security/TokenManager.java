package com.voice.separation.config.security;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.voice.separation.repository.UserRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
 * token管理
 * 目前实现：生成token、移除token
 */
@Component
@Getter // 为了JwtInterceptor解析token时，可以用过get方法获取tokenSignKey
public class TokenManager {

    @Autowired
    private UserRepository userRepository;

    //编码秘钥
    private String tokenSignKey = "2023-03-07 13:07:31";   // 这里暂且用项目开始的日期作为签名

    // 使用jwt根据用户名生成token
    public String genToken(String username) {
//        tokenSignKey = StrUtil.toString(DateUtil.current());
        String token = JWT.create().withAudience(username)
                .withExpiresAt(DateUtil.offsetHour(new Date(), 2))  // token在2小时后过期
                .sign(Algorithm.HMAC256(tokenSignKey));
        return token;
    }
    // 根据token字符串得到用户信息
    public String getUserInfoFromToken(String token) {
        return String.valueOf(JWT.decode(token).getAudience());     // 这里返回的是用户名
    }


    // 删除token
    public boolean removeToken(Object user) {
        // TODO
        return true;
    }


}
