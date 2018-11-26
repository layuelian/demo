package com.thomas.simpletag;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class OtherwiseTag extends SimpleTagSupport{
	@Override
	public void doTag() throws JspException, IOException {
		ChooseTag chooseTag = (ChooseTag) getParent();
		Boolean tag = chooseTag.getTag();
		//如果是true这说明没有进when
		if(tag) {
			JspFragment jspBody = getJspBody();
			jspBody.invoke(null);
		}
	}
}
