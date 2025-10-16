package com.example.demo.model.utils;

public final class GlobalVar {
    // Private constructor to prevent initialization
    private GlobalVar() {}

    public static final int     WIDTH       = 600;
    public static final int     HEIGHT      = 800;
    public static final int     BOTTOM_EDGE = HEIGHT;

    public static final double  FPS         = 60.0;
    public static final String  SECRET_CODE = "PHUC";
    public static final String  SAVE_FILE_NAME = "src/main/resources/Saves/gamestate.json";
}