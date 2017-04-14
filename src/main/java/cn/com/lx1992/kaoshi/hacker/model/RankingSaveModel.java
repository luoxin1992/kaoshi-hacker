/*
 * Copyright © 2017 LuoXin. All Rights Reserved.
 */
package cn.com.lx1992.kaoshi.hacker.model;

import java.io.Serializable;

/**
 * 排行榜 保存
 *
 * @author luoxin
 * @version 2017-4-10
 */
public class RankingSaveModel implements Serializable {
    /**
     * ID
     */
    private Integer id;
    /**
     * 爬取轮次
     */
    private Integer round;
    /**
     * 排名
     */
    private String rank;
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRound() {
        return round;
    }

    public void setRound(Integer round) {
        this.round = round;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

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
