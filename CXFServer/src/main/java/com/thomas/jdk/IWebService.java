package com.thomas.jdk;

import javax.jws.WebMethod;
import javax.jws.WebService;
@WebService
public interface IWebService {
    @WebMethod
    public String sayHello(String name);
    @WebMethod
    public String save(String username,String password);
}
