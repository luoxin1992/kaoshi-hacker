/*
 * Copyright © 2017 LuoXin. All Rights Reserved.
 */
package cn.com.lx1992.kaoshi.hacker.mapper;

import cn.com.lx1992.kaoshi.hacker.model.ScoreSaveModel;
import org.apache.ibatis.annotations.Mapper;

/**
 * 成绩Mapper
 *
 * @author luoxin
 * @version 2017-4-11
 */
@Mapper
public interface ScoreMapper {
    /**
     * 更新
     */
    int save(ScoreSaveModel model);
}
