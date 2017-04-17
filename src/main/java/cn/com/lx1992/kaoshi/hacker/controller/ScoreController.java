/*
 * Copyright © 2017 LuoXin. All Rights Reserved.
 */
package cn.com.lx1992.kaoshi.hacker.controller;

import cn.com.lx1992.kaoshi.hacker.service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author luoxin
 * @version 2017-4-17
 */
@RestController
public class ScoreController {
    @Autowired
    private ScoreService scoreService;

    @RequestMapping("/api/v1/score/{period}")
    public Map<String, Object> query(@PathVariable("period") String period) {
        Map<String, Object> result = new LinkedHashMap<>();
        if (!(period.matches("\\d{10}") || period.matches("\\d{12}"))) {
            result.put("code", "-1");
            result.put("message", "日期时间格式错误，应为yyyyMMddHH或yyyyMMddHHmm");
            result.put("result", null);
            return result;
        }
        result.put("code", "0");
        result.put("message", "成功");
        result.put("result", scoreService.query(period));
        return result;
    }

    @RequestMapping("/api/v1/score/{column}/{dir}/{page}")
    public Map<String, Object> query(@PathVariable("column") String column, @PathVariable("dir") String dir,
                                     @PathVariable("page") Integer page) {
        Map<String, Object> result = new LinkedHashMap<>();
        if (!("score".equals(column) || "time".equals(column) || "period".equals(column))) {
            result.put("code", "-1");
            result.put("message", "排序字段格式错误，只能为score/time/period");
            result.put("result", null);
            return result;
        }
        if (!("asc".equals(dir) || "desc".equals(dir))) {
            result.put("code", "-1");
            result.put("message", "排序方式格式错误，只能为asc/desc");
            result.put("result", null);
            return result;
        }
        if (page <= 0) {
            page = 1;
        }
        result.put("code", "0");
        result.put("message", "成功");
        result.put("result", scoreService.query(column, dir, page));
        return result;
    }

    @RequestMapping("/api/v1/score/analyze/{period}")
    public Map<String, Object> analyze(@PathVariable(value = "period") String period) {
        Map<String, Object> result = new LinkedHashMap<>();
        if (!(period.matches("\\d{8}") || period.matches("\\d{10}"))) {
            result.put("code", "-1");
            result.put("message", "日期时间格式错误，应为yyyyMMdd或yyyyMMddHH");
            result.put("result", null);
            return result;
        }
        result.put("code", "0");
        result.put("message", "成功");
        result.put("result", scoreService.analyze(period));
        return result;
    }
}
