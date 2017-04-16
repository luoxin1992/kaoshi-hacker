/*
 * Copyright © 2017 LuoXin. All Rights Reserved.
 */
package cn.com.lx1992.kaoshi.hacker.controller;

import cn.com.lx1992.kaoshi.hacker.service.RankingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author luoxin
 * @version 2017-4-11
 */
@RestController
public class RankingController {
    @Autowired
    private RankingService rankingService;

    @RequestMapping("/api/v1/ranking/compare/{name}")
    public Map<String, Object> compare(@PathVariable("name") String name) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", "0");
        result.put("message", "成功");
        result.put("data", rankingService.compare(name));
        return result;
    }

    @RequestMapping({"/api/v1/ranking", "/api/v1/ranking/{limit}"})
    public Map<String, Object> query(@PathVariable(value = "limit", required = false) Integer limit) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", "0");
        result.put("message", "成功");
        result.put("data", rankingService.query(limit));
        return result;
    }
}
