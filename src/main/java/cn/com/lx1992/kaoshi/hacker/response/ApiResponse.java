/*
 * Copyright © 2017 LuoXin. All Rights Reserved.
 */
package cn.com.lx1992.kaoshi.hacker.response;

import cn.com.lx1992.kaoshi.hacker.meta.ResponseEnum;

/**
 * @author luoxin
 * @version 1.0
 * @created 2017-4-10
 */
public class ApiResponse<T> {
    private Integer code;
    private String message;
    private T data;

    public ApiResponse(T data) {
        this.code = ResponseEnum.OK.getCode();
        this.message = ResponseEnum.OK.getMessage();
        this.data = data;
    }

    public ApiResponse(ResponseEnum response) {
        this.code = response.getCode();
        this.message = response.getMessage();
        this.data = null;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}
