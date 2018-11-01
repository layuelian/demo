package com.thomas.mvcframework.servlet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.thomas.mvcframework.annotation.MyAutowired;
import com.thomas.mvcframework.annotation.MyController;
import com.thomas.mvcframework.annotation.MyRequestMapping;
import com.thomas.mvcframework.annotation.MyService;

@SuppressWarnings("serial")
public class MyDispatcherServlet extends HttpServlet {
	private Properties contextConfig = new Properties();

	// 保存className
	private List<String> classNames = new ArrayList<String>();

	// IOC容器实际上就是一个Map
	private Map<String, Object> ioc = new HashMap<String, Object>();
	
	//HandlerMapping
	private Map<String,Method> handlerMapping=new HashMap<String, Method>();

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		this.doPost(req, resp);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			doDisPath(req,resp);
		}catch(Exception e){
			resp.getWriter().write(Arrays.toString(e.getStackTrace()));
			e.printStackTrace();
		}
	}

	/**
	 * 运行时处理请求
	 * @param resp 
	 * @param req 
	 * @throws Exception 
	 */
	private void doDisPath(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		//uri 项目名+路径
		String uri = req.getRequestURI();
		//uri是绝对路径，要转换成相对路径
		//contextPath项目名
		//Url=协议+ip+端口号+项目名+路径
		String contextPath = req.getContextPath();
		uri = uri.replaceAll(contextPath, "").replaceAll("/+", "/");
		
		if(!handlerMapping.containsKey(uri)) {
			resp.getWriter().write("Not Found!!");
			return;
		}
		//System.out.println(handlerMapping.get(uri));
//		Map<String,String> params = req.getParameterMap();
//		String name =  req.getParameterMap().get("name")[0];
//		System.out.println(new String(name.getBytes("ISO-8859-1"),"utf-8"));
		Map<String,String[]> params = req.getParameterMap();
		Method method=handlerMapping.get(uri);
		String beanName=lowerFirstCase(method.getDeclaringClass().getSimpleName());
		Object instance = ioc.get(beanName);
		//使用数组的原因是可能有checkBox这种情况
		//确定单个的话可以直接使用Map<String,String>  ，但要知道Map内部的value使用的是数组
		//实际情况是需要进行迭代的
		method.invoke(instance, new Object[] {req,resp,params.get("name")[0]});
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		// 1.加载配置文件
		doLoadConfig(config.getInitParameter("contextConfigLocation"));

		// 2.解析配置文件，并且读取信息完成扫描scanPackage下的所有类
		doScanner(contextConfig.getProperty("scanPackage"));

		// 3.初始化刚刚扫描出的所有的类，并放到IOC容器中
		doInstance();

		// 4.完成自动化注入的工作（DI：依赖注入）
		doAutowired();

		// 5.初始化HandlerMapping，将URL和Method进行一对一关联
		initHandlerMapping();

		System.out.println("My MVC Framework is init...");

	}

	private void initHandlerMapping() {
		if(ioc.isEmpty()) {return;}
		for (Map.Entry<String, Object> entry : ioc.entrySet()) {
			Class<?> clazz = entry.getValue().getClass();
			
			if(!clazz.isAnnotationPresent(MyController.class)) {
				continue;
			}
			
			//类上配置的url
			String baseUrl="";
			//如果class上加了MyRequestMapping注解
			if(clazz.isAnnotationPresent(MyRequestMapping.class)) {
				MyRequestMapping requestMapping = clazz.getAnnotation(MyRequestMapping.class);
				baseUrl = requestMapping.value();
			}
			
			Method[] methods = clazz.getMethods();
			for (Method method : methods) {
				//方法上没加MyRequestMapping注解
				if(!method.isAnnotationPresent(MyRequestMapping.class)) {
					continue;
				}
				MyRequestMapping requestMapping = method.getAnnotation(MyRequestMapping.class);
				//replaceAll("/+", "/")把多个斜杠替换成一个斜杠
				String url=baseUrl+requestMapping.value().replaceAll("/+", "/");
				
				handlerMapping.put(url, method);
				
				System.out.println("Mapped:"+url+","+method);
			}
			
		}
	}

	private void doAutowired() {
		// 为空
		if (ioc.isEmpty()) {
			return;
		}
		for (Map.Entry<String, Object> entry : ioc.entrySet()) {
			// 获取所有属性
			Field[] fields = entry.getValue().getClass().getDeclaredFields();
			// 循环
			for (Field field : fields) {
				// 没有加MyAutowired注解，直接忽略
				if (!field.isAnnotationPresent(MyAutowired.class)) {
					continue;
				}
				// 加了注解
				MyAutowired autowired = field.getAnnotation(MyAutowired.class);
				// MyAutowired可以写明注入ioc中的哪个bean
				String beanName = autowired.value().trim();
				// 没写
				if ("".equals(beanName)) {
					beanName = field.getType().getName();
					
				}
				field.setAccessible(true);//打开私有
				try {
					field.set(entry.getValue(),ioc.get(beanName));
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void doInstance() {
		if (classNames.isEmpty()) {
			return;
		}
		// 不是所有的类都要初始化
		for (String className : classNames) {
			try {
				Class<?> clazz = Class.forName(className);
				// 如果类加了MyController注解::实际上还有很多
				if (clazz.isAnnotationPresent(MyController.class)) {
					// 默认类名首字母小写作为Key
					String beanName = lowerFirstCase(clazz.getSimpleName());
					ioc.put(beanName, clazz.newInstance());
				} else if (clazz.isAnnotationPresent(MyService.class)) {
					// 拿到注解
					MyService service = clazz.getAnnotation(MyService.class);
					// 拿到注解的值:加注解的时候可以写具体的值,不写为创建注解时的默认值
					String beanName = service.value();
					// 默认
					if ("".equals(beanName.trim())) {
						beanName = lowerFirstCase(clazz.getSimpleName());
					}
					Object instance = clazz.newInstance();

					/*
					 * 在spring中，如果存在相同的key，直接报异常
					 */
					if (ioc.containsKey(beanName)) {
						throw new Exception("The bean is definded.");
					}
					ioc.put(beanName, instance);
					// 不做这一步的话，MyAutowired无法注入,因为MyAutowired是根据接口类型注入的
					Class<?>[] interfaces = clazz.getInterfaces();
					for (Class<?> i : interfaces) {
						if (ioc.containsKey(i.getName())) {
							throw new Exception("The bean is definded.");
						}
						ioc.put(i.getName(), instance);
					}

				} else {
					continue;
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 将字符串首字母变小写
	 * 
	 * @param simpleName
	 * @return
	 */
	private String lowerFirstCase(String simpleName) {
		char[] chars = simpleName.toCharArray();
		chars[0] += 32;
		return String.valueOf(chars);
	}

	private void doScanner(String scanPackage) {
		// 拿到的包名实际上只是一个字符串
		URL url = this.getClass().getClassLoader().getResource("/" + scanPackage.replaceAll("\\.", "/"));
		File classDir = new File(url.getFile());
		// 递归获取文件（面试题！！！！！！！）
		for (File file : classDir.listFiles()) {
			// 如果是文件夹
			if (file.isDirectory()) {
				doScanner(scanPackage + "." + file.getName());
			} else {
				String className = (scanPackage + "." + file.getName().replace(".class", ""));
				classNames.add(className);
			}
		}

	}

	private void doLoadConfig(String contextConfigLocation) {
		// classpath下的文件加载可以类加载器
		InputStream is = this.getClass().getClassLoader().getResourceAsStream(contextConfigLocation);
		try {
			contextConfig.load(is);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != is) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
