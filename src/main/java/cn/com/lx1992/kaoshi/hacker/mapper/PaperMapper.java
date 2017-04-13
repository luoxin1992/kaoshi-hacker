/*
 * Copyright © 2017 LuoXin. All Rights Reserved.
 */
package cn.com.lx1992.kaoshi.hacker.mapper;

import cn.com.lx1992.kaoshi.hacker.model.PaperSaveModel;
import org.apache.ibatis.annotations.Mapper;

/**
 * 试卷Mapper
 *
 * @author luoxin
 * @version 2017-4-12
 */
@Mapper
public interface PaperMapper {
    /**
     * 保存
     */
    int save(PaperSaveModel model);
}
