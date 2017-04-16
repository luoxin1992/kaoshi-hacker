/*
 * Copyright © 2017 LuoXin. All Rights Reserved.
 */
package cn.com.lx1992.kaoshi.hacker.service;

import cn.com.lx1992.kaoshi.hacker.constant.MetadataKeyConstant;
import cn.com.lx1992.kaoshi.hacker.mapper.MetadataMapper;
import cn.com.lx1992.kaoshi.hacker.mapper.QuestionMapper;
import cn.com.lx1992.kaoshi.hacker.model.MetadataQueryModel;
import cn.com.lx1992.kaoshi.hacker.model.MetadataUpdateModel;
import cn.com.lx1992.kaoshi.hacker.model.QuestionQueryModel;
import cn.com.lx1992.kaoshi.hacker.model.QuestionSaveModel;
import cn.com.lx1992.kaoshi.hacker.model.QuestionUpdateModel;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 试题Service
 * 事务传播属性均为REQUIRES_NEW 即开启一个新事务作为子事务
 *
 * @author luoxin
 * @version 2017-4-14
 */
@Service
public class QuestionService {
    private final Logger logger = LoggerFactory.getLogger(QuestionService.class);

    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private MetadataMapper metadataMapper;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void parseQuestion(Element element, int questionId) {
        logger.info("parse question {}", questionId);
        //判断相同ID的试题是否已经存在
        Integer id = questionMapper.queryId(questionId);
        if (id != null) {
            logger.warn("question already exist");
            return;
        }
        Elements item;
        //题干和选项
        item = element.getElementsByClass("choice");
        if (item.size() < 1) {
            logger.error("question stem & option size {} incorrect", item.size());
            return;
        }
        String stem = item.get(0).text();
        String option;
        //有部分试题 题干和选项连在一起
        if (item.size() == 2) {
            option = item.get(1).text();
        } else {
            logger.warn("question stem & option may be together");
            option = "";
        }
        //答案
        item = element.select("table").first().select("tr");
        if (item.size() < 3) {
            logger.error("question answer & analysis size {} incorrect", item.size());
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
        logger.info("save question {}", questionSave.getId());
        //更新试题总数
        MetadataQueryModel metadataQuery = metadataMapper.query(MetadataKeyConstant.QUESTION_COUNT);
        MetadataUpdateModel metadataUpdate = new MetadataUpdateModel();
        metadataUpdate.setKey(MetadataKeyConstant.QUESTION_COUNT);
        metadataUpdate.setValue(String.valueOf(Integer.parseInt(metadataQuery.getValue()) + 1));
        metadataMapper.update(metadataUpdate);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    boolean analysisQuestion(int questionId, String choice) {
        Integer id = questionMapper.queryId(questionId);
        if (id == null) {
            logger.error("question {} not exist", questionId);
            throw new RuntimeException("试题不存在");
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
        logger.info("update question {}", questionUpdate.getId());
        //返回true-回答正确 false-回答错误
        return answer.equals(choice);
    }
}
