package com.example.weather.util;

import android.os.Build;
import android.view.textclassifier.TextLinks;
import androidx.annotation.RequiresApi;
import okhttp3.CacheControl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class HttpUtil {
    /**
     * 传入请求地址，注册一个回调来处理服务器请求
     * @param address
     * @param callback
     */
    @RequiresApi(api = Build.VERSION_CODES.P)
    public static void sendOkHttpRequest(String address, okhttp3.Callback callback){
        LogUtil.logInfo("发送HttpRequest");
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .cacheControl(new CacheControl.Builder().noStore().build())
                .url(address)
                .build();
        client.newCall(request).enqueue(callback);
//        try {
//            Response response = client.newCall(request).execute();
//            System.out.println(response.toString());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }
}
