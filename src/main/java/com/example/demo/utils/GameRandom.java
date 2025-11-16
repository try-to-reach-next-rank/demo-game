package com.example.demo.utils;
import java.util.Random;

public class GameRandom {
    private static final Random RANDOM = new Random();
    public static boolean nextBoolean() { return RANDOM.nextBoolean(); }
    public static int nextInt(int bound) { return RANDOM.nextInt(bound); }
    public static int nextInt(int from, int to) { return RANDOM.nextInt(from, to); }
    public static double nextDouble() { return RANDOM.nextDouble(); }
    public static double nextDouble(double from, double to) { return RANDOM.nextDouble(from, to); }
}
