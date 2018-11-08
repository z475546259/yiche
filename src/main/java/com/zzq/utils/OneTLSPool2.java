package com.zzq.utils;

/**
 * @author zhangzhiqiang
 * @date 2018-07-26 17:17
 * &Desc 单向握手认证
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.apache.http.util.TextUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

public class OneTLSPool2 {
    public  CloseableHttpClient httpclient = null;
    public  HttpHost httpHost= null;
//    public static final String KEY_STORE_TRUST_PATH = "D://https//ca//cl.jks"; // truststore的路径
    public static final String KEY_STORE_TRUST_PATH = "C:\\keystore\\keysotre.jks"; // truststore的路径
    public static final String KEY_STORE_TYPE_JKS = "jks"; // truststore的类型
//    private static final String KEY_STORE_TRUST_PASSWORD = "123456"; // truststore的密码
    private static final String KEY_STORE_TRUST_PASSWORD = "changeit"; // truststore的密码
//    public  List<String> cookies = new ArrayList<>();
    public  Map<String,String> cookies = new HashMap<>();
    // 获得池化得HttpClient

    public OneTLSPool2() {
        SSLContext sslcontext = null;
        try {
            // 设置truststore
            KeyStore trustStore = KeyStore.getInstance(KEY_STORE_TYPE_JKS);
            InputStream tsIn = new FileInputStream(new File(KEY_STORE_TRUST_PATH));
            try {
                trustStore.load(tsIn, KEY_STORE_TRUST_PASSWORD.toCharArray());
            } finally {
                try {
                    tsIn.close();
                } catch (Exception ignore) {
                }
            }
            sslcontext = SSLContexts.custom().loadTrustMaterial(trustStore, new TrustSelfSignedStrategy()).build();
            //解决jdk7的ssl的自签名会有问题的bug，如果不是jdk7，则下面的代码可以没有
            //bug地址：http://bugs.java.com/bugdatabase/view_bug.do?bug_id=7018897
            X509TrustManager xtm = new X509TrustManager(){   //创建TrustManager
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
                public X509Certificate[] getAcceptedIssuers() {
                    return null;   //return new java.security.cert.X509Certificate[0];
                }
            };
            sslcontext.init(null, new TrustManager[]{xtm}, null);
            //解决bug结束
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 客户端支持TLSV1，TLSV2,TLSV3这三个版本
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext,
                new String[] { "TLSv1", "TLSv2", "TLSv3" }, null,
                SSLConnectionSocketFactory.getDefaultHostnameVerifier());// 客户端验证服务器身份的策略

        // Create a registry of custom connection socket factories for supported
        // protocol schemes.
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory> create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", new SSLConnectionSocketFactory(sslcontext)).build();
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        // Configure total max or per route limits for persistent connections
        // that can be kept in the pool or leased by the connection manager.
        connManager.setMaxTotal(100);
        connManager.setDefaultMaxPerRoute(10);
        // 个性化设置某个url的连接
        connManager.setMaxPerRoute(new HttpRoute(new HttpHost("127.0.0.1", 8888)), 20);
        httpclient = HttpClients.custom().setConnectionManager(connManager).build();
        httpHost = new HttpHost("127.0.0.1", 8888);
    }

//    static {
//        SSLContext sslcontext = null;
//        try {
//            // 设置truststore
//            KeyStore trustStore = KeyStore.getInstance(KEY_STORE_TYPE_JKS);
//            InputStream tsIn = new FileInputStream(new File(KEY_STORE_TRUST_PATH));
//            try {
//                trustStore.load(tsIn, KEY_STORE_TRUST_PASSWORD.toCharArray());
//            } finally {
//                try {
//                    tsIn.close();
//                } catch (Exception ignore) {
//                }
//            }
//            sslcontext = SSLContexts.custom().loadTrustMaterial(trustStore, new TrustSelfSignedStrategy()).build();
//            //解决jdk7的ssl的自签名会有问题的bug，如果不是jdk7，则下面的代码可以没有
//            //bug地址：http://bugs.java.com/bugdatabase/view_bug.do?bug_id=7018897
//            X509TrustManager xtm = new X509TrustManager(){   //创建TrustManager
//                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
//                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
//                public X509Certificate[] getAcceptedIssuers() {
//                    return null;   //return new java.security.cert.X509Certificate[0];
//                }
//            };
//            sslcontext.init(null, new TrustManager[]{xtm}, null);
//            //解决bug结束
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        // 客户端支持TLSV1，TLSV2,TLSV3这三个版本
//        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext,
//                new String[] { "TLSv1", "TLSv2", "TLSv3" }, null,
//                SSLConnectionSocketFactory.getDefaultHostnameVerifier());// 客户端验证服务器身份的策略
//
//        // Create a registry of custom connection socket factories for supported
//        // protocol schemes.
//        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory> create()
//                .register("http", PlainConnectionSocketFactory.INSTANCE)
//                .register("https", new SSLConnectionSocketFactory(sslcontext)).build();
//        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
//        // Configure total max or per route limits for persistent connections
//        // that can be kept in the pool or leased by the connection manager.
//        connManager.setMaxTotal(100);
//        connManager.setDefaultMaxPerRoute(10);
//        // 个性化设置某个url的连接
//        connManager.setMaxPerRoute(new HttpRoute(new HttpHost("www.y.com", 80)), 20);
//        httpclient = HttpClients.custom().setConnectionManager(connManager).build();
//
//    }

    /**
     * 单向验证且服务端的证书可信
     *
     * @throws IOException
     * @throws ClientProtocolException
     */
    public  void oneWayAuthorizationAcceptedGet() throws ClientProtocolException, IOException {
        // Execution context can be customized locally.
        HttpClientContext context = HttpClientContext.create();
//        HttpGet httpget = new HttpGet("https://www.yunzhu.com:8443");
        HttpGet httpget = new HttpGet("https://account.36kr.com/api/v1/mobile-sign-in");
        // 设置请求的配置
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(5000).setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000).build();
        httpget.setConfig(requestConfig);

        System.out.println("executing request " + httpget.getURI());
        CloseableHttpResponse response = httpclient.execute(httpget, context);
        try {
            System.out.println("----------------------------------------");
            System.out.println(response.getStatusLine());
            System.out.println(EntityUtils.toString(response.getEntity()));
            System.out.println("----------------------------------------");

            // Last executed request
            context.getRequest();
            // Execution route
            context.getHttpRoute();
            // Target auth state
            context.getTargetAuthState();
            // Proxy auth state
            context.getTargetAuthState();
            // Cookie origin
            context.getCookieOrigin();
            // Cookie spec used
            context.getCookieSpec();
            // User security token
            context.getUserToken();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 单向验证且服务端的证书可信 post form
     *
     * @throws IOException
     * @throws ClientProtocolException
     */
    public  String oneWayAuthorizationAcceptedPost(Map<String,String> map,String url) throws ClientProtocolException, IOException {
        String result = "";
        HttpClientContext context = HttpClientContext.create();
//        HttpGet httpget = new HttpGet("https://www.yunzhu.com:8443");
//        HttpPost httppost = new HttpPost("https://account.36kr.com/api/v1/mobile-sign-in");
        HttpPost httppost = new HttpPost(url);
        // 设置请求的配置
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(5000).setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000)
//                .setProxy(new HttpHost("127.0.0.1", 8888))
                .setCookieSpec(CookieSpecs.STANDARD).build();
        httppost.setConfig(requestConfig);
//        httppost.setHeader("User-Agent","36kr-Android com.android36kr.app/7.5.1 (Android:6.0.1 Mobile:Redmi 4A) krchannel/xiaomi krversion7.5.1");

        httppost.setHeader("User-Agent","SEGExcellentHome/4.6.0 (iPhone; iOS 11.4.1; Scale/3.00)");

        String strCookies = "";
//        for (String s:cookies) {
        for (String s:cookies.keySet()) {
            strCookies = cookies.get(s)+s;
        }
        httppost.setHeader("Cookie",strCookies);

        //设置参数
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        Iterator iterator = map.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<String,String> elem = (Map.Entry<String, String>) iterator.next();
            list.add(new BasicNameValuePair(elem.getKey(),elem.getValue()));
        }
        if(list.size() > 0){
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list,"utf-8");
            httppost.setEntity(entity);
        }




        System.out.println("executing request " + httppost.getURI());
        CloseableHttpResponse response=null;
        if(httpHost!=null){
             response = httpclient.execute(httpHost,httppost, context);
        }else{
             response = httpclient.execute(httppost, context);
        }
        Header[] headers = response.getAllHeaders();
        for (Header h : headers) {
            if(h.getName().equalsIgnoreCase("set-cookie")){
                String cutCookie =  h.getValue().substring(0,h.getValue().indexOf(";")+1);
                cookies.put(h.getName(),cutCookie);
//                cookies.add(cutCookie);
            }
        }

        try {
            System.out.println("----------------------------------------");
            System.out.println(response.getStatusLine());
            result = EntityUtils.toString(response.getEntity());
            System.out.println(result);
            System.out.println("----------------------------------------");
            // Last executed request
            context.getRequest();
            // Execution route
            context.getHttpRoute();
            // Target auth state
            context.getTargetAuthState();
            // Proxy auth state
            context.getTargetAuthState();
            // Cookie origin
            context.getCookieOrigin();
            // Cookie spec used
            context.getCookieSpec();
            // User security token
            context.getUserToken();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 单向验证且服务端的证书可信 post json
     *
     * @throws IOException
     * @throws ClientProtocolException
     */
    public  String oneWayAuthorizationAcceptedPostJson(Map<String,String> map,String url) throws ClientProtocolException, IOException {
        HttpClientContext context = HttpClientContext.create();
        HttpPost httppost = new HttpPost(url);
        // 设置请求的配置
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(5000).setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000)
//                .setProxy(new HttpHost("127.0.0.1", 8888))
                .setCookieSpec(CookieSpecs.STANDARD).build();
        httppost.setConfig(requestConfig);
//        httppost.setHeader("User-Agent","36kr-Android com.android36kr.app/7.5.1 (Android:6.0.1 Mobile:Redmi 4A) krchannel/xiaomi krversion7.5.1");
        httppost.setHeader("User-Agent","SEGExcellentHome/4.6.0 (iPhone; iOS 11.4.1; Scale/3.00)");

        httppost.addHeader(HTTP.CONTENT_TYPE,"application/json");
        httppost.addHeader(HTTP.CONTENT_TYPE,  "text/json");

        String strCookies = "";
        for (String s:cookies.keySet()) {
            strCookies = cookies.get(s)+s;
        }
        httppost.setHeader("Cookie",strCookies);

        //设置参数
        JSONObject obj= JSONObject.parseObject(JSON.toJSONString(map));
        StringEntity se = new StringEntity(obj.toString());
        se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        httppost.setEntity(se);
        CloseableHttpResponse  response= httpclient.execute(httppost);
        BufferedReader buffer=null;
        buffer=new BufferedReader(new InputStreamReader(response.getEntity().getContent(),"utf-8"));
        String result =buffer.readLine();
        System.out.println("post JSON 返回结果=="+result);
        return result;
    }


    /**
     * 单向验证且服务端的证书可信 get
     *
     * @throws IOException
     * @throws ClientProtocolException
     */
    public  String oneWayAuthorizationAcceptedGet(Map<String,String> map,String url) throws ClientProtocolException, IOException {
        //将map参数拼装到url上
        Object localObject2 = "?";
        Object localObject1 = localObject2;
        if (map != null) {
            localObject1 = localObject2;
            if (!map.isEmpty()) {
                localObject2 = new StringBuilder();
                Iterator localIterator = map.keySet().iterator();
                while (localIterator.hasNext()) {
                    localObject1 = (String) localIterator.next();
                    String str = (String) map.get(localObject1);
                    if (!TextUtils.isEmpty(str)) {
                        ((StringBuilder) localObject2).append((String) localObject1 + "=");
                        ((StringBuilder) localObject2).append(ToolBox.URLEncode(str));
                        if ((!TextUtils.isEmpty(str)) && (localIterator.hasNext())) {
                            ((StringBuilder) localObject2).append("&");
                        }
                    }
                }
                localObject2 = "?" + ((StringBuilder) localObject2).toString();
                localObject1 = localObject2;
                if (((String) localObject2).endsWith("&")) {
                    localObject1 = ((String) localObject2).substring(0, ((String) localObject2).length() - 1);
                }
                System.out.println("加密前的字符串:" + localObject1);
                url = url + localObject1;
            }
        }



        String result = "";
        HttpClientContext context = HttpClientContext.create();
        HttpGet httpGet = new HttpGet(url);
        // 设置请求的配置
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(5000).setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000)
//                .setProxy(new HttpHost("127.0.0.1", 8888))
                .setCookieSpec(CookieSpecs.STANDARD).build();
        httpGet.setConfig(requestConfig);
        httpGet.setHeader("User-Agent","com.bitauto.carquote 9.0.1 rv:9.0.1.0 (iPhone; iOS 12.0; zh-Hans_HK)");
