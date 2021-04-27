package com.singlemethodgames.wordcurve.utils;

import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.actions.ScaleByAction;

/**
 * Created by cameron on 19/02/2018.
 */

public class WordCurveActions {
    public static ScaleByAction homingInAction() {
        ScaleByAction smallerScale = new ScaleByAction();
        smallerScale.setAmount(-0.90f);
        smallerScale.setDuration(0.5f);

        return smallerScale;
    }

    public static ScaleByAction homingOutAction() {
        ScaleByAction normalScale = new ScaleByAction();
        normalScale.setAmount(0.90f);
        normalScale.setDuration(0.5f);
        return normalScale;
    }

    public static AlphaAction fullAlphaAction() {
        AlphaAction fullAlpha = new AlphaAction();
        fullAlpha.setAlpha(1f);
        fullAlpha.setDuration(0.5f);

        return fullAlpha;
    }

    public static AlphaAction zeroAlphaAction() {
        AlphaAction zeroAlpha = new AlphaAction();
        zeroAlpha.setAlpha(0f);
        zeroAlpha.setDuration(0.5f);

        return zeroAlpha;
    }
}
