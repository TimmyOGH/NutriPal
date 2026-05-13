package com.paingphyomyatdin.nutripal;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ProfileSetupActivity extends AppCompatActivity {

    TextView questionLabel;
    TextInputEditText nameInput;
    AutoCompleteTextView dropdownInput;

    TextInputLayout nameLayout;
    TextInputLayout dropdownLayout;

    ImageView nextBtn, backBtn;
    LinearLayout dotsLayout;

    int currentStep = 0;
    final int totalSteps = 5;

    String userName;
    int userAge = -1;
    int userWeight = -1;
    int userHeight = -1;
    int userGoalCalories = 2100;

    TextView goalValueText;
    TextView goalTypeText;
    View goalContainer;
    com.google.android.material.slider.Slider goalSlider;
    MaterialButton finishBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);

        // Initialize elements
        questionLabel = findViewById(R.id.questionLabel);
        nameInput = findViewById(R.id.nameInput);
        dropdownInput = findViewById(R.id.dropdownInput);

        nameLayout = findViewById(R.id.nameLayout);
        dropdownLayout = findViewById(R.id.dropdownLayout);

        nextBtn = findViewById(R.id.nextBtn);
        backBtn = findViewById(R.id.backBtn);
        dotsLayout = findViewById(R.id.dotsLayout);

        goalContainer = findViewById(R.id.goalContainer);
        goalSlider = findViewById(R.id.goalSlider);
        goalValueText = findViewById(R.id.goalValueText);
        goalTypeText = findViewById(R.id.goalTypeText);
        finishBtn = findViewById(R.id.finishBtn);

        setupNameFilter();
        loadStep();

        goalSlider.addOnChangeListener((slider, value, fromUser) -> {
            userGoalCalories = (int) value;
            updateGoalUI(userGoalCalories);
        });

        nextBtn.setOnClickListener(v -> handleNext());
        backBtn.setOnClickListener(v -> handleBack());
        finishBtn.setOnClickListener(v -> {

            String joinedDate = new java.text.SimpleDateFormat("d MMM yyyy",
                    java.util.Locale.getDefault()).format(new java.util.Date());

            User user = new User(userName, userAge, userWeight,
                    userHeight, userGoalCalories, joinedDate, "");

            DatabaseHelper db = new DatabaseHelper(this);
            long result = db.insertUser(user);

            if (result != -1) {

                Snackbar.make(v, "Profile setup complete!", Snackbar.LENGTH_SHORT).show();

                new Handler().postDelayed(() -> {
                    Intent intent = new Intent(this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }, 1500);

            } else {
                Snackbar.make(v, "Something went wrong!", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    // Restrict name (letters & spaces only)
    private void setupNameFilter() {
        nameInput.setFilters(new InputFilter[]{(
                source, start, end,
                dest, dstart, dend) -> {
            if (source.equals("")) return source;
            if (!source.toString().matches("[a-zA-Z ]+")) return "";
            return null;
        }});
    }

    // Method to load current step
    private void loadStep() {

        nameLayout.setVisibility(View.GONE);
        dropdownLayout.setVisibility(View.GONE);
        goalContainer.setVisibility(View.GONE);

        finishBtn.setVisibility(View.GONE);
        nextBtn.setVisibility(View.VISIBLE);

        // Back button visibility
        backBtn.setVisibility(currentStep == 0 ? View.INVISIBLE : View.VISIBLE);

        updateDots();

        switch (currentStep) {

            // Name
            case 0:
                questionLabel.setText("What’s your name?");
                nameLayout.setHint("Enter your name");

                nameLayout.setVisibility(View.VISIBLE);
                break;

            // Age
            case 1:
                questionLabel.setText("How old are you?");
                dropdownLayout.setHint("Select your age");
                dropdownLayout.setVisibility(View.VISIBLE);

                int minAge = 18;
                int maxAge = 100;

                String[] ages = new String[maxAge - minAge + 1];
                for (int i = minAge; i <= maxAge; i++) {
                    ages[i - minAge] = String.valueOf(i);
                }

                ArrayAdapter<String> ageAdapter = new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_dropdown_item_1line,
                        ages
                );

                dropdownInput.setAdapter(ageAdapter);

                // Restore selection
                if (userAge != -1) {
                    dropdownInput.setText(String.valueOf(userAge), false);
                } else {
                    dropdownInput.setText("", false);
                }

                break;

            // Weight
            case 2:
                questionLabel.setText("What’s your weight?");
                dropdownLayout.setHint("Select your weight (kg)");
                dropdownLayout.setVisibility(View.VISIBLE);

                int minWeight = 35;
                int maxWeight = 180;

                String[] weights = new String[maxWeight - minWeight + 1];
                for (int i = minWeight; i <= maxWeight; i++) {
                    weights[i - minWeight] = String.valueOf(i);
                }

                ArrayAdapter<String> weightAdapter = new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_dropdown_item_1line,
                        weights
                );

                dropdownInput.setAdapter(weightAdapter);

                // Restore selection
                if (userWeight != -1) {
                    dropdownInput.setText(userWeight + " kg", false);
                } else {
                    dropdownInput.setText("", false);
                }

                break;

            // Height
            case 3:
                questionLabel.setText("What’s your height?");
                dropdownLayout.setHint("Select your height (cm)");
                dropdownLayout.setVisibility(View.VISIBLE);

                int minHeight = 140;
                int maxHeight = 210;

                String[] heights = new String[maxHeight - minHeight + 1];
                for (int i = minHeight; i <= maxHeight; i++) {
                    heights[i - minHeight] = String.valueOf(i);
                }

                ArrayAdapter<String> heightAdapter = new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_dropdown_item_1line,
                        heights
                );

                dropdownInput.setAdapter(heightAdapter);

                // Restore selection
                if (userHeight != -1) {
                    dropdownInput.setText(userHeight + " cm", false);
                } else {
                    dropdownInput.setText("", false);
                }

                break;

            // Goal
            case 4:
                questionLabel.setText("What's your daily calorie goal?");

                goalContainer.setVisibility(View.VISIBLE);
                nextBtn.setVisibility(View.GONE);
                finishBtn.setVisibility(View.VISIBLE);

                // Restore value
                goalSlider.setValue(userGoalCalories);
                updateGoalUI(userGoalCalories);

                break;
        }
    }

    // Method to update pagination dots
    private void updateDots() {

        dotsLayout.removeAllViews();

        for (int i = 0; i < totalSteps; i++) {

            View dot = new View(this);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(20, 20);
            params.setMargins(8, 0, 8, 0);
            dot.setLayoutParams(params);

            if (i == currentStep) {
                dot.setBackgroundResource(R.drawable.dot_active);
            } else {
                dot.setBackgroundResource(R.drawable.dot_inactive);
            }

            dotsLayout.addView(dot);
        }
    }

    // Method to handle next button action
    private void handleNext() {

        switch (currentStep) {

            case 0:
                String name = nameInput.getText().toString().trim();
                if (name.isEmpty()) return;

                userName = name;
                currentStep++;
                loadStep();
                break;

            case 1:
                String ageStr = dropdownInput.getText().toString();
                if (ageStr.isEmpty()) return;

                userAge = Integer.parseInt(ageStr);
                currentStep++;
                loadStep();
                break;

            case 2:
                String weightStr = dropdownInput.getText().toString();
                if (weightStr.isEmpty()) return;

                weightStr = weightStr.replace(" kg", "");
                userWeight = Integer.parseInt(weightStr);

                currentStep++;
                loadStep();
                break;

            case 3:
                String heightStr = dropdownInput.getText().toString();
                if (heightStr.isEmpty()) return;

                heightStr = heightStr.replace(" cm", "");
                userHeight = Integer.parseInt(heightStr);

                currentStep++;
                loadStep();
                break;
        }
    }

    // Method to handle back button action
    private void handleBack() {
        if (currentStep > 0) {
            currentStep--;
            loadStep();
        }
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
}