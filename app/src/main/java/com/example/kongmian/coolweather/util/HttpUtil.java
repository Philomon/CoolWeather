package com.example.kongmian.coolweather.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by kongmian on 2017/9/20.
 */


public class HttpUtil {


    public static void sendOkHttpRequset(String adress,okhttp3.Callback callback){
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(adress).build();
        okHttpClient.newCall(request).enqueue(callback);

    }




}
