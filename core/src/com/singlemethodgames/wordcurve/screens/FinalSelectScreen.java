package com.singlemethodgames.wordcurve.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.Scaling;
import com.singlemethodgames.wordcurve.SaveState;
import com.singlemethodgames.wordcurve.WordCurveGame;
import com.singlemethodgames.wordcurve.actors.gamebar.GameBar;
import com.singlemethodgames.wordcurve.actors.gamebar.GameMode;
import com.singlemethodgames.wordcurve.buttons.UIBinaryButton;
import com.singlemethodgames.wordcurve.screens.difficulty.DifficultySelect;
import com.singlemethodgames.wordcurve.screens.difficulty.LetterDifficulty;
import com.singlemethodgames.wordcurve.screens.difficulty.LetterSettings;
import com.singlemethodgames.wordcurve.screens.difficulty.SpeedSetting;
import com.singlemethodgames.wordcurve.screens.variants.Classic;
import com.singlemethodgames.wordcurve.screens.variants.Switch;
import com.singlemethodgames.wordcurve.screens.variants.Variant;
import com.singlemethodgames.wordcurve.utils.Assets;
import com.singlemethodgames.wordcurve.utils.Constants;
import com.singlemethodgames.wordcurve.utils.Utils;
import com.singlemethodgames.wordcurve.utils.questionproviders.QuestionProvider;
import com.singlemethodgames.wordcurve.utils.questionproviders.UnlimitedQuestionProvider;
import com.singlemethodgames.wordcurve.utils.tracking.Tracker;

public class FinalSelectScreen extends BaseScreen {
    private Stage stage;

