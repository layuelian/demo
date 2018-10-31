package com.thomas.jdk;

import javax.jws.WebService;

@WebService
public class WebServiceImpl implements IWebService{

    public String sayHello(String name) {
        System.out.println("WebService sayHello has be invoked");
        return "你好"+name;
    }

    @Override
    public String save(String username, String password) {
        System.out.println("WebService save has be invoked");
        return "save Success";
    }
}
