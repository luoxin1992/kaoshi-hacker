/*
 * Copyright © 2017 LuoXin. All Rights Reserved.
 */
package cn.com.lx1992.kaoshi.hacker.service;

import cn.com.lx1992.kaoshi.hacker.constant.MetadataKeyConstant;
import cn.com.lx1992.kaoshi.hacker.constant.UrlConstant;
import cn.com.lx1992.kaoshi.hacker.mapper.MetadataMapper;
import cn.com.lx1992.kaoshi.hacker.mapper.RankingMapper;
import cn.com.lx1992.kaoshi.hacker.model.MetadataQueryModel;
import cn.com.lx1992.kaoshi.hacker.model.MetadataUpdateModel;
import cn.com.lx1992.kaoshi.hacker.model.RankingCompareModel;
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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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

    /**
     * 查询
     */
    public Map<String, Object> query(Integer limit) {
        logger.info("query ranking in top {}", limit == null ? "[all]" : limit);
        Map<String, Object> result = new LinkedHashMap<>();
        //排行榜更新时间
        result.put("timestamp", metadataMapper.query(MetadataKeyConstant.RANKING_LAST_CRAWLING).getValue());
        //排行榜
        int round = Integer.parseInt(metadataMapper.query(MetadataKeyConstant.RANKING_NEXT_ROUND).getValue()) - 1;
        if (limit == null) {
            limit = Integer.valueOf(metadataMapper.query(MetadataKeyConstant.RANKING_COUNT).getValue());
        }
        result.put("ranking", rankingMapper.query(round, limit));
        return result;
    }

    /**
     * 比较
     */
    public Map<String, Object> compare(String name) {
        logger.info("compare ranking for {}", name);
        List<RankingCompareModel> models = rankingMapper.compare(name);
        if (CollectionUtils.isEmpty(models)) {
            logger.warn("compare result is empty");
            return null;
        }
        //排行榜爬取累计次数 填充未上榜数据
        Map<Integer, RankingCompareModel> modelMap = new HashMap<>();
        int round = Integer.parseInt(metadataMapper.query(MetadataKeyConstant.RANKING_NEXT_ROUND).getValue());
        for (int i = 1; i < round; i++) {
            modelMap.put(i, RankingCompareModel.buildNull());
        }
        for (RankingCompareModel model : models) {
            modelMap.replace(model.getRound(), model);
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("name", models.get(0).getName());
        result.put("rank", modelMap.values().stream()
                .map(RankingCompareModel::getRank)
                .collect(Collectors.joining(",", "[", "]")));
        result.put("score", modelMap.values().stream()
                .map(RankingCompareModel::getScore)
                .collect(Collectors.joining(",", "[", "]")));
        result.put("time", modelMap.values().stream()
                .map(RankingCompareModel::getTime)
                .collect(Collectors.joining(",", "[", "]")));
        return result;
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
        //爬取轮次
        MetadataQueryModel metadataQuery = metadataMapper.query(MetadataKeyConstant.RANKING_NEXT_ROUND);
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
                    rankingSave.setRound(Integer.valueOf(metadataQuery.getValue()));
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
        metadataUpdate.setKey(MetadataKeyConstant.RANKING_NEXT_ROUND);
        metadataUpdate.setValue(String.valueOf(Integer.parseInt(metadataQuery.getValue()) + 1));
        metadataMapper.update(metadataUpdate);
    }
}