    public FinalSelectScreen(final WordCurveGame game, final Variant variant, final GameMode gameMode) {
        super(game);
        final I18NBundle myBundle = game.getAssetManager().get(Assets.stringsBundle);
        stage = new Stage(game.viewport) {
            @Override
            public boolean keyDown(int keyCode) {
                if(keyCode == Input.Keys.BACK) {
                    game.setScreen(new SelectModeMenu(game, variant));
                    return true;
                }
                return false;
            }
        };
        TextureAtlas textureAtlas = game.getAssetManager().get(Assets.finalSelectAtlas);

        BitmapFont font48 = game.getAssetManager().get(Constants.Fonts.SIZE48, BitmapFont.class);
        BitmapFont font60 = game.getAssetManager().get(Constants.Fonts.SIZE60, BitmapFont.class);
        BitmapFont font72 = game.getAssetManager().get(Constants.Fonts.SIZE72, BitmapFont.class);
        BitmapFont font96 = game.getAssetManager().get(Constants.Fonts.SIZE96, BitmapFont.class);

        TextureRegion texture = textureAtlas.findRegion(variant.equals(Variant.CLASSIC) ? Constants.TextureRegions.CLASSIC_SMALL : Constants.TextureRegions.SWITCH_SMALL);
        Image selectedType = new Image(texture);

        String textureToLoad = Constants.TextureRegions.NO_SELECT;
        switch (gameMode) {
            case TIME: textureToLoad = Constants.TextureRegions.TIME_SMALL;
                break;
            case LIFE: textureToLoad = Constants.TextureRegions.LIFE_SMALL;
                break;
            case CHALLENGES:
                textureToLoad = Constants.TextureRegions.CHALLENGES_SMALL;
                break;
            case CASUAL: textureToLoad = Constants.TextureRegions.CASUAL_SMALL;
                break;
        }

        TextureRegion selectedModeTexture = textureAtlas.findRegion(textureToLoad);
        Image selectedMode = new Image(selectedModeTexture);

        final int letterStart = game.gamePreferences.getLetterDifficulty(variant);

        final LetterSettings letterSettings = LetterDifficulty.getSettingForCorrect(letterStart);
        final SpeedSetting speedSetting = game.gamePreferences.getSpeedDifficulty(variant);

        TextureRegion selectedKeySettingsTexture = textureAtlas.findRegion(getLetterTexture(letterSettings));

        Drawable checkedDrawable = new TextureRegionDrawable(textureAtlas.findRegion(Constants.TextureRegions.CHECKED));
        Color uncheckedColour = Constants.Colours.Keyboard.KEY_COLOUR.cpy();
        uncheckedColour.a = 0.9f;
        Drawable uncheckedDrawable = new TextureRegionDrawable(textureAtlas.findRegion(Constants.TextureRegions.UNCHECKED)).tint(uncheckedColour);

        NinePatchDrawable noDifficultyDrawable = new NinePatchDrawable(textureAtlas.createPatch("option")).tint(Color.GRAY);
        NinePatchDrawable difficultyDrawable = new NinePatchDrawable(textureAtlas.createPatch("option"));

        final ImageTextButton.ImageTextButtonStyle uncheckedBinaryStyle = new ImageTextButton.ImageTextButtonStyle(noDifficultyDrawable, noDifficultyDrawable, noDifficultyDrawable, font60);
        uncheckedBinaryStyle.fontColor = Constants.Colours.Keyboard.KEY_COLOUR.cpy().mul(Color.LIGHT_GRAY);
        uncheckedBinaryStyle.imageUp = uncheckedDrawable;
        final ImageTextButton.ImageTextButtonStyle checkedBinaryStyle = new ImageTextButton.ImageTextButtonStyle(difficultyDrawable, difficultyDrawable, difficultyDrawable, font60);
        checkedBinaryStyle.fontColor = Constants.Colours.Keyboard.KEY_COLOUR;
        checkedBinaryStyle.imageUp = checkedDrawable;

        boolean increaseDifficulty = game.gamePreferences.increaseDifficulty(variant);
        final UIBinaryButton increaseDifficultyButton = new UIBinaryButton(
                new UIBinaryButton.BinaryListener() {
                    @Override
                    public void on() {
                        game.gamePreferences.updateIncreaseDifficultyPreference(variant, true);
                    }

                    @Override
                    public void off() {
                        game.gamePreferences.updateIncreaseDifficultyPreference(variant, false);
                    }
                },
                myBundle.get("difficulty_increase"),
                myBundle.get("on"),
                myBundle.get("off"),
                checkedBinaryStyle,
                uncheckedBinaryStyle,
                increaseDifficulty
        );

        boolean trainingOn = game.gamePreferences.gameTraining(variant);
        final UIBinaryButton trainingButton = new UIBinaryButton(
                new UIBinaryButton.BinaryListener() {
                    @Override
                    public void on() {
                        game.notifyUser(myBundle.get("not_recorded"));
                        game.gamePreferences.updateTrainingModePreference(variant, true);
                    }

                    @Override
                    public void off() {
                        game.gamePreferences.updateTrainingModePreference(variant, false);
                    }
                },
                myBundle.get("training"),
                myBundle.get("on"),
                myBundle.get("off"),
                checkedBinaryStyle,
                uncheckedBinaryStyle,
                trainingOn
        );

        TextureRegionDrawable normal = new TextureRegionDrawable(textureAtlas.findRegion(Constants.TextureRegions.PLAY_GAME_BUTTON));
        Drawable pressed = normal.tint(Color.LIGHT_GRAY);

        final ImageButton playButton = new ImageButton(normal, pressed, normal);
        playButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                game.newSeed();
                QuestionProvider questionProvider = new UnlimitedQuestionProvider(game.wordLookup, game.random, new UnlimitedQuestionProvider.UnlimitedState());
                Tracker tracker = new Tracker(variant.toString(), gameMode.toString(), letterStart, speedSetting.start);
                SaveState saveState = new SaveState(game.getSeed(), variant, gameMode, tracker, trainingButton.isChecked());
                GameBar.createNewStateForMode(saveState, increaseDifficultyButton.isChecked());
                saveState.getGameState().setLetterDifficultyCorrect(letterStart);
                saveState.getGameState().setLetterDifficultyStart(letterStart);
                saveState.getGameState().setSpeedDifficultyCorrect(speedSetting.start);
                switch (variant) {
                    case CLASSIC:
                        game.setScreen(new Classic(game, saveState, questionProvider));
                        break;
                    case SWITCH:
                        game.setScreen(new Switch(game, saveState, questionProvider));
                        break;
                }
            }
        });

        Image pointsImage = new Image(textureAtlas.findRegion(Constants.TextureRegions.STAR));
        pointsImage.setScaling(Scaling.fit);
        pointsImage.setColor(Color.YELLOW);
        pointsImage.setOrigin(Align.center);

        Drawable drawable = new TextureRegionDrawable(textureAtlas.findRegion(Constants.TextureRegions.PREVIOUS_BUTTON));
        ImageButton backButton = new ImageButton(drawable);
        backButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new SelectModeMenu(game, variant));
            }
        });

        drawable = new TextureRegionDrawable(new TextureRegion(textureAtlas.findRegion(Constants.TextureRegions.LEADERBOARD_ICON)));
        final ImageButton leaderboardButton = new ImageButton(drawable);
        leaderboardButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(!Utils.showLeaderboard(game, variant, gameMode)) {
                    game.notifyUser(myBundle.format("sign_in_to_view", myBundle.get(game.platformResolver.getPlatform())));
                }
            }
        });

        ClickListener settingsClickListener = new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new DifficultySelect(game, true, variant, gameMode));
            }
        };

        drawable = new TextureRegionDrawable(new TextureRegion(selectedKeySettingsTexture));
        final ImageButton keySettings = new ImageButton(drawable);
        keySettings.addListener(settingsClickListener);

        NinePatchDrawable selectedSpeedSettingsTexture = new NinePatchDrawable(textureAtlas.createPatch("button_base")).tint(Constants.Colours.Keyboard.KEY_COLOUR);
        ImageTextButton.ImageTextButtonStyle speedStyle = new ImageTextButton.ImageTextButtonStyle(selectedSpeedSettingsTexture, selectedSpeedSettingsTexture, selectedSpeedSettingsTexture, font96);
        speedStyle.fontColor = Constants.Colours.BACKGROUND_COLOUR;
        final ImageTextButton speedSettings = new ImageTextButton("×" + myBundle.get("speed_"+speedSetting.toString().toLowerCase()), speedStyle);
        speedSettings.addListener(settingsClickListener);

        int currentHighScore = game.highScores.getHighscore(variant, gameMode);

        Label.LabelStyle highScoreStyle = new Label.LabelStyle(font72, Color.WHITE);
        Label highScoreLabel = new Label(myBundle.get("high_score"), highScoreStyle);
        highScoreLabel.setAlignment(Align.left);
        Label highScoreValueLabel = new Label(String.valueOf(currentHighScore), new Label.LabelStyle(font96, Color.WHITE));
        highScoreValueLabel.setAlignment(Align.right);

        Label keyboardSettingsLabel = new Label(
                gameMode.equals(GameMode.CASUAL) ? myBundle.get("difficulty") : myBundle.get("starting_difficulty"),
                highScoreStyle);
        keyboardSettingsLabel.setAlignment(Align.center);

        Table table = new Table();
        table.setFillParent(true);
