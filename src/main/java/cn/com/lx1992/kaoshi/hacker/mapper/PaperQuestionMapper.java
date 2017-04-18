/*
 * Copyright © 2017 LuoXin. All Rights Reserved.
 */
package cn.com.lx1992.kaoshi.hacker.mapper;

import cn.com.lx1992.kaoshi.hacker.model.PaperQuestionQueryModel;
import cn.com.lx1992.kaoshi.hacker.model.PaperQuestionSaveModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 试卷-试题Mapper
 *
 * @author luoxin
 * @version 2017-4-18
 */
@Mapper
public interface PaperQuestionMapper {
    /**
     * 保存
     */
    void save(PaperQuestionSaveModel model);

    /**
     * 查询(试卷ID)
     */
    List<PaperQuestionQueryModel> query(Integer paperId);
}
