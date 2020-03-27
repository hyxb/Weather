package com.example.weather;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.weather.gson.Forecast;
import com.example.weather.gson.Weather;
import com.example.weather.service.AutoUpdateService;
import com.example.weather.util.HttpUtil;
import com.example.weather.util.LogUtil;
import com.example.weather.util.Utility;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.w3c.dom.Text;

import java.io.IOException;

public class WeatherActivity extends AppCompatActivity {
    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleupdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;
    private LinearLayout forecastLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageButton cityChoose_btn;
    private Intent intent;

    private String weather_id;
    private String key = "1952b7328e884f1a875dc4d6a09c2cd2";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= 21){
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            LogUtil.logError("状态栏设置透明");

        }

        setContentView(R.layout.activity_weather);
        init();
        LogUtil.logError("WeatherActivity 开启，初始化数据");

        SharedPreferences prefs = getSharedPreferences("Weather",Context.MODE_PRIVATE);
        final String weatherJsonString = prefs.getString("weather", null);
        weather_id = getIntent().getStringExtra("weather_id");

        if (weather_id != null){
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weather_id);
        }else if (weatherJsonString != null){
            Weather weather = Utility.handleWeatherResponse(weatherJsonString);
            showWeatherInfo(weather);
            weather_id = weather.basic.weatherId;
        }

//        if (weatherJsonString != null){
//            Weather weather = Utility.handleWeatherResponse(weatherJsonString);
//            showWeatherInfo(weather);
//            weather_id = weather.basic.weatherId;
//        } else {
//            weatherLayout.setVisibility(View.INVISIBLE);
//            requestWeather(weather_id);
//        }

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.sw_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(weather_id);
            }
        });
    }

    public void requestWeather(final String weatherId){
        String weatherUrl = "http://guolin.tech/api/weather?cityid="+weatherId+"&key="+key;
        LogUtil.logError("URL IS :"+weatherUrl);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(WeatherActivity.this,"无法获取天气信息",Toast.LENGTH_SHORT).show();
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String responseText = response.body().string();
                    LogUtil.logError("on Respnse:responseText ->"+responseText);
                    final Weather weather = Utility.handleWeatherResponse(responseText);
                    LogUtil.logError("on Response:weather->"+weather.toString());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (weather != null && "ok".equals(weather.status)){
                                SharedPreferences.Editor editor = getSharedPreferences("Weather", Context.MODE_PRIVATE).edit();
                                editor.putString("weather",responseText);
                                editor.apply();
                                showWeatherInfo(weather);
                            }else{
                                Toast.makeText(WeatherActivity.this,"获取天气信息失败", Toast.LENGTH_SHORT).show();
                            }
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
                }
            });
        }
    }


    private void init(){
        titleCity = (TextView)findViewById(R.id.title_city);
        titleupdateTime = (TextView)findViewById(R.id.title_update_time);
        degreeText = (TextView)findViewById(R.id.degree_text);
        weatherInfoText = (TextView)findViewById(R.id.weather_info_text);
        aqiText = (TextView)findViewById(R.id.aqi_text);
        pm25Text = (TextView)findViewById(R.id.pm25_text);
        comfortText = (TextView)findViewById(R.id.comfort_text);
        carWashText = (TextView)findViewById(R.id.car_wash_text);
        sportText = (TextView)findViewById(R.id.sport_text);
        forecastLayout = (LinearLayout)findViewById(R.id.forecast_layout);
        weatherLayout = (ScrollView)findViewById(R.id.weather_layout);
        cityChoose_btn = (ImageButton)findViewById(R.id.city_choose_btn);

        cityChoose_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(WeatherActivity.this, "选择城市", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void showWeatherInfo(Weather weather){
        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature + "°C";
        String weatherInfo = weather.now.more.info;

        titleCity.setText(cityName);
        titleupdateTime.setText(updateTime);
        weatherInfoText.setText(weatherInfo);
        degreeText.setText(degree);

        forecastLayout.removeAllViews();
        for (Forecast forecast : weather.forecasts) {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);
            TextView dateText = (TextView) view.findViewById(R.id.date_text);
            TextView infoText = (TextView) view.findViewById(R.id.info_text);
            TextView maxText = (TextView) view.findViewById(R.id.max_text);
            TextView minText = (TextView) view.findViewById(R.id.min_text);
            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max);
            minText.setText(forecast.temperature.min);
            forecastLayout.addView(view);
        }

        if (weather.aqi != null){
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }

        String comfort = "舒适度："+weather.suggestion.comfort.info;
        String carWash = "洗车指数"+weather.suggestion.carWash.info;
        String sport = "运动建议"+weather.suggestion.sport.info;
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);
        intent = new Intent(this, AutoUpdateService.class);
        startService(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(intent);
    }
}
