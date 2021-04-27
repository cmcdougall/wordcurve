package com.singlemethodgames.wordcurve.actors;

/**
 * Created by cameron on 19/02/2018.
 */

public interface WordCurveActorAction {
    void progress(float percent, float duration);
    void reset();
}
