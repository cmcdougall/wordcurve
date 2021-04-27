package com.singlemethodgames.wordcurve.actors.gamebar;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.ObjectMap;
import com.singlemethodgames.wordcurve.SaveState;
import com.singlemethodgames.wordcurve.WordCurveGame;
import com.singlemethodgames.wordcurve.screens.difficulty.LetterSettings;
import com.singlemethodgames.wordcurve.screens.variants.BaseMode;
import com.singlemethodgames.wordcurve.screens.variants.Variant;
import com.singlemethodgames.wordcurve.services.GameServices;
import com.singlemethodgames.wordcurve.services.PlatformResolver;
import com.singlemethodgames.wordcurve.utils.Constants;
import com.singlemethodgames.wordcurve.utils.Utils;
import com.singlemethodgames.wordcurve.utils.challenges.ChallengeTiers;
import com.singlemethodgames.wordcurve.utils.challenges.Level;
import com.singlemethodgames.wordcurve.utils.progress.GameProgress;
import com.singlemethodgames.wordcurve.utils.questionproviders.ChallengeQuestionProvider;
import com.singlemethodgames.wordcurve.utils.questionproviders.QuestionProvider;

import java.util.Arrays;

public class ChallengesBar extends GameBar {
    private Label correctAnswerLabel;
    private Image correctImage;
    private int colspan;
    private int totalQuestions;
    private int totalCorrect;
    private Image[] questionImages;
    private int currentQuestion;
    private final String correctAnswersLabelText;
    private ChallengeQuestionProvider questionProvider;
    private int[] answerArray;
    private int increaseDifficultyAtCorrect;

    public ChallengesBar(BaseMode baseMode, SaveState saveState, ChallengeQuestionProvider questionProvider, TextureAtlas textureAtlas, I18NBundle bundle, BitmapFont bitmapFont, final int totalQuestions) {
        super(baseMode, saveState, questionProvider, textureAtlas, bundle, bitmapFont);
        this.questionProvider = questionProvider;
        this.totalQuestions = totalQuestions;
        this.increaseDifficultyAtCorrect = this.totalQuestions / 2;
        answerArray = new int[totalQuestions];

        ChallengeState challengeState = (ChallengeState)saveState.getGameState();
        if (challengeState.getAnswerArray() != null) {
            answerArray = challengeState.answerArray;
        } else {
            Arrays.fill(answerArray, -1);
        }
        currentQuestion = challengeState.currentIndex;

        TextureRegionDrawable correctTexture = new TextureRegionDrawable(textureAtlas.findRegion(Constants.TextureRegions.CORRECT_ICON));
        correctImage = new Image(correctTexture);
        correctImage.setAlign(Align.center);
        correctImage.scaleBy(-1);

        correctAnswersLabelText = " / " + totalQuestions;
        totalCorrect = challengeState.totalCorrect;
        correctAnswerLabel = new Label(totalCorrect + correctAnswersLabelText, new Label.LabelStyle(bitmapFont, Constants.Colours.Score.NORMAL));
        correctAnswerLabel.setAlignment(Align.center);
        correctAnswerLabel.getColor().a = 0f;

        colspan = 4;

        add(correctAnswerLabel).height(100f).center().right().expandX();
        add(correctImage).width(correctImage.getWidth()).center().pad(0, 25, 0, 50);

        float sidePadding = 5f;
        float widthForCell = (1080f / (float)totalQuestions) - (sidePadding * 2);

        TextureRegion whitePixelRegion = textureAtlas.findRegion(Constants.TextureRegions.WHITE_PIXEL);
        Utils.fixWhitePixelRegion(whitePixelRegion);

        Drawable drawable = new TextureRegionDrawable(whitePixelRegion);
        drawable.setMinHeight(20f);
        questionImages = new Image[totalQuestions];
        Table imagesTable = new Table();
        imagesTable.setSize(1080f, 20f);
        for(int i = 0; i < totalQuestions; i++) {
            questionImages[i] = new Image(drawable);
            questionImages[i].setHeight(20f);
            questionImages[i].setWidth(widthForCell);
            if(answerArray[i] == 0) {
                questionImages[i].setColor(Constants.Colours.INCORRECT_KEY_RED);
            } else if(answerArray[i] == 1) {
                questionImages[i].setColor(Constants.Colours.Keyboard.Button.CORRECT);
            } else {
                questionImages[i].setColor(Constants.Colours.Keyboard.KEY_COLOUR);
            }
            questionImages[i].getColor().a = 0f;

            imagesTable.add(questionImages[i]).width(widthForCell).padLeft(sidePadding).padRight(sidePadding).expandY();
        }

        row();
        add(imagesTable).colspan(4).padTop(10f);
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

        float delay = 3f / (float)totalQuestions;
        for(int i = 0; i < questionImages.length; i++) {
            questionImages[i].addAction(
                    Actions.sequence(
                        Actions.delay((float)i * delay),
                        Actions.alpha(
                                answerArray[i] == 0 || answerArray[i] == 1 ? 1f : 0.5f,
                                0.2f)
                    )
            );
        }
    }

