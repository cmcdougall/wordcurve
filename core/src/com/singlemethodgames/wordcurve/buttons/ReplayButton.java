package com.singlemethodgames.wordcurve.buttons;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.singlemethodgames.wordcurve.actors.gamebar.GameMode;
import com.singlemethodgames.wordcurve.utils.Constants;
import com.singlemethodgames.wordcurve.utils.ReplayListener;

/**
 * Created by cameron on 19/02/2018.
 */

public class ReplayButton extends ImageButton {

    private final ReplayListener replayListener;

    public ReplayButton(Drawable imageUp, final ReplayListener replayListener) {
        super(imageUp);
        getImage().setOrigin(100f, 100f);
        this.replayListener = replayListener;
        setTransform(true);
        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                rotate();
                replayWord();
            }
        });
        setTouchable(Touchable.disabled);
        getImage().getColor().a = 0.5f;
        getImage().setScale(0.6f);
    }

    private void replayWord() {
        if(replayListener != null) {
            replayListener.replayQuestion();
        }
    }

    public void displayReplayButtonAndRotateForever() {
        setTouchable(Touchable.enabled);
        getImage().clearActions();
        getImage().addAction(
                Actions.sequence(
                        Actions.parallel(
                                Actions.fadeIn(0.2f),
                                Actions.rotateBy(360f, 0.5f, Interpolation.exp5),
                                Actions.scaleTo(1f, 1f, 0.2f)
                        ),
                        Actions.forever(
                                Actions.sequence(
                                        Actions.delay(2.5f),
                                        Actions.rotateBy(360f, 0.5f, Interpolation.exp5)
                                )
                        )
                )
        );
    }

    public void displayReplayButton() {
        displayReplayButtonWithDelay(0f);
    }

    public void displayReplayButtonWithDelay(float delay) {
        setTouchable(Touchable.enabled);
        getImage().clearActions();
        getImage().addAction(
                Actions.sequence(
                        Actions.delay(delay),
                        Actions.parallel(
                                Actions.alpha(1f, 0.2f),
                                Actions.rotateBy(360f, 0.5f, Interpolation.exp5),
                                Actions.scaleTo(1f, 1f, 0.2f)
                        )
                )
        );
    }

    public void hideReplayButton() {
        setTouchable(Touchable.disabled);
        getImage().clearActions();
        getImage().addAction(
                Actions.parallel(
                        Actions.alpha(0.5f, 0.2f),
                        Actions.scaleTo(0.6f, 0.6f, 0.2f)
                )
        );
    }

    public void rotate() {
        getImage().clearActions();
        getImage().setRotation(0f);
        getImage().addAction(Actions.rotateBy(360f, 0.5f, Interpolation.exp5));
    }

    public static Table createReplayTable(TextureAtlas textureAtlas, ReplayButton replayButton, GameMode gameMode) {
        return createReplayTable(textureAtlas, replayButton, gameMode, null, null);
    }

    public static Table createReplayTable(TextureAtlas textureAtlas, ReplayButton replayButton, GameMode gameMode, ClickListener pauseListener, ClickListener settingsListener) {
        Table replayTable = new Table();
        replayTable.setFillParent(true);
        replayTable.align(Align.center | Align.bottom);
//        replayTable.setDebug(true);

        Drawable drawable = new TextureRegionDrawable(textureAtlas.findRegion(Constants.TextureRegions.PAUSE_BUTTON));

        ImageButton pauseButton = new ImageButton(drawable);
        if (pauseListener != null) {
            pauseButton.addListener(pauseListener);
        }

        replayTable.row().center().padBottom(25f);
        if(!gameMode.equals(GameMode.TUTORIAL)) {
            replayTable.add(pauseButton).size(200f).width(Value.percentWidth(1 / 3f, replayTable));
        } else {
            replayTable.add().size(200f).width(Value.percentWidth(1 / 3f, replayTable));
        }

        if(!gameMode.equals(GameMode.CHALLENGES)) {
            replayTable.add(replayButton).size(200f).expandX();
        } else {
            replayTable.add().size(200f).expandX();
        }

        if (gameMode.equals(GameMode.CASUAL)) {
            drawable = new TextureRegionDrawable(textureAtlas.findRegion(Constants.TextureRegions.KEYBOARD_SETTINGS_ICON));
            ImageButton settingsButton = new ImageButton(drawable);
            if (settingsListener != null) {
                settingsButton.addListener(settingsListener);
            }

            replayTable.add(settingsButton).size(200f).width(Value.percentWidth(1/3f, replayTable));
        } else {
            replayTable.add().size(200f).width(Value.percentWidth(1/3f, replayTable));
        }

        return replayTable;
    }
}
