package com.baidu.paddle.lite.demo.mask_detection;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.baidu.paddle.lite.demo.common.CovidSafeDetectorModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DetectorActivity extends AppCompatActivity {

    private Button imageView;
    private TextView textView;
    private StorageReference mStorageReference;
    private FirebaseFirestore db;
    public Uri imgUri;
    private static final int CAMERA_REQUEST_CODE = 1;
    private List<CovidSafeDetectorModel> modelArrayList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getIntent();
        super.onCreate(savedInstanceState);

        setContentView(R.layout.detector_view);
        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);

        mStorageReference = FirebaseStorage.getInstance().getReference("Images");
        // Initializing firestore object
        db = FirebaseFirestore.getInstance();

      /*  long ctx = 0;
        String savedText = "";
        Object img = new Object();

        Native.visitorDetails(ctx, img, savedText);
        Log.i("DetectorActivity", savedText);*/

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/'");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, CAMERA_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            //imgUri = data.getData();
            Fileuploader(data.getData());
        }
    }

    public void displayImage(Bitmap bitmap) {
        Log.i("DetectorActivity", "Image received");
        // imageView.setImageBitmap(bitmap);
    }

    private void Fileuploader(Uri imgUri) {

       /* File filepath = new File(Environment.getExternalStorageDirectory() + "/PyTorchDemoApp/sample.png");

        File compressedImageFile = new Compressor.Builder(this)
                .setMaxWidth(640)
                .setMaxHeight(480)
                .setQuality(90)
                .setCompressFormat(Bitmap.CompressFormat.PNG)
                .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES).getAbsolutePath())
                .build()
                .compressToFile(filepath);

        imgUri = Uri.fromFile(compressedImageFile);
        Log.i("CameraXfromFileUpload ", imgUri.toString());*/

        final StorageReference Ref = mStorageReference.child(System.currentTimeMillis() + ".png");

        Ref.putFile(imgUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        Ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Log.i("DetectorActivity", uri.toString());
                                saveUrlToFirestore(uri.toString());
                                return;
                            }
                        });
                        Log.i("DetectorActivity", "Upload success!!");
                        Toast.makeText(DetectorActivity.this, "Upload finish!!", Toast.LENGTH_LONG).show();
                        return;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                    }
                });
    }

    private void saveUrlToFirestore(String downloadUrl) {

        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
        String date = df.format(Calendar.getInstance().getTime());
        final String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        //  String docKey = "Visitor" + Integer.toString(Constants.visitor_no + 1);

        final CovidSafeDetectorModel covidSafeDetectorModel =
                new CovidSafeDetectorModel("Visitor"
                        ,
                        downloadUrl,
                        date,
                        "0");

        final Map<String, Object> docData = new HashMap<>();
        docData.put("user_no", covidSafeDetectorModel.getUser_no());
        docData.put("timestamp", date);
        docData.put("image_url", downloadUrl);
        docData.put("maskOnOrNot", covidSafeDetectorModel.getMaskOnOrNot());

        final ArrayList<Map<String, Object>> list = new ArrayList<>();

    /*    db.collection("mask_detection")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            if (documentSnapshot.getId().equals("2020-06-01")) {
                                list.add(documentSnapshot.getData());

                            }
                        }
                    }
                });
*/

    }
}
