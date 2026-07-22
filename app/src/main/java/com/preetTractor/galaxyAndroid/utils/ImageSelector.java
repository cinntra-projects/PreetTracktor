package com.preetTractor.galaxyAndroid.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

public class ImageSelector {

    private static final int REQUEST_IMAGE_SELECTOR = 1765332;

    public void openImageSelector(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        activity.startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_IMAGE_SELECTOR);
    }

    // Call this method in onActivityResult of the activity to get the selected image URI
    public Uri getImageUriFromResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_SELECTOR && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                // If intent's data is null, no image selected
                return null;
            } else {
                return data.getData();
            }
        }
        return null;
    }
}

