package com.example.ahp.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 *@Author keith
 *@Date 2024/3/5 16:50
 *@Description 远程调用工具
 */
public class RemoteInvokeUtil {
    /**
     *@Author keith
     *@Date 2024/3/5 16:50
     *@Description post请求
     */
    public Map<String,Object> sendPostRequest(HashMap<String, Object> params, URL url, Map<String,String> requestHead){
        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("result","请求失败");
        try{
            //请求参数
            RequestBody requestBody = RequestBody.create(new Gson().toJson(params), MediaType.parse("application/json"));

            //准备请求
            Request.Builder builder = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json");
            for(Map.Entry<String,String> entry:requestHead.entrySet()){
                builder.addHeader(entry.getKey(),entry.getValue());
            }
            Request request = builder.build();

            //发起请求
            OkHttpClient HTTP_CLIENT = new OkHttpClient().newBuilder().build();
            Response response = HTTP_CLIENT.newCall(request).execute();

            //返回结果
//            resultMap.put("data",new Gson().fromJson(response.body().string(), CreateOptions.class));
        }catch (Exception e){
            e.printStackTrace();
            return resultMap;
        }
        resultMap.put("result","请求成功");
        return resultMap;
    }

    /**
     *@Author keith
     *@Date 2024/3/30 16:35
     *@Description get请求 localhost:8836/ahpFirstData/searchProportionComments/参数
     */
    public static Map<String,Object> sendGetRequest1(String urlStr,Map<String,String> requestHead){
        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("result","请求失败");
        try {
            //准备连接参数
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            //设置请求头,指明请求内容为json格式
            conn.setRequestProperty("Content-Type", "application/json");
            for(Map.Entry<String,String> entry:requestHead.entrySet()){
                conn.setRequestProperty(entry.getKey(),entry.getValue());
            }

            //获得返回结果
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            //处理返回结果
            TypeToken<Map<String, Object>> token = new TypeToken<Map<String, Object>>(){};
            Map<String, Object> map = new Gson().fromJson(response.toString(), token.getType());
            resultMap.put("data",map.get("data"));

            //连接关闭
            conn.disconnect();
        }catch (Exception e){
            e.printStackTrace();
            return resultMap;
        }
        resultMap.put("result","请求成功");
        return resultMap;
    }

    /**
     *@Author keith
     *@Date 2024/3/5 17:23
     *@Description get请求 往/拼接参数:参数名1=X&参数名2=Y
     */
//    public Map<String,Object> sendGetRequest1(Map<String,String> paramsMap, String urlStr,Map<String,String> requestHead){
//        HashMap<String, Object> resultMap = new HashMap<>();
//        resultMap.put("result","请求失败");
//        try {
//            //准备请求参数
//            String params = "";
//            for(Map.Entry<String,String> entry:paramsMap.entrySet()){
//                //是否添加&
//                if(!"".equals(params)){
//                    params += "&";
//                }
//                params = params + entry.getKey() +"=" + URLEncoder.encode(entry.getValue(), "UTF-8");
//            }
//
//            //准备连接参数
//            urlStr += params;
//            URL url = new URL(urlStr);
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setRequestMethod("GET");
//
//            //设置请求头,指明请求内容为json格式
//            conn.setRequestProperty("Content-Type", "application/json");
//            for(Map.Entry<String,String> entry:requestHead.entrySet()){
//                conn.setRequestProperty(entry.getKey(),entry.getValue());
//            }
//
//            //获得返回结果
//            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//            String line;
//            StringBuilder response = new StringBuilder();
//            while ((line = reader.readLine()) != null) {
//                response.append(line);
//            }
//            reader.close();
//
//            //处理返回结果
//            TypeToken<Map<String, Object>> token = new TypeToken<Map<String, Object>>(){};
//            Map<String, Object> map = new Gson().fromJson(response.toString(), token.getType());
//            resultMap.put("data",map);
//
//            //连接关闭
//            conn.disconnect();
//        }catch (Exception e){
//            e.printStackTrace();
//            return resultMap;
//        }
//        resultMap.put("result","请求成功");
//        return resultMap;
//    }
}
