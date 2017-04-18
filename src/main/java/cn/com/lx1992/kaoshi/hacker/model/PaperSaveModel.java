/*
 * Copyright © 2017 LuoXin. All Rights Reserved.
 */
package cn.com.lx1992.kaoshi.hacker.model;

import java.io.Serializable;

/**
 * 试卷 保存
 *
 * @author luoxin
 * @version 2017-4-12
 */
public class PaperSaveModel implements Serializable {
    /**
     * ID
     */
    private Integer id;
    /**
     * 试卷ID
     */
    private Integer paperId;
    /**
     * 成绩
     */
    private Integer score;

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

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }
}
