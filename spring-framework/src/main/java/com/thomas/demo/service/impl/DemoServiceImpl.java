package com.thomas.demo.service.impl;

import com.thomas.demo.service.IDemoService;
import com.thomas.mvcframework.annotation.MyService;
@MyService
public class DemoServiceImpl implements IDemoService {

	public String get(String name) {
		return "哈哈:"+name;
	}
}
