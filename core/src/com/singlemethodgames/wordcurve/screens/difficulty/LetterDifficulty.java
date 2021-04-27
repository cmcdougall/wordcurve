package com.singlemethodgames.wordcurve.screens.difficulty;

import com.badlogic.gdx.math.MathUtils;

public class LetterDifficulty {
    private int currentCorrect;
    private int currentDelay;
    private LetterSettings letterSettings;
    private LetterSettings startSettings;
    private boolean increaseDifficulty;

    public LetterDifficulty(int currentCorrect, int start) {
        this(currentCorrect, start, true);
    }

    private LetterDifficulty(int currentCorrect, int start, boolean increaseDifficulty) {
        this.currentCorrect = currentCorrect;
        this.increaseDifficulty = increaseDifficulty;
        this.currentDelay = 0;
        this.letterSettings = getSettingForCorrect(currentCorrect);
        this.startSettings = getSettingForCorrect(start);
    }

    public static LetterSettings getSettingForCorrect(int currentCorrect) {
        LetterSettings letterSettings;
        if(currentCorrect >= LetterSettings.LETTERS.start && currentCorrect < LetterSettings.LETTERS.end) {
            letterSettings = LetterSettings.LETTERS;
        } else if (currentCorrect >= LetterSettings.DISAPPEARING_LETTERS.start && currentCorrect < LetterSettings.DISAPPEARING_LETTERS.end) {
            letterSettings = LetterSettings.DISAPPEARING_LETTERS;
        } else if (currentCorrect >= LetterSettings.NO_LETTERS.start && currentCorrect < LetterSettings.NO_LETTERS.end) {
            letterSettings = LetterSettings.NO_LETTERS;
        } else if(currentCorrect >= LetterSettings.DISAPPEARING_KEYS.start && currentCorrect < LetterSettings.DISAPPEARING_KEYS.end) {
            letterSettings = LetterSettings.DISAPPEARING_KEYS;
        } else {
            letterSettings = LetterSettings.NONE;
        }

        return letterSettings;
    }

    public void correctAnswer() {
        if(increaseDifficulty) {
            if (currentCorrect == 12 && currentDelay < 3) {
                currentDelay++;
            } else {
                currentCorrect++;
            }

            this.letterSettings = getSettingForCorrect(currentCorrect);
        }
    }

    public void incorrectAnswer() {
        if(increaseDifficulty) {
            if (currentCorrect > 0) {
                currentCorrect--;
            }

            if (currentDelay > 0) {
                currentDelay--;
            }

            this.letterSettings = getSettingForCorrect(currentCorrect);
        }
    }

    public int getPoints() {
        float percentage = 0;
        int correctForSettings = currentCorrect -  letterSettings.start;
        int totalToAnswer = letterSettings.end - letterSettings.start;
        if(totalToAnswer > 0) {
            percentage = (float)correctForSettings / (float)totalToAnswer;
        }
        int pointDiff = letterSettings.maximumPoints - letterSettings.minimumPoints;
        int toAdd = MathUtils.floor(pointDiff * percentage);
        return letterSettings.minimumPoints + toAdd;
    }

    public LetterSettings getLetterSettings() {
        return letterSettings;
    }

    public int getLetterCountToHide() {
        int toDisappear = 0;

        if(letterSettings.letterDisplay == 0) {
            int correctForSettings = currentCorrect - letterSettings.start;
            toDisappear = correctForSettings + 1;
        }

        return toDisappear;
    }

    public int getKeyCountToHide() {
        int toDisappear = 0;

        if(letterSettings.keyDisplay == 0) {
            int correctForSettings = currentCorrect - letterSettings.start;
            toDisappear = correctForSettings + 1;
        }

        return toDisappear;
    }

    public int getCurrentCorrect() {
        return currentCorrect;
    }

    public int getStart() {
        return startSettings.start;
    }

    public LetterSettings getStartDifficulty() {
        return startSettings;
    }

    public void setCurrentCorrect(int currentCorrect) {
        this.currentCorrect = currentCorrect;
        this.letterSettings = getSettingForCorrect(currentCorrect);
        this.startSettings = getSettingForCorrect(currentCorrect);
    }
}
