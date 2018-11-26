package com.thomas.mvc.framework.context;

import com.thomas.mvc.framework.annotation.MyAutowired;
import com.thomas.mvc.framework.annotation.MyController;
import com.thomas.mvc.framework.annotation.MyService;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class MyApplicationContext {
    private Map<String, Object> instanceMapping = new ConcurrentHashMap<String, Object>();
    //类似于内部的配置信息，我们在外面是看不到的
    //我们能看到的只有ioc容器 getBean方法来间接调用
    private List<String> classCache = new ArrayList<String>();

    private Properties config = new Properties();

    public MyApplicationContext(String location) {
        //先加载配置文件
        //定位、载入、注册、初始化、注入
        InputStream is = null;
        try {
            //1.定位
            is = this.getClass().getClassLoader().getResourceAsStream(location);
            //2.载入
            config.load(is);
            //3.注册，把所有class找出来存着
            String packageName = config.getProperty("scanPackage");
            //  System.out.println("packageName"+packageName);
            doRegister(packageName);

            //4.实例化需要ioc的对象（就是加了@Service，@Controller注解等的类）
            doCreateBean();
            //5.依赖注入
            populate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("IOC容器已经初始化");
    }

    /**
     * 依赖注入
     */
    private void populate() {
        //首先判断ioc容器中有没有东西
        if(instanceMapping.isEmpty()){
            return;
        }
        for(Map.Entry<String,Object> entry:instanceMapping.entrySet()){
            //把所有的属性全部取出来，包括私有属性
            Field[] fields = entry.getValue().getClass().getDeclaredFields();
            for(Field field:fields){
                //没有MyAutowired注解
                if(!field.isAnnotationPresent(MyAutowired.class)){
                    continue;
                }
                MyAutowired autowired=field.getAnnotation(MyAutowired.class);
                String id = autowired.value().trim();
                //若没有指定
                if("".equals(id)){
                    //根据类型名：：：
                    id=field.getType().getName();
                }
                field.setAccessible(true);//私有变量开放访问权限

                try {
                    field.set(entry.getValue(),instanceMapping.get(id));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    continue;
                }
            }
        }
    }

    private void doCreateBean() {
        //检查看有没有注册信息，注册信息里面保存了所有的class名字
        //BeanDefinition保存了类的名字，也保存了类和类之间的关系（Map/list/set/ref/parent）
        if (classCache.size() == 0) {
            return;
        }
        try {
            for (String className : classCache) {
                Class<?> clazz = Class.forName(className);

                //哪个类需要初始化，哪个类不需要初始化
                if (clazz.isAnnotationPresent(MyController.class)) {
                    //名字起啥？默认就是类的首字母小写
                    String id = lowerFirstChar(clazz.getSimpleName());
                    instanceMapping.put(id, clazz.newInstance());
                } else if (clazz.isAnnotationPresent(MyService.class)) {
                    MyService service = clazz.getAnnotation(MyService.class);
                    //如果设置了自定义名字，就优先用它自己定义的
                    String id = service.value();
                    if (!"".equals(id.trim())) {
                        instanceMapping.put(id, clazz.newInstance());
                        continue;
                    }
                    //如果是空的，就用默认规则
                    //1.类名首字母小写
                    //如果这个类是接口
                    //2.可以根据类型来匹配
                    Class<?>[] interfaces = clazz.getInterfaces();
                    //如果这个类实现了接口，就用接口的类型作为id
                    for (Class<?> i : interfaces) {
                        instanceMapping.put(i.getName(), clazz.newInstance());
                    }
                } else {
                    continue;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 将首字母小写
     *
     * @param str
     * @return
     */
    private String lowerFirstChar(String str) {
        char[] chars = str.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }

    /**
     * 把符合条件所有的class全部找出来，注册到缓存里面
     *
     * @param packageName
     */
    private void doRegister(String packageName) {
        //全类名其实就是对应着操作系统的路径，这里是把 "."换成"/"
        URL url = this.getClass().getClassLoader().getResource("/" + packageName.replaceAll("\\.", "/"));
        File dir = new File(url.getFile());
        for (File file : dir.listFiles()) {
            //如果是一个文件夹，继续递归
            if (file.isDirectory()) {
                doRegister(packageName + "." + file.getName());
            } else {
                classCache.add(packageName + "." + file.getName().replace(".class", "").trim());
            }
        }
    }

    //    public Object getBean(String name){
//        return null;
//    }
    public Map<String, Object> getAll() {
        return instanceMapping;
    }
    public Properties getConfig(){
        return config;
    }
}
