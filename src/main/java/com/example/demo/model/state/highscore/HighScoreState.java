package com.example.demo.model.state.highscore;

import com.example.demo.utils.Input;
import com.example.demo.utils.var.GameVar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

public class HighScoreState {
    private static final int CAPACITY = GameVar.HIGHSCORE_CAPACITY;
    private final PriorityQueue<Integer> heap = new PriorityQueue<>();

    public HighScoreState() {
    }

    public void addScore(int score) {
        if (score <= 0) return; // bỏ điểm không hợp lệ
        if (heap.size() < CAPACITY) {
            heap.add(score);
        } else if (score > heap.peek()) { // Nếu đủ và điểm mới > phần tử nhỏ nhất hiện tại: loại phần tử nhỏ nhất rồi thêm.
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
