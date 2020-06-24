package com.baidu.paddle.lite.demo.mask_detection;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AnalyticsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getIntent();
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_analytics);
    }
}
