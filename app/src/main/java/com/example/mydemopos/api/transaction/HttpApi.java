package com.example.mydemopos.api.transaction;

import org.json.JSONObject;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpApi {
    public final static String URL = "http://172.16.24.23:9090/transaction";
    /*
    提交交易数据【交易编号 卡号 基本金额 小费 到期日期】
     */
    public static Request submitTransaction(Map<String,String> map,int flag){
        Request request = null;
        switch (flag){
            case 0:
                //如果以表单的形式使用post提交 需要将传递的数据封装到RequestBody中
                FormBody.Builder builder = new FormBody.Builder();
                for(String key : map.keySet()){
                    builder.add(key,map.get(key));
                }
                RequestBody requestBody_form = builder.build();
                request = new Request.Builder()
                        .url(URL)
                        .post(requestBody_form)
                        .build();
                break;
            case 1:
                //如果是直接传一个json字符串
                JSONObject jsonObject = new JSONObject(map);
                MediaType JSON = MediaType.parse("application/json; charset=gbk");
                RequestBody requestBody_json = RequestBody.create(JSON, jsonObject.toString());
                //Log.e("jsonString",jsonObject.toString());
                request = new Request.Builder()
                        .url(URL)
                        .post(requestBody_json)
                        .build();
                break;

            case 2:
                request = new Request.Builder()
                        .url(URL)
                        .build();
            default:
                break;
        }
        return request;
    }
}
