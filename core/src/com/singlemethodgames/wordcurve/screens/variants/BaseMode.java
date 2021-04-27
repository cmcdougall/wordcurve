package com.singlemethodgames.wordcurve.screens.variants;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.I18NBundle;
import com.singlemethodgames.wordcurve.SaveState;
import com.singlemethodgames.wordcurve.WordCurveGame;
import com.singlemethodgames.wordcurve.actors.gamebar.CasualBar;
import com.singlemethodgames.wordcurve.actors.gamebar.ChallengesBar;
import com.singlemethodgames.wordcurve.actors.gamebar.GameBar;
import com.singlemethodgames.wordcurve.actors.gamebar.GameMode;
import com.singlemethodgames.wordcurve.actors.gamebar.LifeBar;
import com.singlemethodgames.wordcurve.actors.gamebar.TimeBar;
import com.singlemethodgames.wordcurve.buttons.ReplayButton;
import com.singlemethodgames.wordcurve.screens.BaseScreen;
import com.singlemethodgames.wordcurve.screens.GameOverScreen;
import com.singlemethodgames.wordcurve.screens.LevelCompleteScreen;
import com.singlemethodgames.wordcurve.screens.difficulty.DifficultySelect;
import com.singlemethodgames.wordcurve.screens.difficulty.LetterDifficulty;
import com.singlemethodgames.wordcurve.screens.difficulty.SpeedDifficulty;
import com.singlemethodgames.wordcurve.utils.Assets;
import com.singlemethodgames.wordcurve.utils.Constants;
import com.singlemethodgames.wordcurve.utils.GameState;
import com.singlemethodgames.wordcurve.utils.PauseMenuGroup;
import com.singlemethodgames.wordcurve.utils.ReplayListener;
import com.singlemethodgames.wordcurve.utils.State;
import com.singlemethodgames.wordcurve.utils.Utils;
import com.singlemethodgames.wordcurve.utils.questionproviders.ChallengeQuestionProvider;
import com.singlemethodgames.wordcurve.utils.questionproviders.QuestionProvider;
import com.singlemethodgames.wordcurve.utils.questionproviders.UnlimitedQuestionProvider;
import com.singlemethodgames.wordcurve.utils.tracking.Tracker;
import com.singlemethodgames.wordcurve.utils.wordlist.Word;

/**
 * Created by cameron on 19/02/2018.
 */

public abstract class BaseMode extends BaseScreen implements GameState, ReplayListener {
    GameBar gameBar;
    protected Stage stage;
    protected TextureRegion replay;
    boolean recordTime = false;
    private final Variant gameVariant;

    private State state;
    private PauseMenuGroup pauseMenu;

    ReplayButton replayButton;
    Table replayTable;

    private Stack continueStack;
    private Button continueButton;
    private TextureRegionDrawable backgroundDrawable;
    protected final GameMode gameMode;

    private Stack[] countdownImages;

    Table correctTable;
    Table incorrectTable;
    Table replayScoreTable;

    protected TextureAtlas textureAtlas;
    BitmapFont font96;

    protected LetterDifficulty letterDifficulty;
    protected SpeedDifficulty speedDifficulty;
    private boolean displaySettingsChanged = false;
    private boolean speedSettingsChanged = false;

    private Tracker tracker;

