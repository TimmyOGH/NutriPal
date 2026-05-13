package com.paingphyomyatdin.nutripal;

import java.util.ArrayList;
import java.util.List;

public class CaptureSessionManager {

    private static final List<CapturedFoodResult> sessionResults = new ArrayList<>();

    public static void addResult(CapturedFoodResult result) {
        sessionResults.add(result);
    }

    public static List<CapturedFoodResult> getResults() {
        return sessionResults;
    }

    public static void clearSession() {
        sessionResults.clear();
    }

    public static int getSessionCount() {
        return sessionResults.size();
    }

    public static double getTotalCalories() {

        double total = 0;

        for (CapturedFoodResult item : sessionResults) {
            total += item.calories;
        }

        return total;
    }
}