/*
 * Copyright © 2017 LuoXin. All Rights Reserved.
 */
package cn.com.lx1992.kaoshi.hacker.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 公共工具类
 *
 * @author luoxin
 * @version 2017-4-13
 */
public class CommonUtils {
    /**
     * 从字符串中抽取数字
     */
    public static int extractNumber(String str) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(str);
        if (!matcher.find()) {
            return -1;
        }
        return Integer.parseInt(matcher.group());
    }

    /**
     * 从字符串中抽取日期时间
     */
    public static String extractDatetime(String str) {
        //2017-04-09 13:51:20
        Pattern pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}");
        Matcher matcher = pattern.matcher(str);
        if (!matcher.find()) {
            return null;
        }
        return matcher.group();
    }
}
