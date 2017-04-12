/*
 * Copyright © 2017 LuoXin. All Rights Reserved.
 */
package cn.com.lx1992.kaoshi.hacker.exception;

/**
 * 业务异常
 *
 * @author luoxin
 * @version 2017-4-11
 */
public class BizException extends RuntimeException {
    public BizException(String message) {
        super(message);
    }
}
