package com.example.demo.model.menu;

import com.example.demo.controller.core.SaveController;
import com.example.demo.engine.GameWorld;
import com.example.demo.model.state.achievement.AchievementData;
import com.example.demo.model.state.highscore.HighScoreState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


public class AchievementModel {
    private static final String ACHIEVEMENT_FILE = "src/main/resources/Saves/achievement.json";
    private static final Logger log = LoggerFactory.getLogger(AchievementModel.class);
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

    private void saveAchievements() {
        AchievementData data = new AchievementData(Achievements);
        SaveController.save(data, ACHIEVEMENT_FILE);
        log.info("Saved achievements to file:" + ACHIEVEMENT_FILE);
    }

    private void loadAchievements() {
        AchievementData data = SaveController.load(ACHIEVEMENT_FILE, AchievementData.class);

        if (data != null && data.getAchievements() != null) { // Nếu file tồn tại thif load
            Achievements = new ArrayList<>(data.getAchievements());
            log.info("Loaded {} achievements from file", Achievements.size());
        } else {
            Achievements = new ArrayList<>();
            Achievements.add(new Achievement("Win Level 1", false));  // false = chưa unlock
            Achievements.add(new Achievement("Win Level 2", false));
            Achievements.add(new Achievement("Win Game", false));
            Achievements.add(new Achievement("Easter Egg", false));

            saveAchievements();  // Lưu vào file
            log.info("Created new achievement file with default values");
        }
    }

    public boolean unlockAchievement(String achievementName) {
        for (Achievement achievement : Achievements) {
            if (achievement.getName().equals(achievementName)) {
                if (!achievement.isUnlocked()) {
                    achievement.setUnlocked(true);
                    saveAchievements();
                    log.info("🏆 Achievement unlocked: {}", achievementName);
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    public boolean isAchievementUnlocked(String achievementName) {
        for (Achievement achievement : Achievements) {
            if (achievement.getName().equals(achievementName)) {
                return achievement.isUnlocked();
            }
        }
        return false;
    }

    public void unlockWinLevel1() {
        unlockAchievement("Win Level 1");
    }

    public void unlockWinLevel2() {
        unlockAchievement("Win Level 2");
    }

    public void unlockWinGame() {
        unlockAchievement("Win Game");
    }

    public void unlockEasterEgg() {
        unlockAchievement("Easter Egg");
    }

    public List<Integer> getHighestScores() { return highScores; }
    public List<Achievement> getAchievements() { return Achievements; }
}
