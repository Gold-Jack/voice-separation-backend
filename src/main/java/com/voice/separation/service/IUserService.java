package com.voice.separation.service;

import com.voice.separation.pojo.File;
import com.voice.separation.pojo.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author Gold_Jack
 * @since 2022-12-07
 */
public interface IUserService extends IService<User> {
    public String getAuthority(int userId);

    public String getUsernameById(int userId);

    public User getOneByUsername(String username);

    public User updateUserInfo(User user);

    public User createUser(User user);
}
