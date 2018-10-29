package com.thomas.servlet;

import com.thomas.http.MyRequest;
import com.thomas.http.MyResponse;
import com.thomas.http.MyServlet;

public class FirstServlet extends MyServlet {
    @Override
    public void doGet(MyRequest request, MyResponse response) throws Exception {
        this.doPOST(request,response);
    }

    @Override
    public void doPOST(MyRequest request, MyResponse response) throws Exception {
            //处理自己所有的逻辑
        response.write("Hello Tomcat Demo"+",method="+request.getMethod()+",url="+request.getUrl());
    }
}
