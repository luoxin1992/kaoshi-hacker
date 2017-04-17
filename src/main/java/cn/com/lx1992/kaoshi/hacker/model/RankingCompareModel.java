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
     * 胖行帮爬取时间
     */
    private String timestamp;
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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
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
}
