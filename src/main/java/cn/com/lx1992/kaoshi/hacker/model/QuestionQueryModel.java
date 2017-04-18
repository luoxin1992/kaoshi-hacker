/*
 * Copyright © 2017 LuoXin. All Rights Reserved.
 */
package cn.com.lx1992.kaoshi.hacker.model;

import java.io.Serializable;

/**
 * 试题 查询
 *
 * @author luoxin
 * @version 2017-4-12
 */
public class QuestionQueryModel implements Serializable {
    /**
     * 试题ID
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
    /**
     * 相似福
     */
    private Double similarity;

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

    public Double getSimilarity() {
        return similarity;
    }

    public void setSimilarity(Double similarity) {
        this.similarity = similarity;
    }
}
