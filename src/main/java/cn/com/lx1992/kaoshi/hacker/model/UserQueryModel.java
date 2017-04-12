/*
 * Copyright © 2017 LuoXin. All Rights Reserved.
 */
package cn.com.lx1992.kaoshi.hacker.model;

import java.io.Serializable;

/**
 * 用户 查询
 *
 * @author luoxin
 * @version 2017-4-10
 */
public class UserQueryModel implements Serializable {
    /**
     * ID
     */
    private Integer id;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * Cookie "exam_psid"
     */
    private String cookie1;
    /**
     * Cookie "exam_currentuser"
     */
    private String cookie2;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCookie1() {
        return cookie1;
    }

    public void setCookie1(String cookie1) {
        this.cookie1 = cookie1;
    }

    public String getCookie2() {
        return cookie2;
    }

    public void setCookie2(String cookie2) {
        this.cookie2 = cookie2;
    }
}
