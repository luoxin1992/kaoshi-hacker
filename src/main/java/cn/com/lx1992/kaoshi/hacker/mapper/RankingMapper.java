/*
 * Copyright © 2017 LuoXin. All Rights Reserved.
 */
package cn.com.lx1992.kaoshi.hacker.mapper;

import cn.com.lx1992.kaoshi.hacker.model.RankingSaveModel;
import org.apache.ibatis.annotations.Mapper;

/**
 * 排行榜Mapper
 *
 * @author luoxin
 * @version 2017-4-10
 */
@Mapper
public interface RankingMapper {
    /**
     * 保存
     */
    int save(RankingSaveModel model);
}
