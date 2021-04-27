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
import com.singlemethodgames.wordcurve.screens.variants.BaseMode;
import com.singlemethodgames.wordcurve.screens.variants.Variant;
import com.singlemethodgames.wordcurve.services.GameServices;
import com.singlemethodgames.wordcurve.services.PlatformResolver;
import com.singlemethodgames.wordcurve.utils.Constants;
import com.singlemethodgames.wordcurve.utils.progress.GameProgress;
import com.singlemethodgames.wordcurve.utils.questionproviders.QuestionProvider;
import com.singlemethodgames.wordcurve.utils.questionproviders.UnlimitedQuestionProvider;

import static com.singlemethodgames.wordcurve.actors.gamebar.CasualBar.updateQuestionLengths;

public class LifeBar extends GameBar {
    private int lives;
    private Image[] lifeImage;
    private Image background;
    final private int colSpan;
    private UnlimitedQuestionProvider questionProvider;

    public LifeBar(BaseMode baseMode, SaveState saveState, UnlimitedQuestionProvider questionProvider, TextureAtlas textureAtlas, I18NBundle bundle, BitmapFont font72, final Image background) {
        super(baseMode, saveState, questionProvider, textureAtlas, bundle, font72);
        this.questionProvider = questionProvider;
        this.background = background;

        // User Lives
        lives = ((LifeState)saveState.getGameState()).lives;

        lifeImage = new Image[3];
        for (int i = 0; i < 3; i++) {
            lifeImage[i] = new Image(textureAtlas.findRegion(Constants.TextureRegions.HEART));
            lifeImage[i].setSize(60, 60);

            if(Constants.Conditions.START_LIVES - i > lives) {
                lifeImage[i].setColor(Constants.Colours.Heart.FADED_HEART_COLOUR);
            } else {
                lifeImage[i].setColor(Constants.Colours.Heart.HEART_COLOUR);
            }

            lifeImage[i].setOrigin(Align.center);
        }

        Table lifeTable = new Table();
//        lifeTable.setDebug(true);
        lifeTable.align(Align.center);
        lifeTable.setWidth(getWidth() / (1f/3f));

        for (Image life : lifeImage) {
            lifeTable.add(life).size(60f).center().padLeft(20);
            life.scaleBy(-1f, -1f);
        }

        colSpan = 3;
        add(lifeTable).right().height(100f).expandX().padRight(50f);

        // Need to make table row at least 120f!
        padBottom(10f);
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

        pointsLabel = new Label("0", new Label.LabelStyle(bitmapFont, Color.YELLOW));
        pointsLabel.setAlignment(Align.center);

        table.background(background.tint(Color.DARK_GRAY));
        table.add(pointsImage).center().size(50f).padRight(20f).padLeft(20f);
        table.add(pointsLabel).padRight(20f);

        return table;
    }

    @Override
    public Table getIncorrectTable(BitmapFont bitmapFont, NinePatchDrawable background) {
        Table table = new Table();
        Image brokenHeartImage = new Image(textureAtlas.findRegion(Constants.TextureRegions.HEART_BROKEN));
        brokenHeartImage.setColor(Constants.Colours.INCORRECT_KEY_RED);
        brokenHeartImage.setSize(80f, 80f);
        brokenHeartImage.setOrigin(Align.center);

        table.background(background.tint(Color.DARK_GRAY));
        table.add(brokenHeartImage).center().size(80f).pad(10f, 20f, 10f, 20f);

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

        int i = 0;
        for (final Image life : lifeImage) {
            if (i == 2 && lives == 1) {
                life.addAction(
                        Actions.sequence(
                                Actions.delay(2f),
                                Actions.scaleBy(1f, 1f),
                                Actions.run(new Runnable() {
                                    @Override
                                    public void run() {
                                        animateFinalLife(1);
                                    }
                                })
                        )
                );
            } else {
                life.addAction(Actions.sequence(
                        Actions.delay(2f),
                        Actions.scaleBy(1.5f, 1.5f, 0.25f, Interpolation.sine),
                        Actions.scaleBy(-0.5f, -0.5f, 0.25f, Interpolation.sine)
                ));
            }
            i++;
        }
    }

    @Override
    public void correctAnswer(float time, GameMode gameMode) {
        super.correctAnswer(time, gameMode);
        updateQuestionLengths(getGameProgress().getMatched() - getGameProgress().getIncorrectAnswers(), questionProvider);

        baseMode.game.statistics.updateAnsweredStatForVariantMode(saveState.getVariant(), GameMode.LIFE, 1);
        int currentCount = baseMode.game.statistics.getTotalAnsweredForVariantMode(saveState.getVariant(), GameMode.LIFE);

        if(saveState.getVariant().equals(Variant.CLASSIC) && !isTraining()) {
            baseMode.game.gameServices.incrementAchievement(
                    baseMode.game.platformResolver.getAchievementClassicLifeFirst10(), 1,
                    currentCount > 10 ? 1f : currentCount / 10f
            );
            baseMode.game.gameServices.incrementAchievement(
                    baseMode.game.platformResolver.getAchievementClassicLifeFirst50(), 1,
                    currentCount > 50 ? 1f : currentCount / 50f
            );
            baseMode.game.gameServices.incrementAchievement(
                    baseMode.game.platformResolver.getAchievementClassicLifeFirst100(), 1,
                    currentCount > 100 ? 1f : currentCount / 100f
            );
        } else if (saveState.getVariant().equals(Variant.SWITCH) && !isTraining()) {
            baseMode.game.gameServices.incrementAchievement(
                    baseMode.game.platformResolver.getAchievementSwitchLifeFirst10(), 1,
                    currentCount > 10 ? 1f : currentCount / 10f
            );
            baseMode.game.gameServices.incrementAchievement(
                    baseMode.game.platformResolver.getAchievementSwitchLifeFirst50(), 1,
                    currentCount > 50 ? 1f : currentCount / 50f
            );
            baseMode.game.gameServices.incrementAchievement(
                    baseMode.game.platformResolver.getAchievementSwitchLifeFirst100(), 1,
                    currentCount > 100 ? 1f : currentCount / 100f
            );
        }
    }

