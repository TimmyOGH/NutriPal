package com.paingphyomyatdin.nutripal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "nutripal.db";
    private static final int DATABASE_VERSION = 1;

    // User table
    public static final String TABLE_USER = "User";
    public static final String USER_ID = "userId";
    public static final String USER_NAME = "name";
    public static final String USER_AGE = "age";
    public static final String USER_WEIGHT = "weight";
    public static final String USER_HEIGHT = "height";
    public static final String USER_GOAL = "dailyCalorieGoal";
    public static final String USER_JOINED_DATE = "userJoinedDate";
    public static final String USER_PROFILE_IMAGE = "profile_image";

    // Food record table
    public static final String TABLE_RECORD = "FoodRecord";
    public static final String RECORD_ID = "recordId";
    public static final String RECORD_USER_ID = "userId";
    public static final String RECORD_FOOD_NAME = "foodName";
    public static final String RECORD_PROTEIN = "protein";
    public static final String RECORD_CARBS = "carbs";
    public static final String RECORD_FAT = "fat";
    public static final String RECORD_DATETIME = "dateTime";
    public static final String RECORD_PORTION = "portionSize";
    public static final String RECORD_CALORIES = "calories";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // User table
        String createUser = "CREATE TABLE " + TABLE_USER + " ("
                + USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + USER_NAME + " TEXT, "
                + USER_AGE + " INTEGER, "
                + USER_WEIGHT + " REAL, "
                + USER_HEIGHT + " REAL, "
                + USER_GOAL + " REAL, "
                + USER_JOINED_DATE + " TEXT, "
                + USER_PROFILE_IMAGE + " TEXT"
                + ")";

        // Food record table
        String createRecord = "CREATE TABLE " + TABLE_RECORD + " ("
                + RECORD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + RECORD_USER_ID + " INTEGER, "
                + RECORD_FOOD_NAME + " TEXT, "
                + RECORD_DATETIME + " TEXT, "
                + RECORD_PORTION + " REAL, "
                + RECORD_CALORIES + " REAL, "
                + RECORD_PROTEIN + " REAL, "
                + RECORD_CARBS + " REAL, "
                + RECORD_FAT + " REAL, "
                + "FOREIGN KEY(" + RECORD_USER_ID + ") REFERENCES " + TABLE_USER + "(" + USER_ID + ")"
                + ")";

        db.execSQL(createUser);
        db.execSQL(createRecord);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECORD);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate(db);
    }

    // Insert user
    public long insertUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(USER_NAME, user.name);
        values.put(USER_AGE, user.age);
        values.put(USER_WEIGHT, user.weight);
        values.put(USER_HEIGHT, user.height);
        values.put(USER_GOAL, user.dailyCalorieGoal);
        values.put(USER_JOINED_DATE, user.joinedDate);

        return db.insert(TABLE_USER, null, values);
    }

    // Check user existence
    public boolean hasUser() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USER + " LIMIT 1", null);

        boolean exists = cursor.moveToFirst();
        cursor.close();

        return exists;
    }

    // Retrieve user
    public User getUser() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USER + " LIMIT 1", null);

        if (cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(USER_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(USER_NAME));
            int age = cursor.getInt(cursor.getColumnIndexOrThrow(USER_AGE));
            double weight = cursor.getDouble(cursor.getColumnIndexOrThrow(USER_WEIGHT));
            double height = cursor.getDouble(cursor.getColumnIndexOrThrow(USER_HEIGHT));
            double goal = cursor.getDouble(cursor.getColumnIndexOrThrow(USER_GOAL));
            String joined = cursor.getString(cursor.getColumnIndexOrThrow(USER_JOINED_DATE));
            String profileImage = cursor.getString(cursor.getColumnIndexOrThrow(USER_PROFILE_IMAGE));

            cursor.close();
            return new User(id, name, age, weight, height, goal, joined, profileImage);
        }

        cursor.close();
        return null;
    }

    // Update profile image
    public void updateProfileImage(String uri) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(USER_PROFILE_IMAGE, uri);

        db.update(TABLE_USER, values, null, null);
    }

    // Update user
    public void updateUser(int age, int weight, int height, int goal) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(USER_AGE, age);
        values.put(USER_WEIGHT, weight);
        values.put(USER_HEIGHT, height);
        values.put(USER_GOAL, goal);

        db.update(TABLE_USER, values, null, null);
    }

    // Insert food record
    public long insertFoodRecord(int userId, String foodName, String dateTime,
                                 double portionSize, double calories,
                                 double protein, double carbs, double fat) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(RECORD_USER_ID, userId);
        values.put(RECORD_FOOD_NAME, foodName);
        values.put(RECORD_DATETIME, dateTime);
        values.put(RECORD_PORTION, portionSize);
        values.put(RECORD_CALORIES, calories);
        values.put(RECORD_PROTEIN, protein);
        values.put(RECORD_CARBS, carbs);
        values.put(RECORD_FAT, fat);

        return db.insert(TABLE_RECORD, null, values);
    }

    // Get all food records
    public List<HistoryItem> getAllFoodRecords() {
        List<HistoryItem> historyList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT " + RECORD_FOOD_NAME + ", " + RECORD_CALORIES + ", " +
                RECORD_DATETIME + " FROM " + TABLE_RECORD + " ORDER BY " + RECORD_ID + " DESC";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                String foodName = cursor.getString(cursor.getColumnIndexOrThrow(RECORD_FOOD_NAME));
                double calories = cursor.getDouble(cursor.getColumnIndexOrThrow(RECORD_CALORIES));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(RECORD_DATETIME));

                historyList.add(new HistoryItem(formatFoodName(foodName), Math.round(calories)
                        + " cal", date));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return historyList;
    }

    // Format food name
    private String formatFoodName(String foodName) {
        String[] parts = foodName.split("_");
        StringBuilder builder = new StringBuilder();

        for (String part : parts) {
            if (part.length() > 0) {
                builder.append(Character.toUpperCase(part.charAt(0)))
                        .append(part.substring(1))
                        .append(" ");
            }
        }

        return builder.toString().trim();
    }

    // Method to get analytics for today
    public AnalyticsData getTodayAnalytics() {
        SQLiteDatabase db = this.getReadableDatabase();

        String todayPrefix = new java.text.SimpleDateFormat("d MMM yyyy", java.util.Locale.getDefault())
                .format(java.util.Calendar.getInstance().getTime());

        Cursor summaryCursor = db.rawQuery(
                "SELECT " +
                        "IFNULL(SUM(" + RECORD_CALORIES + "), 0), " +
                        "COUNT(*), " +
                        "IFNULL(AVG(" + RECORD_CALORIES + "), 0) " +
                        "FROM " + TABLE_RECORD + " " +
                        "WHERE " + RECORD_DATETIME + " LIKE ?",
                new String[]{todayPrefix + "%"}
        );

        double totalCalories = 0;
        int foodCount = 0;
        double averageCalories = 0;

        if (summaryCursor.moveToFirst()) {
            totalCalories = summaryCursor.getDouble(0);
            foodCount = summaryCursor.getInt(1);
            averageCalories = summaryCursor.getDouble(2);
        }
        summaryCursor.close();

        Cursor highestCursor = db.rawQuery(
                "SELECT " + RECORD_FOOD_NAME +
                        " FROM " + TABLE_RECORD +
                        " WHERE " + RECORD_DATETIME + " LIKE ?" +
                        " ORDER BY " + RECORD_CALORIES + " DESC LIMIT 1", new String[]{todayPrefix + "%"}
        );

        String highestFoodName = "--";
        if (highestCursor.moveToFirst()) {
            highestFoodName = formatFoodName(highestCursor.getString(0));
        }
        highestCursor.close();

        User user = getUser();
        double goalCalories = user != null ? user.dailyCalorieGoal : 0;
        int progressPercent = goalCalories > 0
                ? (int) Math.min(100, Math.round((totalCalories / goalCalories) * 100)) : 0;

        return new AnalyticsData(totalCalories, foodCount, averageCalories, highestFoodName,
                goalCalories, progressPercent);
    }

    // Method to get analytics for this week
    public AnalyticsData getWeekAnalytics() {
        SQLiteDatabase db = this.getReadableDatabase();

        java.util.Calendar calendar = java.util.Calendar.getInstance();
        java.text.SimpleDateFormat formatter =
                new java.text.SimpleDateFormat("d MMM yyyy", java.util.Locale.getDefault());

        double totalCalories = 0;
        int foodCount = 0;
        String highestFoodName = "--";
        double highestCalories = -1;

        for (int i = 0; i < 7; i++) {
            String dayPrefix = formatter.format(calendar.getTime());

            Cursor dayCursor = db.rawQuery(
                    "SELECT " +
                            RECORD_FOOD_NAME + ", " +
                            RECORD_CALORIES +
                            " FROM " + TABLE_RECORD +
                            " WHERE " + RECORD_DATETIME + " LIKE ?", new String[]{dayPrefix + "%"}
            );

            while (dayCursor.moveToNext()) {
                String foodName = dayCursor.getString(0);
                double calories = dayCursor.getDouble(1);

                totalCalories += calories;
                foodCount++;

                if (calories > highestCalories) {
                    highestCalories = calories;
                    highestFoodName = formatFoodName(foodName);
                }
            }

            dayCursor.close();
            calendar.add(java.util.Calendar.DAY_OF_YEAR, -1);
        }

        double averageCalories = foodCount > 0 ? totalCalories / foodCount : 0;

        User user = getUser();
        double goalCalories = user != null ? user.dailyCalorieGoal * 7 : 0;
        int progressPercent = goalCalories > 0
                ? (int) Math.min(100, Math.round((totalCalories / goalCalories) * 100)) : 0;

        return new AnalyticsData(totalCalories, foodCount, averageCalories, highestFoodName,
                goalCalories, progressPercent);
    }

    // Method to get calories from last 7 days
    public float[] getLast7DaysCalories() {

        float[] data = new float[7];

        SQLiteDatabase db = this.getReadableDatabase();

        java.util.Calendar calendar = java.util.Calendar.getInstance();
        java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("d MMM yyyy",
                java.util.Locale.getDefault());

        for (int i = 6; i >= 0; i--) {
            String dayPrefix = formatter.format(calendar.getTime());

            Cursor cursor = db.rawQuery(
                    "SELECT IFNULL(SUM(" + RECORD_CALORIES + "), 0) FROM " + TABLE_RECORD +
                            " WHERE " + RECORD_DATETIME + " LIKE ?", new String[]{dayPrefix + "%"});

            if (cursor.moveToFirst()) {
                data[i] = cursor.getFloat(0);
            }

            cursor.close();
            calendar.add(java.util.Calendar.DAY_OF_YEAR, -1);
        }

        return data;
    }

    // Method to get macronutrients totals
    public float[] getTotalMacros() {

        float[] macros = new float[3];

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT " +
                        "IFNULL(SUM(" + RECORD_PROTEIN + "), 0), " +
                        "IFNULL(SUM(" + RECORD_CARBS + "), 0), " +
                        "IFNULL(SUM(" + RECORD_FAT + "), 0) " +
                        "FROM " + TABLE_RECORD,
                null
        );

        if (cursor.moveToFirst()) {
            macros[0] = cursor.getFloat(0);
            macros[1] = cursor.getFloat(1);
            macros[2] = cursor.getFloat(2);
        }

        cursor.close();
        return macros;
    }
}