package com.baidu.paddle.lite.demo.mask_detection;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

public class MonitorMainActivity extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore;
    private RecyclerView mFirestoreList;
    private FirestoreRecyclerAdapter firestoreRecyclerAdapter;
    private ImageButton analyticsBtn;
    private TextView textViolatorsNo;
    private ImageView warningImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getIntent();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor_main);

        firebaseFirestore = FirebaseFirestore.getInstance();

        analyticsBtn = findViewById(R.id.analyticsButton);
        textViolatorsNo = findViewById(R.id.violators_no_text);
        warningImage = findViewById(R.id.warningImage);
        mFirestoreList = findViewById(R.id.my_recycler_view);

        //Query
        Query query = firebaseFirestore.collection("User details");

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

            @Override
            protected void onBindViewHolder(@NonNull MonitorMainActivity.CovidSafeDetectorViewHolder holder, int position, @NonNull CovidSafeDetectorModel model) {
                holder.user_no.setText(model.getUser_no());
                holder.time.setText(model.getTimestamp());
                Picasso.get().load(model.getImage_url()).transform(new CircleTransform()).into(holder.imageview);
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

        public CovidSafeDetectorViewHolder(@NonNull View itemView) {
            super(itemView);

            user_no = itemView.findViewById(R.id.textViewName);
            time = itemView.findViewById(R.id.textViewVersion);
            imageview = itemView.findViewById(R.id.imageView);
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
