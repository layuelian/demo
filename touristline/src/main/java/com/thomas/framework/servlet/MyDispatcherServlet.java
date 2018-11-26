package com.thomas.framework.servlet;

import com.thomas.framework.annotation.MyController;
import com.thomas.framework.annotation.MyRequestMapping;
import com.thomas.framework.annotation.MyRequestParam;
import com.thomas.framework.context.MyApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyDispatcherServlet extends HttpServlet {
    private static final String LOCATION = "MyContextConfigLocation";

    private List<Handler> handlerMapping = new ArrayList<Handler>();

    private Map<Handler,HandlerAdapter> adapterMapping=new HashMap<Handler,HandlerAdapter>();


    //初始化
    public void init(ServletConfig config) throws ServletException {
        //IOC容器初始化
        MyApplicationContext context = new MyApplicationContext(config.getInitParameter(LOCATION));

        //解析url和Method的关联关系
        initHandlerMappings(context);
        //适配器（匹配的过程）
        initHandlerAdapters(context);

        //解析模板中的内容（拿到服务器传过来的数据，生成HTML代码）
        initViewResolvers(context);


        System.out.println("My Spring MVC is init.");
    }

    /**
     * 解析模板中的内容（拿到服务器传过来的数据，生成HTML代码）
     * @param context
     */
    private void initViewResolvers(MyApplicationContext context) {
        //模板一般是不会放到WebRoot下的，而是放在WEB-INF下，或者classes下
        //这样就避免了用户直接请求到模板
        //加载模板的个数，存储到缓存中
        //检查模板中的语法错误

        String templateRoot = context.getConfig().getProperty("templateRoot");

        //归根到底就是一个文件，普通文件
        String rootPath=this.getClass().getClassLoader().getResource(templateRoot).getFile();

        File rootDir=new File(rootPath);
        for (File template : rootDir.listFiles()) {
            //viewResolvers.add(new ViewResolver(template.getName(),template));
        }
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
        //把加有MyController注解的类，里面的方法全部找出来
        //而且这个方法应该要加了MyRequestMapping注解，
        // 没加这个注解则不能被外界访问
        for (Map.Entry<String, Object> entry : ioc.entrySet()) {
            Class<?> clazz = entry.getValue().getClass();
            //没加，不处理
            if (!clazz.isAnnotationPresent(MyController.class)) {
                continue;
            }
            String url = "";
            //类上加了MyRequestMapping注解
            if (clazz.isAnnotationPresent(MyRequestMapping.class)) {
                MyRequestMapping requestMapping = clazz.getAnnotation(MyRequestMapping.class);
                url = requestMapping.value();
            }
            //扫描Controller下面的所有方法
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                //方法上没有MyRequestMapping注解
                if (!method.isAnnotationPresent(MyRequestMapping.class)) {
                    continue;
                }
                MyRequestMapping requestMapping = method.getAnnotation(MyRequestMapping.class);
                //如果多个“/”，当做一个
                String regex = (url + requestMapping.value()).replaceAll("/+", "/");
                Pattern pattern = Pattern.compile(regex);

                handlerMapping.add(new Handler(pattern, entry.getValue(), method));

                System.out.println("Mapping: " + regex + " " + method.toString());

            }

        }

    }

    /**
     * 适配器（匹配的过程）
     * 主要是动态匹配参数的
     *
     * @param context
     */
    private void initHandlerAdapters(MyApplicationContext context) {
        if (handlerMapping.isEmpty()) {
            return;
        }
        //参数类型作为key，参数的索引号作为值
        Map<String, Integer> paramMapping = new HashMap<>();

        //只需要取出来具体的某个方法
        for (Handler handler : handlerMapping) {
            //把这个方法上面所有参数获取到
            Class<?>[] parameterTypes = handler.method.getParameterTypes();

            //有顺序，但是通过反射，没法拿到参数名字
            //匹配自定义参数列表
            for (int i = 0; i < parameterTypes.length; i++) {
                Class<?> type = parameterTypes[i];
                if (type == HttpServletRequest.class || type == HttpServletResponse.class) {
                    paramMapping.put(type.getName(), i);
                }
            }

            //这里匹配Request和Response
            Annotation[][] pa = handler.method.getParameterAnnotations();
            for (int i = 0; i < pa.length; i++) {
                for (Annotation a : pa[i]) {
                    if (a instanceof MyRequestParam) {
                        String paramName = ((MyRequestParam) a).value();
                        if (!"".equals(paramName.trim())) {
                            paramMapping.put(paramName, i);
                        }
                    }
                }
            }

            adapterMapping.put(handler,new HandlerAdapter(paramMapping));

        }
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }
    //在这里调用自己写的Controller的方法
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try{
            doDispatch(req,resp);
        }catch (Exception e){
            resp.getWriter().write("500 Exception, Msg :" + Arrays.toString(e.getStackTrace()));
        }

    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception{

        try{
            //先取出来一个Handler，从HandlerMapping取
            Handler handler=getHandler(req);
            if(handler==null){
                resp.getWriter().write("404 Not Found");
                return;
            }
        }catch (Exception e){
            throw e;
        }

    }

    private Handler getHandler(HttpServletRequest req) {
        //循环handlerMapping
        if(handlerMapping.isEmpty()){
            return null;
        }
        String url=req.getRequestURI();
        String contextPath=req.getContextPath();
        url=url.replace(contextPath,"").replaceAll("/+","/");
        for(Handler handler:handlerMapping){
            Matcher matcher = handler.pattern.matcher(url);
            if(!matcher.matches()){
                continue;
            }
            return handler;
        }
        return null;
    }

    private class HandlerAdapter {
        private Map<String, Integer> paramMapping;

        public HandlerAdapter(Map<String, Integer> paramMapping) {
            this.paramMapping = paramMapping;
        }

        //主要目的是用反射调用url对应的method
        public MyModelAndView handle(HttpServletRequest req, HttpServletResponse resp, Handler handler) throws InvocationTargetException, IllegalAccessException {
            //为什么要传req、为什么要传resp、为什么要传handler
            Class<?>[] parameterTypes = handler.method.getParameterTypes();

            //要想给参数赋值，只能通过索引号来找到具体的参数
            Object[] paramValues = new Object[parameterTypes.length];

            Map<String, String[]> params = req.getParameterMap();

            for (Map.Entry<String, String[]> param : params.entrySet()) {
                String value = Arrays.toString(param.getValue()).replaceAll("\\[|\\]", "").replaceAll(",\\s", ",");

                if (!this.paramMapping.containsKey(param.getKey())) {
                    continue;
                }
                int index=this.paramMapping.get(param.getKey());

                //单个赋值是不行的
                paramValues[index]=castStringValue(value,parameterTypes[index]);
            }
            //request和response要赋值
            String reqName=HttpServletRequest.class.getName();
            if(this.paramMapping.containsKey(reqName)){
                int reqIndex=this.paramMapping.get(reqName);
                paramValues[reqIndex]=req;
            }

            String respName=HttpServletResponse.class.getName();
            if(this.paramMapping.containsKey(respName)){
                int respIndex=this.paramMapping.get(respName);
                paramValues[respIndex]=resp;
            }
            boolean isModelAndView=handler.method.getReturnType()==MyModelAndView.class;
            Object r=handler.method.invoke(handler.controller,paramValues);
            if(isModelAndView){
                return (MyModelAndView)r;
            }else {
                return null;
            }
        }

        private Object castStringValue(String value, Class<?> clazz) {
            if(clazz==String.class){
                return value;
            }else if(clazz==Integer.class){
                return Integer.valueOf(value);
            }else if(clazz==int.class){
                return Integer.valueOf(value).intValue();
            }else {
                return null;
            }
        }
    }

    /**
     * HandlerMapping 定义
     */
    private class Handler {
        protected Object controller;
        protected Method method;
        protected Pattern pattern;

        protected Handler(Pattern pattern, Object controller, Method method) {
            this.pattern = pattern;
            this.controller = controller;
            this.method = method;
        }
    }
    private class ViewResolver{
        private String viewName;
        private File file;
        protected ViewResolver(String viewName,File file){
            this.viewName=viewName;
            this.file=file;
        }
        protected String parse(MyModelAndView mv) throws Exception {
            StringBuffer sb=new StringBuffer();

            RandomAccessFile ra=new RandomAccessFile(this.file,"r");

            try{
                //模板框架的语法是非常复杂的，但是，原理是一样的
                //无非都是用正则表达式来处理字符串而已
                //就这么简单，不要认为这个模板框架的语法是有多么高大上
                String line=null;
                while (null!=(line=ra.readLine())){
                    Matcher m = matcher(line);
                    while (m.find()) {
                        for (int i = 1; i <= m.groupCount(); i ++) {
                            String paramName = m.group(i);
                            Object paramValue = mv.getModel().get(paramName);
                            if(null == paramValue){ continue; }
                            line = line.replaceAll("@\\{" + paramName + "\\}", paramValue.toString());
                        }
                    }

                    sb.append(line);
                }
            }finally {
                ra.close();
            }
            return sb.toString();
        }
        private Matcher matcher(String str){
            Pattern pattern = Pattern.compile("@\\{(.+?)\\}",Pattern.CASE_INSENSITIVE);
            Matcher m = pattern.matcher(str);
            return m;
        }
    }
}
