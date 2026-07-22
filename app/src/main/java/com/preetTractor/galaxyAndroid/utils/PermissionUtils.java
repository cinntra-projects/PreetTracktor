package com.preetTractor.galaxyAndroid.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionUtils {

    private static final int PERMISSION_REQUEST_CODE = 10111;

    private static final int PERMISSION_CODE = 100;

    public static boolean checkPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE);
                return false;
            }
        }
        return true;
    }

    public static void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                                  @NonNull int[] grantResults, OnPermissionResultListener listener) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (listener != null) {
                    listener.onPermissionGranted();
                }
            } else {
                if (listener != null) {
                    listener.onPermissionDenied();
                }
            }
        }
    }

    public interface OnPermissionResultListener {
        void onPermissionGranted();
        void onPermissionDenied();
    }

    // add code  --->  25-09-2024 (Tarun Sharma)

    public static void requestCameraAndPhotoPermissions(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                // Request both permissions for Photos and Videos, and Camera
                ActivityCompat.requestPermissions(activity, new String[]{
                        Manifest.permission.READ_MEDIA_IMAGES,
                        Manifest.permission.CAMERA
                }, PERMISSION_CODE);
            }
        } else {
            // For Android 12 and below
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                // Request both permissions for Photos and Videos, and Camera
                ActivityCompat.requestPermissions(activity, new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                }, PERMISSION_CODE);
            }
        }
    }

    public static void handlePermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, Activity activity) {
        if (requestCode == PERMISSION_CODE) {
            boolean cameraGranted = false;
            boolean storageGranted = false;

            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    if (permissions[i].equals(Manifest.permission.CAMERA)) {
                        cameraGranted = true;
                    } else if (permissions[i].equals(Manifest.permission.READ_MEDIA_IMAGES) ||
                            permissions[i].equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        storageGranted = true;
                    }
                }
            }

        }
    }
}

