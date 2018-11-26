package com.thomas.mvc.framework.servlet;

import com.thomas.mvc.framework.annotation.MyController;
import com.thomas.mvc.framework.annotation.MyRequestMapping;
import com.thomas.mvc.framework.context.MyApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;

public class MyDispatcherServlet extends HttpServlet {
    //配置文件路径
    private static final String LOCATION = "MycontextConfigLocation";
    //Spring中使用的是List  --ArrayList
    private Map<String,Handler> handlerMapping=new HashMap<String, Handler>();
   // private List<Handler> handlerMappings = new ArrayList<Handler>();

    //初始化IOC容器
    @Override
    public void init(ServletConfig config) throws ServletException {
        //IOC容器必须要先初始化
        MyApplicationContext context = new MyApplicationContext(config.getInitParameter(LOCATION));
        //测试---IOC没问题
//        Map<String, Object> all = context.getAll();
//        System.out.println(all);
//        System.out.println(all.get("firstAction"));
        //请求解析
        initMultipartResolver(context);
        //多语言、国际化
        initLocaleResolver(context);
        //主题View层的
        initThemeResolver(context);

        //============== 重要 ================
        //解析url和Method的关联关系
        initHandlerMappings(context);
        //适配器（匹配的过程）
        initHandlerAdapters(context);
        //============== 重要 ================


        //异常解析
        initHandlerExceptionResolvers(context);
        //视图转发（根据视图名字匹配到一个具体模板）
        initRequestToViewNameTranslator(context);

        //解析模板中的内容（拿到服务器传过来的数据，生成HTML代码）
        initViewResolvers(context);

        initFlashMapManager(context);
        System.out.println("MySpringMVC is init");
    }

    /**
     * 请求解析
     *
     * @param context
     */
    private void initMultipartResolver(MyApplicationContext context) {
    }

    /**
     * 多语言、国际化
     *
     * @param context
     */
    private void initLocaleResolver(MyApplicationContext context) {
    }

    /**
     * 主题View层的
     *
     * @param context
     */
    private void initThemeResolver(MyApplicationContext context) {
    }

    /**
     * 解析url和Method的关联关系
     *
     * @param context
     */
    private void initHandlerMappings(MyApplicationContext context) {
        Map<String, Object> ioc = context.getAll();
        if (ioc.isEmpty()) {
            return;
        }
        //只要是有MyController修饰的类，里面的方法全部找出来
        //而且这个方法上应该要加了RequestMapping注解，如果没加这个注解，这个方法是不能被外界访问的
        for (Map.Entry<String, Object> entry : ioc.entrySet()) {
            Class<?> clazz = entry.getValue().getClass();
            if (!clazz.isAnnotationPresent(MyController.class)) {
                continue;
            }
            String url = "";
            //类上加了@MyRequestMapping注解
            if (clazz.isAnnotationPresent(MyRequestMapping.class)) {
                MyRequestMapping requestMapping = clazz.getAnnotation(MyRequestMapping.class);
                url = requestMapping.value();
            }
            //扫描Controller下面的所有方法
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                //方法上没加@MyRequestMapping注解
                if (!method.isAnnotationPresent(MyRequestMapping.class)) {
                    continue;
                }
                MyRequestMapping requestMapping = method.getAnnotation(MyRequestMapping.class);
                String mappingUrl=(url+requestMapping.value());
                //handlerMapping.put(mappingUrl,)
                //可能加了多个/，多个也视作一个
                //String regex=(url+requestMapping.value()).replaceAll("/+","/");
            }
        }

    }

    /**
     * 适配器（匹配的过程）
     *
     * @param context
     */
    private void initHandlerAdapters(MyApplicationContext context) {
    }

    /**
     * 异常解析
     *
     * @param context
     */
    private void initHandlerExceptionResolvers(MyApplicationContext context) {
    }

    /**
     * 视图转发（根据视图名字匹配到一个具体模板）
     *
     * @param context
     */
    private void initRequestToViewNameTranslator(MyApplicationContext context) {
    }

    /**
     * 解析模板中的内容（拿到服务器传过来的数据，生成HTML代码）
     *
     * @param context
     */
    private void initViewResolvers(MyApplicationContext context) {
    }

    private void initFlashMapManager(MyApplicationContext context) {
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    //调用Controller方法
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            doDispatch(req, resp);

        } catch (Exception e) {
            resp.getWriter().write("500 Exception,Msg:" + Arrays.toString(e.getStackTrace()));
        }

    }

    private Handler getHandler(HttpServletRequest req) {
        return null;
    }

    private HandlerAdapter getHandlerAdapter(Handler handler) {
        return null;
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        //先取出来一个Handler，从HandlerMapping取
        Handler handler = getHandler(req);
        if (handler == null) {
            resp.getWriter().write("404 Not Found");
            return;
        }
        //再取出来一个适配器
        HandlerAdapter adapter = getHandlerAdapter(handler);
        //再由适配器去调用我们具体的方法
        adapter.handle(req, resp, handler);
    }
    //源码中不是内部类

    /**
     * HandlerMapping定义
     */
    private class Handler {
        protected Object controller;
        protected Method method;
        protected Handler(Object controller,Method method){
            this.controller=controller;
            this.method=method;
        }
    }
    //源码中不是内部类

    /**
     * 方法适配器
     */
    private class HandlerAdapter {
        public void handle(HttpServletRequest req, HttpServletResponse resp, Handler handler) {

        }
    }
}
