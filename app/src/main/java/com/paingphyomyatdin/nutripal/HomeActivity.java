package com.paingphyomyatdin.nutripal;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.util.concurrent.ExecutionException;

public class HomeActivity extends AppCompatActivity {

    private PreviewView previewView;
    private ImageCapture imageCapture;
    private static final int CAMERA_PERMISSION_CODE = 100;
    LinearLayout historyButton;
    LinearLayout profileIconLayout, analyticsIconLayout;

    private ImageClassifier classifier;
    private View loadingOverlay;
    private ImageView captureButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Go to Profile screen
        profileIconLayout = findViewById(R.id.profileIconLayout);
        profileIconLayout.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        // Go to Analytics screen
        analyticsIconLayout = findViewById(R.id.analyticsIconLayout);
        analyticsIconLayout.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, AnalyticsActivity.class);
            startActivity(intent);
        });

        previewView = findViewById(R.id.cameraPreview);
        captureButton = findViewById(R.id.captureButton);
        loadingOverlay = findViewById(R.id.loadingOverlay);

        if (checkCameraPermission()) {
            startCamera();
        } else {
            requestCameraPermission();
        }

        captureButton.setOnClickListener(v -> takePhoto());

        // Go to History screen
        historyButton = findViewById(R.id.historyLayout);
        historyButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, HistoryActivity.class);
            startActivity(intent);
        });

        // Load food list
        FoodItemLoader.load(this);

        classifier = new ImageClassifier(this);
    }

    // Method to check camera permission
    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED;
    }

    // Method to request camera permission
    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                CAMERA_PERMISSION_CODE
        );
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Check permissions then start camera
        if (requestCode == CAMERA_PERMISSION_CODE && grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            startCamera();
        }
    }

    // Method to start camera
    private void startCamera() {

        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {

                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                imageCapture = new ImageCapture.Builder().build();

                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                cameraProvider.unbindAll();

                cameraProvider.bindToLifecycle(this, cameraSelector, preview,
                        imageCapture);

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }

        }, ContextCompat.getMainExecutor(this));
    }

    // Method to take photo
    private void takePhoto() {

        if (imageCapture == null) return;

        File photoFile = new File(getCacheDir(), "captured_food.jpg");

        ImageCapture.OutputFileOptions outputOptions =
                new ImageCapture.OutputFileOptions.Builder(photoFile).build();

        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(this),
                new ImageCapture.OnImageSavedCallback() {

                    @Override
                    public void onImageSaved(
                            @NonNull ImageCapture.OutputFileResults outputFileResults) {

                        showLoading();

                        new Thread(() -> {
                            try {
                                Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                                bitmap = rotateBitmap(photoFile.getAbsolutePath(), bitmap);
                                bitmap = cropCenterSquare(bitmap);

                                ClassificationResult result = classifier.classifyImage(bitmap);

                                if (!result.isValid) {
                                    new Handler(Looper.getMainLooper()).post(() -> {
                                        hideLoading();
                                        Toast.makeText(HomeActivity.this,
                                                "Could not recognize a supported food. Try again with one clear food item.",
                                                Toast.LENGTH_LONG).show();
                                    });
                                    return;
                                }

                                new Handler(Looper.getMainLooper()).post(() -> {
                                    hideLoading();

                                    Intent intent = new Intent(HomeActivity.this, ResultsActivity.class);
                                    intent.putExtra("imagePath", photoFile.getAbsolutePath());
                                    intent.putExtra("foodIndex", result.index);
                                    intent.putExtra("confidence", result.confidence);
                                    startActivity(intent);
                                });

                            } catch (Exception e) {
                                e.printStackTrace();

                                new Handler(Looper.getMainLooper()).post(() -> {
                                    hideLoading();
                                    Toast.makeText(HomeActivity.this, "Classification failed",
                                            Toast.LENGTH_SHORT).show();
                                });
                            }
                        }).start();
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        hideLoading();
                        Toast.makeText(HomeActivity.this, "Capture failed",
                                Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    // Method to rotate Bitmap
    private Bitmap rotateBitmap(String imagePath, Bitmap bitmap) {
        try {
            androidx.exifinterface.media.ExifInterface exif =
                    new androidx.exifinterface.media.ExifInterface(imagePath);

            int orientation = exif.getAttributeInt(
                    androidx.exifinterface.media.ExifInterface.TAG_ORIENTATION,
                    androidx.exifinterface.media.ExifInterface.ORIENTATION_NORMAL
            );

            android.graphics.Matrix matrix = new android.graphics.Matrix();

            switch (orientation) {
                case androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.postRotate(90);
                    break;

                case androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.postRotate(180);
                    break;

                case androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_270:
                    matrix.postRotate(270);
                    break;

                default:
                    return bitmap;
            }

            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
                    matrix, true);

        } catch (Exception e) {
            e.printStackTrace();
            return bitmap;
        }
    }

    // Method to crop bitmap
    private Bitmap cropCenterSquare(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int size = Math.min(width, height);

        int x = (width - size) / 2;
        int y = (height - size) / 2;

        return Bitmap.createBitmap(bitmap, x, y, size, size);
    }

    // Method to show loading
    private void showLoading() {
        loadingOverlay.setVisibility(View.VISIBLE);
        captureButton.setEnabled(false);
    }

    // Method to hide loading
    private void hideLoading() {
        loadingOverlay.setVisibility(View.GONE);
        captureButton.setEnabled(true);
    }

    // Hide loading automatically
    @Override
    protected void onResume() {
        super.onResume();
        hideLoading();
    }
}