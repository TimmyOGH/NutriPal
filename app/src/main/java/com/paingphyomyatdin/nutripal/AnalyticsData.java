package com.paingphyomyatdin.nutripal;

public class AnalyticsData {

    public double totalCalories;
    public int foodCount;
    public double averageCalories;
    public String highestFoodName;
    public double goalCalories;
    public int progressPercent;

    public AnalyticsData(double totalCalories, int foodCount, double averageCalories,
                         String highestFoodName, double goalCalories, int progressPercent) {
        this.totalCalories = totalCalories;
        this.foodCount = foodCount;
        this.averageCalories = averageCalories;
        this.highestFoodName = highestFoodName;
        this.goalCalories = goalCalories;
        this.progressPercent = progressPercent;
    }
}