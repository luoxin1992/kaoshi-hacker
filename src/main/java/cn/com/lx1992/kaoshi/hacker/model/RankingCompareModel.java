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
    private Integer round;
    /**
     * 姓名
     */
    private String name;
    /**
     * 排名
     */
    private String rank;
    /**
     * 累计得分
     */
    private String score;
    /**
     * 累计答题时间
     */
    private String time;

    public Integer getRound() {
        return round;
    }

    public void setRound(Integer round) {
        this.round = round;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
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

    public static RankingCompareModel buildNull() {
        RankingCompareModel model = new RankingCompareModel();
        model.setRank("-");
        model.setScore("-");
        model.setTime("-");
        return model;
    }
}
