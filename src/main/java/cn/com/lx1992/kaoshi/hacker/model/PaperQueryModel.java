/*
 * Copyright © 2017 LuoXin. All Rights Reserved.
 */
package cn.com.lx1992.kaoshi.hacker.model;

import java.io.Serializable;
import java.util.List;

/**
 * 试卷 查询
 *
 * @author luoxin
 * @version 2017-4-12
 */
public class PaperQueryModel implements Serializable {
    /**
     * 成绩
     */
    private Integer score;
    /**
     * 试题
     */
    private List<PaperQuestionQueryModel> questions;

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public List<PaperQuestionQueryModel> getQuestions() {
        return questions;
    }

    public void setQuestions(List<PaperQuestionQueryModel> questions) {
        this.questions = questions;
    }
}
