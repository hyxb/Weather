package com.example.weather.util;

import android.os.Build;
import android.view.textclassifier.TextLinks;
import androidx.annotation.RequiresApi;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class HttpUtil {
    /**
     * 传入请求地址，注册一个回调来处理服务器请求
     * @param address
     * @param callback
     */
    @RequiresApi(api = Build.VERSION_CODES.P)
    public static void sendOkHttpRequest(String address, okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }
}
