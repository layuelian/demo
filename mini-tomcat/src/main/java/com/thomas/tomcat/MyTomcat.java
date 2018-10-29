package com.thomas.tomcat;

import com.thomas.http.MyRequest;
import com.thomas.http.MyResponse;
import com.thomas.http.MyServlet;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

public class MyTomcat {
    //默认端口号8080
    private int port = 8080;
    private ServerSocket server;
    private Map<Pattern, Class<?>> servletMapping = new HashMap<Pattern, Class<?>>();
    private Properties webxml = new Properties();
    private String WEB_INF = this.getClass().getResource("/").getPath();

    //WEB容器的基本架构就起来了
    public MyTomcat() {
    }

    //可以设置端口号
    public MyTomcat(int port) {
        this.port = port;
    }

    //WEB启动前，加载所有的配置文件
    private void init() {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(WEB_INF + "web.properties");
            webxml.load(fis);
            //遍历properties中的key和value
            for (Object k : webxml.keySet()) {
                String key = k.toString();
                //如果是url
                if (key.endsWith(".url")) {
                    //   \\.即匹配 .  $结尾符
                    String servletName = key.replaceAll("\\.url$", "");
                    //获取key对应的值
                    String url = webxml.getProperty(key);

                    Pattern pattern = Pattern.compile(url);

                    String className = webxml.getProperty(servletName+".className");

                    Class<?> servletClass = Class.forName(className);

                    servletMapping.put(pattern, servletClass);

                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    //等待客户请求
    private void process(Socket client) throws Exception {
        //首先要把Request和Response搞出来
        //Request 实际上就是对我们的InputStream的一个封装
        //Response 是OutputStream的封装
        InputStream is = client.getInputStream();
        OutputStream out = client.getOutputStream();
        MyRequest request = new MyRequest(is);
        MyResponse response = new MyResponse(out);
        try {
            //此时此刻，还缺一个Servlet
            //service(Request,Response)  doGet doPost

            //这个Servlet自己从没亲手new过？
            //读取web.xml文件来获取自己的Servlet
            //利用反射机制new处理
            String url = request.getUrl();
            boolean isPattern = false;

            for (Map.Entry<Pattern, Class<?>> entry : servletMapping.entrySet()) {
                if (entry.getKey().matcher(url).matches()) {
                    MyServlet servlet = (MyServlet) entry.getValue().newInstance();
                    servlet.service(request, response);
                    isPattern = true;
                    break;
                }
            }
            if (!isPattern) {
                response.write("404-Not Found");
            }

        } catch (Exception e) {
            response.write("500 ," + e.getMessage());
        } finally {
            is.close();
            out.close();
            //HTTP本身是无状态的协议
            client.close();
        }

    }

    public void start() {
        init();

        //BIO写法，现在新的tomcat已经支持nio了 （tomcat8）
        try {
            server = new ServerSocket(this.port);
            System.out.println("My Tomcat 已启动，监听端口是：" + this.port);
        } catch (Exception e) {
            System.out.println("My Tomcat启动失败," + e.getMessage());
            return;
        }
        while (true) {
            //容器已经能够持续提供服务了
            try {
                Socket client = server.accept();
                process(client);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new MyTomcat().start();
    }
}
