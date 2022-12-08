package com.voice.separation.service.impl;

import cn.hutool.core.date.DateUtil;
import com.voice.separation.pojo.User;
import com.voice.separation.mapper.UserMapper;
import com.voice.separation.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Date;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author Gold_Jack
 * @since 2022-12-07
 */
@Service
@Primary
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Resource
    private UserMapper userMapper;

    public String getAuthority(int userId) {
        return userMapper.getAuthority(userId);
    }

    @Override
    public String getUsernameById(int userId) {
        return userMapper.getUsernameById(userId);
    }

    @Override
    public User getOneByUsername(String username) {
        return userMapper.getOneByUsername(username);
    }

    @Override
    public User updateUserInfo(User user) {
        userMapper.updateById(user);
        return user;
    }

    @Override
    public User createUser(User user) {
        userMapper.insert(user);
        return user;
    }


}
