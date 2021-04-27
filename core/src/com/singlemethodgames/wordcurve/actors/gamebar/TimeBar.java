package com.singlemethodgames.wordcurve.actors.gamebar;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.I18NBundle;
import com.singlemethodgames.wordcurve.SaveState;
import com.singlemethodgames.wordcurve.WordCurveGame;
import com.singlemethodgames.wordcurve.screens.variants.BaseMode;
import com.singlemethodgames.wordcurve.screens.variants.Variant;
import com.singlemethodgames.wordcurve.services.GameServices;
import com.singlemethodgames.wordcurve.services.PlatformResolver;
import com.singlemethodgames.wordcurve.utils.Constants;
import com.singlemethodgames.wordcurve.utils.Utils;
import com.singlemethodgames.wordcurve.utils.progress.GameProgress;
import com.singlemethodgames.wordcurve.utils.progress.TimeLeftProgressBar;
import com.singlemethodgames.wordcurve.utils.questionproviders.QuestionProvider;
import com.singlemethodgames.wordcurve.utils.questionproviders.UnlimitedQuestionProvider;

import static com.singlemethodgames.wordcurve.actors.gamebar.CasualBar.updateQuestionLengths;

public class TimeBar extends GameBar {
    private WordCurveGame game;
    private Label timeLeftLabel;
    private TimeLeftProgressBar timeLeftProgressBar;
    private Image clockImage;
    private Image background;
    private static float MAX_TIME = 30f;
    private static final float DANGER_ZONE = 5f;
    private float totalTime;
    private static final float INCORRECT_DEDUCTION = 5f;
    private static final float CORRECT_ADDITION = 5f;
    final private int colSpan;
    private UnlimitedQuestionProvider questionProvider;

    public TimeBar(WordCurveGame game, BaseMode baseMode, SaveState saveState, UnlimitedQuestionProvider questionProvider, TextureAtlas textureAtlas, I18NBundle bundle, BitmapFont bitmapFont, float width, final Image background) {
        super(baseMode, saveState, questionProvider, textureAtlas, bundle, bitmapFont);
        this.game = game;
        this.questionProvider = questionProvider;
        // Create the labels with the white color, this allows the action to change the tinting of the Label properly
        Label.LabelStyle labelStyle = new Label.LabelStyle(bitmapFont, Constants.Colours.Score.NORMAL);
        this.background = background;

        totalTime = ((TimeState)saveState.getGameState()).timeLeft;
        clockImage = new Image(textureAtlas.findRegion(Constants.TextureRegions.CLOCK));
        clockImage.setColor(Constants.Colours.CLOCK_COLOUR);
        clockImage.setSize(60f, 60f);
        clockImage.setOrigin(Align.center);
        clockImage.scaleBy(-1);

        timeLeftLabel = new Label("", labelStyle);
        updateTimeLeftLabel(timeLeftLabel, totalTime);
        timeLeftLabel.setAlignment(Align.center);
        timeLeftLabel.getColor().a = 0f;

        add(timeLeftLabel).right().expandX().height(100f);
        add(clockImage).size(60f).center().pad(0, 25, 0, 50);

        TextureRegion whitePixelRegion = textureAtlas.findRegion(Constants.TextureRegions.WHITE_PIXEL);
        Utils.fixWhitePixelRegion(whitePixelRegion);

        colSpan = 4;
        timeLeftProgressBar = new TimeLeftProgressBar((int)width, 20, 0, MAX_TIME, new TextureRegionDrawable(whitePixelRegion));
        timeLeftProgressBar.fadeOut(0f);
        timeLeftProgressBar.setPercent(totalTime);
        row();
        add(timeLeftProgressBar).padTop(10f).width(width).colspan(colSpan);
    }

    @Override
    public int getColSpan() {
        return colSpan;
    }

    @Override
    public Table getCorrectTable(BitmapFont bitmapFont, NinePatchDrawable background) {
        Table table = new Table();
        Image pointsImage = new Image(textureAtlas.findRegion(Constants.TextureRegions.STAR));
        pointsImage.setColor(Color.YELLOW);
        pointsImage.setSize(50f, 50f);
        pointsImage.setOrigin(Align.center);

        table.add(pointsImage).center().size(50f).padRight(20f).padLeft(20f);
        table.add(pointsLabel).padRight(20f);
        table.background(background.tint(Color.DARK_GRAY));
        return createAnswerTable(table, bundle.format("add_to", CORRECT_ADDITION), bitmapFont, Constants.Colours.Keyboard.Button.CORRECT);
    }

    @Override
    public Table getIncorrectTable(BitmapFont bitmapFont, NinePatchDrawable background) {
        Table table = new Table();
        table.background(background.tint(Color.DARK_GRAY));
        return createAnswerTable(table, bundle.format("remove_seconds", INCORRECT_DEDUCTION), bitmapFont, Constants.Colours.INCORRECT_KEY_RED);
    }

