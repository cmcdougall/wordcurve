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
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.I18NBundle;
import com.singlemethodgames.wordcurve.SaveState;
import com.singlemethodgames.wordcurve.screens.variants.BaseMode;
import com.singlemethodgames.wordcurve.screens.variants.Variant;
import com.singlemethodgames.wordcurve.services.GameServices;
import com.singlemethodgames.wordcurve.services.PlatformResolver;
import com.singlemethodgames.wordcurve.utils.Constants;
import com.singlemethodgames.wordcurve.utils.progress.GameProgress;
import com.singlemethodgames.wordcurve.utils.questionproviders.QuestionProvider;
import com.singlemethodgames.wordcurve.utils.questionproviders.UnlimitedQuestionProvider;

public class CasualBar extends GameBar {
    private Label correctAnswerLabel;
    private Image correctImage;
    private UnlimitedQuestionProvider questionProvider;
    private boolean increaseDifficulty;

    public CasualBar(BaseMode baseMode, SaveState saveState, UnlimitedQuestionProvider questionProvider, TextureAtlas textureAtlas, I18NBundle bundle, BitmapFont bitmapFont) {
        super(baseMode, saveState, questionProvider, textureAtlas, bundle, bitmapFont);
        this.questionProvider = questionProvider;
        this.increaseDifficulty = ((CasualState)saveState.getGameState()).increaseDifficulty;

        TextureRegionDrawable correctTexture = new TextureRegionDrawable(textureAtlas.findRegion(Constants.TextureRegions.CORRECT_ICON));
        correctImage = new Image(correctTexture);
        correctImage.setSize(correctTexture.getMinWidth(), correctTexture.getMinHeight());
        correctImage.setAlign(Align.center);
        correctImage.scaleBy(-1);

        correctAnswerLabel = new Label(String.valueOf(getGameProgress().getMatched()), new Label.LabelStyle(bitmapFont, Constants.Colours.Score.NORMAL));
        correctAnswerLabel.setAlignment(Align.center);
        correctAnswerLabel.getColor().a = 0f;

        add(correctAnswerLabel).right().expandX().height(100f);
        add(correctImage).width(correctImage.getWidth()).center().pad(0, 25, 0, 50);
        padBottom(10f);
    }

    @Override
    public Table getCorrectTable(BitmapFont bitmapFont, NinePatchDrawable background) {
        Table table = new Table();
        Image pointsImage = new Image(textureAtlas.findRegion(Constants.TextureRegions.STAR));
        pointsImage.setColor(Color.YELLOW);
        pointsImage.setSize(50f, 50f);
        pointsImage.setOrigin(Align.center);

        pointsLabel = new Label("0", new Label.LabelStyle(bitmapFont, Color.YELLOW));
        pointsLabel.setAlignment(Align.center);

        table.background(background.tint(Color.DARK_GRAY));
        table.add(pointsImage).center().size(50f).padRight(20f).padLeft(20f);
        table.add(pointsLabel).padRight(20f);

        return table;
    }

    @Override
    public Table getIncorrectTable(BitmapFont bitmapFont, NinePatchDrawable background) {
        return new Table();
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

        correctImage.addAction(
                Actions.sequence(
                        Actions.delay(2f),
                        Actions.scaleBy(1.5f, 1.5f, 0.25f, Interpolation.sine),
                        Actions.scaleBy(-0.5f, -0.5f, 0.25f, Interpolation.sine)
                )
        );

        correctAnswerLabel.addAction(Actions.sequence(
                Actions.delay(2f),
                Actions.fadeIn(0.5f)

        ));
    }

    @Override
    public boolean updateState(float elapsedTime) {
        this.elapsedTime += elapsedTime;
        return true;
    }

    @Override
    public boolean newQuestion() {
        return true;
    }

    @Override
    public int getColSpan() {
        return 4;
    }

