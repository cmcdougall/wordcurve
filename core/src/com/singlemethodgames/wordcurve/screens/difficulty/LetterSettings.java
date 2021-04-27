package com.singlemethodgames.wordcurve.screens.difficulty;

public enum LetterSettings {
    // Display status: -1 is hide, 0 is some missing, 1 is display
    LETTERS(1, 1, 10, 10, 0, 12),
    DISAPPEARING_LETTERS(0, 1, 11, 39, 12, 37),
    NO_LETTERS(-1, 1, 40, 40, 37, 47),
    DISAPPEARING_KEYS(-1, 0, 41, 69, 47, 72),
    NONE(-1, -1, 70, 70, 72, -1);

    public final int letterDisplay;
    public final int keyDisplay;
    public final int minimumPoints;
    public final int maximumPoints;
    public final int start;
    public final int end;

    LetterSettings(final int letterDisplay, final int keyDisplay, final int minimumPoints, final int maximumPoints, int start, int end) {
        this.letterDisplay = letterDisplay;
        this.keyDisplay = keyDisplay;
        this.minimumPoints = minimumPoints;
        this.maximumPoints = maximumPoints;
        this.start = start;
        this.end = end;
    }

    public int getLetterDisplay() {
        return letterDisplay;
    }

    public int getKeyDisplay() {
        return keyDisplay;
    }
}
