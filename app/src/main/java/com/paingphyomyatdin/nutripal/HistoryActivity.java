package com.paingphyomyatdin.nutripal;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    ImageView backBtn, analyticsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        backBtn = findViewById(R.id.backBtn);
        analyticsBtn = findViewById(R.id.analyticsBtn);

        backBtn.setOnClickListener(v -> finish());

        analyticsBtn.setOnClickListener(v -> {
            Intent intent = new Intent(HistoryActivity.this, AnalyticsActivity.class);
            startActivity(intent);
        });

        RecyclerView recyclerView = findViewById(R.id.historyRecycler);

        DatabaseHelper db = new DatabaseHelper(this);
        List<HistoryItem> list = db.getAllFoodRecords();

        HistoryAdapter adapter = new HistoryAdapter(list);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
}