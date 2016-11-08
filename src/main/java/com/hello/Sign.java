package com.hello;

import com.hello.demo.NeteaseMusic;

import javax.servlet.annotation.WebServlet;
import java.io.IOException;

/**
 * Created by Administrator on 2016/9/28.
 */
@WebServlet("/Sign")
public class Sign extends javax.servlet.http.HttpServlet {
    protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        NeteaseMusic netMusic = new NeteaseMusic();

        String result = netMusic.sign();


        response.getWriter().append(result);
    }

    protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        NeteaseMusic netMusic = new NeteaseMusic();
        request.setCharacterEncoding("UTF-8");
        String result = netMusic.sign();
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=utf-8");
        response.getWriter().append(result);
    }
}
