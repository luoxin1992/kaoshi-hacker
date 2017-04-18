/*
 * Copyright © 2017 LuoXin. All Rights Reserved.
 */
package cn.com.lx1992.kaoshi.hacker.service;

import cn.com.lx1992.kaoshi.hacker.constant.MetadataKeyConstant;
import cn.com.lx1992.kaoshi.hacker.constant.UrlConstant;
import cn.com.lx1992.kaoshi.hacker.mapper.MetadataMapper;
import cn.com.lx1992.kaoshi.hacker.mapper.RankingMapper;
import cn.com.lx1992.kaoshi.hacker.model.MetadataUpdateModel;
import cn.com.lx1992.kaoshi.hacker.model.RankingCompareModel;
import cn.com.lx1992.kaoshi.hacker.model.RankingQueryModel;
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
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
            throw new RuntimeException("爬取排行榜失败(请求错误)");
        }
    }

    private void parseData(String data) {
        //上榜人数
        AtomicInteger count = new AtomicInteger(0);
        //解析数据
        Document document = Jsoup.parse(data);
        Elements elements = document.body().select("table").first().select("tr");
        elements.stream()
                //跳过表头行
                .skip(1)
                .forEach((element) -> {
                    logger.info("parse ranking item {}", element.toString().replaceAll("\n", ""));
                    Elements item = element.select("td");
                    if (item.size() != 4) {
                        logger.warn("ranking item size {} incorrect", item.size());
                        return;
                    }
                    //保存排行榜
                    RankingSaveModel rankingSave = new RankingSaveModel();
                    rankingSave.setRank(item.get(0).html());
                    rankingSave.setName(item.get(1).html());
                    rankingSave.setScore(item.get(2).html());
                    rankingSave.setTime(item.get(3).html());
                    rankingMapper.save(rankingSave);
                    logger.info("save ranking item {}", rankingSave.getId());
                    //上榜人数自增
                    count.incrementAndGet();
                });
        logger.info("parse {} ranking item(s) successfully", count.get());
        //更新元数据
        MetadataUpdateModel metadataUpdate = new MetadataUpdateModel();
        metadataUpdate.setKey(MetadataKeyConstant.RANKING_LAST_CRAWLING);
        metadataUpdate.setValue(DateTimeUtils.getNowStr());
        metadataMapper.update(metadataUpdate);
        metadataUpdate.setKey(MetadataKeyConstant.RANKING_COUNT);
        metadataUpdate.setValue(String.valueOf(count.get()));
        metadataMapper.update(metadataUpdate);
    }

    /**
     * 查询
     */
    public Map<String, Object> query(Integer limit) {
        logger.info("query ranking in top {}", limit == null ? "[all]" : limit);
        //排行榜
        int count = Integer.valueOf(metadataMapper.query(MetadataKeyConstant.RANKING_COUNT).getValue());
        if (limit == null || limit <= 0 || limit > count) {
            limit = count;
        }
        List<RankingQueryModel> models = rankingMapper.query(count, limit);
        if (CollectionUtils.isEmpty(models)) {
            logger.warn("ranking result is empty");
            return null;
        }
        //排行榜更新时间
        String timestamp = metadataMapper.query(MetadataKeyConstant.RANKING_LAST_CRAWLING).getValue();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("timestamp", timestamp);
        result.put("count", models.size());
        result.put("data", models);
        return result;
    }

    /**
     * 比较
     */
    public Map<String, Object> compare(String name) {
        logger.info("compare ranking for {}", name);
        List<RankingCompareModel> models = rankingMapper.compare(name);
        if (CollectionUtils.isEmpty(models)) {
            logger.warn("ranking result is empty");
            return null;
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("count", models.size());
        result.put("data", models);
        return result;
    }
}
