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
}
