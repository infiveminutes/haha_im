package com.haha.im.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpClientUtil {
    private static final Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);

    private static CloseableHttpClient httpClient = null;

    static {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        // 总连接池数量
        connectionManager.setMaxTotal(1500);
        // 可为每个域名设置单独的连接池数量
        // connectionManager.setMaxPerRoute(new HttpRoute(new HttpHost("127.0.0.1")), 500);
        connectionManager.setDefaultMaxPerRoute(150);  // 这个必须设置，默认2，也就是单个路由最大并发数是2

        // setTcpNoDelay 是否立即发送数据，设置为true会关闭Socket缓冲，默认为false
        // setSoReuseAddress 是否可以在一个进程关闭Socket后，即使它还没有释放端口，其它进程还可以立即重用端口
        // setSoLinger 关闭Socket时，要么发送完所有数据，要么等待60s后，就关闭连接，此时socket.close()是阻塞的
        // setSoTimeout 接收数据的等待超时时间，单位ms
        // setSoKeepAlive 开启监视TCP连接是否有效
        SocketConfig socketConfig = SocketConfig.custom()
                .setTcpNoDelay(true)
                .setSoReuseAddress(true)
                .setSoLinger(60)
                .setSoTimeout(500)
                .setSoKeepAlive(true)
                .build();
        connectionManager.setDefaultSocketConfig(socketConfig);

        // setConnectTimeout表示设置建立连接的超时时间
        // setConnectionRequestTimeout表示从连接池中拿连接的等待超时时间
        // setSocketTimeout表示发出请求后等待对端应答的超时时间
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(10000)
                .setConnectionRequestTimeout(10000)
                .setSocketTimeout(10000)
                .build();

        // 重试处理器，StandardHttpRequestRetryHandler这个是官方提供的，看了下感觉比较挫，很多错误不能重试，可自己实现HttpRequestRetryHandler接口去做
//        HttpRequestRetryHandler retryHandler = new StandardHttpRequestRetryHandler();
        // 关闭重试策略
        HttpRequestRetryHandler requestRetryHandler = new DefaultHttpRequestRetryHandler(0, false);

        // 自定义请求存活策略
        ConnectionKeepAliveStrategy connectionKeepAliveStrategy = new ConnectionKeepAliveStrategy() {
            /**
             * 返回时间单位是毫秒
             */
            @Override
            public long getKeepAliveDuration(HttpResponse httpResponse, HttpContext httpContext) {
                return 60 * 1000;
            }
        };

        httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig)
                .setRetryHandler(requestRetryHandler)
                .setKeepAliveStrategy(connectionKeepAliveStrategy)
                .build();
    }

    /**
     * httpclient get
     *
     * @param uri       请求地址
     * @param getParams 请求参数
     * @return
     */
    public static JSONObject doHttpGet(String uri, Map<String, String> getParams) {
        HttpGet httpGet = null;
        CloseableHttpResponse response = null;
        try {
            URIBuilder uriBuilder = new URIBuilder(uri);
            if (null != getParams && !getParams.isEmpty()) {
                List<NameValuePair> list = new ArrayList<>();
                for (Map.Entry<String, String> param : getParams.entrySet()) {
                    list.add(new BasicNameValuePair(param.getKey(), param.getValue()));
                }
                uriBuilder.setParameters(list);
            }
            httpGet = new HttpGet(uriBuilder.build());
            response = httpClient.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            if (HttpStatus.SC_OK == statusCode) {
                HttpEntity entity = response.getEntity();
                if (null != entity) {
                    String resStr = EntityUtils.toString(entity, "utf-8");
                    return JSON.parseObject(resStr);
                }
            }
        } catch (Exception e) {
            logger.error("CloseableHttpClient-get-请求异常", e);
        } finally {
            try {
                if (null != response)
                    response.close();
            } catch (IOException e) {
                logger.error("CloseableHttpClient-post-请求异常,释放连接异常", e);
            }
            try {
                if (null != httpGet)
                    httpGet.releaseConnection();
            } catch (Exception e) {
                logger.error("CloseableHttpClient-post-请求异常,释放连接异常", e);
            }
        }
        return new JSONObject();
    }


    public static JSONObject doHttpPost(String uri, Map<String, String> getParams) {
        HttpPost httpPost = null;
        CloseableHttpResponse response = null;
        try {
            httpPost = new HttpPost(uri);
            if (null != getParams && !getParams.isEmpty()) {
                List<NameValuePair> list = new ArrayList<>();
                for (Map.Entry<String, String> param : getParams.entrySet()) {
                    list.add(new BasicNameValuePair(param.getKey(), param.getValue()));
                }
                HttpEntity httpEntity = new UrlEncodedFormEntity(list, "utf-8");
                httpPost.setEntity(httpEntity);
            }
            response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (HttpStatus.SC_OK == statusCode) {
                HttpEntity entity = response.getEntity();
                if (null != entity) {
                    String resStr = EntityUtils.toString(entity, "utf-8");
                    return JSON.parseObject(resStr);
                }
            }
        } catch (Exception e) {
            logger.error("CloseableHttpClient-post-请求异常", e);
        } finally {
            try {
                if (null != response)
                    response.close();
            } catch (IOException e) {
                logger.error("CloseableHttpClient-post-请求异常,释放连接异常", e);
            }
            try {
                if (null != httpPost)
                    httpPost.releaseConnection();
            } catch (Exception e) {
                logger.error("CloseableHttpClient-post-请求异常,释放连接异常", e);
            }
        }
        return new JSONObject();
    }

    /**
     * httpclient post
     *
     * @param uri       请求地址
     * @param reqParams json串
     * @return
     */
    public static JSONObject doHttpPost(String uri, String reqParams) {
        HttpPost httpPost = null;
        CloseableHttpResponse response = null;
        try {
            httpPost = new HttpPost(uri);
            httpPost.addHeader("Content-Type", "application/json;charset=utf-8");
            if (StringUtils.isNotBlank(reqParams)) {
                StringEntity postingString = new StringEntity(reqParams, "utf-8");
                httpPost.setEntity(postingString);
            }
            response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (HttpStatus.SC_OK == statusCode) {
                HttpEntity entity = response.getEntity();
                if (null != entity) {
                    String resStr = EntityUtils.toString(entity, "utf-8");
                    return JSON.parseObject(resStr);
                }
            }
        } catch (Exception e) {
            logger.error("CloseableHttpClient-post-请求异常", e);
        } finally {
            try {
                if (null != response)
                    response.close();
            } catch (IOException e) {
                logger.error("CloseableHttpClient-post-请求异常,释放连接异常", e);
            }
            try {
                if (null != httpPost)
                    httpPost.releaseConnection();
            } catch (Exception e) {
                logger.error("CloseableHttpClient-post-请求异常,释放连接异常", e);
            }
        }
        return new JSONObject();

    }

    public static void main(String[] args) {
        System.out.println(doHttpPost("http://localhost:8088/msg_id/next_id", "{\"userId\":1}"));
    }

}
