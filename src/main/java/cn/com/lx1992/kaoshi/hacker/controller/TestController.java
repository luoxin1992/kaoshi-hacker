/*
 * Copyright © 2017 LuoXin. All Rights Reserved.
 */
package cn.com.lx1992.kaoshi.hacker.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author luoxin
 * @version 2017-4-10
 */
@RestController
public class TestController {
    @RequestMapping("/api/v1/test")
    public Map<String, Object> test() {
        Map<String, Object> result = new HashMap<>();
        result.put("code", "0");
        result.put("message", "成功");
        result.put("data", null);
        return result;
    }
}
