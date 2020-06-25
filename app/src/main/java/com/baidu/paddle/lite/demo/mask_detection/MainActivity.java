package com.baidu.paddle.lite.demo.mask_detection;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.baidu.paddle.lite.demo.common.Utils;

import java.io.File;


public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String faceImagePath = Utils.getDCIMDirectory() + File.separator + "covidsafe/";
        Log.i("MainActivity", faceImagePath);
        findViewById(R.id.main_vision_click_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, DetectorMainActivity.class));
            }
        });

       /* findViewById(R.id.main_vision_click_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, DetectorActivity.class
                ));
            }
        });*/
        findViewById(R.id.main_nlp_click_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, MonitorMainActivity.class));
            }
        });
    }
}
