package com.thomas.simpletag;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.SkipPageException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 * @author lf
 *	控制整个jsp页面是否执行
 */
public class SimpleTag2 extends SimpleTagSupport {
	@Override
	public void doTag() throws JspException, IOException {
		//有异常，标签体后面的内容都不输出
		//throw new SkipPageException();
	}
}
