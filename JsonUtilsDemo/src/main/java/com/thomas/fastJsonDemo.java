package com.thomas;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

import java.util.ArrayList;
import java.util.List;

/**
 * fastJson使用demo
 * 注意点：fastJson是根据get方法生成json字符串的
 */
public class fastJsonDemo {
    public static void main(String[] args) {
        //json转对象
        String a = "{\"id\":1,\"name\":\"小黑\",\"sex\":\"男\"}";
        User user = JSON.parseObject(a, new TypeReference<User>() {
        });
        System.out.println(user);
        //对象转json
        User u=new User(2,"小白","女");
        User u1=new User(3,"小黑","男");
        Object o = JSON.toJSON(u);
        System.out.println(o.toString());

        //集合转json
        List<User> list=new ArrayList<>();
        list.add(u);
        list.add(u1);
        Object o1 = JSON.toJSON(list);
        System.out.println(o1.toString());
        //json转集合
        String jsonlist="[{\"tTT\":\"sdsadas\",\"sex\":\"女\",\"name\":\"小白\",\"id\":2},{\"tTT\":\"sdsadas\",\"sex\":\"男\",\"name\":\"小黑\",\"id\":3}]";
        ArrayList<User> students = JSON.parseObject(jsonlist, new TypeReference<ArrayList<User>>() {});
        System.out.println(students.get(0));
        //手动构建json
        JSONObject all=new JSONObject();
        JSONObject Jdasbject=new JSONObject();
        Jdasbject.fluentPut("status","200");
        Jdasbject.fluentPut("data",22222);
        JSONArray jsonArray=new JSONArray();
        jsonArray.fluentAdd(Jdasbject);
        jsonArray.fluentAdd(Jdasbject);
        all.fluentPut("status",10000);
        all.fluentPut("data",jsonArray);
        System.out.println(all.toJSONString());
        //解析json
        JSONObject jsonObject = JSON.parseObject(all.toJSONString());
        String status = jsonObject.getString("status");
        System.out.println(status);

    }
}
