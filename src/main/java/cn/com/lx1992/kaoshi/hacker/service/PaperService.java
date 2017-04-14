/*
 * Copyright © 2017 LuoXin. All Rights Reserved.
 */
package cn.com.lx1992.kaoshi.hacker.service;

import cn.com.lx1992.kaoshi.hacker.constant.CommonConstant;
import cn.com.lx1992.kaoshi.hacker.constant.MetadataKeyConstant;
import cn.com.lx1992.kaoshi.hacker.constant.UrlConstant;
import cn.com.lx1992.kaoshi.hacker.mapper.MetadataMapper;
import cn.com.lx1992.kaoshi.hacker.mapper.PaperMapper;
import cn.com.lx1992.kaoshi.hacker.mapper.QuestionMapper;
import cn.com.lx1992.kaoshi.hacker.model.MetadataQueryModel;
import cn.com.lx1992.kaoshi.hacker.model.MetadataUpdateModel;
import cn.com.lx1992.kaoshi.hacker.model.PaperSaveModel;
import cn.com.lx1992.kaoshi.hacker.model.QuestionQueryModel;
import cn.com.lx1992.kaoshi.hacker.model.QuestionSaveModel;
import cn.com.lx1992.kaoshi.hacker.model.QuestionUpdateModel;
import cn.com.lx1992.kaoshi.hacker.util.CommonUtils;
import cn.com.lx1992.kaoshi.hacker.util.DateTimeUtils;
import cn.com.lx1992.kaoshi.hacker.util.HttpUtils;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
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
    private PaperMapper paperMapper;
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private MetadataMapper metadataMapper;

    @Transactional
    public void crawling() {
        MetadataQueryModel metadataQuery;
        //试卷总数
        metadataQuery = metadataMapper.query(MetadataKeyConstant.PAPER_COUNT);
        AtomicInteger count = new AtomicInteger(Integer.parseInt(metadataQuery.getValue()));
        //已爬取最大试卷ID
        metadataQuery = metadataMapper.query(MetadataKeyConstant.PAPER_LAST_CRAWLING_MAX_ID);
        int start = Integer.parseInt(metadataQuery.getValue());
        //每次随机爬取25份以内试卷
        int end = start + (int) (Math.random() * 25);
        //记录爬取开始时间
        MetadataUpdateModel metadataUpdate = new MetadataUpdateModel();
        metadataUpdate.setKey(MetadataKeyConstant.PAPER_LAST_CRAWLING_START);
        metadataUpdate.setValue(DateTimeUtils.getNowStr());
        metadataMapper.update(metadataUpdate);

        logger.info("start crawling paper(s) from {} to {}", start, end);
        //随机乱序
        IntStream ids = IntStream.range(start, end).unordered();
        ids.forEach((id) -> {
            //试卷内容
            ConcurrentHashMap<Integer, String> result = parsePaper(requestData(id));
            if (!CollectionUtils.isEmpty(result)) {
                analysisPaper(id, result);
            }
            count.incrementAndGet();
            try {
                //每爬取一份随机休眠0~10秒
                Thread.sleep((long) (Math.random() * 10000));
            } catch (InterruptedException ignored) {
            }
        });

        //记录爬取结束时间和试卷记录数
        metadataUpdate.setKey(MetadataKeyConstant.PAPER_LAST_CRAWLING_END);
        metadataUpdate.setValue(DateTimeUtils.getNowStr());
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

    private ConcurrentHashMap<Integer, String> parsePaper(String data) {
        //试题总数
        MetadataQueryModel metadataQuery = metadataMapper.query(MetadataKeyConstant.QUESTION_COUNT);
        AtomicInteger count = new AtomicInteger(Integer.parseInt(metadataQuery.getValue()));
        //作答情况
        ConcurrentHashMap<Integer, String> result = new ConcurrentHashMap<>();
        //解析试卷
        Document document = Jsoup.parse(data);
        Elements elements = document.body().getElementsByClass("paperexamcontent");
        //部分ID对应的试卷不存在 返回的页面只有一个架子 此时elements是空的 下面的逻辑不会执行
        elements.forEach((element -> {
            logger.info("parse paper item {}", element.toString().replaceAll("\n", ""));
            //试题id
            int id = CommonUtils.extractNumber(element.id());
            if (id == -1) {
                logger.warn("extract question id failed from {}", element.id());
                return;
            }
            //解析试题
            //该方法的事务传播属性为Propagation.REQUIRES_NEW 即开启一个新事务作为子事务
            //但在同一个service中直接调用不会触发切面 除非通过自身的代理类调用
            parseQuestion(element, id, count);
            //分析试卷
            Elements item = element.select("table").first().select("tr");
            if (item.size() < 5) {
                logger.warn("question answer & analysis size {} incorrect", item.size());
                return;
            }
            String choice = item.get(4).text();
            result.put(id, choice);
        }));
        count.incrementAndGet();
        return result;
    }

    private void parseQuestion(Element element, int questionId, AtomicInteger count) {
        logger.info("parse question {}", questionId);
        //判断相同ID的试题是否已经存在
        Integer id = questionMapper.queryId(questionId);
        if (id != null) {
            logger.warn("question already exist");
            return;
        }
        Elements item;
        //解析题干和选项
        item = element.getElementsByClass("choice");
        if (item.size() != 2) {
            logger.warn("question stem & option size {} incorrect", item.size());
        }
        String stem = item.get(0).text();
        String option;
        //有部分试题 题干和选项连在一起
        if (item.size() == 2) {
            option = item.get(1).text();
        } else {
            option = "";
        }
        //解析答案
        item = element.select("table").first().select("tr");
        if (item.size() < 3) {
            logger.warn("question answer & analysis size {} incorrect", item.size());
            return;
        }
        String answer = item.get(2).text();
        //保存到数据库
        QuestionSaveModel questionSave = new QuestionSaveModel();
        questionSave.setQuestionId(questionId);
        questionSave.setStem(stem);
        questionSave.setOption(option);
        questionSave.setAnswer(answer);
        questionMapper.save(questionSave);
        count.incrementAndGet();
    }

    private void analysisPaper(int paperId, ConcurrentHashMap<Integer, String> results) {
        //成绩
        AtomicInteger score = new AtomicInteger(0);
        //分析试题
        results.forEach((key, value) -> analysisQuestion(key, value, score));
        //保存试卷
        String resultData = results.entrySet().stream()
                .map((result) -> result.getKey() + "=" + result.getValue())
                .collect(Collectors.joining(",", "[", "]"));
        PaperSaveModel paperSave = new PaperSaveModel();
        paperSave.setPaperId(paperId);
        paperSave.setData(resultData);
        paperSave.setScore(score.get());
        paperMapper.save(paperSave);
    }

    private void analysisQuestion(int questionId, String choice, AtomicInteger score) {
        Integer id = questionMapper.queryId(questionId);
        if (id == null) {
            logger.warn("question {} not exist", questionId);
            return;
        }
        QuestionQueryModel questionQuery = questionMapper.query(id);
        //试题分析
        QuestionUpdateModel questionUpdate = new QuestionUpdateModel();
        questionUpdate.setId(id);
        //试题抽中次数
        int selectTimes = questionQuery.getSelectTimes() + 1;
        questionUpdate.setSelectTimes(selectTimes);
        //各选项选择人数
        if ("A".equals(choice)) {
            int aChooser = questionQuery.getaChooser() + 1;
            questionUpdate.setaChooser(aChooser);
        } else if ("B".equals(choice)) {
            int bChooser = questionQuery.getbChooser() + 1;
            questionUpdate.setbChooser(bChooser);
        } else if ("C".equals(choice)) {
            int cChooser = questionQuery.getcChooser() + 1;
            questionUpdate.setcChooser(cChooser);
        } else if ("D".equals(choice)) {
            int dChooser = questionQuery.getdChooser() + 1;
            questionUpdate.setdChooser(dChooser);
        }
        String answer = questionQuery.getAnswer();
        //正确率 精确到小数点后三位
        if ("A".equals(answer)) {
            int aChooser = answer.equals(choice) ? questionUpdate.getaChooser() : questionQuery.getaChooser();
            double correctRate = Math.floorDiv(aChooser * 1000, selectTimes);
            questionUpdate.setCorrectRate((int) correctRate);
        } else if ("B".equals(answer)) {
            int bChooser = answer.equals(choice) ? questionUpdate.getbChooser() : questionQuery.getbChooser();
            double correctRate = Math.floorDiv(bChooser * 1000, selectTimes);
            questionUpdate.setCorrectRate((int) correctRate);
        } else if ("C".equals(answer)) {
            int cChooser = answer.equals(choice) ? questionUpdate.getcChooser() : questionQuery.getcChooser();
            double correctRate = Math.floorDiv(cChooser * 1000, selectTimes);
            questionUpdate.setCorrectRate((int) correctRate);
        } else if ("D".equals(answer)) {
            int dChooser = answer.equals(choice) ? questionUpdate.getdChooser() : questionQuery.getdChooser();
            double correctRate = Math.floorDiv(dChooser * 1000, selectTimes);
            questionUpdate.setCorrectRate((int) correctRate);
        }
        questionMapper.update(questionUpdate);
        //成绩统计
        if (answer.equals(choice)) {
            score.incrementAndGet();
        }
    }
}
