package com.singlemethodgames.wordcurve.screens.variants;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.singlemethodgames.wordcurve.SaveState;
import com.singlemethodgames.wordcurve.WordCurveGame;
import com.singlemethodgames.wordcurve.buttons.WordButton;
import com.singlemethodgames.wordcurve.groups.WordCurveKeyboardGroup;
import com.singlemethodgames.wordcurve.utils.Constants;
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

public class Classic extends BaseMode {
    private static final int TOTAL_ANSWERS = 4;
    private final WordCurveKeyboardGroup wordCurveKeyboardGroup;
    private final List<WordButton> wordButtons;

    private Word currentWord;
    public Classic(final WordCurveGame game, SaveState saveState, final QuestionProvider questionProvider) {
        super(game, saveState, questionProvider);

        wordCurveKeyboardGroup = new WordCurveKeyboardGroup(textureAtlas, 5, 1400, 1070, Touchable.disabled, this.game.camera, speedDifficulty, letterDifficulty, game.viewport);
        wordCurveKeyboardGroup.getColor().a = 0;
        wordCurveKeyboardGroup.setTraining(saveState.isTraining());
        stage.addActor(wordCurveKeyboardGroup);

        Group answerGroup = new Group();
        wordButtons = new ArrayList<>();

        int defaultY = 350;
        for (int i = 0; i < TOTAL_ANSWERS; i++) {
            WordButton newButton = createButton(defaultY + (i * 250), font96);
            answerGroup.addActor(newButton);
            wordButtons.add(newButton);
        }

        answerGroup.setName("answers");
        stage.addActor(answerGroup);
        stage.addActor(replayTable);

        loadUI();
    }

    @Override
    public void loadModeComponent() {
        wordCurveKeyboardGroup.addAction(
                Actions.sequence(
                        Actions.fadeIn(0.4f)
                )
        );
    }

    @Override
    protected void upgradeOccured() {
        wordCurveKeyboardGroup.loadKeyboardDisplay();
    }

    @Override
    protected void continueQuestions() {
        for (final WordButton option : wordButtons) {
            option.incorrectGuessAction();
        }

        wordCurveKeyboardGroup.reset(0.2f);
        loadQuestion();
    }

    @Override
    protected void noAnswer() {
        replayButton.hideReplayButton();
        float elapsedTime = gameBar.getElapsedTime();
        gameBar.resetElapsedTime();

        List<String> possibleAnswers = new ArrayList<>();
        for (final WordButton option : wordButtons) {
            possibleAnswers.add(option.getWord().getWord());
            option.setTouchable(Touchable.disabled);
            if (!option.isAnswer()) {
                option.incorrectGuessAction();
            }
        }

        Set<Character> correctLetters = new HashSet<>();
        for(int i = 0; i < currentWord.getWord().length(); i++) {
            Character character = currentWord.getWord().toLowerCase().charAt(i);
            correctLetters.add(character);
        }

        Tracker.Answer answer = new Tracker.Answer(
                possibleAnswers, currentWord.getWord(), "", false, elapsedTime
        );
        answeredIncorrectly(answer);

        wordCurveKeyboardGroup.reset();
        wordCurveKeyboardGroup.clearWordCurveGroupActions();
        wordCurveKeyboardGroup.showWordCurve();
        wordCurveKeyboardGroup.highlightAnswer(correctLetters, Constants.Colours.Keyboard.KEY_COLOUR,
                Collections.EMPTY_SET, Color.WHITE);
    }

    @Override
    public void loadQuestion() {
        continueGame();
    }

    @Override
    public void continueGame() {
        super.continueGame();
        if (gameBar.getQuestionProvider().hasNextQuestion()) {
            currentWord = gameBar.getQuestionProvider().nextQuestion(TOTAL_ANSWERS, wordButtons);
            wordCurveKeyboardGroup.setWord(currentWord);
            if(currentWord != null) {
                RunnableAction runnableAction = new RunnableAction();
                runnableAction.setRunnable(new Runnable() {
                    @Override
                    public void run() {
                        moveButtonsOnScreen();
                    }
                });
                wordCurveKeyboardGroup.performWordCurveAction(currentWord, Collections.singletonList(runnableAction), null, true);
            }
        } else {
            gameFinished();
        }
    }

    private void moveButtonsOnScreen() {
        recordTime = true;
        replayButton.displayReplayButton();

        for (final WordButton wordButton : wordButtons) {
            wordButton.moveOntoScreenAction();
        }
    }

