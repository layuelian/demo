package com.thomas.simpletag;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 * @author lf
 *	控制jsp页面某一部分内容是否执行
 */
public class SimpleTag1 extends SimpleTagSupport{
	@Override
	public void doTag() throws JspException, IOException {
		JspFragment jspFragment = getJspBody();
		jspFragment.invoke(null);//参数为null，表示拿到JspWriter.getOut。
		//即内容会被输出到浏览器,不调用此方法，内容就不会输出到浏览器
	}
}
