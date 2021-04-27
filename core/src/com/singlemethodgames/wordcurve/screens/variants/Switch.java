package com.singlemethodgames.wordcurve.screens.variants;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.singlemethodgames.wordcurve.SaveState;
import com.singlemethodgames.wordcurve.WordCurveGame;
import com.singlemethodgames.wordcurve.buttons.WordCurveKeyboardButton;
import com.singlemethodgames.wordcurve.utils.Constants;
import com.singlemethodgames.wordcurve.utils.QuestionAnsweredListener;
import com.singlemethodgames.wordcurve.utils.questionproviders.QuestionProvider;
import com.singlemethodgames.wordcurve.utils.tracking.Tracker;
import com.singlemethodgames.wordcurve.utils.wordlist.Word;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by cameron on 19/02/2018.
 */

public class Switch extends BaseMode implements QuestionAnsweredListener {
    private static final int TOTAL_ANSWERS = 4;
    // Keep track of buttons
    private List<WordCurveKeyboardButton> wordCurveKeyboardButtonList;

    private Label currentWordLabel;
    private Word currentWord;

    public Switch(final WordCurveGame game, SaveState saveState, final QuestionProvider questionProvider) {
        super(game, saveState, questionProvider);

        wordCurveKeyboardButtonList = new ArrayList<>();

        float width = 950;
        float x = (game.viewport.getWorldWidth() - width) / 2;
        for(int i = 0; i < 4; i++) {
            WordCurveKeyboardButton newButton = createButton(
                    x,
                    (i * 350) + 250,
                    width,
                    this,
                    game.camera
            );
            newButton.getColor().a = 0;
            newButton.setTraining(saveState.isTraining());
            wordCurveKeyboardButtonList.add(newButton);
            stage.addActor(newButton);
        }

        Label.LabelStyle labelStyle = new Label.LabelStyle(font96, Color.WHITE);
        currentWordLabel = new Label("", labelStyle);
        currentWordLabel.getColor().a = 0;
        currentWordLabel.setAlignment(Align.center);
        currentWord = new Word("");

        // Calculate the remaining height between the table and the first keyboard
        float remainingHeight =
                stage.getViewport().getWorldHeight()
                        - wordCurveKeyboardButtonList.get(3).getY()
                        - wordCurveKeyboardButtonList.get(3).getHeight()
                        - gameBar.getPrefHeight();

        currentWordLabel.setBounds(
                0, stage.getViewport().getWorldHeight() - gameBar.getPrefHeight() - remainingHeight,
                stage.getViewport().getWorldWidth(), remainingHeight
        );

        stage.addActor(currentWordLabel);
        stage.addActor(replayTable);

        loadUI();
    }

    @Override
    public void loadModeComponent() {
        stage.addAction(
            Actions.run(new Runnable() {
                @Override
                public void run() {
                    for(WordCurveKeyboardButton wordCurveKeyboardButton : wordCurveKeyboardButtonList) {
                        wordCurveKeyboardButton.addAction(
                                Actions.fadeIn(0.4f)
                        );
                    }
                }
            })
        );
    }

    @Override
    protected void upgradeOccured() {
        for(WordCurveKeyboardButton wordWaveButton: wordCurveKeyboardButtonList) {
            wordWaveButton.loadKeyboardDisplay();
        }
    }

    @Override
    protected void continueQuestions() {
        for (WordCurveKeyboardButton wordCurveKeyboardButton : wordCurveKeyboardButtonList) {
            if (wordCurveKeyboardButton.getColor().a < 1f) {
                wordCurveKeyboardButton.addAction(Actions.fadeIn(0.2f));
            }

            if (!wordCurveKeyboardButton.getColor().equals(Constants.Colours.Keyboard.BASE_COLOUR)) {
                wordCurveKeyboardButton.changeKeyboardColour(Constants.Colours.Keyboard.BASE_COLOUR, 0.2f);
            }

            wordCurveKeyboardButton.resetWordWaveGroup();
            wordCurveKeyboardButton.resetAllKeys();
        }
        loadQuestion();
    }

