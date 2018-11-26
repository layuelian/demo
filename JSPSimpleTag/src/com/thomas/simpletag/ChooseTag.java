package com.thomas.simpletag;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class ChooseTag extends SimpleTagSupport{
	//定义一个标记，用于控制跳转去向
	private Boolean tag = true;

	public Boolean getTag() {
		return tag;
	}

	public void setTag(Boolean tag) {
		this.tag = tag;
	}
		
	@Override
	public void doTag() throws JspException, IOException {
		JspFragment jspBody = getJspBody();
		jspBody.invoke(null);//什么都不干，直接执行标签体内容
	}
	
}
