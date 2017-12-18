一个spring-boot项目，主要用到的轮子还有okhttp和jsoup

主要用来抓取“厦门大学2017年信息安全知识竞赛”的一些数据

包括试题(question)、试卷(paper)(和试题1:n)、成绩(score)(和试卷1:1)、排行(ranking)等

抓取频次从30分钟到12小时不等，抓取的数据简单处理后即存入mysql，然后提供了几个restful的api

地址|说明
---|---
/api/v1/test|系统存活检查
/api/v1/paper/{id}|查询试卷
/api/v1/question/{keyword}|关键词查询试题
/api/v1/question/analyze/{id}|查询试题分析结果
/api/v1/score/{column}/{dir}/{page}|查询成绩，column-排序字段、dir-排序方式、page-页码
/api/v1/score/{period}|查询一段时间内的成绩汇总
/api/v1/score/analyze/{period}|查询一段时间内的成绩分析
/api/v1/ranking/compare/{name}|对比某个选手在排行榜上的变化趋势
/api/v1/ranking, /api/v1/ranking/{limit}|查询排行榜，limit-topN

当时抓取的域名是 http://www.aohua168.com/kaoshi

目前(2017/12/18)看来 http://turing.xmu.edu.cn/kaoshi 部署的可能也是同一个项目
