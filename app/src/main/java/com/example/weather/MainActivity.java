package com.example.weather;

import android.graphics.Color;
import android.os.Build;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.weather.util.HttpUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setStatusBarColor(Color.parseColor("#FFFFFF"));
        setContentView(R.layout.activity_main);

    }
}