    @Override
    public boolean updateState(float elapsedTime) {
        this.elapsedTime += elapsedTime;
        return true;
    }

    @Override
    public boolean newQuestion() {
        boolean gameContinues = currentQuestion < totalQuestions;
        if(gameContinues) {
            questionImages[currentQuestion].addAction(
                    Actions.fadeIn(0.2f)
            );
        }

        if (totalCorrect == increaseDifficultyAtCorrect && canIncreaseDifficulty()) {
            if(this.letterDifficulty.getLetterSettings().equals(LetterSettings.LETTERS)) {
                this.letterDifficulty.setCurrentCorrect(LetterSettings.DISAPPEARING_LETTERS.start);
            } else if (this.letterDifficulty.getLetterSettings().equals(LetterSettings.NO_LETTERS)) {
                this.letterDifficulty.setCurrentCorrect(LetterSettings.DISAPPEARING_KEYS.start);
            }
        }

        return gameContinues;
    }

    @Override
    public int getColSpan() {
        return colspan;
    }

    private boolean canIncreaseDifficulty() {
        return this.letterDifficulty.getLetterSettings().equals(LetterSettings.LETTERS) || this.letterDifficulty.getLetterSettings().equals(LetterSettings.NO_LETTERS);
    }

    @Override
    public void correctAnswer(float time, GameMode gameMode) {
        super.correctAnswer(time, gameMode);
        totalCorrect++;

        if(this.letterDifficulty.getLetterSettings().equals(LetterSettings.DISAPPEARING_LETTERS) || this.letterDifficulty.getLetterSettings().equals(LetterSettings.DISAPPEARING_KEYS)) {
            this.letterDifficulty.correctAnswer();
        }

        answerArray[currentQuestion] = 1;
        correctAnswerLabel.setText(totalCorrect + correctAnswersLabelText);
        changeColourAndIncrement(Constants.Colours.Keyboard.Button.CORRECT);
    }

    @Override
    public void incorrectAnswer(float time, GameMode gameMode) {
        super.incorrectAnswer(time, gameMode);
        answerArray[currentQuestion] = 0;
        changeColourAndIncrement(Constants.Colours.INCORRECT_KEY_RED);
    }

    private void changeColourAndIncrement(Color colour) {
        questionImages[currentQuestion].addAction(Actions.color(colour));
        currentQuestion++;
    }

    @Override
    public QuestionProvider getQuestionProvider() {
        return questionProvider;
    }

    @Override
    public ChallengeState getState() {
        return new ChallengeState(getGameProgress(), questionProvider.getLevelName(), questionProvider.getCurrentIndex() - 1, totalCorrect, answerArray, replayCount,
                speedDifficulty.getCurrentCorrect(), speedDifficulty.getStart(),
                letterDifficulty.getCurrentCorrect(), letterDifficulty.getStart()
        );
    }

    public static class ChallengeState extends State {
        private String levelName;
        private int currentIndex;
        private int totalCorrect;
        private int[] answerArray;

        public ChallengeState() {
        }

        public ChallengeState(GameProgress gameProgress, String levelName, int currentIndex, int totalCorrect, int[] answerArray, int replayCount, int speedCurrentCorrect, int speedStart, int letterCurrentCorrect, int letterStart) {
            super(gameProgress, replayCount, speedCurrentCorrect, speedStart, letterCurrentCorrect, letterStart);
            this.levelName = levelName;
            this.currentIndex = currentIndex;
            this.answerArray = answerArray;
            this.totalCorrect = totalCorrect;
        }

        public String getLevelName() {
            return levelName;
        }

        public void setLevelName(String levelName) {
            this.levelName = levelName;
        }

        public int getCurrentIndex() {
            return currentIndex;
        }

        public void setCurrentIndex(int currentIndex) {
            this.currentIndex = currentIndex;
        }

        public int[] getAnswerArray() {
            return answerArray;
        }

