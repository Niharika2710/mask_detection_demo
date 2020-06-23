package com.baidu.paddle.lite.demo.mask_detection;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class DetectorActivity extends AppCompatActivity {

    ImageView imageView;
    TextView textView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.detector_view);
        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);



      /*  long ctx = 0;
        String savedText = "";
        Object img = new Object();

        Native.visitorDetails(ctx, img, savedText);
        Log.i("DetectorActivity", savedText);*/
    }

    public void displayImage(Bitmap bitmap) {
        Log.i("DetectorActivity", "Image received");
        imageView.setImageBitmap(bitmap);
    }
}
