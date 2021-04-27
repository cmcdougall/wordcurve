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
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Scaling;
import com.singlemethodgames.wordcurve.SaveState;
import com.singlemethodgames.wordcurve.WordCurveGame;
import com.singlemethodgames.wordcurve.actors.gamebar.ChallengesBar;
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
import com.singlemethodgames.wordcurve.utils.challenges.ChallengeSet;
import com.singlemethodgames.wordcurve.utils.challenges.ChallengeTiers;
import com.singlemethodgames.wordcurve.utils.challenges.Level;
import com.singlemethodgames.wordcurve.utils.progress.GameProgress;
import com.singlemethodgames.wordcurve.utils.questionproviders.ChallengeQuestionProvider;
import com.singlemethodgames.wordcurve.utils.tracking.Tracker;

public class LevelCompleteScreen extends BaseScreen {
    private Stage stage;
    private ObjectMap<String, Level> levels;
    private final Variant variant;

    public LevelCompleteScreen(final WordCurveGame game, final GameProgress gameProgress, final Variant variant, final ChallengeQuestionProvider challengeQuestionProvider) {
        super(game);
        this.variant = variant;
        game.clearSave();

        Json json = new Json();
        final ObjectMap<String, ChallengeSet> challenges = json.fromJson(ObjectMap.class, Gdx.files.internal(Constants.JsonFiles.CHALLENGES));
        this.levels = challenges.get(variant.toString()).getLevels();

        I18NBundle myBundle = game.getAssetManager().get(Assets.stringsBundle);
        TextureAtlas textureAtlas = game.getAssetManager().get(Assets.levelSelectAtlas);
        BitmapFont font60 = game.getAssetManager().get(Constants.Fonts.SIZE60, BitmapFont.class);
        BitmapFont font72 = game.getAssetManager().get(Constants.Fonts.SIZE72, BitmapFont.class);
        BitmapFont font96 = game.getAssetManager().get(Constants.Fonts.SIZE96, BitmapFont.class);

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

        Label.LabelStyle labelStyle = new Label.LabelStyle(font96, Color.WHITE);
        Label levelFinishedLabel = new Label(myBundle.get("level_complete"), labelStyle);
        levelFinishedLabel.setAlignment(Align.center);

        TextureRegionDrawable starDrawable = new TextureRegionDrawable(textureAtlas.findRegion(Constants.TextureRegions.STAR));
        Image pointsImage = new Image(starDrawable.tint(Color.YELLOW));
        pointsImage.setScaling(Scaling.fit);
        pointsImage.setOrigin(Align.center);

        ChallengeTiers challengeTiers = challengeQuestionProvider.getChallengeTiers();
        Color finalScoreColour = challengeTiers.getColourForScore(gameProgress.getScore());

        TextureRegionDrawable trophyDrawable = new TextureRegionDrawable(textureAtlas.findRegion(Constants.TextureRegions.TROPHY));
        Image trophyImage = new Image(trophyDrawable.tint(finalScoreColour));

        Label.LabelStyle labelStyleMedium = new Label.LabelStyle(font72, Color.WHITE);
        Label endScore = new Label(String.valueOf(gameProgress.getScore()), labelStyleMedium);
        endScore.setAlignment(Align.center);

        Drawable drawable = new TextureRegionDrawable(textureAtlas.findRegion(Constants.TextureRegions.EXIT_BUTTON));
        final ImageButton exitButton = new ImageButton(drawable);
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                goToMainMenu();
            }
        });

        NinePatchDrawable normal = new NinePatchDrawable(textureAtlas.createPatch(Constants.TextureRegions.BUTTON));
        NinePatchDrawable pressed = normal.tint(Color.LIGHT_GRAY);
        ImageTextButton.ImageTextButtonStyle imageTextButtonStyle = new ImageTextButton.ImageTextButtonStyle(normal, pressed, normal, font72);
        final ImageTextButton playAgainButton = new ImageTextButton(myBundle.get("play_again"), imageTextButtonStyle);
        playAgainButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                game.newSeed();
                challengeQuestionProvider.reset(game.random);
                final LetterSettings letterSettings = LetterDifficulty.getSettingForCorrect(game.gamePreferences.getLetterDifficulty(variant));
                final SpeedSetting speedSetting = game.gamePreferences.getSpeedDifficulty(variant);
                Tracker tracker = new Tracker(variant.toString(), GameMode.CHALLENGES.toString(), letterSettings.start, speedSetting.start);
                SaveState saveState = new SaveState(game.getSeed(), variant, GameMode.CHALLENGES, tracker, false);
                GameBar.createNewStateForMode(saveState);
                saveState.getGameState().setLetterDifficultyCorrect(letterSettings.start);
                saveState.getGameState().setSpeedDifficultyCorrect(speedSetting.start);
                switch (variant) {
                    case CLASSIC:
                        game.setScreen(new Classic(game, saveState, challengeQuestionProvider));
                        break;
                    case SWITCH:
                        game.setScreen(new Switch(game, saveState, challengeQuestionProvider));
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

        Table table = new Table();
        table.setFillParent(true);
        table.align(Align.center | Align.top);
        table.add(levelFinishedLabel).center().padTop(50f);

        Table pointsTable = new Table();
        pointsTable.add(pointsImage).width(100).center().expandX().expandY().right().padRight(15f);
        pointsTable.add(endScore).center().expandX().expandY().left().padLeft(15f);
        table.row();
        table.add(pointsTable).center().expandY();

        if(gameProgress.getScore() == challengeTiers.getPlatinum()) {
            Label perfectScoreLabel = new Label(myBundle.get("perfect_score"), labelStyleMedium);
            table.row();
            table.add(perfectScoreLabel).expandY();
        }

        ChallengeTiers.Trophies trophyAwared = challengeTiers.getTrophyType(gameProgress.getScore());
        String message;
        if(trophyAwared.equals(ChallengeTiers.Trophies.NONE)) {
            message = myBundle.get("no_trophy");
        } else {
            labelStyleMedium.font.getData().markupEnabled = true;
            String trophyWon = "";
            switch(trophyAwared) {
                case GOLD: trophyWon = myBundle.get("gold_trophy");
                    break;
                case BRONZE: trophyWon = myBundle.get("bronze_trophy");
                    break;
                case SILVER: trophyWon = myBundle.get("silver_trophy");
                    break;
                case PLATINUM: trophyWon = myBundle.get("platinum_trophy");
                    break;
            }
            message = myBundle.format("you_won", "[#" + finalScoreColour.toString() + "]" + trophyWon + "[]");
        }

        Label messageLabel = new Label(message, labelStyleMedium);
        table.row();
        table.add(messageLabel).center().padBottom(50f);

        table.row();
        table.add(trophyImage).center().padBottom(50f);

        if(gameProgress.getScore() != challengeTiers.getPlatinum()) {
            Image nextTrophyImage = new Image(trophyDrawable.tint(challengeTiers.getNextColourFromScore(gameProgress.getScore())));
            Label nextTrophyLabel = new Label(myBundle.get("next_trophy"), labelStyleMedium);
            String nextPoints = String.valueOf(challengeTiers.getNextScore(gameProgress.getScore()));
            Label nextPointsLabel = new Label(nextPoints, labelStyleMedium);
            addNextTrophyToTable(table, nextTrophyLabel, nextPointsLabel, nextTrophyImage, starDrawable);
        }

        table.row();
        table.add(playAgainButton).center().size(500, 200).expandY();
        table.row();
        table.add(rateAppButton).center().size(300, 100).expandY();
        table.row();
        table.add(exitButton).center().bottom().size(150f).padBottom(100f);

        stage.addActor(table);
        uploadScore(variant, challengeQuestionProvider.getLevelName(), gameProgress);
    }

    public static void addNextTrophyToTable(Table table, Label nextTrophyLabel, Label nextPointsLabel, Image nextTrophyImage, TextureRegionDrawable starDrawable) {
        nextTrophyLabel.setAlignment(Align.center);

        float starIconHeight = 60f;
        Image nextStarImage = new Image(starDrawable.tint(Color.YELLOW));
        float newStarWidth = (starIconHeight / nextStarImage.getHeight()) * nextStarImage.getWidth();

        float trophyIconHeight = 100f;
        float newTrophyWidth = (trophyIconHeight / nextTrophyImage.getHeight()) * nextTrophyImage.getWidth();

        nextStarImage.setOrigin(Align.center);

        table.row();
        table.add(nextTrophyLabel).padBottom(50f);
        table.row();
        table.add(nextTrophyImage).center().size(newTrophyWidth, trophyIconHeight).padBottom(50f);
        table.row();

        Table nextPointsTable = new Table();
        nextPointsTable.add(nextStarImage).size(newStarWidth, starIconHeight).center().expandX().expandY().right().padRight(15f);
        nextPointsTable.add(nextPointsLabel).center().expandX().expandY().left().padLeft(15f);
        table.add(nextPointsTable);
    }

    private void uploadScore(final Variant variant, final String level, GameProgress gameProgress) {
        game.highScores.setHighscoreForChallenge(variant, level, gameProgress.getScore());
    }

    @Override
    public void show() {
        ChallengesBar.unlockChallengeAchievements(game, variant, levels);
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
