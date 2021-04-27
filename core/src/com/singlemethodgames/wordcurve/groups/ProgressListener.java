package com.singlemethodgames.wordcurve.groups;

public interface ProgressListener {
    void atPosition(float percent, float duration);
    void setPointPercentages(float[] percentages);
}
