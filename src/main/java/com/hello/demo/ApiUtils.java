package com.hello.demo;

public enum ApiUtils{
    LOGIN("https://music.163.com/weapi/login/",1),
    CELLPHONELOGIN("https://music.163.com/weapi/login/",1),
    SIGN("http://music.163.com/weapi/point/dailyTask",1);

    private  ApiUtils(String addres,Integer httpType){
        this.address =addres;
        this.httpType = httpType;
    }

    private  String address;
    private Integer httpType;  //1 post 0get

    public Integer getHttpType() {
        return httpType;
    }

    public String getAddress() {
        return address;
    }

}

