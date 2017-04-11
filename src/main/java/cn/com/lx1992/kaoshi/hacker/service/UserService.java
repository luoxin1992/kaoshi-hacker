/*
 * Copyright © 2017 LuoXin. All Rights Reserved.
 */
package cn.com.lx1992.kaoshi.hacker.service;

import cn.com.lx1992.kaoshi.hacker.constant.UrlConstant;
import cn.com.lx1992.kaoshi.hacker.mapper.MetadataMapper;
import cn.com.lx1992.kaoshi.hacker.mapper.UserMapper;
import cn.com.lx1992.kaoshi.hacker.meta.MetadataKeyEnum;
import cn.com.lx1992.kaoshi.hacker.model.MetadataQueryModel;
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

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
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
     * 查询
     */
    public UserQueryModel query(Integer id) {
        UserQueryModel user = userMapper.query(id);
        if (user == null) {
            logger.error("user {} not exist", id);
            throw new IllegalArgumentException("用户不存在");
        }
        return user;
    }

    /**
     * 登录
     * 不会事先检查session是否有效，需要调用check()方法
     */
    @Transactional
    public void login(Integer id) throws Exception {
        UserQueryModel userQuery = query(id);
        //构建登录url
        String url = buildLoginUrl(userQuery.getUsername(), userQuery.getPassword());
        Map<String, String> cookies = getCookie(null);
        Response response = HttpUtils.execute(url, cookies);
        //登录成功，重定向到考试首页
        if (response.isRedirect()) {
            logger.info("user {} login successful", id);
            //获取session
            String header = response.header("Set-Cookie");
            if (StringUtils.isEmpty(header)) {
                logger.warn("no session return from server");
                return;
            }
            String session = header.substring(header.indexOf('=') + 1, header.indexOf(";"));
            //更新数据库，保存session
            UserUpdateModel userUpdate = new UserUpdateModel();
            userUpdate.setId(id);
            userUpdate.setSession(URLDecoder.decode(session, "utf-8"));
            userMapper.update(userUpdate);
        } else {
            logger.warn("user {} login failed", id);
        }
    }

    /**
     * 登出
     * 不会事先检查session是否失效，需要调用check()方法
     */
    @Transactional
    public void logout(Integer id) throws Exception {
        UserQueryModel userQuery = query(id);
        if (StringUtils.isEmpty(userQuery.getSession())) {
            logger.info("user {} not login", id);
            return;
        }
        Map<String, String> cookies = getCookie(userQuery.getSession());
        Response response = HttpUtils.execute(UrlConstant.USER_LOGOUT, cookies);
        //登出成功，重定向到登录页
        if (response.isRedirect()) {
            logger.info("user {} logout successful", id);
            //更新数据库，将session清空
            UserUpdateModel userUpdate = new UserUpdateModel();
            userUpdate.setId(id);
            userUpdate.setSession("");
            userMapper.update(userUpdate);
        } else {
            logger.error("user {} logout failed", id);
        }
    }

    /**
     * 检查Session有效性
     */
    public boolean check(Integer id) throws Exception {
        UserQueryModel user = query(id);
        //从未登录过，或已经正常登出
        if (StringUtils.isEmpty(user.getSession())) {
            logger.info("session for user {} not exist", id);
            return false;
        }
        //需要带入上次登录的session
        Map<String, String> cookies = getCookie(user.getSession());
        //session失效，重定向到登录页
        if (HttpUtils.execute(UrlConstant.EXAM_INDEX, cookies).isRedirect()) {
            logger.info("session for user {} is invalid", id);
            return false;
        } else {
            logger.info("session for user {} is valid", id);
            return true;
        }
    }

    private String buildLoginUrl(String username, String password) {
        return UrlConstant.USER_LOGIN + "&userlogin=1"
                + "&args[username]=" + username
                + "&args[userpassword]=" + password;
    }

    /**
     * 对需要验证登录的URL，带入相应cookie
     */
    public Map<String, String> getCookie(String session) throws Exception {
        Map<String, String> cookies = new HashMap<>();
        //PHPSESSID和exam_psid两个cookie带入的是同一个值
        MetadataQueryModel metadata = metadataMapper.query(MetadataKeyEnum.EXAM_PSID.getKey());
        cookies.put("PHPSESSID", metadata.getValue());
        cookies.put("exam_psid", metadata.getValue());
        //设置登录用户标识cookie
        if (!StringUtils.isEmpty(session)) {
            //session中含有‘%’，需要转义
            cookies.put("exam_currentuser", URLEncoder.encode(session, "utf-8"));
        }
        return cookies;
    }
}
