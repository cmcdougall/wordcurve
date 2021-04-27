package com.singlemethodgames.wordcurve.buttons;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.singlemethodgames.wordcurve.utils.ButtonMovements;
import com.singlemethodgames.wordcurve.utils.Constants;
import com.singlemethodgames.wordcurve.utils.wordlist.Word;

/**
 * Created by cameron on 19/02/2018.
 */

public class WordButton extends ImageTextButton implements WordCurveButton {
    private Word word;
    private boolean isAnswer;
    private float screenWidth;
    private ImageTextButtonStyle correctStyle;
    private ImageTextButtonStyle incorrectStyle;

    public static WordButton createWordButton(Word word, boolean isAnswer, BitmapFont bitmapFont, float screenWidth, final TextureAtlas textureAtlas) {
        NinePatchDrawable unansweredDrawable = new NinePatchDrawable(textureAtlas.createPatch(Constants.TextureRegions.BUTTON));
        NinePatchDrawable pressedDrawable = new NinePatchDrawable(textureAtlas.createPatch(Constants.TextureRegions.BUTTON_PRESSED));
        NinePatchDrawable correctDrawable = new NinePatchDrawable(textureAtlas.createPatch(Constants.TextureRegions.BUTTON_CORRECT));
        NinePatchDrawable incorrectDrawable = new NinePatchDrawable(textureAtlas.createPatch(Constants.TextureRegions.BUTTON_INCORRECT));

        ImageTextButtonStyle correctButtonStyle = new ImageTextButtonStyle(unansweredDrawable, pressedDrawable, correctDrawable, bitmapFont);
        ImageTextButtonStyle incorrectButtonStyle = new ImageTextButtonStyle(unansweredDrawable, pressedDrawable, incorrectDrawable, bitmapFont);

        return new WordButton(correctButtonStyle, incorrectButtonStyle, word, isAnswer, screenWidth);
    }

    private WordButton(ImageTextButtonStyle correctStyle, ImageTextButtonStyle incorrectStyle, Word word, boolean isAnswer, float screenWidth) {
        super(word.getWord(), correctStyle);

        this.isAnswer = isAnswer;
        this.screenWidth = screenWidth;
        this.word = word;

        this.correctStyle = correctStyle;
        this.incorrectStyle = incorrectStyle;

        if (this.isAnswer) {
            setStyle(correctStyle);
        } else {
            setStyle(incorrectStyle);
        }

    }

    public boolean isAnswer() {
        return isAnswer;
    }

    public void setAnswer(boolean isAnswer) {
        this.isAnswer = isAnswer;
    }

    private void updateStyle() {
        if (this.isAnswer) {
            setStyle(correctStyle);
        } else {
            setStyle(incorrectStyle);
        }
    }

    public void setWord(Word word) {
        this.word = word;
    }

    public void moveOntoScreenAction() {
        updateStyle();
        setChecked(false);
        setText(word.getWord());
        addAction(Actions.color(Color.WHITE));
        setTouchable(Touchable.enabled);
        float x = (screenWidth / 2) - (getWidth() / 2);

        MoveToAction moveToAction = new MoveToAction();
        moveToAction.setX(x - 200);
        moveToAction.setY(getY());

        ParallelAction parallelAction = ButtonMovements.moveButtonOnScreen(x, getY(), Constants.Timing.BUTTON_MOVEMENT);

        addAction(new SequenceAction(moveToAction, parallelAction));
    }

    public void moveOffScreenAction(float x, float delay, RunnableAction runnableAction) {
        SequenceAction sequenceAction = new SequenceAction();
        sequenceAction.addAction(Actions.delay(delay));
        sequenceAction.addAction(ButtonMovements.moveButtonOffScreen(x, getY(), Constants.Timing.BUTTON_MOVEMENT));

        sequenceAction.addAction(Actions.parallel(
                Actions.moveTo(0-getWidth(), getY()),
                Actions.fadeOut(0f),
                runnableAction
        ));

        addAction(sequenceAction);
    }

    public void incorrectGuessAction() {
        final float originalY = getY();

        SequenceAction sequenceAction = ButtonMovements.moveButtonOffScreen(getX(), getY() - 100, Constants.Timing.INCORRECT_GUESS_MOVEMENT);

        sequenceAction.addAction(Actions.parallel(
                Actions.moveTo(0 - getWidth(), originalY),
                Actions.fadeOut(0f)
        ));

        addAction(sequenceAction);
    }

    public Word getWord() {
        return word;
    }

    @Override
    public Actor getActor() {
        return this;
    }
}
