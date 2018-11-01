package com.thomas.filter;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class CharFilter implements Filter {

	public void init(FilterConfig filterConfig) throws ServletException {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
//		final HttpServletRequest req=(HttpServletRequest)request;
//		HttpServletResponse resp=(HttpServletResponse) response;
		// 处理post提交的中文乱码问题
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=utf-8");
		// 处理get提交的中文乱码问题
		//ClassLoader loader:类加载器
        //Class<?>[] interfaces：需要代理对象接口
        //InvocationHandler :动态代理处理器
//		HttpServletRequest proxy=(HttpServletRequest) Proxy.newProxyInstance(req.getClass().getClassLoader(), new Class[] {HttpServletRequest.class}, new InvocationHandler() {
//			
//			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//				// 获取方法的名字
//				String methodName = method.getName();
//				if("getParameter".equals(methodName)) {
//					String value = req.getParameter(args[0].toString());
//					// 只需要处理Get的形式
//					String methodSubmit = req.getMethod();//GET
//					if("GET".equals(methodSubmit)) {
//						value=new String(value.getBytes("ISO-8859-1"),"utf-8");	
//					}
//					return value;
//				}else if("getParameterMap".equals(methodName)) {
//					Map params = req.getParameterMap();
//					Set<Entry<String, String[]>> entrySet = params.entrySet();
//					for (Entry<String, String[]> entry : entrySet) {
//						String[] values=new String[] {new String(entry.getValue()[0].getBytes("ISO-8859-1"),"utf-8")};
//						//entry.setValue(String values=new String(entry.getValue().getBytes("ISO-8859-1"),"utf-8")});
//						entry.setValue(values);
//					}
//					return params;
//				}else {
//					return method.invoke(req, args);
//				}
//			}
//		});
		//chain.doFilter(proxy, response);
		chain.doFilter(request, response);
	}

	public void destroy() {
	}


}
