/*
 * Copyright © 2017 LuoXin. All Rights Reserved.
 */
package cn.com.lx1992.kaoshi.hacker.constant;

/**
 * URL常量
 *
 * @author luoxin
 * @version 2017-4-10
 */
public class UrlConstant {
    /**
     * URL前缀
     */
    private static final String PREFIX = "http://www.aohua168.com/kaoshi";
    /**
     * 排行榜
     */
    public static final String RANKING = PREFIX + "/rank-phone.php";
    /**
     * 用户登录
     */
    public static final String USER_LOGIN = PREFIX + "/index.php?user-phone-login";
    /**
     * 用户登出
     */
    public static final String USER_LOGOUT = PREFIX + "/index.php?user-phone-logout";
    /**
     * 成绩单
     */
    public static final String SCORE = PREFIX + "/index.php?exam-phone-score";
    /**
     * 试卷解析
     */
    public static final String PAPER = PREFIX + "/index.php?exam-phone-history-view";
}
