package com.example.demo.model.utils;
import java.util.Random;

public class GameRandom {
    private static final Random RANDOM = new Random();
    public static int nextInt(int bound) { return RANDOM.nextInt(bound); }
    public static double nextDouble() { return RANDOM.nextDouble(); }
}

