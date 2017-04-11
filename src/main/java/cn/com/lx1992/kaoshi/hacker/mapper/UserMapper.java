/*
 * Copyright © 2017 LuoXin. All Rights Reserved.
 */
package cn.com.lx1992.kaoshi.hacker.mapper;

import cn.com.lx1992.kaoshi.hacker.model.UserQueryModel;
import cn.com.lx1992.kaoshi.hacker.model.UserUpdateModel;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户Mapper
 *
 * @author luoxin
 * @version 2017-4-11
 */
@Mapper
public interface UserMapper {
    /**
     * 更新
     */
    int update(UserUpdateModel model);

    /**
     * 查询
     */
    UserQueryModel query(Integer id);
}
