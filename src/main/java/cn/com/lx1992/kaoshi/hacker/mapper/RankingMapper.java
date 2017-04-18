/*
 * Copyright © 2017 LuoXin. All Rights Reserved.
 */
package cn.com.lx1992.kaoshi.hacker.mapper;

import cn.com.lx1992.kaoshi.hacker.model.RankingCompareModel;
import cn.com.lx1992.kaoshi.hacker.model.RankingQueryModel;
import cn.com.lx1992.kaoshi.hacker.model.RankingSaveModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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

    /**
     * 查询
     */
    List<RankingQueryModel> query(@Param("count") int count, @Param("limit") int limit);

    /**
     * 对比
     */
    List<RankingCompareModel> compare(String name);
}
