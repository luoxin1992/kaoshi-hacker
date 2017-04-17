/*
 * Copyright © 2017 LuoXin. All Rights Reserved.
 */
package cn.com.lx1992.kaoshi.hacker.model;

import java.io.Serializable;

/**
 * 成绩单 分析
 *
 * @author luoxin
 * @version 2017-4-17
 */
public class ScoreAnalyzeModel implements Serializable {
    /**
     * 时间区间
     */
    private String time;
    /**
     * 考试次数
     */
    private Integer count;
    /**
     * 平均成绩
     */
    private Double avgScore;
    /**
     * 平均用时
     */
    private Double avgPeriod;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Double getAvgScore() {
        return avgScore;
    }

    public void setAvgScore(Double avgScore) {
        this.avgScore = avgScore;
    }

    public Double getAvgPeriod() {
        return avgPeriod;
    }

    public void setAvgPeriod(Double avgPeriod) {
        this.avgPeriod = avgPeriod;
    }
}
