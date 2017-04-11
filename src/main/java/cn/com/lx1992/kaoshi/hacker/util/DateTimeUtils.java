/*
 * Copyright © 2017 LuoXin. All Rights Reserved.
 */
package cn.com.lx1992.kaoshi.hacker.util;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

/**
 * 日期时间工具类
 *
 * @author luoxin
 * @version 2017-4-11
 */
public class DateTimeUtils {
    public static String format(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM));
    }

    public static LocalDateTime parse(String dateTime) {
        if (dateTime == null) {
            return null;
        }
        return LocalDateTime.parse(dateTime, DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM));
    }

    public static String getTodayStr() {
        return LocalDate.now().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM));
    }

    public static String getNowStr() {
        return LocalDateTime.now().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM));
    }
}
