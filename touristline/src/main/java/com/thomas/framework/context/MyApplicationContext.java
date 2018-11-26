package com.thomas.framework.context;

import com.thomas.framework.annotation.MyAutowired;
import com.thomas.framework.annotation.MyController;
import com.thomas.framework.annotation.MyService;

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

    private List<String> classCache = new ArrayList<String>();

    private Properties config = new Properties();

    public MyApplicationContext(String location) {
        InputStream is = null;
        try {
            //1.定位
            is = this.getClass().getClassLoader().getResourceAsStream(location);
            //2.载入
            config.load(is);
            //3.注册，把所有class找出来存着
            String packageName = config.getProperty("scanPackage");
            doRegister(packageName);
            //4.实例化需要IOC的对象（即加了@MyService，@MyController等的类）
            doCreateBean();
            //5.依赖注入
            populate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void populate() {
        //判断IOC容器有没有东西
        if (instanceMapping.isEmpty()) {
            return;
        }
        for(Map.Entry<String, Object> entry:instanceMapping.entrySet()){
            //把所有的属性全部取出来，包括私有属性
            Field[] fields = entry.getValue().getClass().getDeclaredFields();
            for(Field field:fields){
                //判断有没有MyAutowired注解
                if(!field.isAnnotationPresent(MyAutowired.class)){
                    continue;
                }
                MyAutowired autowired = field.getAnnotation(MyAutowired.class);
                String id = autowired.value().trim();
                //若没有指定
                if("".equals(id)){
                    //根据类型名
                    id=field.getType().getName();
                }
                field.setAccessible(true);
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
        // (BeanDefinition)保存了类的名字，也保存了类与类之间的关系（Map、List等）
        if(classCache.size()==0){
            return;
        }
        try{
            for(String className:classCache){
                Class<?> clazz = Class.forName(className);

                //哪些类需要初始化，哪些类不需要
                if(clazz.isAnnotationPresent(MyController.class)){
                    //名字起啥，默认就是类的首字母小写
                    String id=lowerFirstChar(clazz.getSimpleName());
                    //创建实例
                    instanceMapping.put(id,clazz.newInstance());
                }else if(clazz.isAnnotationPresent(MyService.class)){
                    //获取注解
                    MyService service=clazz.getAnnotation(MyService.class);
                    //如果设置了自定义名字，优先用它
                    String id =service.value();
                    if(!"".equals(id.trim())){
                        instanceMapping.put(id,clazz.newInstance());
                        continue;
                    }
                    //如果是空的，就用默认规则
                    //1.类名首字母小写
                    //如果这个类实现接口
                    //2.可以根据类型匹配
                    Class<?>[] interfaces = clazz.getInterfaces();
                    for(Class<?> i:interfaces){
                        instanceMapping.put(i.getName(),clazz.newInstance());
                    }
                }else {
                    continue;
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * 首字母小写
     * @param simpleName
     * @return
     */
    private String lowerFirstChar(String simpleName) {
        char[] chars=simpleName.toCharArray();
        chars[0]+=32;
        return String.valueOf(chars);
    }

    //把符合条件所有的class全部找出来，注册到缓存里面去
    private void doRegister(String packageName){
        //全类名对应着操作系统的路径，这里把“.”换成"/"
        URL url = this.getClass().getClassLoader().getResource("/" + packageName.replaceAll("\\.", "/"));
        File dir = new File(url.getFile());
        for (File file : dir.listFiles()) {
            //如果是一个文件夹，继续递归
            if(file.isDirectory()){
                doRegister(packageName + "." + file.getName());
            }else{
                classCache.add(packageName + "." + file.getName().replace(".class", "").trim());
            }
        }
    }

    public Map<String, Object> getAll() {
        return instanceMapping;
    }
    public Properties getConfig(){
        return config;
    }
}