    private WordButton createButton(float y, BitmapFont bitmapFont) {
        final WordButton wordButton = WordButton.createWordButton(new Word(""), false, bitmapFont, stage.getViewport().getWorldWidth(), textureAtlas);
        wordButton.setBounds(0-Constants.Sizes.ANSWER_BUTTON_WIDTH, y, Constants.Sizes.ANSWER_BUTTON_WIDTH, Constants.Sizes.ANSWER_BUTTON_HEIGHT);

        wordButton.getColor().a = 0;

        wordButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                recordTime = false;

                final float answeredTime = gameBar.getElapsedTime();
                gameBar.resetElapsedTime();

//                wordCurveKeyboardGroup.reset();
                wordCurveKeyboardGroup.clearWordCurveGroupActions();
                wordCurveKeyboardGroup.showWordCurve();

                float animateAtX = wordButton.getX() + x;
                float animateAtY = wordButton.getY() + wordButton.getHeight();

                if (wordButton.isAnswer()) {
                    animateAnswerTable(correctTable, animateAtX, animateAtY);
                    correctAnswer(wordButton, answeredTime);
                } else {
                    animateAnswerTable(incorrectTable, animateAtX, animateAtY);
                    incorrectAnswer(wordButton, answeredTime);
                }
                gameBar.resetReplayCount();
            }
        });

        return wordButton;
    }

    @Override
    public void replayQuestion() {
        if (currentWord != null && !currentWord.getWord().isEmpty()) {
            gameBar.increaseReplayCount();
            wordCurveKeyboardGroup.createAndPerformWordCurve();

            float animateAtX = replayButton.getX() + (replayButton.getWidth() / 2f);
            float animateAtY = replayButton.getY() + replayButton.getHeight();
            animateAnswerTable(replayScoreTable, animateAtX, animateAtY);
        }
    }

    @Override
    public Word getCorrectAnswer() {
        return currentWord;
    }

    private void correctAnswer(final WordButton wordButton, float time) {
        List<String> possibleAnswers = new ArrayList<>();
        // Make all button untouchable, find all other buttons to move off screen
        for (final WordButton option : wordButtons) {
            possibleAnswers.add(option.getWord().getWord());
            option.setTouchable(Touchable.disabled);
            if (!option.equals(wordButton)) {
                option.incorrectGuessAction();
            }
        }

        Tracker.Answer answer = new Tracker.Answer(
            possibleAnswers, currentWord.getWord(), wordButton.getWord().getWord(), true, time
        );
        answeredCorrectly(answer);
        replayButton.hideReplayButton();

        // Highlight button and keys green
        wordCurveKeyboardGroup.highlightLetters(wordButton.getWord().getWord(), 1f, Constants.Colours.Keyboard.Button.CORRECT);

        wordButton.moveOffScreenAction(wordButton.getX() + 200, 1f,
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        wordCurveKeyboardGroup.fadeWordCurveOut(Constants.Timing.BUTTON_MOVEMENT);
                        loadQuestion();
                    }
                })
        );
    }

    private void incorrectAnswer(final WordButton wordButton, float incorrectAnswerTime) {
        replayButton.hideReplayButton();
        List<String> possibleAnswers = new ArrayList<>();

        for (final WordButton option : wordButtons) {
            possibleAnswers.add(option.getWord().getWord());
            option.setTouchable(Touchable.disabled);
            if (!option.equals(wordButton) && !option.isAnswer()) {
                option.incorrectGuessAction();
            } else if (option.isAnswer()) {
                option.setChecked(true);
            }
        }

        Tracker.Answer answer = new Tracker.Answer(
                possibleAnswers, currentWord.getWord(), wordButton.getWord().getWord(), false, incorrectAnswerTime
        );
        answeredIncorrectly(answer);

        Set<Character> incorrectLetters = new HashSet<>();
        for(int i = 0; i < wordButton.getWord().getWord().length(); i++) {
            Character character = wordButton.getWord().getWord().toLowerCase().charAt(i);
            incorrectLetters.add(character);
        }

        Set<Character> correctLetters = new HashSet<>();
        for(int i = 0; i < currentWord.getWord().length(); i++) {
            Character character = currentWord.getWord().toLowerCase().charAt(i);
            if(!incorrectLetters.contains(character)) {
                correctLetters.add(character);
            }
        }

        wordCurveKeyboardGroup.highlightAnswer(correctLetters, incorrectLetters);
    }

    @Override
    public void pauseActors() {
        this.wordCurveKeyboardGroup.hideWordWave();

        for (WordButton wordButton : wordButtons) {
            wordButton.setText("");
        }
    }

    @Override
    public void resumeActors() {
        this.wordCurveKeyboardGroup.displayWordWave();

        for (WordButton wordButton : wordButtons) {
            wordButton.setText(wordButton.getWord().getWord());
        }
    }
}
