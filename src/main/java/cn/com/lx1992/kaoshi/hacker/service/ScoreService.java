/*
 * Copyright © 2017 LuoXin. All Rights Reserved.
 */
package cn.com.lx1992.kaoshi.hacker.service;

import cn.com.lx1992.kaoshi.hacker.constant.CommonConstant;
import cn.com.lx1992.kaoshi.hacker.constant.MetadataKeyConstant;
import cn.com.lx1992.kaoshi.hacker.constant.UrlConstant;
import cn.com.lx1992.kaoshi.hacker.mapper.MetadataMapper;
import cn.com.lx1992.kaoshi.hacker.mapper.ScoreMapper;
import cn.com.lx1992.kaoshi.hacker.model.MetadataQueryModel;
import cn.com.lx1992.kaoshi.hacker.model.MetadataUpdateModel;
import cn.com.lx1992.kaoshi.hacker.model.ScoreAnalyzeModel;
import cn.com.lx1992.kaoshi.hacker.model.ScoreQueryModel;
import cn.com.lx1992.kaoshi.hacker.model.ScoreSaveModel;
import cn.com.lx1992.kaoshi.hacker.util.CommonUtils;
import cn.com.lx1992.kaoshi.hacker.util.DateTimeUtils;
import cn.com.lx1992.kaoshi.hacker.util.HttpUtils;
import okhttp3.Response;
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
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 成绩单Service
 *
 * @author luoxin
 * @version 2017-4-11
 */
@Service
public class ScoreService {
    private final Logger logger = LoggerFactory.getLogger(ScoreService.class);

    @Autowired
    private UserService userService;

    @Autowired
    private ScoreMapper scoreMapper;
    @Autowired
    private MetadataMapper metadataMapper;

    /**
     * 爬取
     */
    @Transactional
    public void crawling() {
        //成绩单只需增量更新 查询上次爬取的时间和记录数
        MetadataQueryModel metadataQuery;
        metadataQuery = metadataMapper.query(MetadataKeyConstant.SCORE_LAST_CRAWLING_START);
        String start = metadataQuery.getValue();
        metadataQuery = metadataMapper.query(MetadataKeyConstant.SCORE_COUNT);
        AtomicInteger count = new AtomicInteger(Integer.parseInt(metadataQuery.getValue()));
        logger.info("already crawled {} score item(s) before {}", count.get(), start);
        //记录爬取开始时间
        MetadataUpdateModel metadataUpdate = new MetadataUpdateModel();
        metadataUpdate.setKey(MetadataKeyConstant.SCORE_LAST_CRAWLING_START);
        metadataUpdate.setValue(DateTimeUtils.getNowStr());
        metadataMapper.update(metadataUpdate);

        //先爬取第一页
        String data = requestData(1);
        parseScore(data, start, count);
        //解析页码 继续爬第2~N页
        int number = parsePageNumber(data);
        if (number > 1) {
            IntStream pages = IntStream.rangeClosed(2, number);
            //并行请求存在BUG
            //若cookie突然失效 多个线程同时发起登录 得到多个新的cookie(但仅只有1个是有效的)
            //且更新cookie字段和其他操作位于同一个事务中 在爬取流程完成之前不会提交 导致死锁
            pages.forEach((page) -> {
                try {
                    //每爬取一页随机休眠0~3秒
                    Thread.sleep((long) (Math.random() * 3000));
                } catch (InterruptedException ignored) {
                }
                parseScore(requestData(page), start, count);
            });
        }
        logger.info("parse {} score item(s) successfully", count.get());

        //记录爬取结束时间和爬取记录数
        metadataUpdate.setKey(MetadataKeyConstant.SCORE_LAST_CRAWLING_END);
        metadataUpdate.setValue(DateTimeUtils.getNowStr());
        metadataMapper.update(metadataUpdate);
        metadataUpdate.setKey(MetadataKeyConstant.SCORE_COUNT);
        metadataUpdate.setValue(String.valueOf(count.get()));
        metadataMapper.update(metadataUpdate);
    }

    private String requestData(int page) {
        logger.info("request score data in page {}", page);
        String url = UrlConstant.SCORE;
        if (page > 1) {
            url += "&page=" + page;
        }
        try {
            Map<String, String> cookies = userService.buildCookies(CommonConstant.CRAWLING_USER_ID);
            Response response = HttpUtils.execute(url, cookies);
            //发生重定向 则cookie失效 需要重新登录
            while (response.isRedirect()) {
                logger.info("session invalid, try re-login");
                try {
                    Thread.sleep((long) (Math.random() * 5000));
                } catch (InterruptedException ignored) {
                }
                userService.login(CommonConstant.CRAWLING_USER_ID);
                cookies = userService.buildCookies(CommonConstant.CRAWLING_USER_ID);
                response = HttpUtils.execute(url, cookies);
            }
            return response.body().string();
        } catch (IOException e) {
            logger.error("request score page failed", e);
            throw new RuntimeException("爬取成绩单失败(请求错误)");
        }
    }

