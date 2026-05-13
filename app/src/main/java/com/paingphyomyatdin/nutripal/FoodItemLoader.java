package com.paingphyomyatdin.nutripal;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class FoodItemLoader {

    private static List<FoodItem> foodList;

    // Method to load food items list
    public static void load(Context context) {

        if (foodList != null) return;

        foodList = new ArrayList<>();

        try {
            InputStream is = context.getAssets().open("food_data.json");
            int size = is.available();

            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            String json = new String(buffer, "UTF-8");

            JSONArray array = new JSONArray(json);

            for (int i = 0; i < array.length(); i++) {

                JSONObject obj = array.getJSONObject(i);

                FoodItem item = new FoodItem(
                        obj.getString("name"),
                        obj.getDouble("caloriesPer100g"),
                        obj.getDouble("protein"),
                        obj.getDouble("carbs"),
                        obj.getDouble("fat")
                );

                foodList.add(item);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to get food item by index
    public static FoodItem getByIndex(int index) {
        if (foodList == null || index >= foodList.size()) return null;
        return foodList.get(index);
    }
}