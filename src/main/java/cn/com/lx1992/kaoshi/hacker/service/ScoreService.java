/*
 * Copyright © 2017 LuoXin. All Rights Reserved.
 */
package cn.com.lx1992.kaoshi.hacker.service;

import cn.com.lx1992.kaoshi.hacker.constant.CommonConstant;
import cn.com.lx1992.kaoshi.hacker.constant.MetadataKeyConstant;
import cn.com.lx1992.kaoshi.hacker.constant.UrlConstant;
import cn.com.lx1992.kaoshi.hacker.exception.BizException;
import cn.com.lx1992.kaoshi.hacker.mapper.MetadataMapper;
import cn.com.lx1992.kaoshi.hacker.mapper.ScoreMapper;
import cn.com.lx1992.kaoshi.hacker.model.MetadataQueryModel;
import cn.com.lx1992.kaoshi.hacker.model.MetadataUpdateModel;
import cn.com.lx1992.kaoshi.hacker.model.ScoreSaveModel;
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

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
        metadataQuery = metadataMapper.query(MetadataKeyConstant.SCORE_LAST_CRAWLING);
        String last = metadataQuery.getValue();
        metadataQuery = metadataMapper.query(MetadataKeyConstant.SCORE_COUNT);
        AtomicInteger count = new AtomicInteger(Integer.parseInt(metadataQuery.getValue()));
        //先爬取第一页
        String data = requestData(1);
        parseScore(data, last, count);
        //解析页码 继续爬第2~N页
        int number = parsePageNumber(data);
        if (number > 1) {
            IntStream pages = IntStream.rangeClosed(2, number);
            pages.parallel()
                    .forEach((page) -> {
                        parseScore(requestData(page), last, count);
                        try {
                            //每爬取一页随机休眠10~20秒
                            Thread.sleep((long) (Math.random() * 10000 + 10000));
                        } catch (InterruptedException ignored) {
                        }
                    });
        }
        //更新元数据
        MetadataUpdateModel MetadataUpdate = new MetadataUpdateModel();
        MetadataUpdate.setKey(MetadataKeyConstant.SCORE_LAST_CRAWLING);
        MetadataUpdate.setValue(DateTimeUtils.getNowStr());
        metadataMapper.update(MetadataUpdate);
        MetadataUpdate.setKey(MetadataKeyConstant.SCORE_COUNT);
        MetadataUpdate.setValue(String.valueOf(count.get()));
        metadataMapper.update(MetadataUpdate);
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
                    Thread.sleep((long) (Math.random() * 10000));
                } catch (InterruptedException ignored) {
                }
                userService.login(CommonConstant.CRAWLING_USER_ID);
                cookies = userService.buildCookies(CommonConstant.CRAWLING_USER_ID);
                response = HttpUtils.execute(url, cookies);
            }
            return response.body().string();
        } catch (IOException e) {
            logger.error("request score page failed", e);
            throw new BizException("爬取成绩单失败(请求错误)");
        }
    }

    /**
     * 解析成绩数据
     */
    private void parseScore(String data, String last, AtomicInteger count) {
        Document document = Jsoup.parse(data);
        Elements elements = document.body().select("table").first().select("tr");
        elements.parallelStream()
                .skip(1)
                .forEach((element -> {
                    logger.info("parse score item {}", element.toString().replaceAll("\n", ""));
                    Elements item = element.select("td");
                    if (item.size() != 5) {
                        logger.warn("score item size {} incorrect", item.size());
                        return;
                    }
                    if (last.compareTo(item.get(3).html()) > 0) {
                        ScoreSaveModel scoreSave = new ScoreSaveModel();
                        scoreSave.setName(item.get(1).html());
                        scoreSave.setScore(item.get(2).html());
                        scoreSave.setTime(item.get(3).html());
                        //页面上的"用时"格式为"XX秒"或"XX分" 爬取时统一用秒 去掉文字单位
                        String periodStr = item.get(4).html();
                        int periodNum = matchNumber(periodStr);
                        if (periodStr.contains("分钟")) {
                            periodNum *= 60;
                        }
                        scoreSave.setPeriod(String.valueOf(periodNum));
                        scoreMapper.save(scoreSave);
                        count.incrementAndGet();
                    }
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
        int number = matchNumber(href);
        logger.info("score total page(s) {}", number);
        return number;
    }

    /**
     * 从字符串中匹配数字
     */
    private int matchNumber(String str) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(str);
        if (!matcher.find()) {
            logger.warn("mismatch number from str {}", str);
            return -1;
        }
        return Integer.parseInt(matcher.group());
    }
}