    private Table createAnswerTable(Table table, String labelText, BitmapFont bitmapFont, Color colour) {
        Image clockImage = new Image(textureAtlas.findRegion(Constants.TextureRegions.CLOCK));
        clockImage.setColor(colour);
        clockImage.setSize(50f, 50f);
        clockImage.setOrigin(Align.center);

        Label label = new Label(labelText, new Label.LabelStyle(bitmapFont, colour));
        label.setAlignment(Align.center);
        table.add(label).padLeft(20f);
        table.add(clockImage).center().size(50f).padLeft(20f).padRight(20f);
        table.addAction(Actions.fadeOut(0f));
        table.pack();
        table.setOrigin(Align.center);

        return table;
    }

    @Override
    public void beginAnimateUI() {
        // First load the labels
        userScoreLabel.addAction(
                Actions.sequence(
                        Actions.delay(1f),
                        Actions.fadeIn(0.5f)
                )
        );

        pointsImage.addAction(Actions.sequence(
                Actions.delay(1f),
                Actions.scaleBy(1.5f, 1.5f, 0.25f, Interpolation.sine),
                Actions.scaleBy(-0.5f, -0.5f, 0.25f, Interpolation.sine)
        ));

        clockImage.addAction(
                Actions.sequence(
                        Actions.delay(2f),
                        Actions.scaleBy(1.5f, 1.5f, 0.25f, Interpolation.sine),
                        Actions.scaleBy(-0.5f, -0.5f, 0.25f, Interpolation.sine)
                )
        );

        timeLeftLabel.addAction(
                Actions.sequence(
                        Actions.delay(2f),
                        Actions.parallel(
                                Actions.fadeIn(0.5f),
                                Actions.run(
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                timeLeftProgressBar.fadeIn(0.5f);
                                            }
                                        }
                                )
                        )

        ));
    }

    @Override
    public void correctAnswer(float time, GameMode gameMode) {
        super.correctAnswer(time, gameMode);

        baseMode.game.statistics.updateAnsweredStatForVariantMode(saveState.getVariant(), GameMode.TIME, 1);
        int currentCount = baseMode.game.statistics.getTotalAnsweredForVariantMode(saveState.getVariant(), GameMode.TIME);

        if(saveState.getVariant().equals(Variant.CLASSIC) && !isTraining()) {
            baseMode.game.gameServices.incrementAchievement(
                    baseMode.game.platformResolver.getAchievementClassicTimeFirst10(), 1,
                    currentCount > 10 ? 1f : currentCount / 10f
            );
            baseMode.game.gameServices.incrementAchievement(
                    baseMode.game.platformResolver.getAchievementClassicTimeFirst50(), 1,
                    currentCount > 50 ? 1f : currentCount / 50f
            );
            baseMode.game.gameServices.incrementAchievement(
                    baseMode.game.platformResolver.getAchievementClassicTimeFirst100(), 1,
                    currentCount > 100 ? 1f : currentCount / 100f
            );
        } else if (saveState.getVariant().equals(Variant.SWITCH) && !isTraining()) {
            baseMode.game.gameServices.incrementAchievement(
                    baseMode.game.platformResolver.getAchievementSwitchTimeFirst10(), 1,
                    currentCount > 10 ? 1f : currentCount / 10f
            );
            baseMode.game.gameServices.incrementAchievement(
                    baseMode.game.platformResolver.getAchievementSwitchTimeFirst50(), 1,
                    currentCount > 50 ? 1f : currentCount / 50f
            );
            baseMode.game.gameServices.incrementAchievement(
                    baseMode.game.platformResolver.getAchievementSwitchTimeFirst100(), 1,
                    currentCount > 100 ? 1f : currentCount / 100f
            );
        }

        // HACK! It's not the time left, it's the time total!
        updateTimeLeftLabel(timeLeftLabel, time);
        flashActorWithColour(timeLeftLabel, Constants.Colours.Keyboard.Button.CORRECT, 3);
        flashActorWithColour(clockImage, Constants.Colours.Keyboard.Button.CORRECT, 3);
        this.totalTime = this.totalTime - time + CORRECT_ADDITION;
        if(this.totalTime > MAX_TIME) {
            this.totalTime = MAX_TIME;
        }

        if(this.totalTime > DANGER_ZONE) {
            background.getColor().a = 0;
        }

        updateQuestionLengths(getGameProgress().getMatched() - getGameProgress().getIncorrectAnswers(), questionProvider);
    }

    private static void flashActorWithColourForever(Actor actor, Color color) {
        flashActorWithColour(actor, color, -1);
    }

    private static void flashActorWithColour(Actor actor, Color color, int count) {
        actor.clearActions();
        actor.addAction(
                Actions.sequence(
                        Actions.color(color),
                        Actions.repeat(count,
                                Actions.sequence(
                                        Actions.fadeOut(0f),
                                        Actions.delay(.2f),
                                        Actions.fadeIn(0f),
                                        Actions.delay(.2f)
                                )
                        )
                )
        );
    }

    @Override
    public void incorrectAnswer(float time, GameMode gameMode) {
        super.incorrectAnswer(time, gameMode);
        this.totalTime = this.totalTime - time - INCORRECT_DEDUCTION;

        if (this.totalTime <= 0) {
            noTimeLeft(totalTime);
        }

        updateQuestionLengths(getGameProgress().getMatched() - getGameProgress().getIncorrectAnswers(), questionProvider);
    }

    private void noTimeLeft(float timeLeft) {
        updateTimeLeftLabel(timeLeftLabel, timeLeft);
        flashActorWithColourForever(timeLeftLabel, Constants.Colours.INCORRECT_KEY_RED);
        flashActorWithColourForever(clockImage, Constants.Colours.INCORRECT_KEY_RED);
    }

    @Override
    public boolean updateState(float elapsedTime) {
        this.elapsedTime += elapsedTime;

        float totalTimeLeft = this.totalTime - this.elapsedTime;
        updateTimeLeftLabel(this.timeLeftLabel, totalTimeLeft);

        if (totalTimeLeft >= 0f){
            timeLeftProgressBar.setPercent(totalTimeLeft);
        }

        if (totalTimeLeft < 5f) {
            if(totalTimeLeft <= 0f) {
                background.getColor().a = 0f;
                noTimeLeft(totalTimeLeft);
                this.totalTime = totalTimeLeft;
                return false;
            } else {
                background.getColor().a = calculateAlpha(totalTimeLeft);
            }
        }

        return true;
    }

    private static float calculateAlpha(float totalTimeLeft) {
        return (1f - (totalTimeLeft / DANGER_ZONE)) * 0.75f;
    }

    @Override
    public boolean newQuestion() {
        boolean continueQuestions = this.totalTime > 0;
        if(continueQuestions) {
            timeLeftProgressBar.setPercent(totalTime);
            timeLeftLabel.clearActions();
            updateTimeLeftLabel(this.timeLeftLabel, this.totalTime);
            timeLeftLabel.addAction(Actions.color(Constants.Colours.Timer.NORMAL));

            clockImage.clearActions();
            clockImage.addAction(Actions.color(Constants.Colours.CLOCK_COLOUR));
        }

        return continueQuestions;
    }

    private static void updateTimeLeftLabel(Label timeLeftLabel, float timeLeft) {
        if (timeLeft < 0) {
            timeLeft = 0;
        }

        timeLeftLabel.setText(String.format(java.util.Locale.US, "%.2f", timeLeft));
    }

    @Override
    public QuestionProvider getQuestionProvider() {
        return questionProvider;
    }

    @Override
    public TimeState getState() {
        return new TimeState(getGameProgress(), this.totalTime - this.elapsedTime, questionProvider.getState(), replayCount,
                speedDifficulty.getCurrentCorrect(), speedDifficulty.getStart(),
                letterDifficulty.getCurrentCorrect(), letterDifficulty.getStart());
    }

    public static class TimeState extends State {
        private float timeLeft;
        private UnlimitedQuestionProvider.UnlimitedState unlimitedState;

        public TimeState() {
            timeLeft = 10f;
            unlimitedState = new UnlimitedQuestionProvider.UnlimitedState();
        }

        public TimeState(GameProgress gameProgress, float timeLeft, UnlimitedQuestionProvider.UnlimitedState unlimitedState, int replayCount, int speedCurrentCorrect, int speedStart, int letterCurrentCorrect, int letterStart) {
            super(gameProgress, replayCount, speedCurrentCorrect, speedStart, letterCurrentCorrect, letterStart);
            this.timeLeft = timeLeft;
            this.unlimitedState = unlimitedState;
        }

        public float getTimeLeft() {
            return timeLeft;
        }

        public void setTimeLeft(float timeLeft) {
            this.timeLeft = timeLeft;
        }

        public UnlimitedQuestionProvider.UnlimitedState getUnlimitedState() {
            return unlimitedState;
        }

        public void setUnlimitedState(UnlimitedQuestionProvider.UnlimitedState unlimitedState) {
            this.unlimitedState = unlimitedState;
        }
    }

    @Override
    void allLettersCleared(GameServices gameServices, PlatformResolver platformResolver, Variant variant) {
        if(variant.equals(Variant.CLASSIC)) {
            gameServices.unlockAchievement(platformResolver.getAchievementClassicTimeRemoveAllLetters());
        } else if (variant.equals(Variant.SWITCH)) {
            gameServices.unlockAchievement(platformResolver.getAchievementSwitchTimeRemoveAllLetters());
        }
    }

    @Override
    void allKeysCleared(GameServices gameServices, PlatformResolver platformResolver, Variant variant) {
        if(variant.equals(Variant.CLASSIC)) {
            gameServices.unlockAchievement(platformResolver.getAchievementClassicTimeRemoveAllKeys());
        } else if (variant.equals(Variant.SWITCH)) {
            gameServices.unlockAchievement(platformResolver.getAchievementSwitchTimeRemoveAllKeys());
        }
    }
}
