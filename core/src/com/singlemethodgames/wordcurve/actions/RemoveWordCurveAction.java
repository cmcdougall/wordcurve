package com.singlemethodgames.wordcurve.actions;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import com.singlemethodgames.wordcurve.actors.WordCurveGroup;

/**
 * Created by cameron on 19/02/2018.
 */

public class RemoveWordCurveAction extends TemporalAction {

    private float duration;

    public RemoveWordCurveAction(int wordLength, final float speed) {
        duration = wordLength * speed;
        setDuration(duration);
    }

    @Override
    protected void update(float percent) {
        WordCurveGroup wordWaveGroup = (WordCurveGroup)getActor();
        wordWaveGroup.setStart(percent);
    }

    @Override
    public float getDuration() {
        return duration;
    }
}