    <T extends QuestionProvider> BaseMode(final WordCurveGame game, final SaveState saveState, final T questionProvider) {
        super(game);
        stage = new Stage(game.viewport) {
            @Override
            public boolean keyDown(int keyCode) {
                if (keyCode == Input.Keys.BACK) {
                    pause();
                    return true;
                }

                return false;
            }
        };
        game.secureView.secureView();
        this.gameVariant = saveState.getVariant();
        this.gameMode = saveState.getGameMode();
        this.tracker = saveState.getTracker();
        letterDifficulty = new LetterDifficulty(
                saveState.getGameState().getLetterDifficultyCorrect(),
                saveState.getGameState().getLetterDifficultyStart()
        );
        speedDifficulty = new SpeedDifficulty(
                saveState.getGameState().getSpeedDifficultyCorrect(),
                saveState.getGameState().getSpeedDifficultyStart()
        );

        BitmapFont font60 = game.getAssetManager().get(Constants.Fonts.SIZE60, BitmapFont.class);
        BitmapFont font72 = game.getAssetManager().get(Constants.Fonts.SIZE72, BitmapFont.class);
        font96 = game.getAssetManager().get(Constants.Fonts.SIZE96, BitmapFont.class);

        textureAtlas = game.getAssetManager().get(Assets.ingameAtlas);

        TextureRegion backgroundRegion = textureAtlas.findRegion(Constants.TextureRegions.WHITE_PIXEL);
        Utils.fixWhitePixelRegion(backgroundRegion);
        backgroundRegion.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        backgroundDrawable = new TextureRegionDrawable(backgroundRegion);
        Image backgroundImage = new Image(backgroundDrawable);
        backgroundImage.setSize(stage.getViewport().getWorldWidth(), stage.getViewport().getWorldHeight());
        backgroundImage.setColor(Constants.Colours.INCORRECT_RED);
        backgroundImage.addAction(Actions.alpha(0f));
        backgroundImage.setPosition(0, 0);

        Image topBarImage = new Image(backgroundDrawable);
        topBarImage.setSize(stage.getViewport().getWorldWidth(), 120);
        topBarImage.setColor(Constants.Colours.TOP_BAR);
        topBarImage.setPosition(0, stage.getViewport().getWorldHeight() - topBarImage.getHeight());

        stage.addActor(backgroundImage);
        stage.addActor(topBarImage);

        I18NBundle stringsBundle = game.getAssetManager().get(Assets.stringsBundle);
        switch (gameMode) {
            case LIFE:
                this.gameBar = new LifeBar(this, saveState, (UnlimitedQuestionProvider)questionProvider, textureAtlas, stringsBundle, font72, backgroundImage);
                break;
            case TIME:
                this.gameBar = new TimeBar(game, this, saveState, (UnlimitedQuestionProvider)questionProvider, textureAtlas, stringsBundle, font72, stage.getViewport().getWorldWidth(), backgroundImage);
                break;
            case CHALLENGES:
                this.gameBar = new ChallengesBar(this, saveState, (ChallengeQuestionProvider) questionProvider, textureAtlas, stringsBundle, font72, questionProvider.totalQuestions());
                break;
            case CASUAL:
                this.gameBar = new CasualBar(this, saveState, (UnlimitedQuestionProvider) questionProvider, textureAtlas, stringsBundle, font72);
                break;
        }
        gameBar.setSpeedDifficulty(speedDifficulty);
        gameBar.setLetterDifficulty(letterDifficulty);
        stage.addActor(gameBar);

        Drawable continueDrawable = backgroundDrawable.tint(Constants.Colours.TOP_BAR);

        continueStack = new Stack();
        continueStack.setSize(stage.getViewport().getWorldWidth(), 200f);
        continueStack.setPosition(0, 0);
        continueStack.setTouchable(Touchable.disabled);

        continueStack.add(new Image(continueDrawable));

        Label tapToContinueLabel = new Label(stringsBundle.get("tap_to_continue"), new Label.LabelStyle(font60, Color.WHITE));
        tapToContinueLabel.setAlignment(Align.center);
        continueStack.add(tapToContinueLabel);

        continueButton = new Button(new Button.ButtonStyle(continueDrawable, continueDrawable, continueDrawable));
        continueButton.setPosition(0, 0);
        continueButton.setFillParent(true);
        continueButton.setTouchable(Touchable.disabled);
        continueButton.getColor().a = 0f;
        continueButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                continueButton.setTouchable(Touchable.disabled);
                continueQuestions();
                continueButton.remove();
                continueStack.remove();
            }
        });


        pauseMenu = null;
        state = State.RUN;

        float x = (stage.getViewport().getWorldWidth() - 200f) / 2f;
        float y = (stage.getViewport().getWorldHeight() - 200f) / 2f;

        countdownImages = new Stack[3];

        for(int i = 0; i < 3; i++) {
            Stack number = createIndexImage(font96, i, x, y, textureAtlas.findRegion(Constants.TextureRegions.POINT));
            countdownImages[i] = number;
        }

        ClickListener pauseListener = new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                pause();
            }
        };

        ClickListener settingsListener = new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                pause();
                game.setScreen(new DifficultySelect(game, true, gameVariant, gameMode, BaseMode.this, letterDifficulty.getCurrentCorrect(), speedDifficulty.getCurrentCorrect()));
            }
        };

        replay = textureAtlas.findRegion(Constants.TextureRegions.REPLAY_BUTTON);
        Drawable drawable = new TextureRegionDrawable(replay);
        replayButton = new ReplayButton(drawable, this);
        replayTable = ReplayButton.createReplayTable(textureAtlas, replayButton, gameMode, pauseListener, settingsListener);

        NinePatchDrawable tableBackground = new NinePatchDrawable(textureAtlas.createPatch(Constants.TextureRegions.KEYBOARD_BASE));
        correctTable = gameBar.getCorrectTable(font72, tableBackground);
        incorrectTable = gameBar.getIncorrectTable(font72, tableBackground);
        replayScoreTable = gameBar.getReplayTable(tableBackground);
    }

    public void loadUI() {
        gameBar.beginAnimateUI();

        performCountdown(new Runnable() {
            @Override
            public void run() {
                removeCountdownImagesFromStage();
                loadQuestion();
            }
        });

        stage.addAction(Actions.delay(2f, Actions.run(new Runnable() {
                        @Override
                        public void run() {
                            loadModeComponent();
                        }
                    })
                )
        );
    }

    private void performCountdown(Runnable endRunnable) {
        for(int i = 2; i >= 0; i--) {
            stage.addActor(countdownImages[i]);
            Runnable run = null;
            if (i == 0) {
                run = endRunnable;
            }

            addCountdownAction(countdownImages[i], 2 - i, run);
        }
    }

    private void addCountdownAction(Stack actor, float delay, Runnable runnable) {
        SequenceAction sequenceAction = Actions.sequence(
                Actions.delay(0.5f + delay),
                Actions.fadeIn(0f),
                Actions.fadeOut(0.9f)
        );

        if(runnable != null) {
            sequenceAction.addAction(Actions.run(runnable));
        }

        actor.addAction(sequenceAction);
    }

    private void removeCountdownImagesFromStage() {
        for(Stack countdownImage: countdownImages) {
            countdownImage.remove();
        }
    }

    protected abstract void loadModeComponent();
    protected abstract void upgradeOccured();
    protected abstract void continueQuestions();
    protected abstract void noAnswer();

    public void gameFinished() {
        this.game.secureView.removeSecureView();
        gameBar.gameFinished(game, this.gameVariant);
        if(gameBar.getQuestionProvider() instanceof ChallengeQuestionProvider) {
            this.game.setScreen(new LevelCompleteScreen(this.game, gameBar.getGameProgress(), this.gameVariant, (ChallengeQuestionProvider) gameBar.getQuestionProvider()));
        } else {
            this.game.setScreen(new GameOverScreen(this.game, gameBar.getGameProgress(), this.gameVariant, this.gameMode, gameBar.getQuestionProvider(), tracker, gameBar.isTraining()));
        }
        dispose();
    }

    void answeredCorrectly(Tracker.Answer answer) {
        gameBar.correctAnswer(answer.getTime(), gameMode);
        tracker.addAnswer(answer);
    }

    void answeredIncorrectly(Tracker.Answer answer) {
        gameBar.incorrectAnswer(answer.getTime(), gameMode);
        tracker.addAnswer(answer);

        stage.addActor(continueStack);
        stage.addActor(continueButton);
        continueButton.setTouchable(Touchable.enabled);
    }

    @Override
    public void show() {
    }

    public void render(float delta) {
        Gdx.gl.glClearColor(Constants.Colours.BACKGROUND_COLOUR.r, Constants.Colours.BACKGROUND_COLOUR.g, Constants.Colours.BACKGROUND_COLOUR.b, Constants.Colours.BACKGROUND_COLOUR.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.camera.update();

        switch (state) {
            case RUN:
                checkQuestionStatus();
                stage.act(Gdx.graphics.getDeltaTime());
                break;
            case PAUSE:
                stage.act(0);
                break;
            default:
                break;
        }

        stage.draw();
    }

    public abstract void pauseActors();

    public abstract void resumeActors();

    @Override
    public void resumeGame() {
        state = State.RUN;

        if (pauseMenu != null) {
            pauseMenu.remove();
        }
        resumeActors();
    }

    @Override
    public void quitGame() {
        gameFinished();
    }

    private void checkQuestionStatus() {
        if (recordTime) {
            boolean continueGame = gameBar.updateState(Gdx.graphics.getRawDeltaTime());
            if(!continueGame) {
                recordTime = false;
                noAnswer();
            }
        }
    }

    abstract public void loadQuestion();

    public void continueGame() {
        if(gameBar.newQuestion()) {
            if(displaySettingsChanged) {
                letterDifficulty.setCurrentCorrect(game.gamePreferences.getLetterDifficulty(gameVariant));
                displaySettingsChanged = false;
            }
            if(speedSettingsChanged) {
                speedDifficulty.setCurrentCorrect(game.gamePreferences.getSpeedDifficulty(gameVariant).start);
                speedSettingsChanged = false;
            }

            upgradeOccured();
        } else {
            gameFinished();
        }
    }

    public void settingsChanged(boolean displaySettingsChanged, boolean speedSettingsChanged) {
        this.displaySettingsChanged = displaySettingsChanged;
        this.speedSettingsChanged = speedSettingsChanged;
    }

    public abstract Word getCorrectAnswer();

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
        game.saveGame(gameVariant, gameMode, gameBar.getState(), tracker, gameBar.isTraining());

        if (state != State.PAUSE) {
            state = State.PAUSE;

            gameBar.pack();
            pauseMenu = new PauseMenuGroup(this,
                    stage.getViewport().getWorldWidth(),
                    stage.getViewport().getWorldHeight(),
                    gameBar.getPrefHeight(),
                    game.getAssetManager(),
                    backgroundDrawable);

            pauseActors();

            stage.addActor(pauseMenu);
        }
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
    }

    @Override
    public InputProcessor getInputProcessor() {
        return stage;
    }

    void animateAnswerTable(final Table table, float x, float y) {
        // Move to position
        // Increase alpha to 1
        // Move string up the screen and fade out after x seconds
        gameBar.pointsValueChanged();
        table.pack();
        stage.addActor(table);
        table.clearActions();
        table.addAction(
                Actions.sequence(
                        Actions.moveTo(x - (table.getWidth() / 2), y),
                        Actions.parallel(
                                Actions.fadeIn(0.2f),
                                Actions.moveBy(0f, 50f, 0.2f)
                        ),
                        Actions.delay(0.8f),
                        Actions.parallel(
                                Actions.moveBy(0f, 50f, 0.2f),
                                Actions.fadeOut(0.2f)
                        ),
                        Actions.run(new Runnable() {
                            @Override
                            public void run() {
                                table.remove();
                            }
                        })
                )
        );
    }

    private static Stack createIndexImage(BitmapFont bitmapFont, int i, float x, float y, TextureRegion background) {
        String labelText = String.valueOf(i + 1);

        Image number = new Image(background);
        number.setOrigin(Align.center);
        number.setColor(Constants.Colours.Keyboard.KEY_COLOUR);

        Label label = new Label(labelText, new Label.LabelStyle(bitmapFont, Color.WHITE));
        label.setOrigin(Align.center);
        label.setAlignment(Align.center);

        Stack numberStack = new Stack();
        numberStack.setBounds(x, y, 200f, 200f);
        numberStack.setOrigin(Align.center);
        numberStack.addAction(Actions.fadeOut(0f));
        numberStack.setName(labelText);

        numberStack.add(number);
        if("1".equals(labelText)) {
            Table table = new Table();
            label.getGlyphLayout().setText(label.getStyle().font, label.getText());
            float labelWidth = label.getGlyphLayout().width;
            table.add(label).padRight(labelWidth / 2f).center().expand().fill();
            numberStack.add(table);
        } else {
            numberStack.add(label);
        }

        return numberStack;
    }
}
