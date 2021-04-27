package com.singlemethodgames.wordcurve.screens.difficulty;

public class SpeedDifficulty {
    private int currentCorrect;
    private SpeedSetting speedSetting;
    private boolean increaseDifficulty;
    private SpeedSetting startSetting;

    public SpeedDifficulty(int currentCorrect, int start) {
        this(currentCorrect, start, true);
    }

    public SpeedDifficulty(int currentCorrect, int start, boolean increaseDifficulty) {
        this.currentCorrect = currentCorrect;
        this.increaseDifficulty = increaseDifficulty;
        this.speedSetting = getSettingForCorrect(currentCorrect);
        this.startSetting = getSettingForCorrect(start);
    }

    private static SpeedSetting getSettingForCorrect(int currentCorrect) {
        SpeedSetting speedSetting;
        if(currentCorrect >= SpeedSetting.ONE.start && currentCorrect < SpeedSetting.ONE.end) {
            speedSetting = SpeedSetting.ONE;
        } else if (currentCorrect >= SpeedSetting.TWO.start && currentCorrect < SpeedSetting.TWO.end) {
            speedSetting = SpeedSetting.TWO;
        } else if (currentCorrect >= SpeedSetting.THREE.start && currentCorrect < SpeedSetting.THREE.end) {
            speedSetting = SpeedSetting.THREE;
        } else if (currentCorrect >= SpeedSetting.FOUR.start && currentCorrect < SpeedSetting.FOUR.end) {
            speedSetting = SpeedSetting.FOUR;
        } else {
            speedSetting = SpeedSetting.FIVE;
        }

        return speedSetting;
    }

    public void correctAnswer() {
        if(increaseDifficulty) {
            currentCorrect++;
            this.speedSetting = getSettingForCorrect(currentCorrect);
        }
    }

    public void incorrectAnswer() {
        if(increaseDifficulty) {
            if (currentCorrect > 0) {
                currentCorrect--;
            }
            this.speedSetting = getSettingForCorrect(currentCorrect);
        }
    }

    public float getSpeed() {
        return speedSetting.speed;
    }

    public int getCurrentCorrect() {
        return currentCorrect;
    }

    public void setCurrentCorrect(int currentCorrect) {
        this.currentCorrect = currentCorrect;
        this.speedSetting = getSettingForCorrect(currentCorrect);
        this.startSetting = getSettingForCorrect(currentCorrect);
    }

    public int getPoints() {
        return speedSetting.points;
    }

    public SpeedSetting getSpeedSetting() {
        return speedSetting;
    }

    public int getStart() {
        return startSetting.start;
    }
}
