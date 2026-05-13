package com.paingphyomyatdin.nutripal;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.components.Legend;

import androidx.appcompat.app.AppCompatActivity;

public class AnalyticsActivity extends AppCompatActivity {

    LinearLayout homeIconLayout, profileIconLayout;

    TextView timeDropdown, caloriesValue, foodsValue, avgValue, highestValue, progressText,
            recommendationText;
    android.widget.ProgressBar progressBar;
    RecommendationService recommendationService;

    boolean isToday = true;
    LineChart weeklyChart;
    PieChart macrosPieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);

        timeDropdown = findViewById(R.id.timeDropdown);

        caloriesValue = findViewById(R.id.caloriesValue);
        foodsValue = findViewById(R.id.foodsValue);
        avgValue = findViewById(R.id.avgValue);
        highestValue = findViewById(R.id.highestValue);
        progressText = findViewById(R.id.progressText);
        progressBar = findViewById(R.id.progressBar);

        recommendationText = findViewById(R.id.recommendationText);
        recommendationService = new RecommendationService();

        timeDropdown.setKeyListener(null);
        timeDropdown.setFocusable(false);
        timeDropdown.setClickable(true);
        timeDropdown.setCursorVisible(false);

        timeDropdown.setOnClickListener(v -> toggleDropdown());

        weeklyChart = findViewById(R.id.weeklyChart);
        setupWeeklyChart();

        macrosPieChart = findViewById(R.id.macrosPieChart);
        setupMacrosPieChart();

        loadAnalytics();

        // Go to Home
        homeIconLayout = findViewById(R.id.homeIconLayout);
        homeIconLayout.setOnClickListener(v -> {
            Intent intent = new Intent(AnalyticsActivity.this, HomeActivity.class);
            startActivity(intent);
        });

        // Go to Profile
        profileIconLayout = findViewById(R.id.profileIconLayout);
        profileIconLayout.setOnClickListener(v -> {
            Intent intent = new Intent(AnalyticsActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }

    // Method to toggle drop down / up
    private void toggleDropdown() {
        isToday = !isToday;

        if (isToday) {
            timeDropdown.setText("Today");
            timeDropdown.setCompoundDrawablesWithIntrinsicBounds(
                    0, 0, R.drawable.drop_down_arrow, 0
            );
        } else {
            timeDropdown.setText("This Week");
            timeDropdown.setCompoundDrawablesWithIntrinsicBounds(
                    0, 0, R.drawable.drop_up_arrow, 0
            );
        }

        loadAnalytics();
    }

    // Method to load today / this week analytics
    private void loadAnalytics() {
        DatabaseHelper db = new DatabaseHelper(this);
        AnalyticsData data;

        if (isToday) {
            data = db.getTodayAnalytics();
        } else {
            data = db.getWeekAnalytics();
        }

        caloriesValue.setText(Math.round(data.totalCalories) + " cal");
        foodsValue.setText(String.valueOf(data.foodCount));
        avgValue.setText(Math.round(data.averageCalories) + " cal");
        highestValue.setText(data.highestFoodName);

        // Progress bar info
        progressText.setText(Math.round(data.totalCalories) + " / " + Math.round(data.goalCalories) + " cal");
        progressBar.setProgress(data.progressPercent);

        recommendationText.setText(recommendationService
                .getAnalyticsRecommendation(isToday, data.totalCalories, data.goalCalories));
    }

    // Method to set up weekly line chart
    private void setupWeeklyChart() {

        DatabaseHelper db = new DatabaseHelper(this);

        float[] calories = db.getLast7DaysCalories();

        // Check empty state
        boolean hasData = false;

        for (float value : calories) {
            if (value > 0) {
                hasData = true;
                break;
            }
        }

        if (!hasData) {
            weeklyChart.clear();
            weeklyChart.setNoDataText("No calorie records this week yet");
            weeklyChart.setNoDataTextColor(android.graphics.Color.GRAY);
            return;
        }

        java.util.ArrayList<Entry> entries = new java.util.ArrayList<>();

        for (int i = 0; i < calories.length; i++) {
            entries.add(new Entry(i, calories[i]));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Calories");

        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setValueTextSize(10f);
        dataSet.setColor(android.graphics.Color.BLACK);
        dataSet.setCircleColor(android.graphics.Color.BLACK);
        dataSet.setHighLightColor(android.graphics.Color.parseColor("#4CAF50"));
        dataSet.setDrawFilled(false);

        LineData lineData = new LineData(dataSet);

        weeklyChart.setData(lineData);

        String[] days = getLast7DayLabels();

        XAxis xAxis = weeklyChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(days));
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        weeklyChart.getAxisRight().setEnabled(false);
        weeklyChart.getDescription().setEnabled(false);

        weeklyChart.invalidate();
    }

    // Method to get last 7day labels
    private String[] getLast7DayLabels() {

        String[] labels = new String[7];

        java.util.Calendar calendar = java.util.Calendar.getInstance();
        java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("EEE",
                java.util.Locale.getDefault());

        for (int i = 6; i >= 0; i--) {
            labels[i] = formatter.format(calendar.getTime());
            calendar.add(java.util.Calendar.DAY_OF_YEAR, -1);
        }

        return labels;
    }

    // Method to set up macros pie chart
    private void setupMacrosPieChart() {

        DatabaseHelper db = new DatabaseHelper(this);
        float[] macros = db.getTotalMacros();

        // Check empty state
        if (macros[0] == 0 && macros[1] == 0 && macros[2] == 0) {
            macrosPieChart.clear();
            macrosPieChart.setNoDataText("No macro data yet");
            return;
        }

        java.util.ArrayList<PieEntry> entries = new java.util.ArrayList<>();

        if (macros[0] > 0) entries.add(new PieEntry(macros[0], "Protein"));
        if (macros[1] > 0) entries.add(new PieEntry(macros[1], "Carbs"));
        if (macros[2] > 0) entries.add(new PieEntry(macros[2], "Fat"));

        PieDataSet dataSet = new PieDataSet(entries, "");

        java.util.ArrayList<Integer> colors = new java.util.ArrayList<>();
        colors.add(android.graphics.Color.parseColor("#00bf63"));
        colors.add(android.graphics.Color.parseColor("#7ed957"));
        colors.add(android.graphics.Color.parseColor("#b0e867"));

        dataSet.setColors(colors);
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(android.graphics.Color.BLACK);

        PieData pieData = new PieData(dataSet);

        macrosPieChart.setData(pieData);
        macrosPieChart.getDescription().setEnabled(false);
        macrosPieChart.setDrawHoleEnabled(true);
        macrosPieChart.setHoleRadius(45f);
        macrosPieChart.setTransparentCircleRadius(50f);
        macrosPieChart.setCenterText("Macros");
        macrosPieChart.setCenterTextSize(16f);

        Legend legend = macrosPieChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);

        macrosPieChart.invalidate();
    }
}