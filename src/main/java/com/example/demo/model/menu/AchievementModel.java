package com.example.demo.model.menu;

import com.example.demo.engine.GameWorld;
import com.example.demo.model.state.highscore.HighScoreState;

import java.util.ArrayList;
import java.util.List;


public class AchievementModel {
    private List<Integer> highScores ;
    private List<Achievement> Achievements;
    //GameWorld gameWorld;

    public AchievementModel(HighScoreState highScoreState) {
        loadHighestScores(highScoreState);
        loadAchievements();
        //this.gameWorld = gameWorld;
    }

    private void loadHighestScores(HighScoreState highScoreState) {
        highScores = highScoreState.getScoresDescending();
    }

    private void loadAchievements() {
        Achievements = List.of(
                new Achievement("Win Level 1",  checkWinLevel(1)),
                new Achievement("Win Level 2",  checkWinLevel(2)),
                new Achievement("Win Game",     checkWinGame()),
                new Achievement("Easter Egg",   checkEasterEgg())
        );
    }

    private boolean checkEasterEgg() {
        return true;
    }

    private boolean checkWinGame() {
        return true;
    }

    private boolean checkWinLevel(int i) {
        return true ;// gameWorld.getCurrentLevel() > i;
    }

    public List<Integer> getHighestScores() { return highScores; }
    public List<Achievement> getAchievements() { return Achievements; }
}
