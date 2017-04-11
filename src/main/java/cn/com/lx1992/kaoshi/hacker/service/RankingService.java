/*
 * Copyright © 2017 LuoXin. All Rights Reserved.
 */
package cn.com.lx1992.kaoshi.hacker.service;

import cn.com.lx1992.kaoshi.hacker.constant.UrlConstant;
import cn.com.lx1992.kaoshi.hacker.mapper.MetadataMapper;
import cn.com.lx1992.kaoshi.hacker.mapper.RankingMapper;
import cn.com.lx1992.kaoshi.hacker.meta.MetadataKeyEnum;
import cn.com.lx1992.kaoshi.hacker.model.MetadataUpdateModel;
import cn.com.lx1992.kaoshi.hacker.model.RankingSaveModel;
import cn.com.lx1992.kaoshi.hacker.util.DateTimeUtil;
import cn.com.lx1992.kaoshi.hacker.util.HttpUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        try {
            parse(request());
        } catch (Exception e) {
            logger.error("crawling ranking failed", e);
        }
    }

    public void query(Integer limit) {
        //查询最新完整榜单
        //截取最前面limit条记录
    }

    /**
     * 请求
     */
    private String request() throws Exception {
        logger.info("request ranking page");
        return HttpUtil.request(UrlConstant.RANKING);
    }

    /**
     * 解析
     */
    private void parse(String data) {
        //上榜人数
        AtomicInteger count = new AtomicInteger();
        //解析数据
        Document document = Jsoup.parse(data);
        Elements elements = document.body().select("table").first().select("tr");
        //保存排行榜
        elements.parallelStream()
                .forEach((element) -> {
                    logger.info("parse ranking item {}", element.toString().replaceAll("\n", ""));
                    Elements item = element.select("td");
                    if (item.size() != 4) {
                        //第一个item是表头，不包含td标签
                        logger.warn("ranking item size {} not correct", item.size());
                        return;
                    }
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
        model.setKey(MetadataKeyEnum.RANKING_LAST_CRAWLING.getKey());
        model.setValue(DateTimeUtil.getNowStr());
        metadataMapper.update(model);
        model.setKey(MetadataKeyEnum.RANKING_USER_COUNT.getKey());
        model.setValue(String.valueOf(count.get()));
        metadataMapper.update(model);
    }
}