//        httpGet.setHeader("Accept","*/*");
        httpGet.setHeader("Accept-Encoding","gzip");
//        httpGet.setHeader("Accept-Language","zh-Hans-HK;q=1");
//        httpGet.setHeader("Accept-Language","zh-Hans-HK;q=1");


//        httpGet.setHeader("communityId","1");
//        httpGet.setHeader("deviceId","2FBC9078-4DE7-4BEB-A202-B21162CC904E");
//        httpGet.setHeader("phoneModel","iPhone7(GSM)");
//        httpGet.setHeader("platform","SEGI");
//        httpGet.setHeader("source","2");
//        httpGet.setHeader("SYSCODE","10");
//        httpGet.setHeader("systemVersionCode","11.4.1");
//        httpGet.setHeader("version","4.6.0");
//        httpGet.setHeader("versionCode","50");

        httpGet.setHeader("Cookie","UserGuid=c95e37e0-aaa8-4e51-b093-a5988d0ba2ca; locatecity=500100");
        httpGet.setHeader("If-None-Match","B1Alf/g0uVgalkcGWs2F7w==");
        httpGet.setHeader("63a415bd64be877f06efcd695f43adfb","dvid");
        httpGet.setHeader("9.0.1","av");
        httpGet.setHeader("appstore","cha");
        httpGet.setHeader("dvtype","ios");
