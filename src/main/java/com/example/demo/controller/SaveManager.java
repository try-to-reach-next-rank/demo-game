package com.example.demo.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SaveManager {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void save(Object data, String fileName){
        try{
            Path path = Paths.get(fileName);
            String jsonString = gson.toJson(data);

            Files.writeString(path, jsonString);

            System.out.println("Successfully saved data to " + fileName);
        } catch (Exception e) {
            System.err.println("Error: Failed to save data to " + fileName);
            e.printStackTrace();
        }
    }

    public static <T> T load(String fileName, Class<T> classType) {
        try {
            Path path = Paths.get(fileName);

            if (!Files.exists(path)) {
                System.out.println("Save file not found: " + fileName + ". Returning null.");
                return null;
            }

            String jsonString = Files.readString(path);

            T loadedObject = gson.fromJson(jsonString, classType);

            System.out.println("Successfully loaded data from " + fileName);
            return loadedObject;
        } catch (Exception e) {
            System.err.println("Error: Failed to load data from " + fileName);
            e.printStackTrace();
            return null;
        }
    }
}
