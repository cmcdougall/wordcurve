package com.singlemethodgames.wordcurve.screens.tutorial;

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
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.I18NBundle;
import com.singlemethodgames.wordcurve.WordCurveGame;
import com.singlemethodgames.wordcurve.actors.gamebar.GameMode;
import com.singlemethodgames.wordcurve.buttons.ReplayButton;
import com.singlemethodgames.wordcurve.buttons.WordButton;
import com.singlemethodgames.wordcurve.groups.WordCurveKeyboardGroup;
import com.singlemethodgames.wordcurve.screens.BaseScreen;
import com.singlemethodgames.wordcurve.screens.difficulty.LetterDifficulty;
import com.singlemethodgames.wordcurve.screens.difficulty.SpeedDifficulty;
import com.singlemethodgames.wordcurve.utils.Assets;
import com.singlemethodgames.wordcurve.utils.Constants;
import com.singlemethodgames.wordcurve.utils.ReplayListener;
import com.singlemethodgames.wordcurve.utils.Utils;
import com.singlemethodgames.wordcurve.utils.wordlist.Word;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TutorialQuestion extends BaseScreen implements ReplayListener {
    private WordCurveKeyboardGroup wordCurveKeyboardGroup;
    private Stage stage;
    private Label infoLabel;
    private Label replayLabel;
    private List<WordButton> wordButtons;
    private Word correctAnswer = new Word("snail");
    private Label tapToContinueLabel;
    private Stack continueStack;
    private Image continueImage;
    private Button continueButton;
    private I18NBundle myBundle;

    private ReplayButton replayButton;

    private Word[] options = new Word[]{
            new Word("cat"),
            new Word("dog"),
            correctAnswer,
            new Word("fish")
    };

    public TutorialQuestion(final WordCurveGame game) {
        super(game);

        game.viewport.apply();

        stage = new Stage(game.viewport) {
            @Override
            public boolean keyDown(int keyCode) {
                if (keyCode == Input.Keys.BACK) {
                    game.gamePreferences.updateTutorialPreference(false);
                    goToMainMenu();
                    return true;
                }
                return false;
            }
        };

        TextureAtlas ingameAtlas = game.getAssetManager().get(Assets.ingameAtlas);

        SpeedDifficulty speedDifficulty = new SpeedDifficulty(0, 0);
        LetterDifficulty letterDifficulty = new LetterDifficulty(0, 0);

        wordCurveKeyboardGroup = new WordCurveKeyboardGroup(ingameAtlas, 5, 1325, 1070, Touchable.disabled, this.game.camera, speedDifficulty, letterDifficulty, game.viewport);
        wordCurveKeyboardGroup.setWord(correctAnswer);
        wordCurveKeyboardGroup.setTraining(true);

        Drawable drawable = new TextureRegionDrawable(ingameAtlas.findRegion(Constants.TextureRegions.REPLAY_BUTTON));
        replayButton = new ReplayButton(drawable, this);

        BitmapFont font48 = game.getAssetManager().get(Constants.Fonts.SIZE48, BitmapFont.class);
        BitmapFont font60 = game.getAssetManager().get(Constants.Fonts.SIZE60, BitmapFont.class);
        BitmapFont font72 = game.getAssetManager().get(Constants.Fonts.SIZE72, BitmapFont.class);

        myBundle = game.getAssetManager().get(Assets.stringsBundle);
        infoLabel = new Label(myBundle.get("tutorial_step_4"), new Label.LabelStyle(font48, Color.WHITE));
        infoLabel.setAlignment(Align.center);
        infoLabel.setWrap(true);

        float remainingHeight =
                stage.getViewport().getWorldHeight()
                        - wordCurveKeyboardGroup.getY()
                        - wordCurveKeyboardGroup.getHeight();


        replayLabel = new Label(myBundle.get("tutorial_step_4_replay"), new Label.LabelStyle(font48, Color.WHITE));
        replayLabel.setAlignment(Align.center);
        replayLabel.setWrap(true);
        replayLabel.setBounds(0, wordCurveKeyboardGroup.getY() + wordCurveKeyboardGroup.getHeight(), stage.getViewport().getWorldWidth(), remainingHeight);
        replayLabel.getColor().a = 0;
        stage.addActor(replayLabel);

        TextureRegion whitePixelRegion = ingameAtlas.findRegion(Constants.TextureRegions.WHITE_PIXEL);
        Utils.fixWhitePixelRegion(whitePixelRegion);
        Drawable continueDrawable = new TextureRegionDrawable(whitePixelRegion);
        continueImage = new Image(continueDrawable);
        continueImage.addAction(Actions.color(Constants.Colours.TOP_BAR));

        continueStack = new Stack();
        continueStack.setSize(stage.getViewport().getWorldWidth(), 200f);
        continueStack.setPosition(0, 0);
        continueStack.setTouchable(Touchable.disabled);
        continueStack.add(continueImage);
        tapToContinueLabel = new Label(myBundle.get("tap_to_continue"), new Label.LabelStyle(font60, Color.WHITE));
        tapToContinueLabel.setAlignment(Align.center);
        continueStack.add(tapToContinueLabel);

        continueButton = new Button(new Button.ButtonStyle(continueDrawable, continueDrawable, continueDrawable));
        continueButton.setPosition(0, 0);
        continueButton.setFillParent(true);
        continueButton.setTouchable(Touchable.disabled);
        continueButton.getColor().a = 0f;

        Table table = new Table();
        table.setFillParent(true);
//        table.setDebug(true);
        table.align(Align.center | Align.top);

        table.add(infoLabel).center().expandY().width(776f).padBottom(250f);

        stage.addActor(wordCurveKeyboardGroup);
        stage.addActor(ReplayButton.createReplayTable(ingameAtlas, replayButton, GameMode.TUTORIAL));
        stage.addActor(table);

        wordButtons = createExampleQuestionButtons(ingameAtlas, font72);

        firstBlock();
    }

    private void displayBlockPoint(final Runnable run) {
        stage.addActor(continueStack);
        stage.addActor(continueButton);
        continueButton.setTouchable(Touchable.enabled);
        continueButton.clearListeners();
        continueButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                continueButton.setTouchable(Touchable.disabled);
                continueButton.remove();
                continueStack.remove();
                run.run();
            }
        });
    }

    private void firstBlock() {
        displayBlockPoint(new Runnable() {
            @Override
            public void run() {
                infoLabel.setText("");
                RunnableAction runnableAction = Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        secondBlock();
                    }
                });
                wordCurveKeyboardGroup.performWordCurveAction(correctAnswer, Collections.singletonList(runnableAction), null, true);
            }
        });
    }

    private void secondBlock() {
        infoLabel.setText("");
        replayLabel.getColor().a = 1;
        replayButton.displayReplayButtonAndRotateForever();
        moveButtonsOnScreen();
    }

    private void endTutorial() {
        continueImage.addAction(Actions.color(Constants.Colours.CORRECT_GREEN));
        tapToContinueLabel.setText(myBundle.get("tutorial_correct") + " " + tapToContinueLabel.getText().toString());
        displayBlockPoint(new Runnable() {
            @Override
            public void run() {
                // Save that the user has viewed the tutorial
                game.gamePreferences.updateTutorialPreference(false);
                goToMainMenu();
            }
        });
    }

    private List<WordButton> createExampleQuestionButtons(TextureAtlas textureAtlas, BitmapFont bitmapFont) {
        float defaultY = 320;

        final List<WordButton> buttonOptions = new ArrayList<>();

        for (int i = 0; i < options.length; i++) {
            final Word word = options[i];
            final WordButton wordButton = WordButton.createWordButton(word, word.equals(correctAnswer),
                    bitmapFont, stage.getViewport().getWorldWidth(), textureAtlas
            );
            wordButton.setBounds(0 - Constants.Sizes.ANSWER_BUTTON_WIDTH,
                    defaultY + (i * 250),
                    Constants.Sizes.ANSWER_BUTTON_WIDTH,
                    Constants.Sizes.ANSWER_BUTTON_HEIGHT
            );
            wordButton.setTouchable(Touchable.disabled);

            wordButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    wordButton.clearActions();
                    if (wordButton.isAnswer()) {
                        wordCurveKeyboardGroup.releaseAllKeys(0f);
                        wordCurveKeyboardGroup.highlightLetters(word.getWord(), 0f, Constants.Colours.Keyboard.Button.CORRECT);

                        wordCurveKeyboardGroup.clearWordCurveGroupActions();
                        wordCurveKeyboardGroup.showWordCurve();

                        replayButton.hideReplayButton();

                        for (WordButton other : buttonOptions) {
                            if (!other.equals(wordButton)) {
                                other.addAction(Actions.fadeOut(0.5f));
                                other.setTouchable(Touchable.disabled);
                            }
                        }
                        endTutorial();
                    } else {
                        wordCurveKeyboardGroup.highlightLetters(wordButton.getWord().getWord(), 0.5f, Constants.Colours.INCORRECT_KEY_RED);
                        wordButton.setTouchable(Touchable.disabled);
                    }
                }
            });

            stage.addActor(wordButton);
            buttonOptions.add(wordButton);
        }

        return buttonOptions;
    }

    private void moveButtonsOnScreen() {
        for (final WordButton wordButton : wordButtons) {
            wordButton.moveOntoScreenAction();
        }
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(
                Constants.Colours.BACKGROUND_COLOUR.r,
                Constants.Colours.BACKGROUND_COLOUR.g,
                Constants.Colours.BACKGROUND_COLOUR.b,
                Constants.Colours.BACKGROUND_COLOUR.a
        );
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
    public void replayQuestion() {
        wordCurveKeyboardGroup.createAndPerformWordCurve();
    }
}
