/*
 * Copyright © 2017 LuoXin. All Rights Reserved.
 */
package cn.com.lx1992.kaoshi.hacker.service;

import cn.com.lx1992.kaoshi.hacker.constant.CommonConstant;
import cn.com.lx1992.kaoshi.hacker.constant.MetadataKeyConstant;
import cn.com.lx1992.kaoshi.hacker.constant.UrlConstant;
import cn.com.lx1992.kaoshi.hacker.mapper.MetadataMapper;
import cn.com.lx1992.kaoshi.hacker.mapper.PaperMapper;
import cn.com.lx1992.kaoshi.hacker.mapper.PaperQuestionMapper;
import cn.com.lx1992.kaoshi.hacker.model.MetadataQueryModel;
import cn.com.lx1992.kaoshi.hacker.model.MetadataUpdateModel;
import cn.com.lx1992.kaoshi.hacker.model.PaperQueryModel;
import cn.com.lx1992.kaoshi.hacker.model.PaperQuestionQueryModel;
import cn.com.lx1992.kaoshi.hacker.model.PaperQuestionSaveModel;
import cn.com.lx1992.kaoshi.hacker.model.PaperSaveModel;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

/**
 * 试卷Service
 *
 * @author luoxin
 * @version 2017-4-13
 */
@Service
public class PaperService {
    private final Logger logger = LoggerFactory.getLogger(PaperService.class);

    @Autowired
    private UserService userService;
    @Autowired
    private QuestionService questionService;

    @Autowired
    private PaperMapper paperMapper;
    @Autowired
    private PaperQuestionMapper paperQuestionMapper;
    @Autowired
    private MetadataMapper metadataMapper;

    @Transactional
    public void crawling() {
        MetadataQueryModel metadataQuery;
        //试卷总数和已爬取最大试卷ID
        metadataQuery = metadataMapper.query(MetadataKeyConstant.PAPER_COUNT);
        AtomicInteger count = new AtomicInteger(Integer.parseInt(metadataQuery.getValue()));
        metadataQuery = metadataMapper.query(MetadataKeyConstant.PAPER_LAST_CRAWLING_MAX_ID);
        int start = Integer.parseInt(metadataQuery.getValue());
        //每次随机爬取200份以内试卷
        int end = start + (int) (Math.random() * 200);
        logger.info("already crawled {} paper(s) with id below {}", count.get(), start);
        //记录爬取开始时间
        MetadataUpdateModel metadataUpdate = new MetadataUpdateModel();
        metadataUpdate.setKey(MetadataKeyConstant.PAPER_LAST_CRAWLING_START);
        metadataUpdate.setValue(DateTimeUtils.getNowStr());
        metadataMapper.update(metadataUpdate);

        logger.info("start crawling paper(s) from {} to {}", start, end);
        IntStream ids = IntStream.range(start, end);
        ids.forEach((id) -> {
            try {
                //每爬取一份随机休眠0~3秒
                Thread.sleep((long) (Math.random() * 3000));
            } catch (InterruptedException ignored) {
            }
            //试卷内容
            ConcurrentHashMap<Integer, List<String>> result = parsePaper(requestData(id));
            if (CollectionUtils.isEmpty(result)) {
                logger.warn("paper has no result, maybe not exist");
                return;
            }
            analysisPaper(id, result);
            count.incrementAndGet();
        });

        //记录爬取结束时间、下次爬取开始ID和试卷记录数
        metadataUpdate.setKey(MetadataKeyConstant.PAPER_LAST_CRAWLING_END);
        metadataUpdate.setValue(DateTimeUtils.getNowStr());
        metadataMapper.update(metadataUpdate);
        metadataUpdate.setKey(MetadataKeyConstant.PAPER_LAST_CRAWLING_MAX_ID);
        metadataUpdate.setValue(String.valueOf(end));
        metadataMapper.update(metadataUpdate);
        metadataUpdate.setKey(MetadataKeyConstant.PAPER_COUNT);
        metadataUpdate.setValue(String.valueOf(count.get()));
        metadataMapper.update(metadataUpdate);
    }

    private String requestData(int id) {
        logger.info("request paper data with id {}", id);
        String url = UrlConstant.PAPER + "&ehid=" + id;
        try {
            Map<String, String> cookies = userService.buildCookies(CommonConstant.CRAWLING_USER_ID);
            Response response = HttpUtils.execute(url, cookies);
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
            logger.error("request paper page failed", e);
            throw new RuntimeException("爬取试卷失败(请求错误)");
        }
    }

    private ConcurrentHashMap<Integer, List<String>> parsePaper(String data) {
        //作答情况
        ConcurrentHashMap<Integer, List<String>> result = new ConcurrentHashMap<>();
        //解析试卷
        Document document = Jsoup.parse(data);
        Elements elements = document.body().getElementsByClass("paperexamcontent");
        //部分ID对应的试卷不存在 返回的页面只有一个架子 此时elements是空的 下面的逻辑不会执行
        elements.forEach((element -> {
            logger.info("parse paper item {}", element.toString().replaceAll("\n", ""));
            //试题id
            int id = CommonUtils.extractNumber(element.id());
            if (id == -1) {
                logger.error("extract question id failed from {}", element.id());
                return;
            }
            //解析试题
            questionService.parseQuestion(element, id);
            //分析试卷
            Elements item = element.select("table").first().select("tr");
            if (item.size() < 5) {
                logger.warn("question answer & analysis size {} incorrect", item.size());
                return;
            }
            List<String> paperQuestion = new ArrayList<>();
            //考试答案
            paperQuestion.add(item.get(4).text());
            //作答时间
            paperQuestion.add(CommonUtils.extractDatetime(item.get(0).text()));
            result.put(id, paperQuestion);
        }));
        return result;
    }

    private void analysisPaper(int paperId, ConcurrentHashMap<Integer, List<String>> results) {
        //成绩
        AtomicInteger score = new AtomicInteger(0);
        //分析试题
        results.forEach((key, value) -> {
            if (questionService.analysisQuestion(key, value.get(0))) {
                score.incrementAndGet();
            }
        });
        //保存试卷-试题
        results.forEach((key, value) -> {
            PaperQuestionSaveModel paperQuestionSave = new PaperQuestionSaveModel();
            paperQuestionSave.setPaperId(paperId);
            paperQuestionSave.setQuestionId(key);
            paperQuestionSave.setChoice(value.get(0));
            paperQuestionSave.setTime(value.get(1));
            paperQuestionMapper.save(paperQuestionSave);
            logger.info("save paper-question {}", paperQuestionSave.getId());
        });
        //保存试卷
        PaperSaveModel paperSave = new PaperSaveModel();
        paperSave.setPaperId(paperId);
        paperSave.setScore(score.get());
        paperMapper.save(paperSave);
        logger.info("save paper {}", paperSave.getId());
    }

    public PaperQueryModel query(Integer paperId) {
        logger.info("query paper {}", paperId);
        List<PaperQuestionQueryModel> paperQuestionQuery = paperQuestionMapper.query(paperId);
        if (CollectionUtils.isEmpty(paperQuestionQuery)) {
            logger.warn("paper not exist");
            return null;
        }
        PaperQueryModel paperQuery = paperMapper.query(paperId);
        paperQuery.setQuestions(paperQuestionQuery);
        return paperQuery;
    }
}
