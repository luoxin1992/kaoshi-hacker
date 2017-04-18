/*
 * Copyright © 2017 LuoXin. All Rights Reserved.
 */
package cn.com.lx1992.kaoshi.hacker.job;

import cn.com.lx1992.kaoshi.hacker.service.ScoreService;
import cn.com.lx1992.kaoshi.hacker.util.DateTimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 成绩单定时任务
 * 调度频率 从03:45:00开始每12小时一次
 *
 * @author luoxin
 * @version 2017-4-13
 */
@Component
public class ScoreJob {
    private final Logger logger = LoggerFactory.getLogger(ScoreJob.class);

    @Autowired
    private ScoreService scoreService;

    @Scheduled(cron = "0 45 3/12 * * ?")
    public void execute() {
        logger.info("score crawling job start at {}", DateTimeUtils.getNowStr());
        scoreService.crawling();
        logger.info("score crawling job end at {}", DateTimeUtils.getNowStr());
    }
}
