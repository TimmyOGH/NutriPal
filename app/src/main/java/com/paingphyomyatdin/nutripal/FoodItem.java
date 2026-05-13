package com.paingphyomyatdin.nutripal;

public class FoodItem {

    public String name;
    public double caloriesPer100g;
    public double protein;
    public double carbs;
    public double fat;

    public FoodItem(String name, double caloriesPer100g, double protein, double carbs, double fat) {
        this.name = name;
        this.caloriesPer100g = caloriesPer100g;
        this.protein = protein;
        this.carbs = carbs;
        this.fat = fat;
    }
}