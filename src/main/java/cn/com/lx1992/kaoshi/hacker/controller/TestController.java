/*
 * Copyright © 2017 LuoXin. All Rights Reserved.
 */
package cn.com.lx1992.kaoshi.hacker.controller;

import cn.com.lx1992.kaoshi.hacker.meta.ResponseEnum;
import cn.com.lx1992.kaoshi.hacker.response.ApiResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author luoxin
 * @version 1.0
 * @created 2017-4-10
 */
@RestController
public class TestController {
    @RequestMapping("/api/v1/alive")
    public ApiResponse alive() {
        return new ApiResponse(ResponseEnum.OK);
    }
}
