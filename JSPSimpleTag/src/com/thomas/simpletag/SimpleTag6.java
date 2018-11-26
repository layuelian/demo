package com.thomas.simpletag;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 * @author lf
 *	复合数据类型jsp引擎无法自动转换，在传入时应传入相应类型
 */
public class SimpleTag6 extends SimpleTagSupport{
	private Date date;
	public void setDate(Date date) {
		this.date = date;
	}
	@Override
	public void doTag() throws JspException, IOException {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		String format = sdf.format(date);
		getJspContext().getOut().write(format);
	}
}
