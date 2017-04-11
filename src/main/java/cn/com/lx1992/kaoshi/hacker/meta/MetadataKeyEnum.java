/*
 * Copyright © 2017 LuoXin. All Rights Reserved.
 */
package cn.com.lx1992.kaoshi.hacker.meta;

/**
 * 元数据键
 *
 * @author luoxin
 * @version 2017-4-11
 */
public enum MetadataKeyEnum {
    RANKING_LAST_CRAWLING("ranking_last_crawling", "排行榜上次爬取时间"),
    RANKING_USER_COUNT("ranking_user_count", "排行榜上榜人数"),
    ;

    private String key;
    private String description;

    MetadataKeyEnum(String key, String description) {
        this.key = key;
        this.description = description;
    }

    public String getKey() {
        return key;
    }

    public String getDescription() {
        return description;
    }
}
