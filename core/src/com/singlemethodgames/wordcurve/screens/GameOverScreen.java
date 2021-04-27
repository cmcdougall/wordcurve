package com.singlemethodgames.wordcurve.screens;

import com.badlogic.gdx.Gdx;
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
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
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
import com.singlemethodgames.wordcurve.screens.difficulty.LetterDifficulty;
import com.singlemethodgames.wordcurve.screens.difficulty.LetterSettings;
import com.singlemethodgames.wordcurve.screens.difficulty.SpeedSetting;
import com.singlemethodgames.wordcurve.screens.variants.Classic;
import com.singlemethodgames.wordcurve.screens.variants.Switch;
import com.singlemethodgames.wordcurve.screens.variants.Variant;
import com.singlemethodgames.wordcurve.utils.Assets;
import com.singlemethodgames.wordcurve.utils.Constants;
import com.singlemethodgames.wordcurve.utils.Utils;
import com.singlemethodgames.wordcurve.utils.progress.GameProgress;
import com.singlemethodgames.wordcurve.utils.questionproviders.QuestionProvider;
import com.singlemethodgames.wordcurve.utils.tracking.Tracker;

/**
 * Created by cameron on 19/02/2018.
 */

public class GameOverScreen extends BaseScreen {
    private Stage stage;

    public GameOverScreen(final WordCurveGame game, final GameProgress gameProgress, final Variant gameVariant, final GameMode gameMode, final QuestionProvider questionProvider, final Tracker tracker, final boolean training) {
        super(game);
        game.clearSave();
        stage = new Stage(game.viewport);

        BitmapFont font48 = game.getAssetManager().get(Constants.Fonts.SIZE48, BitmapFont.class);
        BitmapFont font60 = game.getAssetManager().get(Constants.Fonts.SIZE60, BitmapFont.class);
        BitmapFont font72 = game.getAssetManager().get(Constants.Fonts.SIZE72, BitmapFont.class);

        Label.LabelStyle labelStyleMedium = new Label.LabelStyle(font72, Color.WHITE);

        TextureAtlas textureAtlas = game.getAssetManager().get(Assets.finalSelectAtlas);

        NinePatchDrawable normal = new NinePatchDrawable(textureAtlas.createPatch(Constants.TextureRegions.BUTTON));
        NinePatchDrawable pressed = normal.tint(Color.LIGHT_GRAY);

        ImageTextButton.ImageTextButtonStyle imageTextButtonStyle = new ImageTextButton.ImageTextButtonStyle(normal, pressed, normal, font72);

        final I18NBundle myBundle = game.getAssetManager().get(Assets.stringsBundle);

        String highScoreKey = "game_score";
        if(!training && (gameProgress.getScore() > game.highScores.getHighscore(gameVariant, gameMode))) {
            highScoreKey = "new_high_score";
        }

        Label scoreHeadingLabel = new Label(myBundle.get(highScoreKey), labelStyleMedium);

        Image pointsImage = new Image(textureAtlas.findRegion(Constants.TextureRegions.STAR));
        pointsImage.setScaling(Scaling.fit);
        pointsImage.setColor(Color.YELLOW);
        pointsImage.setOrigin(Align.center);

        Label scoreLabel = new Label(String.valueOf(gameProgress.getScore()), labelStyleMedium);
        scoreLabel.setAlignment(Align.center);

        Label matchedHeadingLabel = new Label(myBundle.get("matched"), labelStyleMedium);
        matchedHeadingLabel.setAlignment(Align.center);

        Label matchedTotalLabel = new Label(String.valueOf(gameProgress.getMatched()), labelStyleMedium);
        matchedTotalLabel.setAlignment(Align.center);

        Drawable drawable = new TextureRegionDrawable(textureAtlas.findRegion(Constants.TextureRegions.PREVIOUS_BUTTON));
        final ImageButton backButton = new ImageButton(drawable);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                goToMainMenu();
            }
        });

        drawable = new TextureRegionDrawable(new TextureRegion(textureAtlas.findRegion(Constants.TextureRegions.LEADERBOARD_ICON)));
        final ImageButton leaderboardButton = new ImageButton(drawable);
        leaderboardButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(!Utils.showLeaderboard(game, gameVariant, gameMode)) {
                    game.notifyUser(myBundle.format("sign_in_to_view", myBundle.get(game.platformResolver.getPlatform())));
                }
            }
        });

        final ImageTextButton playAgainButton = new ImageTextButton(myBundle.get("play_again"), imageTextButtonStyle);
        playAgainButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                questionProvider.reset(game.random);
                final LetterSettings letterSettings = LetterDifficulty.getSettingForCorrect(game.gamePreferences.getLetterDifficulty(gameVariant));
                final SpeedSetting speedSetting = game.gamePreferences.getSpeedDifficulty(gameVariant);
                Tracker tracker = new Tracker(gameVariant.toString(), gameMode.toString(), letterSettings.start, speedSetting.start);
                SaveState saveState = new SaveState(game.getSeed(), gameVariant, gameMode, tracker, training);
                GameBar.createNewStateForMode(saveState);

                saveState.getGameState().setLetterDifficultyCorrect(letterSettings.start);
                saveState.getGameState().setSpeedDifficultyCorrect(speedSetting.start);

                switch (gameVariant) {
                    case CLASSIC:
                        game.setScreen(new Classic(game, saveState, questionProvider));
                        break;
                    case SWITCH:
                        game.setScreen(new Switch(game, saveState, questionProvider));
                        break;
                }
            }
        });

        NinePatchDrawable rateDrawable = new NinePatchDrawable(textureAtlas.createPatch(Constants.TextureRegions.BUTTON)).tint(Color.GRAY);
        TextButton.TextButtonStyle rateAppButtonStyle = new TextButton.TextButtonStyle(rateDrawable, rateDrawable.tint(Color.LIGHT_GRAY), rateDrawable, font60);
        rateAppButtonStyle.fontColor = Constants.Colours.Keyboard.KEY_COLOUR;
        final TextButton rateAppButton = new TextButton(myBundle.get("rate_app"), rateAppButtonStyle);
        rateAppButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.net.openURI(game.platformResolver.getStoreLink());
            }
        });

        TextureRegion texture = textureAtlas.findRegion(gameVariant.equals(Variant.CLASSIC) ? Constants.TextureRegions.CLASSIC_SMALL : Constants.TextureRegions.SWITCH_SMALL);
        Image selectedType = new Image(texture);

        String textureToLoad = Constants.TextureRegions.NO_SELECT;
        switch (gameMode) {
            case TIME: textureToLoad = Constants.TextureRegions.TIME_SMALL;
                break;
            case LIFE: textureToLoad = Constants.TextureRegions.LIFE_SMALL;
                break;
            case CHALLENGES: textureToLoad = Constants.TextureRegions.CHALLENGES_SMALL;
                break;
            case CASUAL: textureToLoad = Constants.TextureRegions.CASUAL_SMALL;
                break;
        }

        TextureRegion selectedModeTexture = textureAtlas.findRegion(textureToLoad);
        Image selectedMode = new Image(selectedModeTexture);

        Label.LabelStyle crumbStyle = new Label.LabelStyle(font48, Color.WHITE);
        Label variantLabel = new Label(myBundle.get(gameVariant.toString().toLowerCase() + "_name"), crumbStyle);
        variantLabel.setAlignment(Align.center);

        Label modeLabel = new Label(myBundle.get(gameMode.toString().toLowerCase() + "_name"), crumbStyle);
        modeLabel.setAlignment(Align.center);

        Table selectedGroup = new Table();
