package com.singlemethodgames.wordcurve.actions;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import com.singlemethodgames.wordcurve.actors.WordCurveActorAction;

/**
 * Created by cameron on 19/02/2018.
 */

public class WordCurveAction extends TemporalAction {

    public WordCurveAction(int wordLength, float speed) {
        setDuration(wordLength * speed);
    }

    @Override
    protected void update(float percent) {
        WordCurveActorAction wordCurveActor = (WordCurveActorAction)getActor();
        wordCurveActor.progress(percent, getDuration() - getTime());
    }
}