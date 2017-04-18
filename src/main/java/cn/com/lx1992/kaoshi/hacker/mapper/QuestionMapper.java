/*
 * Copyright © 2017 LuoXin. All Rights Reserved.
 */
package cn.com.lx1992.kaoshi.hacker.mapper;

import cn.com.lx1992.kaoshi.hacker.model.QuestionAnalyzeModel;
import cn.com.lx1992.kaoshi.hacker.model.QuestionQueryModel;
import cn.com.lx1992.kaoshi.hacker.model.QuestionSaveModel;
import cn.com.lx1992.kaoshi.hacker.model.QuestionUpdateModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 试题Mapper
 *
 * @author luoxin
 * @version 2017-4-12
 */
@Mapper
public interface QuestionMapper {
    /**
     * 保存
     */
    int save(QuestionSaveModel model);

    /**
     * 更新
     */
    int update(QuestionUpdateModel model);

    /**
     * 查询ID
     */
    Integer queryId(Integer questionId);

    /**
     * 查询(关键词匹配)
     */
    List<QuestionQueryModel> query(String keyword);

    /**
     * 分析
     */
    QuestionAnalyzeModel analyze(Integer id);
}
