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
     * ID
     */
    private Integer id;
    /**
     * 试题ID
     */
    private Integer questionId;
    /**
     * 选A考生数
     */
    private Integer aChooser;
    /**
     * 选B考生数
     */
    private Integer bChooser;
    /**
     * 选C考生数
     */
    private Integer cChooser;
    /**
     * 选D考生数
     */
    private Integer dChooser;
    /**
     * 正确率
     */
    private Integer correctRate;
    /**
     * 试题抽中次数
     */
    private Integer selectTimes;

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

    public Integer getaChooser() {
        return aChooser;
    }

    public void setaChooser(Integer aChooser) {
        this.aChooser = aChooser;
    }

    public Integer getbChooser() {
        return bChooser;
    }

    public void setbChooser(Integer bChooser) {
        this.bChooser = bChooser;
    }

    public Integer getcChooser() {
        return cChooser;
    }

    public void setcChooser(Integer cChooser) {
        this.cChooser = cChooser;
    }

    public Integer getdChooser() {
        return dChooser;
    }

    public void setdChooser(Integer dChooser) {
        this.dChooser = dChooser;
    }

    public Integer getCorrectRate() {
        return correctRate;
    }

    public void setCorrectRate(Integer correctRate) {
        this.correctRate = correctRate;
    }

    public Integer getSelectTimes() {
        return selectTimes;
    }

    public void setSelectTimes(Integer selectTimes) {
        this.selectTimes = selectTimes;
    }
}
