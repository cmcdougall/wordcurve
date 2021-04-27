package com.singlemethodgames.wordcurve.actors.gamebar;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.I18NBundle;
import com.singlemethodgames.wordcurve.SaveState;
import com.singlemethodgames.wordcurve.WordCurveGame;
import com.singlemethodgames.wordcurve.screens.difficulty.LetterDifficulty;
import com.singlemethodgames.wordcurve.screens.difficulty.LetterSettings;
import com.singlemethodgames.wordcurve.screens.difficulty.SpeedDifficulty;
import com.singlemethodgames.wordcurve.screens.variants.BaseMode;
import com.singlemethodgames.wordcurve.screens.variants.Variant;
import com.singlemethodgames.wordcurve.services.GameServices;
import com.singlemethodgames.wordcurve.services.PlatformResolver;
import com.singlemethodgames.wordcurve.utils.Constants;
import com.singlemethodgames.wordcurve.utils.progress.GameProgress;
import com.singlemethodgames.wordcurve.utils.questionproviders.QuestionProvider;

import java.io.Serializable;

public abstract class GameBar<T extends GameBar.State> extends Table {
    Label userScoreLabel;
    Image pointsImage;
    private QuestionProvider questionProvider;
    TextureAtlas textureAtlas;
    I18NBundle bundle;
    private GameProgress gameProgress;
    Label pointsLabel;
    private Label replayPointsLabel;
    protected LetterDifficulty letterDifficulty;
    protected SpeedDifficulty speedDifficulty;
    int replayCount;
    BaseMode baseMode;
    // Keep track of time
    float elapsedTime = 0f;
    private boolean training;
    protected SaveState saveState;

    public GameBar(BaseMode baseMode, SaveState saveState, QuestionProvider questionProvider, TextureAtlas textureAtlas, I18NBundle bundle, BitmapFont font72) {
        this.baseMode = baseMode;
        this.saveState = saveState;
        this.training = saveState.isTraining();
        gameProgress = saveState.getGameState().getGameProgress();
        replayCount = saveState.getGameState().replayCount;
        this.questionProvider = questionProvider;
        this.textureAtlas = textureAtlas;
        this.bundle = bundle;

        pointsLabel = new Label(bundle.format("add_to", 0), new Label.LabelStyle(font72, Color.YELLOW));
        pointsLabel.setAlignment(Align.center);

        replayPointsLabel = new Label(String.valueOf(0), new Label.LabelStyle(font72, Color.GRAY));
        replayPointsLabel.setAlignment(Align.center);

        // Create the labels with the white color, this allows the action to change the tinting of the Label properly
        Label.LabelStyle labelStyle = new Label.LabelStyle(font72, Constants.Colours.Score.NORMAL);
        userScoreLabel = new Label(String.valueOf(gameProgress.getScore()), labelStyle);
        userScoreLabel.setAlignment(Align.center);
        userScoreLabel.getColor().a = 0f;

        pointsImage = new Image(textureAtlas.findRegion(Constants.TextureRegions.STAR));
        pointsImage.setSize(60, 60);
        pointsImage.setColor(Color.YELLOW);
        pointsImage.setOrigin(Align.center);
        pointsImage.scaleBy(-1);

        setFillParent(true);
        align(Align.center | Align.top);

        padTop(10f);

        add(pointsImage).size(60f).center().pad(0, 50, 0, 25);
        add(this.userScoreLabel).height(100f).center().left().expandX();
    }