    @Override
    public void incorrectAnswer(float time, GameMode gameMode) {
        super.incorrectAnswer(time, gameMode);
        lives -= 1;

        // - 1 cause of zero index!
        final int index = (Constants.Conditions.START_LIVES - lives) - 1;

        lifeImage[index].addAction(Actions.sequence(
                Actions.scaleBy(-1f, -1f, 0.2f, Interpolation.pow2),
                Actions.parallel(
                        Actions.scaleBy(1f, 1f, 0.2f),
                        Actions.color(Constants.Colours.Heart.FADED_HEART_COLOUR, 0.5f)
                )
        ));

        if (lives == 1) {
            animateFinalLife(index);
        } else if (lives == 0) {
            lifeImage[index].removeAction(lifeImage[index].getActions().first());
            float scaleX = lifeImage[index].getScaleX() - 1;
            float scaleY = lifeImage[index].getScaleY() - 1;
            lifeImage[index].addAction(
                    Actions.parallel(
                            Actions.scaleBy(-scaleX, -scaleY, 0.2f),
                            Actions.color(Constants.Colours.Heart.FADED_HEART_COLOUR, .5f, Interpolation.sine)
                    )
            );

            background.clearActions();
            background.addAction(Actions.alpha(0f));
        }
    }

    private void animateFinalLife(int index) {
        final int[] count = new int[] {3};
        lifeImage[index + 1].clearActions();
        lifeImage[index + 1].addAction(Actions.forever(
                Actions.sequence(
                        Actions.parallel(
                                Actions.scaleBy(.5f, .5f, .5f, Interpolation.sine),
                                Actions.alpha(1f, .5f, Interpolation.sine),
                                Actions.run(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (count[0] % 3 == 0) {
                                            background.addAction(
                                                    Actions.sequence(
                                                            Actions.alpha(.5f, 0.5f, Interpolation.sine),
                                                            Actions.alpha(0f, 0.5f, Interpolation.sine)
                                                    )
                                            );
                                        }
                                        count[0]++;
                                    }
                                })
                        ),
                        Actions.parallel(
                                Actions.scaleBy(-.5f, -.5f, .50f, Interpolation.sine),
                                Actions.alpha(0.25f, .50f, Interpolation.sine)
                        )
                )
        ));
    }

    @Override
    public boolean updateState(float elapsedTime) {
        this.elapsedTime += elapsedTime;
        return true;
    }

    @Override
    public boolean newQuestion() {
        return lives > 0;
    }

    @Override
    public QuestionProvider getQuestionProvider() {
        return questionProvider;
    }

    @Override
    public LifeState getState() {
        return new LifeState(getGameProgress(), lives, questionProvider.getState(), replayCount,
                speedDifficulty.getCurrentCorrect(), speedDifficulty.getStart(),
                letterDifficulty.getCurrentCorrect(), letterDifficulty.getStart()
        );
    }

    public static class LifeState extends State {
        private int lives;
        private UnlimitedQuestionProvider.UnlimitedState unlimitedState;

        public LifeState() {
            lives = Constants.Conditions.START_LIVES;
            unlimitedState = new UnlimitedQuestionProvider.UnlimitedState();
        }

        public LifeState(GameProgress gameProgress, int lives, UnlimitedQuestionProvider.UnlimitedState unlimitedState, int replayCount, int speedCurrentCorrect, int speedStart, int letterCurrentCorrect, int letterStart) {
            super(gameProgress, replayCount, speedCurrentCorrect, speedStart, letterCurrentCorrect, letterStart);
            this.lives = lives;
            this.unlimitedState = unlimitedState;
        }

        public int getLives() {
            return lives;
        }

        public void setLives(int lives) {
            this.lives = lives;
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
            gameServices.unlockAchievement(platformResolver.getAchievementClassicLifeRemoveAllLetters());
        } else if (variant.equals(Variant.SWITCH)) {
            gameServices.unlockAchievement(platformResolver.getAchievementSwitchLifeRemoveAllLetters());
        }
    }

    @Override
    void allKeysCleared(GameServices gameServices, PlatformResolver platformResolver, Variant variant) {
        if(variant.equals(Variant.CLASSIC)) {
            gameServices.unlockAchievement(platformResolver.getAchievementClassicLifeRemoveAllKeys());
        } else if (variant.equals(Variant.SWITCH)) {
            gameServices.unlockAchievement(platformResolver.getAchievementSwitchLifeRemoveAllKeys());
        }
    }
}
