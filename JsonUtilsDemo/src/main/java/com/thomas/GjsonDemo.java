package com.thomas;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * Gson使用demo
 */
public class GjsonDemo {
    public static void main(String[] args) {
        Gson gson = new Gson();

        List<User> ulist = new ArrayList();
        User u = new User(1, "小黑", "男");
        for (int i = 0; i < 5; i++) {
            ulist.add(u);
        }
        //对象转json
        String json = gson.toJson(u);
        //集合转json
        String json1 = gson.toJson(ulist);
        //json转对象
        String a = "{\"id\":1,\"name\":\"小黑\",\"sex\":\"男\"}";
        User user = gson.fromJson(a, User.class);
        //json转集合
        String j = "[{\"id\":1,\"name\":\"小黑\",\"sex\":\"男\"},{\"id\":1,\"name\":\"小黑\",\"sex\":\"男\"},{\"id\":1,\"name\":\"小黑\",\"sex\":\"男\"},{\"id\":1,\"name\":\"小黑\",\"sex\":\"男\"},{\"id\":1,\"name\":\"小黑\",\"sex\":\"男\"}]";
        List<User> userList = gson.fromJson(j, new TypeToken<List<User>>() {
        }.getType());
        System.out.println("listsize:"+userList.size());
        System.out.println(user.getName());
        System.out.println(json);
        System.out.println(json1);

        //构建自定义json
        JsonObject aa=new JsonObject();
        JsonArray jsonArray=new JsonArray();
        JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty("url","www.baidu.com");
        jsonObject.addProperty("aa","bb");
        jsonArray.add(jsonObject);
        jsonArray.add(jsonObject);
        aa.addProperty("status","200");
        aa.add("data",jsonArray);
        String json2 = gson.toJson(aa);

        System.out.println(json2);
        //解析json
        JsonObject object = gson.fromJson(json2, JsonObject.class);
        JsonElement status = object.get("status");
        String asString = status.getAsString();
        System.out.println(asString);//200

        JsonElement data = object.get("data");
        JsonArray asJsonArray = data.getAsJsonArray();
        JsonElement jsonElement = asJsonArray.get(1);
        JsonObject asJsonObject = jsonElement.getAsJsonObject();
        JsonElement url = asJsonObject.get("url");
        String asString1 = url.getAsString();
        System.out.println(asString1);//www.baidu.com
    }
}
