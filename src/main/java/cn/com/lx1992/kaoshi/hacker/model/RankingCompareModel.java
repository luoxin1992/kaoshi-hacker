/*
 * Copyright © 2017 LuoXin. All Rights Reserved.
 */
package cn.com.lx1992.kaoshi.hacker.model;

import java.io.Serializable;

/**
 * 排行榜 对比
 *
 * @author luoxin
 * @version 2017-4-14
 */
public class RankingCompareModel implements Serializable {
    /**
     * ID
     */
    private Integer id;
    /**
     * 姓名
     */
    private String name;
    /**
     * 排名
     */
    private Integer rank;
    /**
     * 累计得分
     */
    private Integer score;
    /**
     * 累计答题时间
     */
    private Integer time;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }
}
