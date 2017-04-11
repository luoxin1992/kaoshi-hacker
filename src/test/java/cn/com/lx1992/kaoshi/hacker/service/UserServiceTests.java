/*
 * Copyright © 2017 LuoXin. All Rights Reserved.
 */
package cn.com.lx1992.kaoshi.hacker.service;

import cn.com.lx1992.kaoshi.hacker.model.UserQueryModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author luoxin
 * @version 2017-4-11
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTests {
    @Autowired
    private UserService userService;

    @Test
    public void testQuery() {
        UserQueryModel userQuery = userService.query(1);
        System.out.println(userQuery.getId());
    }

    @Test
    public void testLogin() throws Exception {
        userService.login(1);
    }

    @Test
    public void testLogout() throws Exception {
        userService.logout(1);
    }

    @Test
    public void testCheck() throws Exception {
        userService.check(1);
    }
}
