/*
 * Copyright © 2017 LuoXin. All Rights Reserved.
 */
package cn.com.lx1992.kaoshi.hacker.service;

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
public class ScoreServiceTests {
    @Autowired
    private ScoreService scoreService;

    @Test
    public void testCrawling() throws InterruptedException {
        scoreService.crawling();
    }
}
