package com.example.demo.model.utils;
import com.example.demo.view.ui.DialogueBox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class DialogueLoader {
    private StringBuilder txtLoader;
    private int position = 0;

    public DialogueLoader(String filepath){
        txtLoader = new StringBuilder();

        try (InputStream is = DialogueLoader.class.getResourceAsStream(filepath)){
            if (is == null) {
                System.err.println("script file not found at " + filepath);
                return;
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))){
                String line;
                while ((line = reader.readLine()) != null){
                    txtLoader.append(line).append("\n");
                }
            }

        } catch (IOException e) {
            System.err.println("Error loading script at " + filepath);
            e.printStackTrace();
        }
    }

    public String getNextValidLine(){
        while (position < txtLoader.length()){
            int endIndex = txtLoader.indexOf("\n",position);
            if (endIndex == -1) endIndex = txtLoader.length();

            String line = txtLoader.substring(position,endIndex);

            position = endIndex + 1;

            String trimmedline = line.trim();
            if (trimmedline.isEmpty() || trimmedline.startsWith("#")){
                continue;
            }

            return trimmedline;
        }
        return null;
    }

}

