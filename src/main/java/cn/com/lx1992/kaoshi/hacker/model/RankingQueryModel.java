/*
 * Copyright © 2017 LuoXin. All Rights Reserved.
 */
package cn.com.lx1992.kaoshi.hacker.model;

import java.io.Serializable;

/**
 * 排行榜 查询
 *
 * @author luoxin
 * @version 2017-4-16
 */
public class RankingQueryModel implements Serializable {
    /**
     * 姓名
     */
    private String name;
    /**
     * 累计得分
     */
    private String score;
    /**
     * 累计答题时间(秒)
     */
    private String time;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
