/*
 * Copyright © 2017 LuoXin. All Rights Reserved.
 */
package cn.com.lx1992.kaoshi.hacker.controller;

import cn.com.lx1992.kaoshi.hacker.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author luoxin
 * @version 2017-4-18
 */
@RestController
public class QuestionController {
    @Autowired
    private QuestionService questionService;

    @RequestMapping("/api/v1/question/{keyword}")
    public Map<String, Object> query(@PathVariable("keyword") String keyword) {
        Map<String, Object> result = new LinkedHashMap<>();
        if (keyword.length() < 2) {
            result.put("code", "-1");
            result.put("message", "关键词长度应大于等于2");
            result.put("result", null);
            return result;
        }
        result.put("code", "0");
        result.put("message", "成功");
        result.put("result", questionService.query(keyword));
        return result;
    }

    @RequestMapping("/api/v1/question/analyze/{id}")
    public Map<String, Object> analyze(@PathVariable("id") Integer questionId) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", "0");
        result.put("message", "成功");
        result.put("result", questionService.analyze(questionId));
        return result;
    }
}
