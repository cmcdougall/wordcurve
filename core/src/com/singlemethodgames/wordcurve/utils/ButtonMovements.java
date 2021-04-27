package com.singlemethodgames.wordcurve.utils;

import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

/**
 * Created by cameron on 19/02/2018.
 */

public class ButtonMovements {
    public static ParallelAction moveButtonOnScreen(float x, float y, float duration) {
        AlphaAction alphaAction = new AlphaAction();
        alphaAction.setAlpha(1f);
        alphaAction.setDuration(duration);

        MoveToAction moveToAction = new MoveToAction();
        moveToAction.setPosition(x, y);
        moveToAction.setDuration(duration);

        return new ParallelAction(moveToAction, alphaAction);
    }

    public static SequenceAction moveButtonOffScreen(float x, float y, float duration) {
        return moveButtonOffScreen(x, y, duration, null);
    }

    public static SequenceAction moveButtonOffScreen(float x, float y, float duration, RunnableAction runnableAction) {

        AlphaAction alphaAction = new AlphaAction();
        alphaAction.setAlpha(0f);
        alphaAction.setDuration(duration);

        MoveToAction moveToAction = new MoveToAction();
        moveToAction.setPosition(x, y);
        moveToAction.setDuration(duration);

        ParallelAction parallelAction = new ParallelAction(moveToAction, alphaAction);

        SequenceAction sequenceAction = new SequenceAction();
        sequenceAction.addAction(parallelAction);

        if(runnableAction != null) {
            sequenceAction.addAction(runnableAction);
        }

        return sequenceAction;
    }
}