//        String strCookies = "";
//        for (String s:cookies.keySet()) {
//            strCookies = cookies.get(s)+s;
//        }
//        httpGet.setHeader("Cookie",strCookies);

        System.out.println("executing request " + httpGet.getURI());
        CloseableHttpResponse response=null;
        if(httpHost!=null){
             response = httpclient.execute(httpHost,httpGet, context);
        }else{
             response = httpclient.execute(httpGet, context);
        }
        Header[] headers = response.getAllHeaders();
        for (Header h : headers) {
            if(h.getName().equalsIgnoreCase("set-cookie")){
                String cutCookie =  h.getValue().substring(0,h.getValue().indexOf(";")+1);
                cookies.put(h.getName(),cutCookie);
            }
        }

        try {
            System.out.println("----------------------------------------");
            System.out.println(response.getStatusLine());
            result = EntityUtils.toString(response.getEntity());
            System.out.println(result);
            System.out.println("----------------------------------------");
            // Last executed request
            context.getRequest();
            // Execution route
            context.getHttpRoute();
            // Target auth state
            context.getTargetAuthState();
            // Proxy auth state
            context.getTargetAuthState();
            // Cookie origin
            context.getCookieOrigin();
            // Cookie spec used
            context.getCookieSpec();
            // User security token
            context.getUserToken();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    public static void main(String[] a) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException,
            CertificateException, IOException {
        OneTLSPool2 oneTLSPool2 = new OneTLSPool2();
        Map<String,String> map = new HashMap<>();
        map.put("password","475546259");
        map.put("tel","15923584508");
        String url = "https://www.uhomecp.com/uhomecp-sso/v3/userApp/login";
        oneTLSPool2.oneWayAuthorizationAcceptedPost(map,url);
        map.clear();
        map.put("code","login");
        url = "https://www.uhomecp.com/integral-api/behavior/analyseBehavior";
        oneTLSPool2.oneWayAuthorizationAcceptedPost(map,url);
        map.clear();
        map.put("code","login");
        url = "https://www.uhomecp.com/act-api/actvityBehavior/sign";
        oneTLSPool2.oneWayAuthorizationAcceptedPost(map,url);
    }
}