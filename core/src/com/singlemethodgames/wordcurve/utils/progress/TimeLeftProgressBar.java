package com.singlemethodgames.wordcurve.utils.progress;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.singlemethodgames.wordcurve.utils.Constants;

/**
 * Created by cameron on 23/03/2018.
 */

public class TimeLeftProgressBar extends Table {
    private ProgressBar leftProgressBar;
    private ProgressBar rightProgressBar;
    private float MAX_TIME;

    public TimeLeftProgressBar(int width, int height, float minTime, float maxTime, TextureRegionDrawable drawable) {
        this(width, height,
                Constants.Colours.Keyboard.Button.CORRECT,
                Constants.Colours.INCORRECT_RED,
                minTime, maxTime, drawable
        );
    }

    private TimeLeftProgressBar(int width, int height, Color main, Color other, float minTime, float maxTime, TextureRegionDrawable textureRegionDrawable) {
        textureRegionDrawable.setMinWidth(0f);
        textureRegionDrawable.setMinHeight(height);
        leftProgressBar = createProgressBar(width, height, main, other, minTime, maxTime, textureRegionDrawable);
        rightProgressBar = createProgressBar(width, height, other, main, minTime, maxTime, textureRegionDrawable);
        MAX_TIME = maxTime;
        align(Align.center | Align.top);
        setSize(width, height);

        add(leftProgressBar).height(height).width(width / 2f);
        add(rightProgressBar).height(height).width(width / 2f);
    }

    public void setPercent(float percent) {
        leftProgressBar.setValue(MAX_TIME - percent);
        rightProgressBar.setValue(percent);
    }

    private static ProgressBar createProgressBar(int width, int height, Color background, Color before, float minTime, float maxTime, TextureRegionDrawable textureRegionDrawable) {
        ProgressBar progressBar = new ProgressBar(minTime, maxTime, 0.001f, false, new ProgressBar.ProgressBarStyle());

//        textureRegionDrawable.setMinWidth(width);
//        textureRegionDrawable.setMinHeight(height);

        progressBar.getStyle().background = textureRegionDrawable.tint(background);
        progressBar.getStyle().knob = textureRegionDrawable.tint(background);
        progressBar.getStyle().knobBefore = textureRegionDrawable.tint(before);

        return progressBar;
    }

    public void fadeOut(float duration) {
        this.leftProgressBar.addAction(
                Actions.fadeOut(duration)
        );
        this.rightProgressBar.addAction(
                Actions.fadeOut(duration)
        );
    }

    public void fadeIn(float duration) {
        this.leftProgressBar.addAction(
                Actions.fadeIn(duration)
        );
        this.rightProgressBar.addAction(
                Actions.fadeIn(duration)
        );
    }

    public float getMAX_TIME() {
        return MAX_TIME;
    }
}
