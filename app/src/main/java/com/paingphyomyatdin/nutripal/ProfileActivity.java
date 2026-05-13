package com.paingphyomyatdin.nutripal;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {
    private static final int PICK_IMAGE = 1;

    TextView joinedDate, firstName, lastName, ageValue, weightValue, heightValue, goalValue;
    ImageView profileImage, plusIcon;
    LinearLayout editProfileLayout, analyticsIcon, homeIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Bind views
        plusIcon = findViewById(R.id.plusIcon);
        joinedDate = findViewById(R.id.joinedDate);
        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        ageValue = findViewById(R.id.ageValue);
        weightValue = findViewById(R.id.weightValue);
        heightValue = findViewById(R.id.heightValue);
        goalValue = findViewById(R.id.goalValue);
        analyticsIcon = findViewById(R.id.analyticsLayout);
        homeIcon = findViewById(R.id.homeLayout);

        // Choose profile picture from gallery
        profileImage = findViewById(R.id.profileImage);
        profileImage.setOnClickListener(v -> openGallery());

        loadUserData();

        // Go to Edit Profile
        editProfileLayout = findViewById(R.id.editProfileLayout);
        editProfileLayout.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });

        // Go to Analytics
        analyticsIcon.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, AnalyticsActivity.class);
            startActivity(intent);
        });

        // Go to Home
        homeIcon.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
            startActivity(intent);
        });
    }

    private void loadUserData() {
        DatabaseHelper db = new DatabaseHelper(this);
        User user = db.getUser();

        if (user != null) {

            // Split name
            String[] nameParts = user.name.split(" ", 2);
            firstName.setText(nameParts[0]);

            if (nameParts.length > 1) {
                lastName.setText(nameParts[1]);
            } else {
                lastName.setText("");
            }

            joinedDate.setText(user.joinedDate);
            ageValue.setText(String.valueOf(user.age));
            weightValue.setText(user.weight + " kg");
            heightValue.setText(user.height + " cm");
            goalValue.setText((int)user.dailyCalorieGoal + " cal");
        }

        if (user.profileImage != null && !user.profileImage.isEmpty()) {
            Uri uri = Uri.parse(user.profileImage);
            profileImage.setImageURI(uri);
            plusIcon.setVisibility(View.GONE);
        }
    }

    // Method to open gallery
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, PICK_IMAGE);
    }

    // Handle selected image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();

            // Persist permission
            getContentResolver().takePersistableUriPermission(imageUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION);

            profileImage.setImageURI(imageUri);
            plusIcon.setVisibility(View.GONE);

            // Save to database
            DatabaseHelper db = new DatabaseHelper(this);
            db.updateProfileImage(imageUri.toString());
        }
    }

    // Refresh Profile automatically
    @Override
    protected void onResume() {
        super.onResume();
        loadUserData();
    }
}