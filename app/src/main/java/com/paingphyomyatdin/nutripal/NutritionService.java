package com.paingphyomyatdin.nutripal;

public class NutritionService {

    // Method to calculate nutrition
    public NutritionResult calculateNutrition(FoodItem foodItem, double portionGrams) {

        double factor = portionGrams / 100.0;

        double calories = foodItem.caloriesPer100g * factor;
        double protein = foodItem.protein * factor;
        double carbs = foodItem.carbs * factor;
        double fat = foodItem.fat * factor;

        return new NutritionResult(foodItem.name, portionGrams, calories, protein, carbs, fat);
    }
}