package com.baidu.paddle.lite.demo.mask_detection;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {

    private TextView welcomeText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        welcomeText = findViewById(R.id.welcome_page_app_title);


        Animation a = AnimationUtils.loadAnimation(this, R.anim.zoom_in);
        a.reset();
        welcomeText.clearAnimation();
        welcomeText.startAnimation(a);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
            }
        }, 2000);

    }
}
