/*
 * Copyright © 2017 LuoXin. All Rights Reserved.
 */
package cn.com.lx1992.kaoshi.hacker.controller;

import cn.com.lx1992.kaoshi.hacker.service.RankingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author luoxin
 * @version 2017-4-11
 */
@RestController
public class RankingController {
    @Autowired
    private RankingService rankingService;

    @RequestMapping("/api/v1/ranking/{limit}")
    public void query(@PathVariable("limit") Integer limit) {

    }
}