    public void correctAnswer(float time, final GameMode mode) {
        if(!mode.equals(GameMode.CASUAL) && !mode.equals(GameMode.CHALLENGES)) {
            letterDifficulty.correctAnswer();
            speedDifficulty.correctAnswer();
        }

        if(!mode.equals(GameMode.CHALLENGES) && areLettersCleared() && !training) {
            allLettersCleared(baseMode.game.gameServices, baseMode.game.platformResolver, saveState.getVariant());
        }

        if(!mode.equals(GameMode.CHALLENGES) && areKeysCleared() && !training) {
            allKeysCleared(baseMode.game.gameServices, baseMode.game.platformResolver, saveState.getVariant());
        }

        gameProgress.correctAnswer(getScore());

        userScoreLabel.clearActions();
        userScoreLabel.addAction(Actions.sequence(
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        userScoreLabel.setText(String.valueOf(gameProgress.getScore()));
                    }
                }),
                Actions.color(Constants.Colours.Score.CORRECT),
                Actions.delay(0.5f),
                Actions.color(Constants.Colours.Score.NORMAL, 1f, Interpolation.sine)
        ));

        pointsImage.clearActions();
        pointsImage.addAction(
                Actions.sequence(
                        Actions.color(Constants.Colours.Score.CORRECT),
                        Actions.delay(0.5f),
                        Actions.color(Color.YELLOW, 1f, Interpolation.sine)
                )
        );
    }

    public void incorrectAnswer(float time, final GameMode mode) {
        gameProgress.incorrectAnswer();

        userScoreLabel.clearActions();
        userScoreLabel.addAction(Actions.sequence(
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        userScoreLabel.setText(String.valueOf(gameProgress.getScore()));
                    }
                }),
                Actions.color(Constants.Colours.Score.INCORRECT),
                Actions.delay(0.5f),
                Actions.color(Constants.Colours.Score.NORMAL, 1f, Interpolation.sine)
        ));

        pointsImage.clearActions();
        pointsImage.addAction(
                Actions.sequence(
                        Actions.color(Constants.Colours.Score.INCORRECT),
                        Actions.delay(0.5f),
                        Actions.color(Color.YELLOW, 1f, Interpolation.sine)
                )
        );
    }

    public QuestionProvider getQuestionProvider() {
        return questionProvider;
    }

    public GameProgress getGameProgress() {
        return gameProgress;
    }

    public void pointsValueChanged() {
        pointsLabel.setText(bundle.format("add_to", getScore()));
        replayPointsLabel.setText(String.valueOf(getScore()));
    }

    public int getScore() {
        int totalPoints = letterDifficulty.getPoints() + speedDifficulty.getPoints();
        int deductionIncrement = totalPoints / 10;
        int toDeduct = deductionIncrement * (replayCount + replayCount);
        int finalScore = totalPoints - toDeduct;
        return Math.max(finalScore, 0);
    }

    public void setLetterDifficulty(LetterDifficulty letterDifficulty) {
        this.letterDifficulty = letterDifficulty;
    }

    public void setSpeedDifficulty(SpeedDifficulty speedDifficulty) {
        this.speedDifficulty = speedDifficulty;
    }

    public void increaseReplayCount() {
        replayCount++;
    }
    public void resetReplayCount() {
        replayCount = 0;
    }

    public Table getReplayTable(NinePatchDrawable background) {
        Table table = new Table();
        Image pointsImage = new Image(textureAtlas.findRegion(Constants.TextureRegions.STAR));
        pointsImage.setColor(Color.LIGHT_GRAY);
        pointsImage.setSize(50f, 50f);
        pointsImage.setOrigin(Align.center);

        table.setBackground(background.tint(Color.DARK_GRAY));
        table.add(pointsImage).center().size(50f).padRight(20f).padLeft(20f);
        table.add(replayPointsLabel).padRight(20f);

        return table;
    }

    public void gameFinished(WordCurveGame game, Variant variant) {
        if(!training) {
            game.statistics.updateAnsweredStatForVariant(variant, gameProgress.getMatched());
            int currentCount = game.statistics.getTotalAnsweredForVariant(variant);
            if(variant.equals(Variant.CLASSIC)) {
                game.gameServices.incrementAchievement(
                        game.platformResolver.getAchievementClassicFirst100(), gameProgress.getMatched(),
                        currentCount > 100 ? 1f : currentCount / 100f
                );
                game.gameServices.incrementAchievement(
                        game.platformResolver.getAchievementClassicFirst500(), gameProgress.getMatched(),
                        currentCount > 500 ? 1f : currentCount / 500f
                );
                game.gameServices.incrementAchievement(
                        game.platformResolver.getAchievementClassicFirst2000(), gameProgress.getMatched(),
                        currentCount > 2000 ? 1f : currentCount / 2000f
                );
            } else if (variant.equals(Variant.SWITCH)) {
                game.gameServices.incrementAchievement(
                        game.platformResolver.getAchievementSwitchFirst100(), gameProgress.getMatched(),
                        currentCount > 100 ? 1f : currentCount / 100f
                );
                game.gameServices.incrementAchievement(
                        game.platformResolver.getAchievementSwitchFirst500(), gameProgress.getMatched(),
                        currentCount > 500 ? 1f : currentCount / 500f
                );
                game.gameServices.incrementAchievement(
                        game.platformResolver.getAchievementSwitchFirst2000(), gameProgress.getMatched(),
                        currentCount > 2000 ? 1f : currentCount / 2000f
                );
            }
        }
    }

    private boolean areLettersCleared() {
        LetterSettings startSetting = letterDifficulty.getStartDifficulty();
        LetterSettings currentSetting = letterDifficulty.getLetterSettings();

        return LetterSettings.NO_LETTERS.compareTo(currentSetting) <= 0
                && LetterSettings.DISAPPEARING_LETTERS.compareTo(startSetting) > 0
                && getGameProgress().getMatched() >= LetterSettings.NO_LETTERS.start;
    }

    private boolean areKeysCleared() {
        LetterSettings startSetting = letterDifficulty.getStartDifficulty();
        LetterSettings currentSetting = letterDifficulty.getLetterSettings();
        final int toUnlock = LetterSettings.NONE.start - letterDifficulty.getStart();

        return LetterSettings.NONE.compareTo(currentSetting) <= 0
                && LetterSettings.DISAPPEARING_KEYS.compareTo(startSetting) > 0
                && getGameProgress().getMatched() > toUnlock;
    }

    public boolean isTraining() {
        return training;
    }

    public float getElapsedTime() {
        return this.elapsedTime;
    }
    public void resetElapsedTime() {
        this.elapsedTime = 0;
    }

    abstract public Table getCorrectTable(BitmapFont bitmapFont, NinePatchDrawable background);
    abstract public Table getIncorrectTable(BitmapFont bitmapFont, NinePatchDrawable background);
    abstract public void beginAnimateUI();
    abstract public boolean updateState(float elapsedTime);
    abstract public boolean newQuestion();
    abstract public int getColSpan();
    abstract public T getState();
    abstract void allLettersCleared(GameServices gameServices, PlatformResolver platformResolver, Variant variant);
    abstract void allKeysCleared(GameServices gameServices, PlatformResolver platformResolver, Variant variant);

    public static class State implements Serializable {
        public GameProgress gameProgress;
        int replayCount;
        int letterDifficultyCorrect;
        int letterDifficultyStart;
        int speedDifficultyCorrect;
        int speedDifficultyStart;

        State() {
            gameProgress = new GameProgress();
            replayCount = 0;
            letterDifficultyCorrect = 0;
            letterDifficultyStart = 0;
            speedDifficultyCorrect = 0;
            speedDifficultyStart = 0;
        }

        State(GameProgress gameProgress, int replayCount, int speedDifficultyCorrect, int speedDifficultyStart, int letterDifficultyCorrect, int letterDifficultyStart) {
            this.gameProgress = gameProgress;
            this.replayCount = replayCount;
            this.letterDifficultyCorrect = letterDifficultyCorrect;
            this.letterDifficultyStart = letterDifficultyStart;
            this.speedDifficultyCorrect = speedDifficultyCorrect;
            this.speedDifficultyStart = speedDifficultyStart;
        }

        GameProgress getGameProgress() {
            return gameProgress;
        }

        public void setGameProgress(GameProgress gameProgress) {
            this.gameProgress = gameProgress;
        }

        public int getReplayCount() {
            return replayCount;
        }

        public void setReplayCount(int replayCount) {
            this.replayCount = replayCount;
        }

        public int getLetterDifficultyCorrect() {
            return letterDifficultyCorrect;
        }

        public void setLetterDifficultyCorrect(int letterDifficultyCorrect) {
            this.letterDifficultyCorrect = letterDifficultyCorrect;
        }

        public int getSpeedDifficultyCorrect() {
            return speedDifficultyCorrect;
        }

        public void setSpeedDifficultyCorrect(int speedDifficultyCorrect) {
            this.speedDifficultyCorrect = speedDifficultyCorrect;
        }

        public int getLetterDifficultyStart() {
            return letterDifficultyStart;
        }

        public void setLetterDifficultyStart(int letterDifficultyStart) {
            this.letterDifficultyStart = letterDifficultyStart;
        }

        public int getSpeedDifficultyStart() {
            return speedDifficultyStart;
        }

        public void setSpeedDifficultyStart(int speedDifficultyStart) {
            this.speedDifficultyStart = speedDifficultyStart;
        }

        @Override
        public String toString() {
            return "State{" +
                    "gameProgress=" + gameProgress +
                    ", replayCount=" + replayCount +
                    ", letterDifficultyCorrect=" + letterDifficultyCorrect +
                    ", letterDifficultyStart=" + letterDifficultyStart +
                    ", speedDifficultyCorrect=" + speedDifficultyCorrect +
                    ", speedDifficultyStart=" + speedDifficultyStart +
                    '}';
        }
    }

    public static void createNewStateForMode(SaveState saveState) {
        createNewStateForMode(saveState, true);
    }

    public static void createNewStateForMode(SaveState saveState, boolean increaseDifficulty) {
        GameBar.State state;
        switch(saveState.getGameMode()) {
            case LIFE:
                state = new LifeBar.LifeState();
                break;
            case TIME:
                state = new TimeBar.TimeState();
                break;
            case CHALLENGES:
                state = new ChallengesBar.ChallengeState();
                break;
            default:
                state = new CasualBar.CasualState(increaseDifficulty);
        }
        saveState.setGameState(state);
    }
}
