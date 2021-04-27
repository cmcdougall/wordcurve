package com.singlemethodgames.wordcurve.utils;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.singlemethodgames.wordcurve.utils.wordlist.Word;

/**
 * Created by cameron on 19/02/2018.
 */

public class WordCurveDetails {
    private Array<Vector2> coordinates;
    private int wordLength;

    public WordCurveDetails(Array<Vector2> coordinates, int wordLength) {
        this.coordinates = coordinates;
        this.wordLength = wordLength;
    }

    public float getStartX() {
        if(this.coordinates.size > 0) {
            return this.coordinates.get(0).x;
        }

        return -1;
    }

    public float getStartY() {
        if(this.coordinates.size > 0) {
            return this.coordinates.get(0).y;
        }

        return -1;
    }

    public Array<Vector2> getCoordinates() {
        return coordinates;
    }

    public int getWordLength() {
        return wordLength;
    }

    public static WordCurveDetails getWordWaveDetails(Word word, QwertyKeyboard qwertyKeyboard) {
        Array<Vector2> coordinates = qwertyKeyboard.getKeyboardCoordsForWord(word.getWord());
        return new WordCurveDetails(coordinates, word.getWord().length());
    }
}
