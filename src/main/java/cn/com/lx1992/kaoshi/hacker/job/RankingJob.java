/*
 * Copyright © 2017 LuoXin. All Rights Reserved.
 */
package cn.com.lx1992.kaoshi.hacker.job;

import cn.com.lx1992.kaoshi.hacker.service.RankingService;
import cn.com.lx1992.kaoshi.hacker.util.DateTimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 排行榜定时任务
 * 调度频率 从00:15:00开始每6小时一次
 *
 * @author luoxin
 * @version 2017-4-13
 */
@Component
public class RankingJob {
    private final Logger logger = LoggerFactory.getLogger(RankingJob.class);

    @Autowired
    private RankingService rankingService;

    @Scheduled(cron = "0 15 0/6 * * ?")
    public void execute() {
        logger.info("ranking crawling job start at {}", DateTimeUtils.getNowStr());
        rankingService.crawling();
        logger.info("ranking crawling job end at {}", DateTimeUtils.getNowStr());
    }
}
