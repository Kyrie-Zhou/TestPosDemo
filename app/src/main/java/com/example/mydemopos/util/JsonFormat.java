package com.example.mydemopos.util;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/*
浏览器响应json数据时利用JsonObject进行解析
 */
public class JsonFormat {
    public static Object[] parseJSONWithJSONObject(String jsonData,String[] keys){
        Object objs[] = new Object[keys.length];
        Log.e("jsonData",jsonData);
        //json数组 字符串
        if(jsonData.charAt(0) == '['){
            try {
                JSONArray jsonArray = new JSONArray(jsonData);
                for (int i = 0 ; i < jsonArray.length() ; i ++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    for(int j = 0 ; j < keys.length ; j ++){
                        jsonObject.get(keys[i]);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        //简单的json字符串
        else{
            try{
                JSONObject jsonObject = new JSONObject(jsonData);
                for(int i = 0 ; i < keys.length ; i ++){
                    objs[i] = jsonObject.get(keys[i]);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return objs;
    }

}
