package com.singlemethodgames.wordcurve.screens.difficulty;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.singlemethodgames.wordcurve.WordCurveGame;
import com.singlemethodgames.wordcurve.actors.gamebar.GameMode;
import com.singlemethodgames.wordcurve.groups.WordCurveKeyboardGroup;
import com.singlemethodgames.wordcurve.screens.BaseScreen;
import com.singlemethodgames.wordcurve.screens.FinalSelectScreen;
import com.singlemethodgames.wordcurve.screens.LevelSelectScreen;
import com.singlemethodgames.wordcurve.screens.variants.BaseMode;
import com.singlemethodgames.wordcurve.screens.variants.Variant;
import com.singlemethodgames.wordcurve.utils.Assets;
import com.singlemethodgames.wordcurve.utils.Constants;
import com.singlemethodgames.wordcurve.utils.KeyboardSettingsTable;
import com.singlemethodgames.wordcurve.utils.wordlist.Word;

public class DifficultySelect extends BaseScreen implements KeyboardSettingsTable.Modified {
    protected Stage stage;
    private final WordCurveKeyboardGroup wordCurveKeyboardGroup;
    protected KeyboardSettingsTable keyboardSettingsTable;

    public DifficultySelect(final WordCurveGame game, boolean includeSpeed, final Variant VARIANT, final GameMode gameMode) {
        this(game, includeSpeed, VARIANT, gameMode, null, -10, -10);
    }

    public DifficultySelect(final WordCurveGame game, boolean includeSpeed, final Variant variant, final GameMode gameMode, final BaseMode baseMode, final int displayCurrentCorrect, final int speedCurrentCorrect) {
        super(game);
        stage = new Stage(game.viewport) {
            @Override
            public boolean keyDown(int keyCode) {
                if (keyCode == Input.Keys.BACK) {
                    moveToScreen(baseMode, gameMode, variant);
                    return true;
                }

                return false;
            }
        };

        TextureAtlas textureAtlas = game.getAssetManager().get(Assets.settingsAtlas);
        keyboardSettingsTable = new KeyboardSettingsTable(game, this, includeSpeed, variant, gameMode, textureAtlas, displayCurrentCorrect, speedCurrentCorrect);

        Drawable drawable = new TextureRegionDrawable(textureAtlas.findRegion(Constants.TextureRegions.PREVIOUS_BUTTON));
        ImageButton backButton = new ImageButton(drawable);
        backButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                moveToScreen(baseMode, gameMode, variant);

            }
        });

        keyboardSettingsTable.row();
        keyboardSettingsTable.add(backButton).center().expandY().padBottom(100f).colspan(2).bottom();

        wordCurveKeyboardGroup = new WordCurveKeyboardGroup(game.getAssetManager().get(Assets.ingameAtlas), 5, 1400, 1070, Touchable.disabled, this.game.camera,
        keyboardSettingsTable.getSpeedDifficulty(), keyboardSettingsTable.getLetterSettings(), game.viewport);
        wordCurveKeyboardGroup.loadKeyboardDisplay(0);

        stage.addActor(wordCurveKeyboardGroup);
        stage.addActor(keyboardSettingsTable);

        performWordWave(keyboardSettingsTable.difficultyWord, keyboardSettingsTable.getSpeedDifficulty().getSpeedSetting());
    }

    private void moveToScreen(final BaseMode baseMode, final GameMode gameMode, final Variant variant) {
        if (baseMode == null) {
            if (gameMode.equals(GameMode.CHALLENGES)) {
                game.setScreen(new LevelSelectScreen(game, variant));
            } else {
                game.setScreen(new FinalSelectScreen(game, variant, gameMode));
            }
        } else {
            baseMode.settingsChanged(keyboardSettingsTable.displaySettingsChanged, keyboardSettingsTable.speedSettingsChanged);
            game.setScreen(baseMode);
        }
    }

    @Override
    public void settingsChanged(int start) {
        wordCurveKeyboardGroup.loadKeyboardDisplay();
    }

    @Override
    public void performWordWave(String word, SpeedSetting speedSetting) {
        wordCurveKeyboardGroup.setWord(new Word(word));
        wordCurveKeyboardGroup.createAndPerformWordCurve();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(Constants.Colours.BACKGROUND_COLOUR.r, Constants.Colours.BACKGROUND_COLOUR.g, Constants.Colours.BACKGROUND_COLOUR.b, Constants.Colours.BACKGROUND_COLOUR.a);
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
