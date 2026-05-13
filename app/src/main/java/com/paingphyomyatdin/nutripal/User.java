package com.paingphyomyatdin.nutripal;

public class User {
    public int userId;
    String name;
    int age;
    double weight;
    double height;
    double dailyCalorieGoal;
    String joinedDate;
    String profileImage;

    // Constructor
    public User(String name, int age, double weight, double height,
                double goal, String joinedDate, String profileImage) {
        this.name = name;
        this.age = age;
        this.weight = weight;
        this.height = height;
        this.dailyCalorieGoal = goal;
        this.joinedDate = joinedDate;
        this.profileImage = profileImage;
    }

    // Constructor with userId
    public User(int userId, String name, int age, double weight,
                double height, double goal, String joinedDate,
                String profileImage) {

        this.userId = userId;
        this.name = name;
        this.age = age;
        this.weight = weight;
        this.height = height;
        this.dailyCalorieGoal = goal;
        this.joinedDate = joinedDate;
        this.profileImage = profileImage;
    }

    public double getDailyCalorieGoal() {
        return dailyCalorieGoal;
    }

    public void setDailyCalorieGoal(double goal) {
        this.dailyCalorieGoal = goal;
    }

    public String getJoinedDate() {
        return joinedDate;
    }

    public void setJoinedDate(String joinedDate) {
        this.joinedDate = joinedDate;
    }
}