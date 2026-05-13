package com.paingphyomyatdin.nutripal;

public class CapturedFoodResult {
    public String foodName;
    public double portionGrams;
    public double calories;
    public double protein;
    public double carbs;
    public double fat;
    public String dateTime;

    // Constructor
    public CapturedFoodResult(String foodName, double portionGrams, double calories,
                              double protein, double carbs, double fat, String dateTime) {
        this.foodName = foodName;
        this.portionGrams = portionGrams;
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.fat = fat;
        this.dateTime = dateTime;
    }
}