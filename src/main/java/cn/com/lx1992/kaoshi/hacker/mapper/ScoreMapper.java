/*
 * Copyright © 2017 LuoXin. All Rights Reserved.
 */
package cn.com.lx1992.kaoshi.hacker.mapper;

import cn.com.lx1992.kaoshi.hacker.model.ScoreAnalyzeModel;
import cn.com.lx1992.kaoshi.hacker.model.ScoreQueryModel;
import cn.com.lx1992.kaoshi.hacker.model.ScoreSaveModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 成绩Mapper
 *
 * @author luoxin
 * @version 2017-4-11
 */
@Mapper
public interface ScoreMapper {
    /**
     * 保存
     */
    int save(ScoreSaveModel model);

    /**
     * 查询
     */
    List<ScoreQueryModel> queryPeriod(@Param("start") String start, @Param("end") String end);

    /**
     * 查询
     */
    List<ScoreQueryModel> queryPage(@Param("column") String column, @Param("dir") String dir,
                                    @Param("offset") int offset);

    /**
     * 分析
     */
    List<ScoreAnalyzeModel> analyze(@Param("type") int type, @Param("start") String start, @Param("end") String end);
}