//        table.setDebug(true);
        table.align(Align.center | Align.top);

        Label.LabelStyle crumbStyle = new Label.LabelStyle(font48, Color.WHITE);
        Label variantLabel = new Label(myBundle.get(variant.toString().toLowerCase() + "_name"), crumbStyle);
        variantLabel.setAlignment(Align.center);

        Label modeLabel = new Label(myBundle.get(gameMode.toString().toLowerCase() + "_name"), crumbStyle);
        modeLabel.setAlignment(Align.center);

        Table selectedGroup = new Table();
        selectedType.setScaling(Scaling.fit);
        selectedMode.setScaling(Scaling.fit);
        selectedGroup.add(selectedType).center().width(stage.getViewport().getWorldWidth() / 2f);
        selectedGroup.add(selectedMode).center().width(stage.getViewport().getWorldWidth() / 2f);
        selectedGroup.row().padTop(20);
        selectedGroup.add(variantLabel).center();
        selectedGroup.add(modeLabel).center();
        table.add(selectedGroup).center().colspan(3).width(775f).padTop(75f).padBottom(75f);

        table.row();
        table.add(highScoreLabel).expandY().center().bottom().colspan(3).padBottom(25f);

        Table pointsTable = new Table();
        pointsTable.add(pointsImage).width(100).center().expandX().expandY().right().padRight(15f);
        pointsTable.add(highScoreValueLabel).center().expandX().expandY().left().padLeft(15f);

        table.row();
        table.add(pointsTable).expandY().center().top().colspan(3);

        table.row();
        table.add(keyboardSettingsLabel).center().colspan(3).padBottom(50f);

        Table settingsGroup = new Table();
        settingsGroup.add(keySettings).size(326,150).left().expandX();
        settingsGroup.add(speedSettings).size(326,150).right().expandX();
        table.row();
        table.add(settingsGroup).center().colspan(3).width(775f);

        if(gameMode.equals(GameMode.CASUAL)) {
            table.row();
            table.add(increaseDifficultyButton).height(100).colspan(3).center().width(775f).padTop(25);
        }

        table.row();
        table.add(trainingButton).height(100).colspan(3).center().width(775f).padTop(25);

        table.row();
        table.add(playButton).size(250f).colspan(3).expandY().center().bottom();

        table.row().center().padBottom(50f);
        table.add(backButton).size(150f).left();
        table.add().size(250f);

        if(gameMode.equals(GameMode.CHALLENGES)) {
            table.add().size(150f);
        } else {
            table.add(leaderboardButton).size(150f).right();
        }

        stage.addActor(table);
    }

    @Override
    public void goToMainMenu() {
        super.goToMainMenu();
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
    public void resume() {
        super.resume();
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    public InputProcessor getInputProcessor() {
        return stage;
    }

    private static String getLetterTexture(LetterSettings letterSettings) {
        String textureToLoad = Constants.TextureRegions.NO_SELECT;
        switch (letterSettings) {
            case LETTERS: textureToLoad = Constants.TextureRegions.KEYBOARD_KEY_WITH_LETTER_WIDE;
                break;
            case NO_LETTERS: textureToLoad = Constants.TextureRegions.KEYBOARD_KEY_SETTINGS_WIDE;
                break;
            case NONE: textureToLoad = Constants.TextureRegions.KEYBOARD_KEY_NONE_WIDE;
                break;
            case DISAPPEARING_KEYS: textureToLoad = Constants.TextureRegions.KEYBOARD_KEY_WITH_KEY_DISAPPEARING;
                break;
            case DISAPPEARING_LETTERS: textureToLoad = Constants.TextureRegions.KEYBOARD_KEY_DISAPPEARING_LETTER_WIDE;
                break;
        }

        return textureToLoad;
    }
}
