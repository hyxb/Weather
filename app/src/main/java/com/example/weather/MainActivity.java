package com.example.weather;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.weather.util.HttpUtil;
import com.example.weather.util.LogUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences preferences = getSharedPreferences("Weather", Context.MODE_PRIVATE);
        LogUtil.logError("preferences is :"+preferences.getString("weather",null));
        if (preferences.getString("weather",null) != null){
            Intent intent = new Intent(MainActivity.this, WeatherActivity.class);
            startActivity(intent);
            MainActivity.this.finish();
            LogUtil.logError("MainActivity is Finish!!!");
        }else{
            LogUtil.logError("weather is not found");
        }

    }
}