//        selectedGroup.setDebug(true);
        selectedType.setScaling(Scaling.fit);
        selectedMode.setScaling(Scaling.fit);
        selectedGroup.add(selectedType).center().width(stage.getViewport().getWorldWidth() / 2f);
        selectedGroup.add(selectedMode).center().width(stage.getViewport().getWorldWidth() / 2f);
        selectedGroup.row().padTop(20);
        selectedGroup.add(variantLabel).center();
        selectedGroup.add(modeLabel).center();

        Table table = new Table();
//        table.setDebug(true);
        table.setFillParent(true);
        table.align(Align.center | Align.top);

        Table pointsTable = new Table();
        pointsTable.add(scoreHeadingLabel).center().colspan(2).pad(15);
        pointsTable.row().pad(15);
        pointsTable.add(pointsImage).width(100).center().right();
        pointsTable.add(scoreLabel).center().left();

        table.add(selectedGroup).center().width(775f).padTop(75f);
        table.row();
        table.add(pointsTable).center().expand().fill();
        table.row();

        table.add(matchedHeadingLabel).center();
        table.row().expandY();
        Table matchedTable = new Table();
//        matchedTable.setDebug(true);
        matchedTable.add(matchedTotalLabel);
        final int currentCorrectScore = game.highScores.getCorrect(gameVariant, gameMode);
        if(currentCorrectScore != gameProgress.getMatched()) {
            Table diffTable = getCorrectDiffTable(currentCorrectScore, gameProgress.getMatched(), font48, textureAtlas);
            matchedTable.add(diffTable).padLeft(20f);
        }
        table.add(matchedTable).center();

        table.row();
        if(gameMode.equals(GameMode.TIME) || gameMode.equals(GameMode.CASUAL)) {
            Label mismatchedHeadingLabel = new Label(myBundle.get("mismatched"), labelStyleMedium);
            mismatchedHeadingLabel.setAlignment(Align.center);

            Label mismatchedTotalLabel = new Label(String.valueOf(gameProgress.getIncorrectAnswers()), labelStyleMedium);
            mismatchedTotalLabel.setAlignment(Align.center);

            table.row();
            table.add(mismatchedHeadingLabel).center();
            table.row().expandY();

            Table mismatchedTable = new Table();
//            mismatchedTable.setDebug(true);
            mismatchedTable .add(mismatchedTotalLabel);
            final int currentMismatches = game.highScores.getIncorrect(gameVariant, gameMode);
            if(currentMismatches != gameProgress.getIncorrectAnswers()) {
                Table diffTable = getIncorrectDiffTable(currentMismatches, gameProgress.getIncorrectAnswers(), font48, textureAtlas);
                mismatchedTable.add(diffTable).padLeft(20f);
            }

            table.add(mismatchedTable).center();
        } else {
            table.add().expandY();
        }

        table.row();

        table.add(playAgainButton).center().size(500, 200).expandY().colspan(3);
        table.row();
        table.add(rateAppButton).center().size(300, 100).expandY().colspan(3);

        Table buttonTable = new Table();
