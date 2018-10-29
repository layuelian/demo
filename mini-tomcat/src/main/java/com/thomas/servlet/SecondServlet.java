package com.thomas.servlet;

import com.thomas.http.MyRequest;
import com.thomas.http.MyResponse;
import com.thomas.http.MyServlet;

public class SecondServlet extends MyServlet {
    @Override
    public void doGet(MyRequest request, MyResponse response) throws Exception {
        this.doPOST(request,response);
    }

    @Override
    public void doPOST(MyRequest request, MyResponse response) throws Exception {
        //在这里处理自己的所有逻辑
        response.write("Hello Second Servlet");
    }
}
