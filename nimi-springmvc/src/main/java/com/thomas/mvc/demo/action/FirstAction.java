package com.thomas.mvc.demo.action;

import com.thomas.mvc.demo.service.INamedService;
import com.thomas.mvc.demo.service.IService;
import com.thomas.mvc.framework.annotation.MyAutowired;
import com.thomas.mvc.framework.annotation.MyController;
import com.thomas.mvc.framework.annotation.MyRequestMapping;

@MyController
@MyRequestMapping("/web")
public class FirstAction {
    @MyAutowired
    private IService service;
    @MyAutowired("myName")
    private INamedService namedService;
}