//        buttonTable.setDebug(true);
        buttonTable.row().padBottom(100f).size(150f).expand();
        buttonTable.add(backButton).left();
        buttonTable.add().size(150f);
        if(gameMode.equals(GameMode.CHALLENGES)) {
            buttonTable.add().size(150f);
        } else {
            buttonTable.add(leaderboardButton).size(150f).right();
        }

        table.row().center().bottom();
        table.add(buttonTable).width(775f).colspan(3);

        stage.addActor(table);
        uploadScores(gameVariant, gameMode, gameProgress, training);
    }

    private static Table getCorrectDiffTable(int existingScore, int score, BitmapFont bitmapFont, TextureAtlas textureAtlas) {
        return getScoreDiffTable(existingScore, score, bitmapFont, Constants.Colours.Keyboard.Button.CORRECT, Constants.Colours.INCORRECT_KEY_RED, textureAtlas);
    }

    private static Table getIncorrectDiffTable(int existingScore, int score, BitmapFont bitmapFont, TextureAtlas textureAtlas) {
        return getScoreDiffTable(existingScore, score, bitmapFont, Constants.Colours.INCORRECT_KEY_RED  , Constants.Colours.Keyboard.Button.CORRECT, textureAtlas);
    }

    private static Table getScoreDiffTable(int existingScore, int score, BitmapFont bitmapFont, Color increaseColour, Color decreaseColour, TextureAtlas textureAtlas) {
        Table table = new Table();

        int diff = score - existingScore;
        Label diffLabel;
        Image image;

        if(diff > 0) {
            image = new Image(textureAtlas.findRegion(Constants.TextureRegions.INCREASE));
            image.setColor(increaseColour);
            diffLabel = new Label(String.valueOf(diff), new Label.LabelStyle(bitmapFont, increaseColour));
        } else {
            diff *= -1;
            image = new Image(textureAtlas.findRegion(Constants.TextureRegions.DECREASE));
            image.setColor(decreaseColour);
            diffLabel = new Label(String.valueOf(diff), new Label.LabelStyle(bitmapFont, decreaseColour));
        }

        table.add(image).center().size(30f, 17.517f).padRight(10f);
        table.add(diffLabel);

        return table;
    }

    private void uploadScores(final Variant variant, final GameMode gameMode, GameProgress gameProgress, boolean training) {
        if(!training) {
            boolean newHighScore = game.highScores.getHighscore(variant, gameMode) < gameProgress.getScore();
            if(newHighScore) {
                game.highScores.setHighscore(variant, gameMode, gameProgress.getScore());
            }

            if((gameMode.equals(GameMode.LIFE) || gameMode.equals(GameMode.TIME) || gameMode.equals(GameMode.CASUAL))
                    && game.gameServices.isSignedIn()) {
                String leaderboardCode = Utils.getLeaderboardCode(game, variant, gameMode);
                game.gameServices.updateLeaderboard(leaderboardCode, gameProgress.getScore());
            }

            if(game.highScores.getCorrect(variant, gameMode) < gameProgress.getMatched()) {
                game.highScores.setCorrectAnswers(variant, gameMode, gameProgress.getMatched());
            }

            if(game.highScores.getIncorrect(variant, gameMode) < gameProgress.getIncorrectAnswers()) {
                game.highScores.setIncorrectAnswers(variant, gameMode, gameProgress.getIncorrectAnswers());
            }
        }
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
    public void dispose() { }

    @Override
    public InputProcessor getInputProcessor() {
        return stage;
    }
}