    @Override
    protected void noAnswer() {
        replayButton.hideReplayButton();
        float elapsedTime = gameBar.getElapsedTime();
        gameBar.resetElapsedTime();

        Set<Character> correctLetters = new HashSet<>();
        for(int i = 0; i < getCorrectAnswer().getWord().length(); i++) {
            correctLetters.add(getCorrectAnswer().getWord().charAt(i));
        }

        List<String> possibleAnswers = new ArrayList<>();
        for(WordCurveKeyboardButton wordCurveKeyboardButton : wordCurveKeyboardButtonList) {
            possibleAnswers.add(wordCurveKeyboardButton.getWord().getWord());
            wordCurveKeyboardButton.setTouchable(Touchable.disabled);
            wordCurveKeyboardButton.stopWordWave();

            if(wordCurveKeyboardButton.isAnswer()) {
                wordCurveKeyboardButton.showWordWave();
                wordCurveKeyboardButton.highlightAnswer(correctLetters, Constants.Colours.Keyboard.KEY_COLOUR,
                        Collections.EMPTY_SET, Constants.Colours.INCORRECT_KEY_RED);
            } else {
                wordCurveKeyboardButton.addAction(Actions.alpha(0.3f));
            }
        }

        Tracker.Answer answer = new Tracker.Answer(possibleAnswers, getCorrectAnswer().getWord(), "", false, elapsedTime);
        answeredIncorrectly(answer);
        recordTime = false;
    }

    @Override
    public void loadQuestion() {
        continueGame();
    }

    @Override
    public void continueGame() {
        super.continueGame();
        if (gameBar.getQuestionProvider().hasNextQuestion()) {
            currentWord = gameBar.getQuestionProvider().nextQuestion(TOTAL_ANSWERS, wordCurveKeyboardButtonList);
            if (currentWord != null) {
                currentWordLabel.setText(currentWord.getWord());
                currentWordLabel.addAction(Actions.color(Color.WHITE));

                startWordWaveQuestion();
            }
        } else {
            gameFinished();
        }
    }

    private void startWordWaveQuestion() {
        currentWordLabel.addAction(
                Actions.sequence(
                        Actions.fadeIn(0.2f),
                        Actions.run(new Runnable() {
                            @Override
                            public void run() {
                                for(final WordCurveKeyboardButton wordCurveKeyboardButton : wordCurveKeyboardButtonList) {
                                    wordCurveKeyboardButton.startWordCurve();
                                    wordCurveKeyboardButton.addAction(Actions.delay(
                                            0.5f, Actions.run(new Runnable() {
                                                @Override
                                                public void run() {
                                                    wordCurveKeyboardButton.setTouchable(Touchable.enabled);
                                                }
                                            })
                                    ));
                                }

                                stage.addAction(Actions.delay(0.5f, Actions.run(new Runnable() {
                                    @Override
                                    public void run() {
                                        recordTime = true;
                                    }
                                })));
                            }
                        })
                )
        );

        replayButton.displayReplayButtonWithDelay(0.5f);
    }

    @Override
    public void correctAnswer(Actor actor, final Word word, final float x, final float y) {
        animateAnswerTable(correctTable, x, y);
        replayButton.hideReplayButton();
        recordTime = false;

        List<String> possibleAnswers = new ArrayList<>();
        for(WordCurveKeyboardButton wordCurveKeyboardButton : wordCurveKeyboardButtonList) {
            possibleAnswers.add(wordCurveKeyboardButton.getWord().getWord());
            wordCurveKeyboardButton.setTouchable(Touchable.disabled);

            wordCurveKeyboardButton.stopWordWave();

            if(wordCurveKeyboardButton.isAnswer()) {
                wordCurveKeyboardButton.displayAnswer(1f);
            } else {
                wordCurveKeyboardButton.addAction(Actions.alpha(0.3f));
            }
        }

        float elapsedTime = gameBar.getElapsedTime();
        Tracker.Answer answer = new Tracker.Answer(possibleAnswers, getCorrectAnswer().getWord(), ((WordCurveKeyboardButton)actor).getWord().getWord(), true, elapsedTime);
        answeredCorrectly(answer);
        gameBar.resetElapsedTime();

        currentWordLabel.addAction(
                Actions.sequence(
                    Actions.delay(1f),
                    Actions.fadeOut(0.2f),
                    Actions.run(new Runnable() {
                        @Override
                        public void run() {
                            continueQuestions();
                        }
                    })
                )
        );
        gameBar.resetReplayCount();
    }

