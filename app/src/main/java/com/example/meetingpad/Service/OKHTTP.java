package com.example.meetingpad.Service;


import android.util.Log;

import net.sf.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class OKHTTP {
    //    试试发送给服务器的事前准备，初始化OkHttpClient
    public final static int CONNECT_TIMEOUT = 10000;
    public final static int READ_TIMEOUT = 100;
    public final static int WRITE_TIMEOUT = 60;
    public final static MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public final static String BASE_URL_SIMULATOR = "http://10.0.2.2:8080/face_meeting";
    public final static String BASE_URL_APP = "http://192.168.1.101:8080/face_meeting";
    public final static String BASE_URL_TEST = "http://127.0.0.1:8080/face_meeting";
    public static String BASE_URL = BASE_URL_APP;

    public static final OkHttpClient client = new OkHttpClient.Builder()
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)//设置读取超时时间
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)//设置写的超时时间
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)//设置连接超时时间
            .build();

    ///异步post请求方法
    public static void post(String subUrl, String json, Callback callback)
    {
        System.err.println("OKtest");
        OkHttpClient client=new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(BASE_URL+subUrl)
                .post(body)
                .build();
        System.err.println("OKtest2");

        client.newCall(request).enqueue(callback);//callback中从request取数据
        System.err.println("OKtest3");
    }
    //异步直接传入JSONObject
    public static void post(String subUrl, JSONObject json, Callback callback)
    {
        System.err.println("OKtest");
        OkHttpClient client=new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, json.toString());
        Request request = new Request.Builder()
                .url(BASE_URL+subUrl)
                .post(body)
                .build();
        System.err.println("OKtest2");
        client.newCall(request).enqueue(callback);//callback中从request取数据
        System.err.println("OKtest3");
    }

    //表单提交//对应不要求RequestBody 的Controller方法
    public static void postForm(String subUrl, HashMap<String,String> paramsMap
            , Callback callback)
    {
        FormBody.Builder builder = new FormBody.Builder();
        for (String key : paramsMap.keySet()) {
            //追加表单信息
            Log.i("OKHTTP,paramsMap",key+":"+paramsMap.get(key));
            builder.add(key, paramsMap.get(key));
        }
        OkHttpClient client=new OkHttpClient();
        RequestBody body=builder.build();
        Request request = new Request.Builder()
                .url(BASE_URL+subUrl)
                .post(body)
                .build();
        client.newCall(request).enqueue(callback);//callback中从request取数据
    }


//    //同步的post请求方法
//    public static String post(String subUrl, String json) throws IOException {
//        RequestBody body = RequestBody.create(JSON, json);
//        System.out.println( body.toString());
//
//        Request request = new Request.Builder()
//                .url(BASE_URL_TEST+subUrl)
//                .post(body)
//                .build();
//        System.out.println( request.toString());
//        Response response = client.newCall(request).execute();
//        if (response.isSuccessful()) {
//            return response.body().string();
//        } else {
//            throw new IOException("Unexpected code " + response);
//        }
//    }
//

}
