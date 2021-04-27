package com.singlemethodgames.wordcurve.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.I18NBundle;
import com.singlemethodgames.wordcurve.WordCurveGame;
import com.singlemethodgames.wordcurve.utils.Constants;

public class ExitScreen extends BaseScreen {
    private Stage stage;

    public ExitScreen(final WordCurveGame game, final TextureAtlas textureAtlas, final I18NBundle bundle, final MainMenuScreen mainMenuScreen) {
        super(game);

        game.viewport.apply();

        stage = new Stage(game.viewport) {
            @Override
            public boolean keyDown(int keyCode) {
                if (keyCode == Input.Keys.BACK) {
                    exitApp();
                    return true;
                }

                return false;
            }
        };

        BitmapFont font72 = game.getAssetManager().get(Constants.Fonts.SIZE72, BitmapFont.class);

        Label exitLabel = new Label(bundle.get("exit_message"), new Label.LabelStyle(font72, Color.WHITE));
        exitLabel.setAlignment(Align.center);

        Drawable exitButtonDrawable = new TextureRegionDrawable(textureAtlas.findRegion(Constants.TextureRegions.EXIT_APP_BUTTON));
        Drawable returnButtonDrawable = new TextureRegionDrawable(textureAtlas.findRegion(Constants.TextureRegions.NEXT_BUTTON));

        final ImageButton confirmExitButton = new ImageButton(exitButtonDrawable);
        confirmExitButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        final ImageButton returnButton = new ImageButton(returnButtonDrawable);
        returnButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(mainMenuScreen);
            }
        });

        Table exitTable = new Table();
        exitTable.setFillParent(true);
        exitTable.align(Align.center | Align.top);
//        exitTable.setDebug(true);

        exitTable.add(exitLabel).center().expand();
        exitTable.row();
        exitTable.add(confirmExitButton).center().expandY();
        exitTable.row().center().bottom().padBottom(100f).expandY();
        exitTable.add(returnButton);

        stage.addActor(exitTable);
    }

    private void exitApp() {
        Gdx.app.exit();
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
    public void hide() {

    }

    @Override
    public InputProcessor getInputProcessor() {
        return stage;
    }
}
