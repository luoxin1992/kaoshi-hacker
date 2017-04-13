/*
 * Copyright © 2017 LuoXin. All Rights Reserved.
 */
package cn.com.lx1992.kaoshi.hacker.mapper;

import cn.com.lx1992.kaoshi.hacker.model.QuestionQueryModel;
import cn.com.lx1992.kaoshi.hacker.model.QuestionSaveModel;
import cn.com.lx1992.kaoshi.hacker.model.QuestionUpdateModel;
import org.apache.ibatis.annotations.Mapper;

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
     * 查询
     */
    QuestionQueryModel query(Integer id);

    /**
     * 查询ID
     */
    Integer queryId(Integer questionId);
}