        public void setAnswerArray(int[] answerArray) {
            this.answerArray = answerArray;
        }

        public int getTotalCorrect() {
            return totalCorrect;
        }

        public void setTotalCorrect(int totalCorrect) {
            this.totalCorrect = totalCorrect;
        }
    }

    public static void unlockChallengeAchievements(final WordCurveGame game, Variant variant, ObjectMap<String, Level> levels) {
        boolean unlockedAllBronze = true;
        boolean unlockedAllSilver = true;
        boolean unlockedAllGold = true;
        boolean unlockedAllPlatinum = true;

        for(int i = 1; i <= 5; i++) {
            int highScore = game.highScores.getHighscoreForChallenge(variant, String.valueOf(i));
            ChallengeTiers challengeTiers = new ChallengeTiers(levels.get(String.valueOf(i)).getQuestions().size);

            if(highScore < challengeTiers.getBronze()) {
                unlockedAllBronze = false;
            } else {
                if(variant.equals(Variant.CLASSIC)) {
                    game.gameServices.unlockAchievement(game.platformResolver.getAchievementClassicChallengesFirstBronze());
                } else if (variant.equals(Variant.SWITCH)) {
                    game.gameServices.unlockAchievement(game.platformResolver.getAchievementSwitchChallengesFirstBronze());
                }
            }

            if(highScore < challengeTiers.getSilver()) {
                unlockedAllSilver = false;
            } else {
                if(variant.equals(Variant.CLASSIC)) {
                    game.gameServices.unlockAchievement(game.platformResolver.getAchievementClassicChallengesFirstSilver());
                } else if (variant.equals(Variant.SWITCH)) {
                    game.gameServices.unlockAchievement(game.platformResolver.getAchievementSwitchChallengesFirstSilver());
                }
            }

            if(highScore < challengeTiers.getGold()) {
                unlockedAllGold = false;
            } else {
                if(variant.equals(Variant.CLASSIC)) {
                    game.gameServices.unlockAchievement(game.platformResolver.getAchievementClassicChallengesFirstGold());
                } else if (variant.equals(Variant.SWITCH)) {
                    game.gameServices.unlockAchievement(game.platformResolver.getAchievementSwitchChallengesFirstGold());
                }
            }

            if(highScore < challengeTiers.getPlatinum()) {
                unlockedAllPlatinum = false;
            } else {
                if(variant.equals(Variant.CLASSIC)) {
                    game.gameServices.unlockAchievement(game.platformResolver.getAchievementClassicChallengesFirstPlatinum());
                } else if (variant.equals(Variant.SWITCH)) {
                    game.gameServices.unlockAchievement(game.platformResolver.getAchievementSwitchChallengesFirstPlatinum());
                }
            }
        }

        if(unlockedAllBronze) {
            if(variant.equals(Variant.CLASSIC)) {
                game.gameServices.unlockAchievement(game.platformResolver.getAchievementClassicChallengesAllBronze());
            } else if (variant.equals(Variant.SWITCH)) {
                game.gameServices.unlockAchievement(game.platformResolver.getAchievementSwitchChallengesAllBronze());
            }
        }

        if(unlockedAllSilver) {
            if(variant.equals(Variant.CLASSIC)) {
                game.gameServices.unlockAchievement(game.platformResolver.getAchievementClassicChallengesAllSilver());
            } else if (variant.equals(Variant.SWITCH)) {
                game.gameServices.unlockAchievement(game.platformResolver.getAchievementSwitchChallengesAllSilver());
            }
        }

        if(unlockedAllGold) {
            if(variant.equals(Variant.CLASSIC)) {
                game.gameServices.unlockAchievement(game.platformResolver.getAchievementClassicChallengesAllGold());
            } else if (variant.equals(Variant.SWITCH)) {
                game.gameServices.unlockAchievement(game.platformResolver.getAchievementSwitchChallengesAllGold());
            }
        }

        if(unlockedAllPlatinum) {
            if(variant.equals(Variant.CLASSIC)) {
                game.gameServices.unlockAchievement(game.platformResolver.getAchievementClassicChallengesAllPlatinum());
            } else if (variant.equals(Variant.SWITCH)) {
                game.gameServices.unlockAchievement(game.platformResolver.getAchievementSwitchChallengesAllPlatinum());
            }
        }
    }

    @Override
    void allLettersCleared(GameServices gameServices, PlatformResolver platformResolver, Variant variant) { }

    @Override
    void allKeysCleared(GameServices gameServices, PlatformResolver platformResolver, Variant variant) { }
}
