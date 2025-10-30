package com.example.demo.model.utils.var;

public final class GlobalVar {
    // Private constructor to prevent initialization
    private GlobalVar() {}

    public static final int     WIDTH       = 600;
    public static final int     HEIGHT      = 600;
    public static final int     BOTTOM_EDGE = HEIGHT;

    public static final double  FPS         = 60.0;
    public static final String  SECRET_CODE = "PHUC";
    public static final String SETTINGS_FILE_PATH = "src/main/resources/Saves/settings.json";

    // SAVE
    public static final int SAVE_SLOT_1 = 1;
    public static final int SAVE_SLOT_2 = 2;
    public static final String SAVE_INVALID_SLOT_MSG = "Invalid slot number: ";
}