package com.singlemethodgames.wordcurve.groups;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.singlemethodgames.wordcurve.WordCurveGame;
import com.singlemethodgames.wordcurve.actors.WordCurveGroup;
import com.singlemethodgames.wordcurve.screens.difficulty.LetterDifficulty;
import com.singlemethodgames.wordcurve.screens.difficulty.LetterSettings;
import com.singlemethodgames.wordcurve.screens.difficulty.SpeedDifficulty;
import com.singlemethodgames.wordcurve.utils.Constants;
import com.singlemethodgames.wordcurve.utils.QwertyKeyboard;
import com.singlemethodgames.wordcurve.utils.Utils;
import com.singlemethodgames.wordcurve.utils.wordlist.Word;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by cameron on 19/02/2018.
 */

public class WordCurveKeyboardGroup extends Group implements ProgressListener {
    protected final WordCurveGroup wordCurveGroup;
    private TextureRegion keyboardKey;
    private QwertyKeyboard qwertyKeyboard;
    private Vector2 keyVector;
    private FrameBuffer fbo;
    private TextureRegion textureRegion;
    private LetterDifficulty letterDifficulty;
    private ObjectMap<Character, KeyStatus> keyStatusMap = new ObjectMap<>();
    private Array<Character> letterDisappearOrder = Utils.randomAlphabetList();
    private Array<Character> keyDisappearOrder = Utils.randomAlphabetList();
    private TextureAtlas textureAtlas;
    protected final Image keyboardBaseImg;
    protected Word word;
    protected float[] progressPercentages;
    protected int wordIndex;
    private List<Character> uniqueLetters;
    private Viewport viewport;
    private boolean training = false;

    public WordCurveKeyboardGroup(TextureAtlas textureAtlas, float x, float y, float width, Touchable button, final OrthographicCamera camera, SpeedDifficulty speedDifficulty, LetterDifficulty letterDifficulty, Viewport viewport) {
        this.viewport = viewport;
        this.letterDifficulty = letterDifficulty;
        uniqueLetters = new ArrayList<>();
        this.textureAtlas = textureAtlas;
        this.word = new Word("");

        setTouchable(button);

        float defaultWidth = 1070;
        float defaultHeight = 340;
        float scale = width / defaultWidth;

        setBounds(x, y, width, defaultHeight * scale);

        textureRegion = new TextureRegion();
        fbo = new FrameBuffer(Pixmap.Format.RGBA8888, WordCurveGame.WIDTH, WordCurveGame.HEIGHT, false);

        keyVector = new Vector2();

        keyboardKey = textureAtlas.findRegion(Constants.TextureRegions.KEYBOARD_KEY);

        qwertyKeyboard = new QwertyKeyboard(0, 0, scale);

        TextureRegionDrawable base = new TextureRegionDrawable(textureAtlas.findRegion(Constants.TextureRegions.BASE));
        keyboardBaseImg = new Image(base);
        keyboardBaseImg.setBounds(0, 0, getWidth(), getHeight());
        keyboardBaseImg.setColor(Constants.Colours.Keyboard.BASE_COLOUR);
        keyboardBaseImg.setTouchable(button);

        addActor(keyboardBaseImg);
        addKeys(Touchable.disabled);

        wordCurveGroup = new WordCurveGroup(textureAtlas, camera, qwertyKeyboard, speedDifficulty, this);
        wordCurveGroup.parentPositionChanged(getX(), getY());
        addActor(wordCurveGroup);
    }

    private void addKeys(Touchable button) {
        for (char alphabet = 'a'; alphabet <= 'z'; alphabet++) {
            String letter = String.valueOf(alphabet);

            qwertyKeyboard.getKeyPosition(keyVector, letter);

            final Image keyImg = new Image(keyboardKey);
            keyImg.setPosition(keyVector.x, keyVector.y);
            keyImg.setTouchable(Touchable.disabled);
            keyImg.setScale(qwertyKeyboard.getScale());
            keyImg.setTouchable(button);
            keyImg.setColor(Constants.Colours.Keyboard.KEY_COLOUR);

            TextureRegion letterTexture = textureAtlas.findRegion(letter);

            final Image letterImg = new Image(letterTexture);
            letterImg.setPosition(keyVector.x, keyVector.y);
            letterImg.setTouchable(Touchable.disabled);
            letterImg.setScale(qwertyKeyboard.getScale());
            letterImg.setColor(Constants.Colours.Keyboard.LETTER_COLOUR);
            letterImg.setTouchable(button);

            addActor(keyImg);
            addActor(letterImg);

            this.keyStatusMap.put(alphabet, new KeyStatus(keyImg, letterImg, 1f, 1f));
        }
    }

    public Word getWord() {
        return word;
    }

