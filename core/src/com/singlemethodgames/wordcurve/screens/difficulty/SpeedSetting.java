package com.singlemethodgames.wordcurve.screens.difficulty;

public enum SpeedSetting {
    ONE(0.3f, 0, 0, 10),
    TWO(0.25f, 5, 10, 20),
    THREE(0.2f, 10, 20, 30),
    FOUR(0.15f, 15, 30, 40),
    FIVE(0.1f, 20, 40, -1);

    public final float speed;
    public final int points;
    public final int start;
    public final int end;

    SpeedSetting(float speed, int points, int start, int end) {
        this.speed = speed;
        this.points = points;
        this.start = start;
        this.end = end;
    }
}
