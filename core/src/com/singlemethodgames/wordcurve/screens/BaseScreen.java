package com.singlemethodgames.wordcurve.screens;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.singlemethodgames.wordcurve.WordCurveGame;

/**
 * Created by cameron on 19/02/2018.
 */

public abstract class BaseScreen implements Screen {

    public WordCurveGame game;

    public BaseScreen(WordCurveGame game) {
        this.game = game;
    }

    public void goToMainMenu() {
        game.setScreen(game.mainMenuScreen);
    }

    public abstract void show();

    public abstract void render(float delta);

    public abstract void resize(int width, int height);

    public abstract void pause();

    public void resume() {
    }

    public abstract void hide();

    public void dispose() {
        game.getAssetManager().dispose();
    }

    public abstract InputProcessor getInputProcessor ();
}
