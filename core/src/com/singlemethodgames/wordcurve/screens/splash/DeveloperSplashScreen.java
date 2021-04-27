package com.singlemethodgames.wordcurve.screens.splash;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.singlemethodgames.wordcurve.SaveState;
import com.singlemethodgames.wordcurve.WordCurveGame;
import com.singlemethodgames.wordcurve.screens.BaseScreen;
import com.singlemethodgames.wordcurve.screens.ContinueScreen;
import com.singlemethodgames.wordcurve.screens.MainMenuScreen;
import com.singlemethodgames.wordcurve.screens.tutorial.TutorialStart;
import com.singlemethodgames.wordcurve.utils.Constants;

/**
 * Created by cameron on 19/02/2018.
 */

public class DeveloperSplashScreen extends BaseScreen {
    private Stage stage;
    private float duration;
    private SaveState saveState;

    public DeveloperSplashScreen(final WordCurveGame game, final SaveState saveState, Texture devLogo) {
        super(game);
        this.duration = 0f;
        this.saveState = saveState;

        game.viewport.apply();

        stage = new Stage(game.viewport);

        Image devImage = new Image(new TextureRegion(devLogo));
        devImage.setAlign(Align.center);

        Table table = new Table();
        table.align(Align.center);
        table.setFillParent(true);
        table.add(devImage).center();
        stage.addActor(table);
    }

    @Override
    public void show() {

    }

    @Override
    public void resume() {
        super.resume();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(Constants.Colours.SPLASH_BACKGROUND_COLOUR, Constants.Colours.SPLASH_BACKGROUND_COLOUR, Constants.Colours.SPLASH_BACKGROUND_COLOUR, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.camera.update();

        duration += Gdx.graphics.getDeltaTime();
        if(duration > 1f && game.getAssetManager().update() && game.ready) {
            game.mainMenuScreen = new MainMenuScreen(game);
            game.instantiateNotificationService();
            if(game.gamePreferences.showTutorial()) {
                game.setScreen(new TutorialStart(game));
            } else if (saveState != null) {
                resumeGame(saveState);
            } else {
                game.setScreen(game.mainMenuScreen);
            }
            dispose();
        }

        stage.act();
        stage.draw();
    }

    private void resumeGame(SaveState saveState) {
        game.random.setSeed(saveState.getSeed());
        game.setScreen(new ContinueScreen(game, saveState));
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void hide() {

    }

    @Override
    public InputProcessor getInputProcessor() {
        return stage;
    }

    @Override
    public void dispose() {

    }
}