    public void setWord(Word word) {
        uniqueLetters.clear();
        for(char c: word.getWord().toCharArray()) {
            if(!uniqueLetters.contains(c)) {
                uniqueLetters.add(c);
            }
        }

        this.word = word;
        wordIndex = 0;
    }

    protected void setKeyboardListener(InputListener inputListener) {
        keyboardBaseImg.addListener(inputListener);
    }

    public void loadKeyboardDisplay() {
        loadKeyboardDisplay(0.25f);
    }

    public void loadKeyboardDisplay(float duration) {
        switch(letterDifficulty.getLetterSettings().keyDisplay) {
            case -1:
                KeyStatus.hideAllKeys(keyStatusMap);
                break;
            case 0:
                updateKeyStatusForKey(letterDifficulty.getKeyCountToHide());
                break;
            case 1:
                KeyStatus.displayAllKeys(keyStatusMap);
                break;
        }

        switch(letterDifficulty.getLetterSettings().letterDisplay) {
            case -1:
                KeyStatus.hideAllLetters(keyStatusMap);
                break;
            case 0:
                updateLetterStatusForKey(letterDifficulty.getLetterCountToHide());
                break;
            case 1:
                KeyStatus.displayAllLetters(keyStatusMap);
                break;
        }

        for (char alphabet = 'a'; alphabet <= 'z'; alphabet++) {
            KeyStatus keyStatus = keyStatusMap.get(alphabet);
            keyStatus.getKey().addAction(
                    Actions.alpha(keyStatus.getKeyAlpha(), duration)
            );
            keyStatus.getLetter().addAction(
                    Actions.alpha(keyStatus.getLetterAlpha(), duration)
            );
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (getColor().a < 1) {
            batch.end();

            float currentAlpha = getColor().a;
            getColor().a = 1;

            fbo.begin();

            Gdx.gl.glClearColor(0, 0, 0, 0);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            batch.begin();

            super.draw(batch, 1f);

            batch.end();

            fbo.end();
            textureRegion.setRegion(fbo.getColorBufferTexture());
            textureRegion.flip(false, true);

            viewport.apply();
            batch.setProjectionMatrix(viewport.getCamera().combined);
            batch.begin();
            batch.setColor(getColor().r, getColor().g, getColor().b, currentAlpha * parentAlpha);
            batch.draw(textureRegion, 0, 0);

            getColor().a = currentAlpha;

        } else {
            super.draw(batch, parentAlpha);
        }
    }

    public void pressed() {
        keyboardBaseImg.setColor(Constants.Colours.Keyboard.BASE_COLOUR_PRESSED);

        if(!this.letterDifficulty.getLetterSettings().equals(LetterSettings.NONE)) {
            for (char alphabet = 'a'; alphabet <= 'z'; alphabet++) {
                pressKey(alphabet);
            }
        }
    }

    public void released() {
        keyboardBaseImg.setColor(Constants.Colours.Keyboard.BASE_COLOUR);
        for(KeyStatus keyStatus: keyStatusMap.values()) {
            Color color = Constants.Colours.Keyboard.KEY_COLOUR.cpy();
            color.a = keyStatus.getKeyAlpha();
            keyStatus.getKey().setColor(color);
        }
    }

    public void createAndPerformWordCurve() {
        wordIndex = 0;
        wordCurveGroup.performWordCurveAction(word, true);
    }

    public void reset() {
        reset(0f);
    }

    public void reset(float duration) {
        clearActions();
        releaseAllKeys(duration);

        wordCurveGroup.reset();
    }

    public void releaseAllKeys(float duration) {
        for (char alphabet = 'a'; alphabet <= 'z'; alphabet++) {
            final KeyStatus keyStatus = keyStatusMap.get(alphabet);
            keyStatus.getKey().clearActions();
            keyStatus.getLetter().clearActions();

            if (keyStatus.getKeyAlpha() == 1f) {
                keyStatus.getKey().addAction(Actions.color(Constants.Colours.Keyboard.KEY_COLOUR, duration));
            } else {
                keyStatus.getKey().addAction(Actions.color(Constants.Colours.Keyboard.KEY_COLOUR_ZERO, duration));
            }

            if (keyStatus.getLetterAlpha() == 1f) {
                keyStatus.getLetter().setColor(Constants.Colours.Keyboard.LETTER_COLOUR);
            } else {
                keyStatus.getLetter().addAction(Actions.fadeOut(duration));
            }
        }
    }

    public void changeKeyboardColour(Color color, float duration) {
        SequenceAction sequenceAction = new SequenceAction();
        sequenceAction.addAction(Actions.color(color));

        if(duration > 0f) {
            sequenceAction.addAction(Actions.delay(duration));
            sequenceAction.addAction(Actions.color(Constants.Colours.Keyboard.BASE_COLOUR, Constants.Timing.FADE_TRANSITION_LENGTH));
        }
        keyboardBaseImg.clearActions();
        keyboardBaseImg.addAction(
                sequenceAction
        );
    }

    private String removeDuplicatesFromString(String letters) {
        char[] chars = letters.toCharArray();
        Set<Character> charSet = new LinkedHashSet<>();
        for (char c : chars) {
            charSet.add(c);
        }

        StringBuilder sb = new StringBuilder();
        for (Character character : charSet) {
            sb.append(character);
        }

        return sb.toString();
    }

    public void highlightAnswer(Set<Character> correctLetters, Set<Character> incorrectLetters) {
        this.highlightAnswer(correctLetters, Constants.Colours.Keyboard.Button.CORRECT, incorrectLetters, Constants.Colours.INCORRECT_KEY_RED);
    }


    public void highlightAnswer(Set<Character> correctLetters, Color correctColour, Set<Character> incorrectLetters, Color incorrectColour) {
        for (char character = 'a'; character <= 'z'; character++) {
            KeyStatus keyStatus = keyStatusMap.get(character);
            keyStatus.getKey().clearActions();
            keyStatus.getLetter().clearActions();
            if (correctLetters.contains(character)) {
                highlightKeyWithColour(keyStatus, correctColour);
            } else if (incorrectLetters.contains(character)) {
                highlightKeyWithColour(keyStatus, incorrectColour);
            } else {
                keyStatus.getKey().addAction(Actions.fadeOut(0.2f));
                keyStatus.getLetter().addAction(Actions.fadeOut(0.2f));
            }
        }
    }

    private void highlightKeyWithColour(KeyStatus key, Color colourToHighlight) {
        key.getKey().clearActions();
        key.getKey().setColor(colourToHighlight);
        key.getLetter().clearActions();
        key.getLetter().getColor().a = 1f;
    }

    public void highlightLetters(String word, float duration, Color pressedTint) {
        word = removeDuplicatesFromString(word);

        for (char character = 'a'; character <= 'z'; character++) {
            String letter = String.valueOf(character).toLowerCase();
            KeyStatus keyStatus = keyStatusMap.get(character);
            if (word.toLowerCase().contains(letter)) {
                SequenceAction sequenceAction =  new SequenceAction();
                sequenceAction.addAction(Actions.color(pressedTint));

                if (duration > 0f) {
                    sequenceAction.addAction(Actions.delay(duration));
                    Color toColor = Constants.Colours.Keyboard.KEY_COLOUR.cpy();
                    toColor.a = keyStatus.getKeyAlpha();
                    sequenceAction.addAction(
                            Actions.parallel(
                                    Actions.color(toColor, Constants.Timing.FADE_TRANSITION_LENGTH)
                            )
                    );
                }
                keyStatus.getKey().clearActions();
                keyStatus.getKey().addAction(sequenceAction);

                SequenceAction letterSequence = new SequenceAction();
                letterSequence.addAction(Actions.fadeIn(0f));

                if (duration > 0f && keyStatus.getLetterAlpha() != 1f) {
                    letterSequence.addAction(Actions.delay(duration));
                    letterSequence.addAction(Actions.fadeOut(Constants.Timing.FADE_TRANSITION_LENGTH));
                }

                keyStatus.getLetter().clearActions();
                keyStatus.getLetter().addAction(letterSequence);
            } else {
                keyStatus.getKey().clearActions();
                Color keyColour = Constants.Colours.Keyboard.KEY_COLOUR.cpy();
                keyColour.a = keyStatus.getKey().getColor().a;
                keyStatus.getKey().addAction(Actions.color(keyColour));
                if (keyStatus.getKeyAlpha() == 1f) {
                    keyStatus.getKey().addAction(fadeOutThenIn(duration));
                }

                keyStatus.getLetter().clearActions();
                if (keyStatus.getLetterAlpha() == 1f) {
                    keyStatus.getLetter().addAction(fadeOutThenIn(duration));
                }
            }
        }
    }

    private static SequenceAction fadeOutThenIn(float duration) {
        SequenceAction sequenceAction =  new SequenceAction();
        sequenceAction.addAction(Actions.fadeOut(0f));

        if (duration > 0f) {
            sequenceAction.addAction(Actions.delay(duration));
            sequenceAction.addAction(Actions.fadeIn(Constants.Timing.FADE_TRANSITION_LENGTH));
        }

        return sequenceAction;
    }

    public void resetAllKeys() {
        for (char character = 'a'; character <= 'z'; character++) {
            KeyStatus keyStatus = keyStatusMap.get(character);
            keyStatus.getLetter().clearActions();
            keyStatus.getKey().clearActions();
            if (keyStatus.getLetterAlpha() == 1f) {
                keyStatus.getLetter().addAction(Actions.fadeIn(0.2f));
            } else {
                keyStatus.getLetter().addAction(Actions.fadeOut(0.2f));
            }

            if (keyStatus.getKeyAlpha() == 1f) {
                keyStatus.getKey().addAction(Actions.color(Constants.Colours.Keyboard.KEY_COLOUR, 0.2f));
            } else {
                keyStatus.getKey().addAction(Actions.color(Constants.Colours.Keyboard.KEY_COLOUR_ZERO, 0.2f));
            }
        }
    }

    private void pressKey(Character key) {
        pressKey(keyStatusMap.get(key), 0f, Constants.Colours.Keyboard.KEY_COLOUR_PRESSED);
    }

    private void pressKey(KeyStatus keyStatus, float duration, Color toColour) {
        keyStatus.getKey().clearActions();
        keyStatus.getKey().addAction(Actions.color(toColour));
        if (duration != 0) {
            SequenceAction keySequence = new SequenceAction();
            keySequence.addAction(Actions.delay(duration));
            if(keyStatus.getKeyAlpha() == 0) {
                keySequence.addAction(Actions.fadeOut(0.5f));
            } else {
                keySequence.addAction(Actions.color(Constants.Colours.Keyboard.KEY_COLOUR, 0.5f));
            }
            keyStatus.getKey().addAction(keySequence);

            keyStatus.getLetter().clearActions();
            keyStatus.getLetter().getColor().a = 1f;

            if(keyStatus.getLetterAlpha() == 0f) {
                keyStatus.getLetter().addAction(
                        Actions.sequence(
                                Actions.delay(duration),
                                Actions.fadeOut(0.5f)
                        )
                );
            }
        }
    }

    public void hideWordWave() {
        wordCurveGroup.hideWordCurve();
    }

    public void displayWordWave() {
        wordCurveGroup.displayWordCurve();
    }

    public QwertyKeyboard getQwertyKeyboard() {
        return qwertyKeyboard;
    }

    private void updateLetterStatusForKey(int count) {
        count = count > 25 ? 25 : count;
        letterDisappearOrder.shuffle();
        if(count > 0) {
            int hiddenCount = 0;
            for(int i = 0; i < letterDisappearOrder.size; i++) {
                Character letter = letterDisappearOrder.get(i);
                if (hiddenCount < count) {
                    if (count < 9 && uniqueLetters.contains(letter)) {
                        keyStatusMap.get(letter).setLetterAlpha(1f);
                    } else {
                        keyStatusMap.get(letter).setLetterAlpha(0f);
                        hiddenCount++;
                    }
                } else {
                    keyStatusMap.get(letter).setLetterAlpha(1f);
                }
            }
        }
    }

    private void updateKeyStatusForKey(int count) {
        count = count > 25 ? 25 : count;
        keyDisappearOrder.shuffle();
        if(count > 0) {
            int hiddenCount = 0;
            for(int i = 0; i < keyDisappearOrder.size; i++) {
                Character letter = keyDisappearOrder.get(i);
                if (hiddenCount < count) {
                    if (count < 9 && uniqueLetters.contains(letter)) {
                        keyStatusMap.get(letter).setKeyAlpha(1f);
                    } else {
                        keyStatusMap.get(letter).setKeyAlpha(0f);
                        hiddenCount++;
                    }
                } else {
                    keyStatusMap.get(letter).setKeyAlpha(1f);
                }
            }
        }
    }

    public void setTraining(boolean training) {
        this.training = training;
    }

    public boolean isTraining() {
        return training;
    }

    public void performWordCurveAction(Word word, List<RunnableAction> homingOutActions, List<RunnableAction> tailFinishedActions, boolean removeTail) {
        wordIndex = 0;
        wordCurveGroup.performWordCurveAction(word, homingOutActions, tailFinishedActions, removeTail);
    }

    public void clearWordCurveGroupActions() {
        wordCurveGroup.clearActions();
    }

    public void showWordCurve() {
        wordCurveGroup.showWordCurve();
    }

    public void fadeWordCurveOut(float duration) {
        wordCurveGroup.fadeWordCurveOut(duration);
    }

    public void resetWordWaveGroup() {
        wordIndex = 0;
        wordCurveGroup.reset();
    }

    @Override
    public void setPointPercentages(float[] percentages) {
        progressPercentages = percentages;
    }

    @Override
    public void atPosition(float percent, float duration) {
        if(training) {
            if(wordIndex < word.getWord().length()) {
                if (percent >= progressPercentages[wordIndex]) {
                    KeyStatus keyStatus = keyStatusMap.get(word.getWord().charAt(wordIndex));
                    pressKey(keyStatus, duration + 1f, Color.LIGHT_GRAY);
                    wordIndex++;
                }
            }
        }
    }
}
