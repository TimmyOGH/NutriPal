package com.paingphyomyatdin.nutripal;

public class NutritionResult {

    public String foodName;
    public double portionGrams;
    public double calories;
    public double protein;
    public double carbs;
    public double fat;

    public NutritionResult(String foodName, double portionGrams, double calories,
                           double protein, double carbs, double fat) {
        this.foodName = foodName;
        this.portionGrams = portionGrams;
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.fat = fat;
    }
}