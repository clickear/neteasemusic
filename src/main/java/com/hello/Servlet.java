package com.hello;

import com.crc.demo.NeteaseMusic;

import javax.servlet.annotation.WebServlet;
import java.io.IOException;

/**
 * Created by Administrator on 2016/9/28.
 */
@WebServlet("/Helloword")
public class Servlet extends javax.servlet.http.HttpServlet {
    protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        NeteaseMusic netMusic = new NeteaseMusic();

        String result = netMusic.login("chen.ruo.chen@163.com", "858833crc");


        response.getWriter().append(result);
    }

    protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        NeteaseMusic netMusic = new NeteaseMusic();

        String result = netMusic.login("chen.ruo.chen@163.com", "858833crc");


        response.getWriter().append(result);
    }
}
