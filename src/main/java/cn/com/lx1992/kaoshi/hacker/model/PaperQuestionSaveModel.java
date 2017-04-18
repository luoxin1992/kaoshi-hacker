/*
 * Copyright © 2017 LuoXin. All Rights Reserved.
 */
package cn.com.lx1992.kaoshi.hacker.model;

import java.io.Serializable;

/**
 * 试卷-试题 保存
 *
 * @author luoxin
 * @version 2017-4-18
 */
public class PaperQuestionSaveModel implements Serializable {
    /**
     * ID
     */
    private Integer id;
    /**
     * 试卷ID
     */
    private Integer paperId;
    /**
     * 试题ID
     */
    private Integer questionId;
    /**
     * 考生答案
     */
    private String choice;
    /**
     * 提交时间
     */
    private String time;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPaperId() {
        return paperId;
    }

    public void setPaperId(Integer paperId) {
        this.paperId = paperId;
    }

    public Integer getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
    }

    public String getChoice() {
        return choice;
    }

    public void setChoice(String choice) {
        this.choice = choice;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
