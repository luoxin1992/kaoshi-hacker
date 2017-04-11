/*
 * Copyright © 2017 LuoXin. All Rights Reserved.
 */
package cn.com.lx1992.kaoshi.hacker.model;

import java.io.Serializable;
import java.util.List;

/**
 * 排行榜 查询
 *
 * @author luoxin
 * @version 2017-4-11
 */
public class RankingQueryModel implements Serializable {
    /**
     * 条目
     */
    private List<RankingItemQueryModel> items;
    /**
     * 爬取时间
     */
    private String timestamp;

    public List<RankingItemQueryModel> getItems() {
        return items;
    }

    public void setItems(List<RankingItemQueryModel> items) {
        this.items = items;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
