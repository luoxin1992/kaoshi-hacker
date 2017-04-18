/*
 * Copyright © 2017 LuoXin. All Rights Reserved.
 */
package cn.com.lx1992.kaoshi.hacker.job;

import cn.com.lx1992.kaoshi.hacker.service.PaperService;
import cn.com.lx1992.kaoshi.hacker.util.DateTimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 试卷定时任务
 * 调度频率 从00:00:00开始每30分钟一次
 *
 * @author luoxin
 * @version 2017-4-13
 */
@Component
public class PaperJob {
    private final Logger logger = LoggerFactory.getLogger(PaperJob.class);

    @Autowired
    private PaperService paperService;

    @Scheduled(cron = "0 0/30 * * * ?")
    public void execute() {
        logger.info("paper crawling job start at {}", DateTimeUtils.getNowStr());
        paperService.crawling();
        logger.info("paper crawling job end at {}", DateTimeUtils.getNowStr());
    }
}
