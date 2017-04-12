/*
 * Copyright © 2017 LuoXin. All Rights Reserved.
 */
package cn.com.lx1992.kaoshi.hacker.service;

import cn.com.lx1992.kaoshi.hacker.constant.MetadataKeyConstant;
import cn.com.lx1992.kaoshi.hacker.constant.UrlConstant;
import cn.com.lx1992.kaoshi.hacker.exception.BizException;
import cn.com.lx1992.kaoshi.hacker.mapper.MetadataMapper;
import cn.com.lx1992.kaoshi.hacker.mapper.RankingMapper;
import cn.com.lx1992.kaoshi.hacker.model.MetadataUpdateModel;
import cn.com.lx1992.kaoshi.hacker.model.RankingSaveModel;
import cn.com.lx1992.kaoshi.hacker.util.DateTimeUtils;
import cn.com.lx1992.kaoshi.hacker.util.HttpUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 排行榜
 *
 * @author luoxin
 * @version 2017-4-10
 */
@Service
public class RankingService {
    private final Logger logger = LoggerFactory.getLogger(RankingService.class);

    @Autowired
    private RankingMapper rankingMapper;
    @Autowired
    private MetadataMapper metadataMapper;

    /**
     * 爬取
     */
    @Transactional
    public void crawling() {
        parseData(requestData());
    }

    private String requestData() {
        logger.info("request ranking page");
        try {
            return HttpUtils.execute(UrlConstant.RANKING, null).body().string();
        } catch (IOException e) {
            logger.error("request ranking page failed", e);
            throw new BizException("爬取排行榜失败(请求错误)");
        }
    }

    private void parseData(String data) {
        //上榜人数
        AtomicInteger count = new AtomicInteger(0);
        //解析数据
        Document document = Jsoup.parse(data);
        Elements elements = document.body().select("table").first().select("tr");
        elements.parallelStream()
                //跳过表头行
                .skip(1)
                .forEach((element) -> {
                    logger.info("parse ranking item {}", element.toString().replaceAll("\n", ""));
                    Elements item = element.select("td");
                    if (item.size() != 4) {
                        logger.warn("ranking item size {} not correct", item.size());
                        return;
                    }
                    //保存排行榜
                    RankingSaveModel model = new RankingSaveModel();
                    model.setRank(item.get(0).html());
                    model.setName(item.get(1).html());
                    model.setScore(item.get(2).html());
                    model.setTime(item.get(3).html());
                    rankingMapper.save(model);
                    //上榜人数自增
                    count.incrementAndGet();
                });
        //更新元数据
        MetadataUpdateModel model = new MetadataUpdateModel();
        model.setKey(MetadataKeyConstant.RANKING_LAST_CRAWLING);
        model.setValue(DateTimeUtils.getNowStr());
        metadataMapper.update(model);
        model.setKey(MetadataKeyConstant.RANKING_COUNT);
        model.setValue(String.valueOf(count.get()));
        metadataMapper.update(model);
    }
}
