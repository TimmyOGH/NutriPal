USER GUIDE

1. System Overview

NutriPal is an Android mobile app that estimates calorie and macronutrient values from food images using an on-device deep learning model. Users can take pictures of their food, choose portion sizes, and track daily intake through analytics and history features.

Main features:
o	Food Image Classification
o	Portion-based calorie estimation
o	Macronutrient breakdown
o	Daily intake tracking
o	Personalized calorie goals
o	Offline functionality


2. Prerequisites

Before the application is run, the following items should be available:

o	Android Studio installed
o	Android SDK configured
o	Physical Android device (Android 8.0+ recommended)
o	Project source code opened in Android Studio
o	Internet connection (first-time Gradle sync only)


3. Running the Application

Step 1:
	Open the NutriPal project in Android Studio.
Step 2:
	Allow Gradle to sync automatically.
Step 3:
	Connect an Android device.
Step 4:
	Click “Run” in Android Studio.
Step 5:
	Grant camera permission when prompted.

Then, the application will launch automatically.


4. Using the Application

4.1 Profile Setup

When opening the app for the first time:

1.	Enter name
2.	Select age
3.	Select weight
4.	Select height
5.	Choose calorie goal
6.	Tap “Finish Setup”

4.2 Food Recognition

1.	Tap the capture button on the Home screen
2.	Take a photo of food
3.	Select portion size
4.	View estimated calories and nutrients
5.	Tap “Save”

4.3 Analytics

Open the Analytics tab to view:

o	Intake insights
o	Daily calorie progress
o	Weekly calorie trends
o	Macronutrient distribution

4.4 Profile Editing

Open the Profile tab and tap the edit icon to update user details.


5. Troubleshooting

If the camera does not open: Check camera permission is enabled in device settings. 
If the model does not classify correctly: Retake the image under better lighting with a clearer view of the food item.
If the application does not run: Ensure Gradle sync completed successfully before launching.
