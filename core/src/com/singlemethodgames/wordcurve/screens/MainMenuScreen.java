package com.singlemethodgames.wordcurve.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.I18NBundle;
import com.singlemethodgames.wordcurve.WordCurveGame;
import com.singlemethodgames.wordcurve.actors.WordCurveGroup;
import com.singlemethodgames.wordcurve.screens.difficulty.SpeedDifficulty;
import com.singlemethodgames.wordcurve.screens.difficulty.SpeedSetting;
import com.singlemethodgames.wordcurve.screens.tables.MainMenu;
import com.singlemethodgames.wordcurve.utils.Assets;
import com.singlemethodgames.wordcurve.utils.Constants;
import com.singlemethodgames.wordcurve.utils.QwertyKeyboard;
import com.singlemethodgames.wordcurve.utils.Utils;
import com.singlemethodgames.wordcurve.utils.store.StoreManager;

/**
 * Created by cameron on 19/02/2018.
 */

public class MainMenuScreen extends BaseScreen {

    private Stage stage;
    final Table menuTable;

    final private WordCurveGroup wordCurveGroup;
    public MainMenuScreen(final WordCurveGame game) {
        super(game);

        game.viewport.apply();

        QwertyKeyboard qwertyKeyboard = new QwertyKeyboard(0,0, 1f);
        wordCurveGroup = new WordCurveGroup(game.getAssetManager().get(Assets.mainInfoAtlas), game.camera, qwertyKeyboard, new SpeedDifficulty(SpeedSetting.THREE.start, SpeedSetting.THREE.start));
        Utils.WordCurve wordCurve = Utils.createSpline(0f);

        final TextureAtlas textureAtlas = game.getAssetManager().get(Assets.mainInfoAtlas);
        final I18NBundle myBundle = game.getAssetManager().get(Assets.stringsBundle);
        menuTable = new MainMenu(game, myBundle, textureAtlas, this);

        stage = new Stage(game.viewport) {
            @Override
            public boolean keyDown(int keyCode) {
                if (keyCode == Input.Keys.BACK) {
                    displayExitGameMenu(textureAtlas, myBundle, MainMenuScreen.this);
                    return true;
                }

                return false;
            }
        };

        stage.addActor(wordCurveGroup);
        stage.addActor(menuTable);

        game.storeManager = new StoreManager(game);

        Utils.startWordCurveAction(stage, wordCurveGroup, wordCurve);
    }

    private void displayExitGameMenu(final TextureAtlas textureAtlas, final I18NBundle bundle, final MainMenuScreen mainMenuScreen) {
        game.setScreen(new ExitScreen(game, textureAtlas, bundle, mainMenuScreen));
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(Constants.Colours.BACKGROUND_COLOUR.r, Constants.Colours.BACKGROUND_COLOUR.g, Constants.Colours.BACKGROUND_COLOUR.b, Constants.Colours.BACKGROUND_COLOUR.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.camera.update();

        game.batch.setProjectionMatrix(game.camera.combined);

        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
    }

    @Override
    public InputProcessor getInputProcessor() {
        return stage;
    }
}
