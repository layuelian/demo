package com.thomas.simpletag;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 * @author lf
 *	重复执行输出标签体内容
 */
public class SimpleTag3 extends SimpleTagSupport{
	@Override
	public void doTag() throws JspException, IOException {
		int count=3;
		JspFragment jspFragment = getJspBody();
		for(int i=0;i<count;i++) {
			jspFragment.invoke(null);
		}
	}

}
