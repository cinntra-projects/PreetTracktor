package com.preetTractor.galaxyAndroid.utils;

import android.app.Activity;
import android.content.Intent;
import android.provider.MediaStore;

public class GalleryUtils {

    public static void openGallery(Activity activity, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        activity.startActivityForResult(Intent.createChooser(intent, "Select Picture"), requestCode);
    }
}

