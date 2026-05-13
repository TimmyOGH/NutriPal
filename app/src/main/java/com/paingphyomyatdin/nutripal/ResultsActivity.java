package com.paingphyomyatdin.nutripal;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class ResultsActivity extends AppCompatActivity {

    ImageView backBtn, captureMoreBtn, capturedImage;

    TextView foodLabel, confidenceLabel;
    TextView caloriesText, proteinText, carbsText, fatText, recommendationText,
            sessionCountText, sessionCaloriesText;

    MaterialButton smallBtn, mediumBtn, largeBtn, saveBtn;

    FoodItem foodItem;

    User user;
    RecommendationService recommendationService;
    NutritionResult currentResult;
    boolean portionSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        // Initialize elements
        backBtn = findViewById(R.id.backBtn);
        captureMoreBtn = findViewById(R.id.captureMoreBtn);
        capturedImage = findViewById(R.id.capturedImage);
        foodLabel = findViewById(R.id.foodLabel);
        confidenceLabel = findViewById(R.id.confidenceLabel);
        caloriesText = findViewById(R.id.calories);
        proteinText = findViewById(R.id.protein);
        carbsText = findViewById(R.id.carbs);
        fatText = findViewById(R.id.fat);
        recommendationText = findViewById(R.id.recommendation);
        smallBtn = findViewById(R.id.smallBtn);
        mediumBtn = findViewById(R.id.mediumBtn);
        largeBtn = findViewById(R.id.largeBtn);
        saveBtn = findViewById(R.id.saveBtn);
        sessionCountText = findViewById(R.id.sessionCountText);
        sessionCaloriesText = findViewById(R.id.sessionCaloriesText);

        saveBtn.setEnabled(false);

        backBtn.setOnClickListener(v -> finish());
        captureMoreBtn.setOnClickListener(v -> {
            if (!portionSelected || currentResult == null) return;

            CapturedFoodResult result = buildCapturedResult();
            CaptureSessionManager.addResult(result);

            finish();
        });

        loadResults();

        // Update nutrition on click
        smallBtn.setOnClickListener(v -> updateNutrition(100));
        mediumBtn.setOnClickListener(v -> updateNutrition(225));
        largeBtn.setOnClickListener(v -> updateNutrition(400));

        DatabaseHelper db = new DatabaseHelper(this);
        user = db.getUser();

        recommendationService = new RecommendationService();

        saveBtn.setOnClickListener(v -> saveAllFoodRecords());
    }

    // Method to load results
    private void loadResults() {

        String imagePath = getIntent().getStringExtra("imagePath");
        int foodIndex = getIntent().getIntExtra("foodIndex", 0);
        float confidence = getIntent().getFloatExtra("confidence", 0f);

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        bitmap = rotateBitmap(imagePath, bitmap);
        capturedImage.setImageBitmap(bitmap);

        foodItem = FoodItemLoader.getByIndex(foodIndex);

        foodLabel.setText("Food Classified: " + foodItem.name);

        confidenceLabel.setText("Confidence: " + Math.round(confidence * 100) + "%");

        updateSessionSummary();

        // Hide labels
        caloriesText.setVisibility(View.GONE);
        proteinText.setVisibility(View.GONE);
        carbsText.setVisibility(View.GONE);
        fatText.setVisibility(View.GONE);
        recommendationText.setVisibility(View.GONE);
    }

    // Method to update nutrition results
    private void updateNutrition(int grams) {

        // Show labels
        caloriesText.setVisibility(View.VISIBLE);
        proteinText.setVisibility(View.VISIBLE);
        carbsText.setVisibility(View.VISIBLE);
        fatText.setVisibility(View.VISIBLE);
        recommendationText.setVisibility(View.VISIBLE);

        saveBtn.setEnabled(true);

        NutritionService service = new NutritionService();

        currentResult = service.calculateNutrition(foodItem, grams);
        portionSelected = true;

        caloriesText.setText("Estimated Calories: " + Math.round(currentResult.calories) + " cal");
        proteinText.setText("Protein: " + String.format("%.1f g", currentResult.protein));
        carbsText.setText("Carbohydrate: " + String.format("%.1f g", currentResult.carbs));
        fatText.setText("Fat: " + String.format("%.1f g", currentResult.fat));
        recommendationText.setText("Recommendation: " + recommendationService
                .getRecommendation(user, currentResult));

        updateSessionSummary();
    }

    // Method to rotate bitmap
    private Bitmap rotateBitmap(String imagePath, Bitmap bitmap) {

        try {
            androidx.exifinterface.media.ExifInterface exif = new androidx.exifinterface.media
                    .ExifInterface(imagePath);

            int orientation = exif.getAttributeInt(
                    androidx.exifinterface.media.ExifInterface.TAG_ORIENTATION,
                    androidx.exifinterface.media.ExifInterface.ORIENTATION_NORMAL
            );

            android.graphics.Matrix matrix = new android.graphics.Matrix();

            switch (orientation) {

                case androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.postRotate(90);
                    break;

                case androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.postRotate(180);
                    break;

                case androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_270:
                    matrix.postRotate(270);
                    break;

                default:
                    return bitmap;
            }

            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
                    matrix, true);

        } catch (Exception e) {
            e.printStackTrace();
            return bitmap;
        }
    }

    // Method to save food record
    private void saveAllFoodRecords() {

        if (!portionSelected || currentResult == null) return;

        // Add current result to session first
        CapturedFoodResult currentCaptured = buildCapturedResult();
        CaptureSessionManager.addResult(currentCaptured);

        DatabaseHelper db = new DatabaseHelper(this);

        long lastInsertResult = -1;

        for (CapturedFoodResult item : CaptureSessionManager.getResults()) {
            lastInsertResult = db.insertFoodRecord(user.userId, item.foodName, item.dateTime,
                    item.portionGrams, item.calories, item.protein, item.carbs, item.fat);
        }

        if (lastInsertResult != -1) {
            android.widget.Toast.makeText(this, "Saved successfully",
                    android.widget.Toast.LENGTH_SHORT).show();
            CaptureSessionManager.clearSession();
            finish();
        } else {
            android.widget.Toast.makeText(this, "Save failed",
                    android.widget.Toast.LENGTH_SHORT).show();
        }
    }

    // Method to create current captured result
    private CapturedFoodResult buildCapturedResult() {
        if (currentResult == null) return null;

        String dateTime = new java.text.SimpleDateFormat("d MMM yyyy, HH:mm",
                java.util.Locale.getDefault()).format(new java.util.Date());

        return new CapturedFoodResult(currentResult.foodName, currentResult.portionGrams,
                currentResult.calories, currentResult.protein, currentResult.carbs,
                currentResult.fat, dateTime);
    }

    // Method to update session info
    private void updateSessionSummary() {

        int count = CaptureSessionManager.getSessionCount();
        double calories = CaptureSessionManager.getTotalCalories();

        if (portionSelected && currentResult != null) {
            count += 1;
            calories += currentResult.calories;
        }

        sessionCountText.setText("Session items: " + count);
        sessionCaloriesText.setText("Session calories: " + Math.round(calories) + " cal");
    }
}