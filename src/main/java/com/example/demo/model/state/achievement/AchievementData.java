package com.example.demo.model.state.achievement;

import com.example.demo.model.menu.Achievement;

import java.util.List;

public class AchievementData {
    private List<Achievement> achievements;

    public AchievementData() {
    }

    public AchievementData(List<Achievement> achievements) {
        this.achievements = achievements;
    }

    public List<Achievement> getAchievements() {
        return achievements;
    }

    public void setAchievements(List<Achievement> achievements) {
        this.achievements = achievements;
    }
}