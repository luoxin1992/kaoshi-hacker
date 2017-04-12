/*
 * Copyright © 2017 LuoXin. All Rights Reserved.
 */
package cn.com.lx1992.kaoshi.hacker.service;

import cn.com.lx1992.kaoshi.hacker.constant.UrlConstant;
import cn.com.lx1992.kaoshi.hacker.exception.BizException;
import cn.com.lx1992.kaoshi.hacker.mapper.MetadataMapper;
import cn.com.lx1992.kaoshi.hacker.mapper.UserMapper;
import cn.com.lx1992.kaoshi.hacker.model.UserQueryModel;
import cn.com.lx1992.kaoshi.hacker.model.UserUpdateModel;
import cn.com.lx1992.kaoshi.hacker.util.HttpUtils;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户Service
 *
 * @author luoxin
 * @version 2017-4-11
 */
@Service
public class UserService {
    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MetadataMapper metadataMapper;


    /**
     * 登录 返回会话session
     * 不会事先检查session是否失效 有需要请调用check()方法
     */
    @Transactional
    public void login(Integer id) {
        logger.info("login user {}", id);
        UserQueryModel userQuery = query(id);
        //构建登录url
        String url = buildLoginUrl(userQuery.getUsername(), userQuery.getPassword());
        Response response;
        try {
            response = HttpUtils.execute(url, null);
        } catch (IOException e) {
            logger.error("request login page failed", e);
            throw new BizException("登录失败(请求错误)");
        }
        //登录成功 重定向到考试首页
        if (!response.isRedirect()) {
            logger.error("login failed");
            throw new BizException("登录失败(页面跳转错误)");
        }
        //获取cookies 正常应当是3个 取第2个存为cookie1 第3个存为cookie2
        List<String> cookies = response.headers("Set-Cookie");
        if (cookies.size() != 3) {
            logger.error("header \"Set-cookie\" size {} incorrect", cookies.size());
            throw new BizException("登录失败(获取Cookie错误)");
        }
        String cookie1 = cookies.get(1).substring(cookies.get(1).indexOf('=') + 1, cookies.get(1).indexOf(';'));
        String cookie2 = cookies.get(2).substring(cookies.get(2).indexOf('=') + 1, cookies.get(2).indexOf(';'));
        //更新数据库 保存cookie
        UserUpdateModel userUpdate = new UserUpdateModel();
        userUpdate.setId(id);
        userUpdate.setCookie1(cookie1);
        try {
            userUpdate.setCookie2(URLDecoder.decode(cookie2, "utf-8"));
        } catch (UnsupportedEncodingException ignored) {
        }
        userMapper.update(userUpdate);
        logger.info("login successful");
    }

    /**
     * 登出
     * 不会事先检查session是否失效
     */
    @Transactional
    public void logout(Integer id) {
        logger.info("logout user {}", id);
        UserQueryModel userQuery = query(id);
        //若存有session 则失效session
        if (!StringUtils.isEmpty(userQuery.getCookie1()) && !StringUtils.isEmpty(userQuery.getCookie2())) {
            Map<String, String> cookies = buildCookies(userQuery.getCookie1(), userQuery.getCookie2());
            Response response;
            try {
                response = HttpUtils.execute(UrlConstant.USER_LOGOUT, cookies);
            } catch (IOException e) {
                logger.error("request logout page failed", e);
                throw new BizException("登出失败(请求错误)");
            }
            //登出成功 重定向到登录页
            if (!response.isRedirect()) {
                logger.warn("logout failed");
                throw new BizException("登出失败(页面跳转错误)");
            }
        }
        //更新数据库 清空cookie
        UserUpdateModel userUpdate = new UserUpdateModel();
        userUpdate.setId(id);
        userUpdate.setCookie1("");
        userUpdate.setCookie2("");
        userMapper.update(userUpdate);
        logger.info("logout successful");
    }

    /**
     * 查询
     */
    private UserQueryModel query(Integer id) {
        UserQueryModel userQuery = userMapper.query(id);
        if (userQuery == null) {
            logger.error("user not exist");
            throw new BizException("用户不存在");
        }
        return userQuery;
    }

    /**
     * 构造指定用户的cookies
     */
    public Map<String, String> buildCookies(Integer id) {
        logger.info("build cookies for user {}", id);
        UserQueryModel userQuery = query(id);
        //如果指定用户没有历史cookie 直接返回
        if (StringUtils.isEmpty(userQuery.getCookie1()) || StringUtils.isEmpty(userQuery.getCookie2())) {
            logger.warn("no cookies found");
            return Collections.emptyMap();
        }
        return buildCookies(userQuery.getCookie1(), userQuery.getCookie2());
    }

    /**
     * 构造cookies
     * cookie1名为"PHPSESSID"和"exam_psid"
     * cookie2名为"exam_currentuser"
     */
    private Map<String, String> buildCookies(String cookie1, String cookie2) {
        Map<String, String> cookies = new LinkedHashMap<>();
        //PHPSESSID和exam_psid两个cookie带入的是同一个值
        cookies.put("PHPSESSID", cookie1);
        cookies.put("exam_psid", cookie1);
        if (!StringUtils.isEmpty(cookie2)) {
            //session中含有"%" 需要转义
            try {
                cookies.put("exam_currentuser", URLEncoder.encode(cookie2, "utf-8"));
            } catch (UnsupportedEncodingException ignored) {
            }
        }
        return cookies;
    }

    private String buildLoginUrl(String username, String password) {
        return UrlConstant.USER_LOGIN
                + "&args[username]=" + username
                + "&args[userpassword]=" + password
                + "&userlogin=1";
    }
}