    @Override
    public void incorrectAnswer(final Actor actor, final Word word, final float x, final float y) {
        animateAnswerTable(incorrectTable, x, y);
        replayButton.hideReplayButton();

        recordTime = false;
        final float answeredTime = gameBar.getElapsedTime();
        gameBar.resetElapsedTime();

        currentWordLabel.setText(((WordCurveKeyboardButton)actor).getWord().getWord());
        currentWordLabel.addAction(Actions.color(Constants.Colours.INCORRECT_KEY_RED));

        Set<Character> correctLetters = new HashSet<>();
        for(int i = 0; i < getCorrectAnswer().getWord().length(); i++) {
            correctLetters.add(getCorrectAnswer().getWord().charAt(i));
        }

        List<String> possibleAnswers = new ArrayList<>();
        for(WordCurveKeyboardButton wordCurveKeyboardButton : wordCurveKeyboardButtonList) {
            possibleAnswers.add(wordCurveKeyboardButton.getWord().getWord());
            wordCurveKeyboardButton.setTouchable(Touchable.disabled);
            wordCurveKeyboardButton.stopWordWave();

            if(wordCurveKeyboardButton.equals(actor)) {
                Set<Character> incorrectLetters = new HashSet<>();
                for(int i = 0; i < wordCurveKeyboardButton.getWord().getWord().length(); i++) {
                    Character letter = wordCurveKeyboardButton.getWord().getWord().charAt(i);
                    if(!correctLetters.contains(letter)) {
                        incorrectLetters.add(letter);
                    }
                }

                wordCurveKeyboardButton.highlightAnswer(correctLetters, Constants.Colours.Keyboard.KEY_COLOUR,
                        incorrectLetters, Constants.Colours.INCORRECT_KEY_RED);
                wordCurveKeyboardButton.showWordWave();
            } else if(wordCurveKeyboardButton.isAnswer()) {
                wordCurveKeyboardButton.highlightAnswer(correctLetters, Collections.EMPTY_SET);
                wordCurveKeyboardButton.showWordWave();
            } else {
                wordCurveKeyboardButton.addAction(Actions.alpha(0.3f));
            }
        }

        Tracker.Answer answer = new Tracker.Answer(possibleAnswers, getCorrectAnswer().getWord(), ((WordCurveKeyboardButton)actor).getWord().getWord(), false, answeredTime);
        answeredIncorrectly(answer);
        gameBar.resetReplayCount();
    }

    private WordCurveKeyboardButton createButton(float x, float y, float width, QuestionAnsweredListener questionAnsweredListener, final OrthographicCamera camera) {
        WordCurveKeyboardButton wordCurveKeyboardButton = new WordCurveKeyboardButton(textureAtlas, x, y, width, questionAnsweredListener, camera, speedDifficulty, letterDifficulty, game.viewport);
        wordCurveKeyboardButton.setTouchable(Touchable.disabled);
        return wordCurveKeyboardButton;
    }

    @Override
    public Word getCorrectAnswer() {
        return currentWord;
    }

    @Override
    public void pauseActors() {
        for(WordCurveKeyboardButton wordCurveKeyboardButton : wordCurveKeyboardButtonList) {
            wordCurveKeyboardButton.hideWordWave();
        }

        currentWordLabel.getColor().a = 0f;
    }

    @Override
    public void resumeActors() {
        for(WordCurveKeyboardButton wordCurveKeyboardButton : wordCurveKeyboardButtonList) {
            wordCurveKeyboardButton.displayWordWave();
        }

        currentWordLabel.getColor().a = 1f;
    }

    @Override
    public void replayQuestion() {
        gameBar.increaseReplayCount();

        float animateAtX = replayButton.getX() + (replayButton.getWidth() / 2f);
        float animateAtY = replayButton.getY() + replayButton.getHeight();
        animateAnswerTable(replayScoreTable, animateAtX, animateAtY);

        for(WordCurveKeyboardButton wordWaveButton: wordCurveKeyboardButtonList) {
            wordWaveButton.startWordCurve();
        }
    }
}
