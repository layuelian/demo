package com.thomas.simpletag;

import java.io.IOException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 * @author lf
 *	自定义for循环标签
 */
public class SimpleTagFor extends SimpleTagSupport {
	private List items;

	public void setItems(List items) {
		this.items = items;
	}

	private String var;

	public void setVar(String var) {
		this.var = var;
	}

	@Override
	public void doTag() throws JspException, IOException {
		JspFragment jspBody = getJspBody();
		if(items!=null) {
			for (Object item : items) {
				getJspContext().setAttribute(var, item);
				jspBody.invoke(null);
			}
		}
	}

}