    @Override
    public void correctAnswer(float time, GameMode gameMode) {
        super.correctAnswer(time, gameMode);
        if(increaseDifficulty) {
            letterDifficulty.correctAnswer();
            speedDifficulty.correctAnswer();
        }

        int correctAnswers = getGameProgress().getMatched();
        correctAnswerLabel.setText(String.valueOf(correctAnswers));

        baseMode.game.statistics.updateAnsweredStatForVariantMode(saveState.getVariant(), GameMode.CASUAL, 1);
        int currentCount = baseMode.game.statistics.getTotalAnsweredForVariantMode(saveState.getVariant(), GameMode.CASUAL);

        if(saveState.getVariant().equals(Variant.CLASSIC) && !isTraining()) {
            baseMode.game.gameServices.incrementAchievement(
                    baseMode.game.platformResolver.getAchievementClassicCasualFirst10(), 1,
                    currentCount > 10 ? 1f : currentCount / 10f
            );
            baseMode.game.gameServices.incrementAchievement(
                    baseMode.game.platformResolver.getAchievementClassicCasualFirst50(), 1,
                    currentCount > 50 ? 1f : currentCount / 50f
            );
            baseMode.game.gameServices.incrementAchievement(
                    baseMode.game.platformResolver.getAchievementClassicCasualFirst100(), 1,
                    currentCount > 100 ? 1f : currentCount / 100f
            );
        } else if (saveState.getVariant().equals(Variant.SWITCH) && !isTraining()) {
            baseMode.game.gameServices.incrementAchievement(
                    baseMode.game.platformResolver.getAchievementSwitchCasualFirst10(), 1,
                    currentCount > 10 ? 1f : currentCount / 10f
            );
            baseMode.game.gameServices.incrementAchievement(
                    baseMode.game.platformResolver.getAchievementSwitchCasualFirst50(), 1,
                    currentCount > 50 ? 1f : currentCount / 50f
            );
            baseMode.game.gameServices.incrementAchievement(
                    baseMode.game.platformResolver.getAchievementSwitchCasualFirst100(), 1,
                    currentCount > 100 ? 1f : currentCount / 100f
            );
        }

        int difference = correctAnswers - getGameProgress().getIncorrectAnswers();
        updateQuestionLengths(difference, questionProvider);
    }

    @Override
    public void incorrectAnswer(float time, GameMode gameMode) {
        super.incorrectAnswer(time, gameMode);
        if(increaseDifficulty) {
            letterDifficulty.incorrectAnswer();
            speedDifficulty.incorrectAnswer();
        }

        updateQuestionLengths(getGameProgress().getMatched() - getGameProgress().getIncorrectAnswers(), questionProvider);
    }

    static void updateQuestionLengths(int difference, UnlimitedQuestionProvider questionProvider) {
        if(difference <= 3) {
            questionProvider.setLengths(3, 4);
        } else if(difference <= 6) {
            questionProvider.setLengths(4, 6);
        } else if (difference < 9) {
            questionProvider.setLengths(4, 8);
        } else {
            questionProvider.setLengths(4, 11);
        }
    }

    @Override
    public QuestionProvider getQuestionProvider() {
        return questionProvider;
    }

    @Override
    public CasualState getState() {
        return new CasualState(getGameProgress(), questionProvider.getState(), increaseDifficulty, replayCount,
                speedDifficulty.getCurrentCorrect(), speedDifficulty.getStart(),
                letterDifficulty.getCurrentCorrect(), letterDifficulty.getStart()
        );
    }

    public static class CasualState extends State {
        private boolean increaseDifficulty;
        private UnlimitedQuestionProvider.UnlimitedState unlimitedState;

        public CasualState() {
        }

        public CasualState(final boolean increaseDifficulty) {
            this.increaseDifficulty = increaseDifficulty;
        }

        public CasualState(GameProgress gameProgress, UnlimitedQuestionProvider.UnlimitedState unlimitedState, boolean increaseDifficulty, int replayCount, int speedCurrentCorrect, int speedStart, int letterCurrentCorrect, int letterStart) {
            super(gameProgress, replayCount, speedCurrentCorrect, speedStart, letterCurrentCorrect, letterStart);
            this.unlimitedState = unlimitedState;
            this.increaseDifficulty = increaseDifficulty;
        }

        public UnlimitedQuestionProvider.UnlimitedState getUnlimitedState() {
            return unlimitedState;
        }

        public void setUnlimitedState(UnlimitedQuestionProvider.UnlimitedState unlimitedState) {
            this.unlimitedState = unlimitedState;
        }

        public boolean isIncreaseDifficulty() {
            return increaseDifficulty;
        }

        public void setIncreaseDifficulty(boolean increaseDifficulty) {
            this.increaseDifficulty = increaseDifficulty;
        }
    }

    @Override
    void allLettersCleared(GameServices gameServices, PlatformResolver platformResolver, Variant variant) {
        if(variant.equals(Variant.CLASSIC)) {
            gameServices.unlockAchievement(platformResolver.getAchievementClassicCasualRemoveAllLetters());
        } else if (variant.equals(Variant.SWITCH)) {
            gameServices.unlockAchievement(platformResolver.getAchievementSwitchCasualRemoveAllLetters());
        }
    }

    @Override
    void allKeysCleared(GameServices gameServices, PlatformResolver platformResolver, Variant variant) {
        if(variant.equals(Variant.CLASSIC)) {
            gameServices.unlockAchievement(platformResolver.getAchievementClassicCasualRemoveAllKeys());
        } else if (variant.equals(Variant.SWITCH)) {
            gameServices.unlockAchievement(platformResolver.getAchievementSwitchCasualRemoveAllKeys());
        }
    }
}
