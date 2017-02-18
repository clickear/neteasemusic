package com.com.crc.test;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

/**
 * Created by Administrator on 2016/10/1.
 */
public class TestApi {
    public TestApi() throws UnirestException {
        HttpResponse<JsonNode> jsonResponse = Unirest.post("http://httpbin.org/post")
                .header("accept", "application/json")
                .queryString("apiKey", "123")
                .field("parameter", "value")
                .field("foo", "bar")
                .asJson();
        System.out.println(jsonResponse.getBody().getObject());
    }

    public static void main(String[] args) throws UnirestException {
        TestApi a = new TestApi();

    }
}
