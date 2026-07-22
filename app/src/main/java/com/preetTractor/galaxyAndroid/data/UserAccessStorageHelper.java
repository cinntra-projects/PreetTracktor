package com.preetTractor.galaxyAndroid.data;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.preetTractor.galaxyAndroid.data.UserAccessManagementModel.UserAccessManagementResponse;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class UserAccessStorageHelper {
    private static final String FILE_NAME = "user_access_management.json";

    // ✅ Save API response to a JSON file
    public static void saveJsonToFile(Context context, UserAccessManagementResponse response) {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String jsonResponse = gson.toJson(response);

            File file = new File(context.getFilesDir(), FILE_NAME);
            FileWriter writer = new FileWriter(file);
            writer.write(jsonResponse);
            writer.close();

            Log.d("FILE_SAVE", "Response saved successfully at: " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("FILE_ERROR", "Failed to save JSON: " + e.getMessage());
        }
    }

    // ✅ Read API response from a JSON file
    public static UserAccessManagementResponse readJsonFromFile(Context context) {
        try {
            File file = new File(context.getFilesDir(), FILE_NAME);
            if (!file.exists()) {
                Log.e("FILE_ERROR", "JSON file not found");
                return null;
            }

            FileReader reader = new FileReader(file);
            Gson gson = new Gson();
            UserAccessManagementResponse response = gson.fromJson(reader, UserAccessManagementResponse.class);
            reader.close();

            return response;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("FILE_ERROR", "Failed to read JSON: " + e.getMessage());
            return null;
        }
    }
}

