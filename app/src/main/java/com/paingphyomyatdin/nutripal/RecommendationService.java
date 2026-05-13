package com.paingphyomyatdin.nutripal;

import java.util.HashMap;
import java.util.Map;

public class RecommendationService {

    private final Map<String, String> recommendationMap = new HashMap<>();

    // Constructor
    public RecommendationService() {

        // Lose weight
        recommendationMap.put("lose_light_highProtein",
                "Fits your goal well. Light calories with good protein.");
        recommendationMap.put("lose_light_lowProtein",
                "Low in calories, but add more protein for better balance.");
        recommendationMap.put("lose_medium_highProtein",
                "Reasonable portion. Good protein for a filling meal.");
        recommendationMap.put("lose_medium_lowProtein",
                "Moderate calories. Keep later meals lighter if needed.");
        recommendationMap.put("lose_heavy",
                "Quite heavy for your goal. Consider a smaller portion next time.");

        // Maintain
        recommendationMap.put("maintain_light",
                "Light option. Add a side if you need more energy.");
        recommendationMap.put("maintain_medium_highProtein",
                "Balanced choice for your daily goal.");
        recommendationMap.put("maintain_medium_lowProtein",
                "Moderate calories. Protein could be a bit higher.");
        recommendationMap.put("maintain_heavy",
                "Heavier meal. Balance with lighter foods later.");

        // Gain weight
        recommendationMap.put("gain_light",
                "A bit light for your goal. Add more calories if needed.");
        recommendationMap.put("gain_medium",
                "Good portion for steady progress toward your goal.");
        recommendationMap.put("gain_heavy_highProtein",
                "Strong option for your goal with solid energy intake.");
        recommendationMap.put("gain_heavy_lowProtein",
                "High calories, but more protein would improve balance.");
    }

    // Method to get recommendation
    public String getRecommendation(User user, NutritionResult result) {

        double mealRatio = result.calories / user.dailyCalorieGoal;
        String goalType = getGoalType(user.dailyCalorieGoal);

        boolean highProtein = result.protein >= 15.0;
        boolean lowProtein = result.protein < 10.0;

        // Lose
        if (goalType.equals("lose")) {
            if (mealRatio < 0.15) {
                return highProtein
                        ? recommendationMap.get("lose_light_highProtein")
                        : recommendationMap.get("lose_light_lowProtein");
            } else if (mealRatio <= 0.30) {
                return highProtein
                        ? recommendationMap.get("lose_medium_highProtein")
                        : recommendationMap.get("lose_medium_lowProtein");
            } else {
                return recommendationMap.get("lose_heavy");
            }
        }

        // Maintain
        if (goalType.equals("maintain")) {
            if (mealRatio < 0.15) {
                return recommendationMap.get("maintain_light");
            } else if (mealRatio <= 0.30) {
                return highProtein
                        ? recommendationMap.get("maintain_medium_highProtein")
                        : recommendationMap.get("maintain_medium_lowProtein");
            } else {
                return recommendationMap.get("maintain_heavy");
            }
        }

        // Gain
        if (mealRatio < 0.15) {
            return recommendationMap.get("gain_light");
        } else if (mealRatio <= 0.30) {
            return recommendationMap.get("gain_medium");
        } else {
            return lowProtein
                    ? recommendationMap.get("gain_heavy_lowProtein")
                    : recommendationMap.get("gain_heavy_highProtein");
        }
    }

    // Method to get goal type
    private String getGoalType(double dailyGoal) {
        if (dailyGoal < 2100) {
            return "lose";
        } else if (dailyGoal == 2100) {
            return "maintain";
        } else {
            return "gain";
        }
    }

    // Method to get analytics recommendation
    public String getAnalyticsRecommendation(boolean isToday, double consumed, double goal) {

        if (goal == 0) {
            return "Set a calorie goal to receive insights.";
        }

        double percent = (consumed / goal) * 100;

        if (isToday) {
            if (percent == 0) {
                return "Start logging meals to track today's intake.";
            } else if (percent < 40) {
                return "You're below your goal. Consider a balanced meal.";
            } else if (percent < 80) {
                return "You're on track. Maintain steady portions.";
            } else if (percent <= 100) {
                return "Close to your goal. Choose lighter options next.";
            } else {
                return "You've exceeded today's goal. Balance later meals.";
            }
        } else {
            if (percent == 0) {
                return "No meals logged this week yet.";
            } else if (percent < 40) {
                return "Weekly intake is low. Stay consistent each day.";
            } else if (percent < 80) {
                return "You're progressing well this week.";
            } else if (percent <= 100) {
                return "Approaching weekly goal. Maintain balance.";
            } else {
                return "Weekly intake exceeded goal. Adjust upcoming meals.";
            }
        }
    }
}