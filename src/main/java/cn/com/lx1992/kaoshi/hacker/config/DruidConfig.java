/*
 * Copyright © 2017 LuoXin. All Rights Reserved.
 */
package cn.com.lx1992.kaoshi.hacker.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.SQLException;

/**
 * Druid连接池&监控配置
 *
 * @author luoxin
 * @version 2017-4-10
 */
@Configuration
public class DruidConfig {
    private final Logger logger = LoggerFactory.getLogger(DruidConfig.class);

    @Bean(initMethod = "init", destroyMethod = "close")
    @ConfigurationProperties(prefix = "dataSource")
    public DruidDataSource configDruidDataSource() {
        logger.info("init druid data source");
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl("*****");
        dataSource.setUsername("*****");
        dataSource.setPassword("*****");
        dataSource.setInitialSize(1);
        dataSource.setMinIdle(1);
        dataSource.setMaxActive(5);
        try {
            dataSource.setFilters("wall, stat");
        } catch (SQLException e) {
            logger.error("config druid filter(s) failed", e);
        }
        return dataSource;
    }

    @Bean
    public ServletRegistrationBean configStatViewServlet() {
        logger.info("init druid view servlet");
        ServletRegistrationBean servlet = new ServletRegistrationBean();
        servlet.setServlet(new StatViewServlet());
        servlet.addUrlMappings("/druid/*");
        servlet.addInitParameter("loginUsername", "*****");
        servlet.addInitParameter("loginPassword", "*****");
        servlet.addInitParameter("resetEnable", "false");
        return servlet;
    }

    @Bean
    public FilterRegistrationBean configWebStatFilter() {
        logger.info("init druid web stat filter");
        FilterRegistrationBean filter = new FilterRegistrationBean();
        filter.setFilter(new WebStatFilter());
        filter.addUrlPatterns("/*");
        filter.addInitParameter("exclusions", "/druid/*");
        filter.addInitParameter("profileEnable", "true");
        return filter;
    }
}
