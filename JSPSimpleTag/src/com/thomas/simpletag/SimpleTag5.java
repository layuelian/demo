package com.thomas.simpletag;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 * @author lf
 *	根据show属性值判断标签体内容是否输出
 */
public class SimpleTag5 extends SimpleTagSupport{
	//获取标签属性值
	private boolean show;
	public void setShow(boolean show) {
		this.show = show;
	}
	@Override
	public void doTag() throws JspException, IOException {
		JspFragment jspBody = getJspBody();
		if(show)
			jspBody.invoke(null);
	}

}
