/*
 * Copyright © 2017 LuoXin. All Rights Reserved.
 */
package cn.com.lx1992.kaoshi.hacker.util;

import cn.com.lx1992.kaoshi.hacker.constant.HttpHeaderConstant;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.util.CollectionUtils;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * HTTP工具类
 *
 * @author luoxin
 * @version 2017-4-10
 */
public class HttpUtils {
    private static final OkHttpClient client;

    static {
        client = new OkHttpClient().newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .followRedirects(false)
                //本地调试用Charles代理
                .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 8888)))
                .build();
    }

    private static Request request(String url, Map<String, String> cookies) {
        Request.Builder builder = new Request.Builder()
                .url(url)
                //随机使用iOS或Android的UA
                .addHeader("User-Agent", Math.random() > 0.5 ? HttpHeaderConstant.UA_IOS : HttpHeaderConstant
                        .UA_ANDROID)
                .addHeader("Accept", HttpHeaderConstant.ACCEPT)
                .addHeader("Accept-Language", HttpHeaderConstant.ACCEPT_LANGUAGE)
                .get();
        if (!CollectionUtils.isEmpty(cookies)) {
            String cookie = cookies.entrySet().stream()
                    .map((entry) -> entry.getKey() + "=" + entry.getValue())
                    .collect(Collectors.joining("; "));
            builder.addHeader("Cookie", cookie);
        }
        return builder.build();
    }

    /**
     * 执行GET请求
     *
     * @param url     URL
     * @param cookies Cookies
     * @return HTTP响应
     */
    public static Response execute(String url, Map<String, String> cookies) throws Exception {
        Request request = request(url, cookies);
        return client.newCall(request).execute();
    }

    /**
     * 获取HTTP状态码
     *
     * @param url     URL
     * @param cookies Cookies
     * @return HTTP状态码
     */
    public static String body(String url, Map<String, String> cookies) throws Exception {
        return execute(url, cookies).body().string();
    }
}
