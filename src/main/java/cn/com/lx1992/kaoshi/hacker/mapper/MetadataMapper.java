/*
 * Copyright © 2017 LuoXin. All Rights Reserved.
 */
package cn.com.lx1992.kaoshi.hacker.mapper;

import cn.com.lx1992.kaoshi.hacker.model.MetadataQueryModel;
import cn.com.lx1992.kaoshi.hacker.model.MetadataUpdateModel;
import org.apache.ibatis.annotations.Mapper;

/**
 * 元数据Mapper
 *
 * @author luoxin
 * @version 2017-4-11
 */
@Mapper
public interface MetadataMapper {
    /**
     * 更新
     */
    int update(MetadataUpdateModel model);

    /**
     * 查询
     */
    MetadataQueryModel query(String key);
}
