package com.voice.separation;

import com.voice.separation.pojo.User;
import com.voice.separation.service.IUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserControllerTest {

    @Autowired
    private IUserService userService;

    @Test
    public void getOneByUsernameTest() {
//        System.out.println(userService.getOneByUsername("admin").getPassword());
        User user = userService.getOneByUsername("admin");
        System.out.println(userService.updateUserInfo(user).toString());
    }
}
