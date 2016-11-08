package com.hello.demo;

import java.io.IOException;
import java.net.URLDecoder;

import com.com.common.HttpHeader;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.*;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class Utils {

    public static CloseableHttpClient httpClient = null;
    public static HttpClientContext context = null;
    public static CookieStore cookieStore = null;
    public static RequestConfig requestConfig = null;

    static {
        init();
    }


    private static void init() {
        context = new HttpClientContext();
        cookieStore = new BasicCookieStore();
        // 配置超时时间（连接服务端超时1秒，请求数据返回超时2秒）
        requestConfig = RequestConfig.custom().setConnectTimeout(120000).setSocketTimeout(60000)
                .setConnectionRequestTimeout(60000).setCookieSpec(CookieSpecs.STANDARD).build();
        context.setCookieStore(cookieStore);
        // 设置默认跳转以及存储cookie
        httpClient = HttpClientBuilder.create().setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy())
                .setRedirectStrategy(new DefaultRedirectStrategy()).setDefaultRequestConfig(requestConfig)
                .setDefaultCookieStore(cookieStore).build();

        HttpClientParams.setCookiePolicy(httpClient.getParams(), CookiePolicy.BROWSER_COMPATIBILITY);
    }

    public static JSONObject httpSend(ApiUtils sendData,HttpHeader headers) {
        if(sendData.getHttpType() != 1){
            return httpGet(sendData.getAddress(),"",headers);
        }else{
            return httpPost(sendData.getAddress(),"",headers);
        }
    }

    public static JSONObject httpSend(ApiUtils sendData,String jsonParam,HttpHeader headers) {
        if(sendData.getHttpType() == 1){
            return httpPost(sendData.getAddress(),jsonParam,headers);
        }else{
            return httpGet(sendData.getAddress(), jsonParam, headers);
        }
    }

    /**
     * httpPost
     * @param url  ·��
     * @param jsonParam ����
     * @return
     */
    public static JSONObject httpPost(String url,String jsonParam){
        return httpPost(url, jsonParam, HttpHeader.custom().custom(), false);
    }
	
    /**
     * httpPost
     * @param url  ·��
     * @param jsonParam ����
     * @return
     */
    public static JSONObject httpPost(String url,String jsonParam,HttpHeader headers){
        return httpPost(url, jsonParam, headers, false);
    }

	/**
     * post����
     * @param url         url��ַ
     * @param jsonParam     ����
     * @param noNeedResponse    ����Ҫ���ؽ��
     * @return
     */
    public static JSONObject httpPost(String url,String jsonParam, HttpHeader headers, boolean noNeedResponse){
        //post���󷵻ؽ��
        //HttpClientBuilder.create().setDefaultCookieStore(cookieStore).build()
    	//HttpClient httpClient = HttpClients.createDefault();

        BasicClientCookie cookie = new BasicClientCookie("MUSIC_U", "01facb38a235cf7c615bcc917d3f6cee5ad7bfae5457715274f2b59befd5744387581dcafe41d8e93109871d12225f858b72a47e84f57be3305842396b5dfc01");
        cookie.setDomain("music.163.com");

        cookie.setPath("/");

     //   cookieStore.addCookie(cookie);


        JSONObject jsonResult = null;
        HttpPost method = new HttpPost(url);
        //method.addHeader("Cookie","MUSIC_U=01facb38a235cf7c615bcc917d3f6cee5ad7bfae5457715274f2b59befd5744387581dcafe41d8e93109871d12225f858b72a47e84f57be3305842396b5dfc01");


        try {
            if (null != jsonParam) {
                //���������������
                StringEntity entity = new StringEntity(jsonParam.toString(), "UTF-8");
                entity.setContentEncoding("UTF-8");
                entity.setContentType("application/json");
                method.setEntity(entity);
            }

            method.setHeaders(headers.build());

            HttpResponse result = httpClient.execute(method, context);


         //   System.out.println(result.getAllHeaders().toString());
            
            url = URLDecoder.decode(url, "UTF-8");
            /**�����ͳɹ������õ���Ӧ**/
            if (result.getStatusLine().getStatusCode() == 200) {
                String str = "";
                try {
                    /**��ȡ���������ع�����json�ַ�������**/
                    str = EntityUtils.toString(result.getEntity());
                    
                    if (noNeedResponse) {
                        return null;
                    }
                    /**��json�ַ���ת����json����**/        
                    jsonResult = new JSONObject(str);
                } catch (Exception e) {
                    //logger.error("post�����ύʧ��:" + url, e);
                }
            }
        } catch (IOException e) {
            //logger.error("post�����ύʧ��:" + url, e);
        }
        return jsonResult;
    }


    /** ����get����
    * @param url    ·��
    * @return
    */
   public static JSONObject httpGet(String url, String queryString){
    	return httpGet(url, queryString, HttpHeader.custom());
    }

    /**
     * ����get����
     * @param url    ·��
     * @return
     */
    public static JSONObject httpGet(String url, String queryString,HttpHeader headers){
        //get���󷵻ؽ��
        JSONObject jsonResult = null;
        String htppUrl = url + queryString;
        try {
        	HttpClient client = HttpClients.createDefault();

            ((AbstractHttpClient)client).getCookieStore().getCookies();

            //����get����
            HttpGet request = new HttpGet(htppUrl);
            request.setHeaders(headers.build());
           // request.

            HttpResponse response = client.execute(request);
            String setCookie = response.getFirstHeader("Set-Cookie")
                    .getValue();


            /**�����ͳɹ������õ���Ӧ**/
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                /**��ȡ���������ع�����json�ַ�������**/
                String strResult = EntityUtils.toString(response.getEntity());
                /**��json�ַ���ת����json����**/              
                jsonResult = new JSONObject(strResult);
                
                url = URLDecoder.decode(url, "UTF-8");
            } else {
                //logger.error("get�����ύʧ��:" + url);
            }
        } catch (IOException e) {
            //logger.error("get�����ύʧ��:" + url, e);
        }
        return jsonResult;
    }
	
}
