/*
 * Copyright © 2017 LuoXin. All Rights Reserved.
 */
package cn.com.lx1992.kaoshi.hacker.controller;

import cn.com.lx1992.kaoshi.hacker.service.PaperService;
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
public class PaperController {
    @Autowired
    private PaperService paperService;

    @RequestMapping("/api/v1/paper/{id}")
    public Map<String, Object> query(@PathVariable("id") Integer paperId) {
        Map<String, Object> result = new LinkedHashMap<>();
        if (paperId <= 0) {
            result.put("code", "-1");
            result.put("message", "试卷ID应为正数");
            result.put("result", null);
            return result;
        }
        result.put("code", "0");
        result.put("message", "成功");
        result.put("result", paperService.query(paperId));
        return result;
    }
}
