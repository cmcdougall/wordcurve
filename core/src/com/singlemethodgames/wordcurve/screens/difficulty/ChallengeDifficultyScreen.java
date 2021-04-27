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
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.singlemethodgames.wordcurve.SaveState;
import com.singlemethodgames.wordcurve.WordCurveGame;
import com.singlemethodgames.wordcurve.actors.gamebar.ChallengesBar;
import com.singlemethodgames.wordcurve.actors.gamebar.GameBar;
import com.singlemethodgames.wordcurve.actors.gamebar.GameMode;
import com.singlemethodgames.wordcurve.groups.WordCurveKeyboardGroup;
import com.singlemethodgames.wordcurve.screens.BaseScreen;
import com.singlemethodgames.wordcurve.screens.LevelSelectScreen;
import com.singlemethodgames.wordcurve.screens.variants.Classic;
import com.singlemethodgames.wordcurve.screens.variants.Switch;
import com.singlemethodgames.wordcurve.screens.variants.Variant;
import com.singlemethodgames.wordcurve.utils.Assets;
import com.singlemethodgames.wordcurve.utils.Constants;
import com.singlemethodgames.wordcurve.utils.KeyboardSettingsTable;
import com.singlemethodgames.wordcurve.utils.challenges.Level;
import com.singlemethodgames.wordcurve.utils.questionproviders.ChallengeQuestionProvider;
import com.singlemethodgames.wordcurve.utils.questionproviders.QuestionProvider;
import com.singlemethodgames.wordcurve.utils.tracking.Tracker;
import com.singlemethodgames.wordcurve.utils.wordlist.Word;

public class ChallengeDifficultyScreen extends BaseScreen implements KeyboardSettingsTable.Modified {
    protected Stage stage;
    private final WordCurveKeyboardGroup wordCurveKeyboardGroup;
    protected KeyboardSettingsTable keyboardSettingsTable;

    public ChallengeDifficultyScreen(final WordCurveGame game, final Variant variant, final String selectedLevel, final Level level, final LevelSelectScreen levelSelectScreen) {
        super(game);

        stage = new Stage(game.viewport) {
            @Override
            public boolean keyDown(int keyCode) {
                if (keyCode == Input.Keys.BACK) {
                    game.setScreen(levelSelectScreen);
                    return true;
                }

                return false;
            }
        };


        TextureAtlas textureAtlas = game.getAssetManager().get(Assets.settingsAtlas);
        keyboardSettingsTable = new KeyboardSettingsTable(game, this, true, variant, GameMode.CHALLENGES, textureAtlas);

        Drawable drawable = new TextureRegionDrawable(textureAtlas.findRegion(Constants.TextureRegions.PREVIOUS_BUTTON));
        ImageButton backButton = new ImageButton(drawable);
        backButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(levelSelectScreen);
            }
        });

        drawable = new TextureRegionDrawable(textureAtlas.findRegion(Constants.TextureRegions.PLAY_GAME_BUTTON));
        ImageButton playButton = new ImageButton(drawable);
        playButton.addListener(new ClickListener() {
               public void clicked(InputEvent event, float x, float y) {
                   game.newSeed();
                   Tracker tracker = new Tracker(variant.toString(), GameMode.CHALLENGES.toString(), keyboardSettingsTable.getLetterSettings().getStart(), keyboardSettingsTable.getSpeedDifficulty().getStart());
                   SaveState saveState = new SaveState(game.getSeed(), variant, GameMode.CHALLENGES, tracker, false);
                   GameBar.createNewStateForMode(saveState);
                   QuestionProvider questionProvider = new ChallengeQuestionProvider(selectedLevel, level, game.random, 0);

                   ChallengesBar.ChallengeState challengeState = (ChallengesBar.ChallengeState)saveState.getGameState();
                   challengeState.setLevelName(selectedLevel);
                   challengeState.setCurrentIndex(0);

                   saveState.getGameState().setLetterDifficultyCorrect(keyboardSettingsTable.getLetterSettings().getCurrentCorrect());
                   saveState.getGameState().setSpeedDifficultyCorrect(keyboardSettingsTable.getLetterSettings().getCurrentCorrect());

                   switch (saveState.getVariant()) {
                       case CLASSIC:
                           game.setScreen(new Classic(game, saveState, questionProvider));
                           break;
                       case SWITCH:
                           game.setScreen(new Switch(game, saveState, questionProvider));
                           break;
                   }
               }
           }
        );

        keyboardSettingsTable.row().center().expand();
        Table buttonTable = new Table();
        buttonTable.row().padBottom(50f).expand();
        buttonTable.add(backButton).size(150f).left();
        buttonTable.add(playButton).size(250f).center();
        buttonTable.add().size(150f);

        keyboardSettingsTable.add(buttonTable).width(775f).center().bottom().colspan(2);

        wordCurveKeyboardGroup = new WordCurveKeyboardGroup(game.getAssetManager().get(Assets.ingameAtlas), 5, 1400, 1070, Touchable.disabled, this.game.camera,
        keyboardSettingsTable.getSpeedDifficulty(), keyboardSettingsTable.getLetterSettings(), game.viewport);
        wordCurveKeyboardGroup.loadKeyboardDisplay(0);

        stage.addActor(wordCurveKeyboardGroup);
        stage.addActor(keyboardSettingsTable);
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

    @Override
    public void settingsChanged(int start) {
        wordCurveKeyboardGroup.loadKeyboardDisplay();
    }

    @Override
    public void performWordWave(String word, SpeedSetting speedSetting) {
        wordCurveKeyboardGroup.setWord(new Word(word));
        wordCurveKeyboardGroup.createAndPerformWordCurve();
    }
}
