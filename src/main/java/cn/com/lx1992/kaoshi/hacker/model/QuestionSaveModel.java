/*
 * Copyright © 2017 LuoXin. All Rights Reserved.
 */
package cn.com.lx1992.kaoshi.hacker.model;

import java.io.Serializable;

/**
 * 试题 保存
 *
 * @author luoxin
 * @version 2017-4-12
 */
public class QuestionSaveModel implements Serializable {
    /**
     * ID
     */
    private Integer id;
    /**
     * 问题ID
     */
    private Integer questionId;
    /**
     * 题干
     */
    private String stem;
    /**
     * 选项
     */
    private String option;
    /**
     * 答案
     */
    private String answer;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
    }

    public String getStem() {
        return stem;
    }

    public void setStem(String stem) {
        this.stem = stem;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
