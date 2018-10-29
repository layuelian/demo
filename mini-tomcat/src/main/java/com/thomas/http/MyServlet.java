package com.thomas.http;

public abstract class MyServlet {
    public void service(MyRequest request,MyResponse response) throws Exception {
        //如果客户端发送的是GET请求，就调用doGet
        //POST请求，调用doPost
        if("GET".equalsIgnoreCase(request.getMethod())){
            doGet(request,response);
        }else {
            doPOST(request,response);
        }
    }
    public abstract void doGet(MyRequest request,MyResponse response) throws Exception;

    public abstract void doPOST(MyRequest request,MyResponse response) throws Exception;
}
