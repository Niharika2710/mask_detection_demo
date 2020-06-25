package com.baidu.paddle.lite.demo.mask_detection;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baidu.paddle.lite.demo.common.CircleTransform;
import com.baidu.paddle.lite.demo.common.CovidSafeDetectorModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MonitorMainActivity extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore;
    private RecyclerView mFirestoreList;
    private FirestoreRecyclerAdapter firestoreRecyclerAdapter;
    private ImageButton analyticsBtn;
    private ImageView warningImage;
    private FirebaseFirestore db;
    private TextView textViolatorsNo;
    int count = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getIntent();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor_main);

        firebaseFirestore = FirebaseFirestore.getInstance();

        analyticsBtn = findViewById(R.id.analyticsButton);
        // warningImage = findViewById(R.id.warningImage);
        textViolatorsNo = findViewById(R.id.violators_no_text);
        mFirestoreList = findViewById(R.id.my_recycler_view);

        //Query
        Query query = firebaseFirestore.collection("Mask Detection");

        //Document Reference
        db = FirebaseFirestore.getInstance();

        //get all the sub collections where mask is not on
        db.collection("Mask Detection")
                .whereEqualTo("maskOnOrNot", "0")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                count++;
                                Log.i("MonitorMainActivity", document.getId() + " => " + document.getData());
                                Log.i("MonitorMainActivity", "Violators are : " + count);
                            }
                            Log.i("MonitorMainActivity", "Violators now" + count);
                            textViolatorsNo.setText("You have " + count + " violators today!");
                        } else {
                            Log.d("MonitorMainActivity", "Error getting documents: ", task.getException());
                        }
                    }
                });


        //RecyclerView Options
        FirestoreRecyclerOptions<CovidSafeDetectorModel> details = new FirestoreRecyclerOptions.Builder<CovidSafeDetectorModel>()
                .setQuery(query, CovidSafeDetectorModel.class)
                .build();


        firestoreRecyclerAdapter = new FirestoreRecyclerAdapter<CovidSafeDetectorModel, MonitorMainActivity.CovidSafeDetectorViewHolder>(details) {
            @NonNull
            @Override
            public MonitorMainActivity.CovidSafeDetectorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout, parent, false);
                return new MonitorMainActivity.CovidSafeDetectorViewHolder(view);
            }

            @SuppressLint("SetTextI18n")
            @Override
            protected void onBindViewHolder(@NonNull MonitorMainActivity.CovidSafeDetectorViewHolder holder, int position, @NonNull CovidSafeDetectorModel model) {
                holder.user_no.setText(model.getUser_no() + " " + String.valueOf(position + 1));
                holder.time.setText(model.getTimestamp());
                Picasso.get().load(model.getImage_url()).transform(new CircleTransform()).into(holder.imageview);
                if (model.getMaskOnOrNot().equals("0")) {
                    holder.imageWithNoMask.setVisibility(View.VISIBLE);
                    holder.imageWithMask.setVisibility(View.GONE);
                } else {
                    holder.imageWithNoMask.setVisibility(View.GONE);
                    holder.imageWithMask.setVisibility(View.VISIBLE);
                }
            }
        };
        mFirestoreList.setHasFixedSize(true);
        mFirestoreList.setLayoutManager(new LinearLayoutManager(this));
        mFirestoreList.setAdapter(firestoreRecyclerAdapter);

        analyticsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MonitorMainActivity.this, AnalyticsActivity.class));
            }
        });
    }

    private class CovidSafeDetectorViewHolder extends RecyclerView.ViewHolder {

        private TextView user_no;
        private TextView time;
        private ImageView imageview;
        private ImageView imageWithMask;
        private ImageView imageWithNoMask;

        public CovidSafeDetectorViewHolder(@NonNull View itemView) {
            super(itemView);

            user_no = itemView.findViewById(R.id.textViewName);
            time = itemView.findViewById(R.id.textViewVersion);
            imageview = itemView.findViewById(R.id.imageView);
            imageWithMask = itemView.findViewById(R.id.face_with_mask);
            imageWithNoMask = itemView.findViewById(R.id.face_with_no_mask);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        firestoreRecyclerAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        firestoreRecyclerAdapter.stopListening();
    }

}
