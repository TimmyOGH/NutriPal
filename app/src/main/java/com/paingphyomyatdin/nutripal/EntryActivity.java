package com.paingphyomyatdin.nutripal;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class EntryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

        DatabaseHelper db = new DatabaseHelper(this);

        // Skip if user exists
        if (db.hasUser()) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
            return;
        }

        // Start from Entry screen if not
        findViewById(R.id.getStartedBtn).setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileSetupActivity.class));
        });
    }
}