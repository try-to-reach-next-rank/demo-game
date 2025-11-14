package com.example.demo.model.state.highscore;

import com.example.demo.controller.core.GameController;
import com.example.demo.controller.core.SaveController;
import com.example.demo.utils.var.GameVar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

public class HighScoreState {
    private static final int CAPACITY = GameVar.HIGHSCORE_CAPACITY;
    private static HighScoreState instance;
    private final PriorityQueue<Integer> heap = new PriorityQueue<>();
    private static final String path = "src/main/resources/Saves/highscores.json";

    private HighScoreState() {
    }

    public static HighScoreState getInstance() {
        if(instance == null){
            return SaveController.load(path, HighScoreState.class);
        }
        return instance;
    }

    //Method để reset instance (dùng khi cần reload từ file)
    public static void resetInstance() {
        instance = null;
    }

    public void addScore(int score) {
        if (score <= 0) return;

        // Kiểm tra xem điểm đã có trong heap chưa
        if (heap.contains(score)) {
            return;
        }
        if (heap.size() < CAPACITY) {
            heap.add(score);
        } else if (score > heap.peek()) {
            heap.poll();
            heap.add(score);
        }
    }

    public List<Integer> getScoresDescending() {
        List<Integer> list = new ArrayList<>(heap);
        list.sort(Collections.reverseOrder());
        return list;
    }

    public PriorityQueue<Integer> getHeap() {
        return heap;
    }

    public int size() {
        return heap.size();
    }

    public boolean isFull() {
        return heap.size() >= CAPACITY;
    }

    public Integer getMinCurrent() {
        return heap.peek();
    }
}
