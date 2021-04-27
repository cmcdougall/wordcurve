package com.singlemethodgames.wordcurve.buttons;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.singlemethodgames.wordcurve.groups.WordCurveKeyboardGroup;
import com.singlemethodgames.wordcurve.screens.difficulty.LetterDifficulty;
import com.singlemethodgames.wordcurve.screens.difficulty.SpeedDifficulty;
import com.singlemethodgames.wordcurve.utils.Constants;
import com.singlemethodgames.wordcurve.utils.QuestionAnsweredListener;

/**
 * Created by cameron on 19/02/2018.
 */

public class WordCurveKeyboardButton extends WordCurveKeyboardGroup implements WordCurveButton {
    private boolean answer;
    private boolean answered;
    public final float x;
    public final float y;

    public WordCurveKeyboardButton(TextureAtlas textureAtlas, float x, float y, float width, final QuestionAnsweredListener questionAnsweredListener, final OrthographicCamera camera, SpeedDifficulty speedDifficulty, LetterDifficulty letterDifficulty, Viewport viewport) {
        super(textureAtlas, x, y, width, Touchable.enabled, camera, speedDifficulty, letterDifficulty, viewport);
        answer = false;
        answered = false;
        this.x = x;
        this.y = y;

        InputListener keyboardInputListener = new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                if (isTouchable()) {
                    pressed();
                }
                return true;
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if (isTouchable()) {
                    released();
                }
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {

                released();

                if (x > keyboardBaseImg.getOriginX() && x < (keyboardBaseImg.getOriginX() + keyboardBaseImg.getWidth())
                        && y > keyboardBaseImg.getOriginY() && y < (keyboardBaseImg.getOriginY() + keyboardBaseImg.getHeight())
                        && isTouchable()) {

                    // It's giving me the coordinates from the texture, which is much bigger than this :S
                    x = x * getQwertyKeyboard().getScale();

                    float animateAtX = WordCurveKeyboardButton.this.x + x;
                    float animateAtY = WordCurveKeyboardButton.this.y + getHeight();

                    if (isAnswer()) {
                        questionAnsweredListener.correctAnswer(WordCurveKeyboardButton.this, getWord(), animateAtX, animateAtY);
                    } else {
                        questionAnsweredListener.incorrectAnswer(WordCurveKeyboardButton.this, getWord(), animateAtX, animateAtY);
                    }
                }
            }
        };

        setKeyboardListener(keyboardInputListener);

        wordCurveGroup.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return false;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
            }
        });
    }

    public void displayAnswer(float duration) {
        changeKeyboardColour(Constants.Colours.CORRECT_GREEN, duration);
        highlightLetters(word.getWord(), duration, Constants.Colours.Keyboard.Button.CORRECT);
        wordCurveGroup.showWordCurve();
    }

    public boolean isAnswer() {
        return answer;
    }

    public boolean isAnswered() {
        return answered;
    }

    public void setAnswered(boolean answered) {
        this.answered = answered;
    }

    public void startWordCurve() {
        wordIndex = 0;
        wordCurveGroup.reset();
        wordCurveGroup.performWordCurveAction(word, true, 2f);
    }

    public void showWordWave() {
        wordCurveGroup.showWordCurve();
    }

    public void stopWordWave() {
        wordCurveGroup.reset();
        wordCurveGroup.clearActions();
    }

    public void setAnswer(boolean answer) {
        this.answer = answer;
    }

    @Override
    public Actor getActor() {
        return this;
    }
}