    /**
     * 解析成绩数据
     */
    private void parseScore(String data, String start, AtomicInteger count) {
        Document document = Jsoup.parse(data);
        Elements elements = document.body().select("table").first().select("tr");
        elements.stream()
                .skip(1)
                .forEach((element -> {
                    logger.info("parse score item {}", element.toString().replaceAll("\n", ""));
                    Elements item = element.select("td");
                    if (item.size() != 5) {
                        logger.warn("score item size {} incorrect", item.size());
                        return;
                    }
                    //只需要爬取时间早于上次爬取的成绩
                    if (DateTimeUtils.before(DateTimeUtils.parse(item.get(3).html()), DateTimeUtils.parse(start))) {
                        logger.warn("score item with time {} already crawled", item.get(3).html());
                        return;
                    }
                    ScoreSaveModel scoreSave = new ScoreSaveModel();
                    scoreSave.setName(item.get(1).html());
                    scoreSave.setScore(item.get(2).html());
                    scoreSave.setTime(item.get(3).html());
                    //页面上的"用时"格式为"XX秒"或"XX分" 爬取时统一用秒 去掉文字单位
                    String timeCostStr = item.get(4).html();
                    int timeCostNum = CommonUtils.extractNumber(timeCostStr);
                    if (timeCostStr.contains("分钟")) {
                        timeCostNum *= 60;
                    }
                    scoreSave.setTimeCost(String.valueOf(timeCostNum));
                    scoreMapper.save(scoreSave);
                    logger.info("save score item {}", scoreSave.getId());
                    count.incrementAndGet();
                }));
    }

    /**
     * 解析最大页码
     */
    private int parsePageNumber(String data) {
        Document document = Jsoup.parse(data);
        //获取分页菜单的最后一个链接("末页")的地址
        String href = document.getElementsByClass("pagination pull-right").select("a").last().attr("href");
        //该地址唯一的数字即是最后一页的页码
        int number = CommonUtils.extractNumber(href);
        logger.info("score total page(s) {}", number);
        return number;
    }


    /**
     * 查询成绩(时间段)
     */
    public Map<String, Object> query(String period) {
        logger.info("query score(s) in {}", period);
        String start = null, end = null;
        //日期部分 yyyy-MM-dd
        String base = period.substring(0, 4) + "-" + period.substring(4, 6) + "-" + period.substring(6, 8);
        if (period.length() == 8) {
            //按日期查询 范围 >=给定日期 <给定日期下一天
            start = base;
            end = base.substring(0, 9) + (Integer.parseInt(period.substring(7, 8)) + 1);
        }
        if (period.length() == 10) {
            //按小时查询 范围 >=给定小时 <给定小时的下一小时
            base += " ";
            start = base + period.substring(8, 10);
            end = base + period.substring(8, 9) + (Integer.parseInt(period.substring(9, 10)) + 1);
        }
        if (period.length() == 12) {
            //按分钟查询 范围 >=给定分钟 <给定分钟的下一分钟
            base += " " + period.substring(8, 10) + ":";
            start = base + period.substring(10, 12);
            end = base + period.substring(10, 11) + (Integer.parseInt(period.substring(11, 12)) + 1);
        }
        List<ScoreQueryModel> models = scoreMapper.queryPeriod(start, end);
        if (CollectionUtils.isEmpty(models)) {
            logger.warn("score result is empty");
            return null;
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("count", models.size());
        result.put("data", models);
        return result;
    }

    /**
     * 查询成绩(排序+分页)
     */
    public Map<String, Object> query(String column, String dir, Integer page) {
        logger.info("query all score(s) sort by {} {} in page {}", column, dir, page);
        if (!"time".equals(column)) {
            //除时间外其他字段应是数字 但数据库中为字符串 为排序临时处理方案
            column += "+0";
        }
        List<ScoreQueryModel> models = scoreMapper.queryPage(column, dir, (page - 1) * 20);
        int total = Integer.parseInt(metadataMapper.query(MetadataKeyConstant.SCORE_COUNT).getValue());
        int count = total % 20 == 0 ? total / 20 : total / 20 + 1;
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("count", count);
        result.put("data", models);
        return result;
    }

    /**
     * 分析成绩
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> analyze(String period) {
        logger.info("analyze score(s) in {}", period);
        List<ScoreQueryModel> data = (List<ScoreQueryModel>) query(period).get("data");
        List<ScoreAnalyzeModel> models = data.stream()
                .collect(Collectors.groupingBy((model) -> {
                    if (period.length() == 8) {
                        return model.getTime().substring(0, 13);
                    } else {
                        return model.getTime().substring(0, 16);
                    }
                }))
                .entrySet().stream()
                .map((result) -> {
                    ScoreAnalyzeModel model = new ScoreAnalyzeModel();
                    model.setTime(result.getKey());
                    model.setCount(result.getValue().size());
                    model.setAvgScore(result.getValue().stream()
                            .collect(Collectors.averagingDouble(value -> Double.parseDouble(value.getScore()))));
                    model.setAvgTimeCost(result.getValue().stream()
                            .collect(Collectors.averagingDouble(value -> Double.parseDouble(value.getTimeCost()))));
                    return model;
                })
                .sorted(Comparator.comparing(ScoreAnalyzeModel::getTime))
                .collect(Collectors.toList());
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("count", models.size());
        result.put("data", models);
        return result;
    }
}
