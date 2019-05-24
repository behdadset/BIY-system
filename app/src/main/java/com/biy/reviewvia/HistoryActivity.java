package com.biy.reviewvia;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private HistoryAdapter mAdapter;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        mProgressBar = findViewById(R.id.progressBar);

        mRecyclerView = findViewById(R.id.recent_view_product);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        getData();
    }

    private void getData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("reviews")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot document = task.getResult();
                            if (document != null && !document.isEmpty()) {
                                List<Review> data = new ArrayList<>();
                                for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                    Log.d("Firestore", snapshot.getId() + " => " + snapshot.getData());
                                    Gson gson = new Gson();
                                    String jsonString = gson.toJson(snapshot.getData());
                                    Review review = gson.fromJson(jsonString, Review.class);
                                    data.add(review);
                                }
                                if (!data.isEmpty()) {
                                    mAdapter = new HistoryAdapter(HistoryActivity.this, data);
                                    mRecyclerView.setAdapter(mAdapter);
                                }
                            } else {
                                Log.d("Firestore", "Collection is empty");
                            }


                        } else {
                            Log.w("Firestore", "Error getting documents.", task.getException());
                        }

                        mProgressBar.setVisibility(View.GONE);
                        mRecyclerView.setVisibility(View.VISIBLE);
                    }
                });
    }
}
