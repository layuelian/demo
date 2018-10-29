实现一个简易tomcat容器。

主要用于理解Servlet和Request、Response

1.基础
需求：
通过自己的浏览器去访问某台电脑上的一个文件：E：\heihei.txt
首先，我们去访问互联网任意的一台电脑，是有一个IP标识的。
IP+E:\heihei.txt   这种方式是访问不到的。
解决：觉得引入一个应用程序(web服务器)

我们知道，每个应用程序占用一个端口号：
我们可以通过IP+端口号   去访问一个应用程序
--->写一个应用程序   用IO流去读 heihei.txt
                    用IO流写到浏览器端
ServerSocket serverSocket=new ServerSocket(9000);
Socket socket=serverSocket.accept();
我们可以根据这个socket得到与浏览器交互的输入输出流
（1）应用程序
Class getTxt{
}
（2）让这个应用程序占用一个端口
ServerSocket serverSocket=new ServerSocket(9000);
（3）这个应该程序去读取本地磁盘中的heihei.txt
    前提：首先要拿到浏览器的请求
    Socket socket=serverSocket.accept();
    得到浏览器的输入流
IO读取流读取本地文件
(4)读取heihei.txt然后写到浏览器端

2.前奏
需求：假如又来一个haha.txt
localhost：9000 -->已经对应读取heihei.txt
解决：-->做判断
if(想要heihei.txt){
}else if(想要haha.txt){
}
怎么判断？--->我们都是从url中获取想要的东西
在获取请求的时候进行判断
url/heihei.txt--->heihei.txt
url/haha.txt  ---->haha.txt
--->这样可以知道浏览器想要的内容
但是这样做的话，存在很多硬编码，不合适后期维护（很多if else）
-->能不能有一个.xml文件，我将需要访问的内容写到里面
web.xml ->Servlet

①当文件多了之后，这些文件怎么维护？
把相同的文件放到同一个文件夹中？  server.xml  里面的Context配置
②浏览器的传过来的信息有可能在服务器端多次用到
-->需要用一个对象封装一下浏览器请求的数据
-->Request封装浏览器的请求，Response封装服务器响应  两者属于J2EE规范，不属于tomcat

浏览器的请求和服务器的响应准确的来说应该是业务代码
业务代码不应该写在tomcat服务器中
-->
能不能把浏览器的请求和服务器的响应抽出去
再新建一个类，这个类专门用于处理浏览器的请求和服务器的响应
interface Servlet{
    Service(Request，Response){
        //处理请求和响应
        //根据请求的GET ，POST做不同的操作
        doGet();
        doPost();
    }
}

Tomcat   web服务器/tomcat容器(可看做装载东西的箱子)/Servlet容器

一个请求对应一个Servlet
-->web.xml中的Servlet 、Servletmapping

注意：Servlet不是Tomcat的一部分，Servlet是J2EE规范的东西，
tomcat仅仅是一个容器，用来装Servlet的






