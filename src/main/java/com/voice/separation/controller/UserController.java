package com.voice.separation.controller;

import cn.hutool.core.util.StrUtil;
import com.voice.separation.config.security.TokenManager;
import com.voice.separation.pojo.User;
import com.voice.separation.service.IUserService;
import com.voice.separation.service.impl.UserServiceImpl;
import com.voice.separation.util.R;
import com.voice.separation.util.ResponseCode;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;

import static com.voice.separation.util.ResponseCode.*;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author Gold_Jack
 * @since 2022-12-07
 */
@RestController
@RequestMapping("/user/")
public class UserController {

    @Autowired
    public IUserService userService;
    @Autowired
    public PasswordEncoder passwordEncoder;
    @Autowired
    public TokenManager tokenManager;

    @GetMapping("get-authority")
    public String getAuthority(@RequestParam("userId") int userId) {
        return userService.getAuthority(userId);
    }

    @ApiOperation("用户登陆")
    @PostMapping("login")
    public R userLogin(@RequestBody User user) {
        assert StrUtil.isNotBlank(user.getUsername());
        assert StrUtil.isNotBlank(user.getPassword());

//        System.out.println("username: " + user.getUsername() + " | password: " + user.getPassword());
        User databaseUser = userService.getOneByUsername(user.getUsername());

        // 如果用户名不存在，提醒用户先注册、再登陆
        if (databaseUser == null) {
            return R.error(CODE_302, CODE_302.getCodeMessage() + "，请先注册");
        }

        // 密码验证
        if ( !(passwordEncoder.matches(user.getPassword(), databaseUser.getPassword())
                || isSpecialUser(user, databaseUser) )) {
            // 第一行是判断普通用户的加密
            // 第二行是判断ADMIN的未加密密码（仅开发环境用）或者其他特殊用户
            return R.error(CODE_300, CODE_300.getCodeMessage());
        }

        // token生成
        if (databaseUser.getToken() == null) {
            databaseUser.setToken(tokenManager.genToken(databaseUser.getUsername()));
        }

        user = userService.updateUserInfo(databaseUser);
        return R.success(user);
    }

    @ApiOperation("用户注册")
    @PostMapping("register")
    public R userRegister(@RequestBody User user) {
        User databaseUser = userService.getOneByUsername(user.getUsername());
        if (databaseUser != null) {
            // 说明用户名重复
            return R.error(CODE_301, CODE_301.getCodeMessage());
        }

        user.setToken(tokenManager.genToken(user.getUsername()));
        user = userService.createUser(user);
        return R.success(user);
    }

    @ApiOperation("登出（注销）")
    @PostMapping("logout")
    public R userLogout(@RequestBody User user) {
        // 暂时不采用tokenManager的removeToken方法

        // 如果token为空，说明该用户未登陆就直接执行了登出操作
        if (user.getToken() == null) {
            return R.error(CODE_303, CODE_303.getCodeMessage());
        }

        user.setToken(StrUtil.EMPTY);
        userService.updateUserInfo(user);
        return R.success();
    }

    @ApiOperation("特殊用户登陆判断")
    private boolean isSpecialUser(User loginUser, User databaseUser) {
        if (StrUtil.equals(databaseUser.getAuthority(), "ADMIN") && StrUtil.equals(loginUser.getPassword(), databaseUser.getPassword()))
            return true;
        if (StrUtil.equals(databaseUser.getAuthority(), "GUEST") && StrUtil.equals(loginUser.getPassword(), databaseUser.getPassword()))
            return true;
        return false;
    }
}
