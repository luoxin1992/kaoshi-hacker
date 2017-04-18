/*
 * Copyright © 2017 LuoXin. All Rights Reserved.
 */
package cn.com.lx1992.kaoshi.hacker.model;

import java.io.Serializable;

/**
 * 成绩单 保存
 *
 * @author luoxin
 * @version 2017-4-11
 */
public class ScoreSaveModel implements Serializable {
    /**
     * ID
     */
    private Integer id;
    /**
     * 姓名
     */
    private String name;
    /**
     * 得分
     */
    private String score;
    /**
     * 考试时间
     */
    private String time;
    /**
     * 用时
     */
    private String timeCost;

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

    public String getTimeCost() {
        return timeCost;
    }

    public void setTimeCost(String timeCost) {
        this.timeCost = timeCost;
    }
}
