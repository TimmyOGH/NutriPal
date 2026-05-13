package com.paingphyomyatdin.nutripal;

import android.os.Bundle;
import android.os.Handler;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.slider.Slider;

public class EditProfileActivity extends AppCompatActivity {

    AutoCompleteTextView ageDropdown, weightDropdown, heightDropdown;
    Slider goalSlider;
    TextView goalValueText, goalTypeText;
    MaterialButton updateBtn;
    ImageView backBtn;

    int userAge;
    int userWeight;
    int userHeight;
    int userGoal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(v -> finish());

        ageDropdown = findViewById(R.id.ageInput);
        weightDropdown = findViewById(R.id.weightInput);
        heightDropdown = findViewById(R.id.heightInput);

        goalSlider = findViewById(R.id.goalSlider);
        goalValueText = findViewById(R.id.goalValueText);
        goalTypeText = findViewById(R.id.goalTypeText);

        loadUserData();
        setupDropdowns();
        setupSlider();

        updateBtn = findViewById(R.id.updateBtn);
        updateBtn.setOnClickListener(v -> saveChanges());
    }

    // Method to load user data
    private void loadUserData() {
        DatabaseHelper db = new DatabaseHelper(this);
        User user = db.getUser();

        if (user != null) {
            userAge = user.age;
            userWeight = (int) user.weight;
            userHeight = (int) user.height;
            userGoal = (int) user.dailyCalorieGoal;

            ageDropdown.setText(String.valueOf(userAge), false);
            weightDropdown.setText(userWeight + " kg", false);
            heightDropdown.setText(userHeight + " cm", false);

            goalSlider.setValue(userGoal);
            updateGoalUI(userGoal);
        }
    }

    // Method to set up dropdowns
    private void setupDropdowns() {

        // Age
        String[] ages = new String[83];
        for (int i = 18; i <= 100; i++) {
            ages[i - 18] = String.valueOf(i);
        }
        ageDropdown.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                ages
        ));

        // Weight
        String[] weights = new String[146];
        for (int i = 35; i <= 180; i++) {
            weights[i - 35] = i + " kg";
        }
        weightDropdown.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                weights
        ));

        // Height
        String[] heights = new String[71];
        for (int i = 140; i <= 210; i++) {
            heights[i - 140] = i + " cm";
        }
        heightDropdown.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line,
                heights
        ));
    }

    // Method to set up slider
    private void setupSlider() {
        goalSlider.addOnChangeListener((slider, value, fromUser) -> {
            userGoal = (int) value;
            updateGoalUI(userGoal);
        });
    }

    // Method to update goal interface
    private void updateGoalUI(int calories) {
        goalValueText.setText(calories + " cal");

        if (calories < 2100) {
            goalTypeText.setText("Lose Weight");
        } else if (calories == 2100) {
            goalTypeText.setText("Maintain");
        } else {
            goalTypeText.setText("Gain Weight");
        }
    }

    // Method to update user data
    private void saveChanges() {

        String ageStr = ageDropdown.getText().toString();
        String weightStr = weightDropdown.getText().toString();
        String heightStr = heightDropdown.getText().toString();

        if (ageStr.isEmpty() || weightStr.isEmpty() || heightStr.isEmpty()) return;

        userAge = Integer.parseInt(ageStr);
        userWeight = Integer.parseInt(weightStr.replace(" kg", ""));
        userHeight = Integer.parseInt(heightStr.replace(" cm", ""));

        DatabaseHelper db = new DatabaseHelper(this);
        db.updateUser(userAge, userWeight, userHeight, userGoal);

        Toast.makeText(this, "Update successful", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(() -> finish(), 1500);
    }
}