package com.thomas.demo.action;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.thomas.demo.service.IDemoService;
import com.thomas.mvcframework.annotation.MyAutowired;
import com.thomas.mvcframework.annotation.MyController;
import com.thomas.mvcframework.annotation.MyRequestMapping;
import com.thomas.mvcframework.annotation.MyRequestParam;

@MyController
@MyRequestMapping("/demo")
public class DemoAction {
	@MyAutowired
	private IDemoService demoService;

	@MyRequestMapping("/query.json")
	public void query(HttpServletRequest req, HttpServletResponse resp, @MyRequestParam("name") String name) {
		System.out.println(name);
		System.out.println(req.getCharacterEncoding());
		String reslut = demoService.get(name);
		System.out.println(reslut);
		try {
			resp.getWriter().write(reslut);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@MyRequestMapping("/add.json")
	public void add(HttpServletRequest req, HttpServletResponse resp, @MyRequestParam("a") Integer a,
			@MyRequestParam("b") Integer b) {
		try {
			resp.getWriter().write(a + "+" + b + "=" + (a + b));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@MyRequestMapping("/remove.json")
	public void remove(HttpServletRequest req, HttpServletResponse resp,@MyRequestParam("id") Integer id) {
		
	}
}
