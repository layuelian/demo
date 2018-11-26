package com.thomas.simpletag;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class WhenTag extends SimpleTagSupport{
	private Boolean test;

	public Boolean getTest() {
		return test;
	}

	public void setTest(Boolean test) {
		this.test = test;
	}
	
	@Override
	public void doTag() throws JspException, IOException {
		//获取父标签，并且获取它的tag的值
		ChooseTag chooseTag = (ChooseTag) getParent();
		Boolean tag = chooseTag.getTag();
		//判断tag的值，如果test也成立，就执行
		if(tag&&test) {
			//如果这个执行了，后面都不执行
			chooseTag.setTag(false);
			JspFragment jspBody = getJspBody();
			jspBody.invoke(null);
		}
	}
}
