package com.thomas.simpletag;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 * @author lf
 *	获取标签体内容，并修改输出到jsp中
 */
public class SimpleTag4 extends SimpleTagSupport{
	@Override
	public void doTag() throws JspException, IOException {
		StringWriter sw=new StringWriter();
		JspFragment jspBody = getJspBody();
		jspBody.invoke(sw);
		String content=sw.toString();
		getJspContext().getOut().write(content+"你好");
	}
}
