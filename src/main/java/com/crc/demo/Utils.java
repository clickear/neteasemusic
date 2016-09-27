package com.crc.demo;

import java.io.IOException;
import java.net.URLDecoder;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class Utils {

    /**
     * httpPost
     * @param url  ·��
     * @param jsonParam ����
     * @return
     */
    public static JSONObject httpPost(String url,String jsonParam){
        return httpPost(url, jsonParam, new String[][]{}, false);
    }
	
    /**
     * httpPost
     * @param url  ·��
     * @param jsonParam ����
     * @return
     */
    public static JSONObject httpPost(String url,String jsonParam,String[][] headers){
        return httpPost(url, jsonParam, headers, false);
    }    
	
	/**
     * post����
     * @param url         url��ַ
     * @param jsonParam     ����
     * @param noNeedResponse    ����Ҫ���ؽ��
     * @return
     */
    public static JSONObject httpPost(String url,String jsonParam, String[][] headers, boolean noNeedResponse){
        //post���󷵻ؽ��
    	HttpClient httpClient = HttpClients.createDefault();
        JSONObject jsonResult = null;
        HttpPost method = new HttpPost(url);
        try {
            if (null != jsonParam) {
                //���������������
                StringEntity entity = new StringEntity(jsonParam.toString(), "utf-8");
                entity.setContentEncoding("UTF-8");
                entity.setContentType("application/json");
                method.setEntity(entity);
            }
            
            for(String[] head :headers){
                method.addHeader(head[0],head[1]);
            }
            
            HttpResponse result = httpClient.execute(method);
            
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
   public static JSONObject httpGet(String url){
    	return httpGet(url,new String[][]{});
    }
    /**
     * ����get����
     * @param url    ·��
     * @return
     */
    public static JSONObject httpGet(String url,String[][] headers){
        //get���󷵻ؽ��
        JSONObject jsonResult = null;
        try {
        	HttpClient client = HttpClients.createDefault();
            //����get����
            HttpGet request = new HttpGet(url);
            
            for(String[] head :headers){
            	request.addHeader(head[0],head[1]);
            }
            
            HttpResponse response = client.execute(request);

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
