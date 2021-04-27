package com.singlemethodgames.wordcurve.screens.tutorial;

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
import com.singlemethodgames.wordcurve.actors.WordCurveGroup;
import com.singlemethodgames.wordcurve.screens.BaseScreen;
import com.singlemethodgames.wordcurve.screens.difficulty.SpeedDifficulty;
import com.singlemethodgames.wordcurve.screens.difficulty.SpeedSetting;
import com.singlemethodgames.wordcurve.utils.Assets;
import com.singlemethodgames.wordcurve.utils.Constants;
import com.singlemethodgames.wordcurve.utils.QwertyKeyboard;
import com.singlemethodgames.wordcurve.utils.Utils;

public class TutorialStart extends BaseScreen {
    private Stage stage;

    public TutorialStart(final WordCurveGame game) {
        super(game);

        game.viewport.apply();

        stage = new Stage(game.viewport) {
            @Override
            public boolean keyDown(int keyCode) {
                if (keyCode == Input.Keys.BACK) {
                    goToMainMenu();
                    return true;
                }
                return false;
            }
        };

        QwertyKeyboard qwertyKeyboard = new QwertyKeyboard(0,0, 1f);
        TextureAtlas textureAtlas = game.getAssetManager().get(Assets.ingameAtlas);
        WordCurveGroup wordCurveGroup = new WordCurveGroup(textureAtlas, game.camera, qwertyKeyboard, new SpeedDifficulty(SpeedSetting.THREE.start, SpeedSetting.THREE.start));
        Utils.WordCurve wordCurve = Utils.createSpline(0f);

        I18NBundle myBundle = game.getAssetManager().get(Assets.stringsBundle);

        BitmapFont font48 = game.getAssetManager().get(Constants.Fonts.SIZE48, BitmapFont.class);
        BitmapFont font72 = game.getAssetManager().get(Constants.Fonts.SIZE72, BitmapFont.class);
        BitmapFont font168 = game.getAssetManager().get(Constants.Fonts.SIZE168, BitmapFont.class);

        Label gameLogoText = new Label("word   curve", new Label.LabelStyle(font168, new Color(179 / 255f, 179 / 255f, 179 / 255f, 1f)));
        Label tutorialLabel = new Label(myBundle.get("tutorial_header"), new Label.LabelStyle(font72, Color.WHITE));
        Label infoLabel = new Label(myBundle.get("tutorial_step_1"), new Label.LabelStyle(font48, Color.WHITE));
        infoLabel.setAlignment(Align.center);
        infoLabel.setWrap(true);

        Drawable drawable = new TextureRegionDrawable(textureAtlas.findRegion(Constants.TextureRegions.EXIT_BUTTON));
        final ImageButton exitButton = new ImageButton(drawable);
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                goToMainMenu();
            }
        });

        drawable = new TextureRegionDrawable(textureAtlas.findRegion(Constants.TextureRegions.NEXT_BUTTON));
        final ImageButton nextButton = new ImageButton(drawable);
        nextButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new TutorialQuestion(game));
            }
        });

        Table introductionTable = new Table();
        introductionTable.setFillParent(true);
//        introductionTable.setDebug(true);
        introductionTable.align(Align.center | Align.top);

        introductionTable.add(gameLogoText).center().padTop(120).padBottom(120).colspan(2);
        introductionTable.row();
        introductionTable.add(tutorialLabel).colspan(2);
        introductionTable.row();
        introductionTable.add(infoLabel).colspan(2).center().expandY().width(776f).top().padTop(200f);
        introductionTable.row().bottom().size(150f).padBottom(100f);
        introductionTable.add(exitButton).right().padRight(476f);
        introductionTable.add(nextButton).left();

        stage.addActor(wordCurveGroup);
        stage.addActor(introductionTable);

        Utils.startWordCurveAction(stage, wordCurveGroup, wordCurve);
    }

    @Override
    public void goToMainMenu() {
        game.gamePreferences.updateTutorialPreference(false);
        super.goToMainMenu();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(
                Constants.Colours.BACKGROUND_COLOUR.r,
                Constants.Colours.BACKGROUND_COLOUR.g,
                Constants.Colours.BACKGROUND_COLOUR.b,
                Constants.Colours.BACKGROUND_COLOUR.a
        );
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.camera.update();

        stage.act(Gdx.graphics.getDeltaTime());
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
