package com.singlemethodgames.wordcurve.groups;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.ObjectMap;

import java.util.List;

public class KeyStatus {
    private Image key;
    private Image letter;
    private float keyAlpha;
    private float letterAlpha;

    public KeyStatus(Image key, Image letter, float keyAlpha, float letterAlpha) {
        this.key = key;
        this.letter = letter;
        this.keyAlpha = keyAlpha;
        this.letterAlpha = letterAlpha;
    }

    public Image getKey() {
        return key;
    }

    public Image getLetter() {
        return letter;
    }

    public float getKeyAlpha() {
        return keyAlpha;
    }

    public void setKeyAlpha(float keyAlpha) {
        this.keyAlpha = keyAlpha;
    }

    public float getLetterAlpha() {
        return letterAlpha;
    }

    public void setLetterAlpha(float letterAlpha) {
        this.letterAlpha = letterAlpha;
    }

    public static void hideAllLetters(ObjectMap<Character, KeyStatus> keyStatusMap) {
        for(KeyStatus keyStatus: keyStatusMap.values()) {
            keyStatus.setLetterAlpha(0f);
        }
    }

    public static void displayAllLetters(ObjectMap<Character, KeyStatus> keyStatusMap) {
        for(KeyStatus keyStatus: keyStatusMap.values()) {
            keyStatus.setLetterAlpha(1f);
        }
    }

    public static void hideLettersFromList(ObjectMap<Character, KeyStatus> keyStatusMap, List<Character> letters) {
        for(Character letter: letters) {
            KeyStatus keyStatus = keyStatusMap.get(letter);
            keyStatus.setLetterAlpha(0f);
        }
    }

    public static void displayLettersFromList(ObjectMap<Character, KeyStatus> keyStatusMap, List<Character> letters) {
        for(Character letter: letters) {
            KeyStatus keyStatus = keyStatusMap.get(letter);
            keyStatus.setLetterAlpha(1f);
        }
    }

    public static void hideAllKeys(ObjectMap<Character, KeyStatus> keyStatusMap) {
        for(KeyStatus keyStatus: keyStatusMap.values()) {
            keyStatus.setKeyAlpha(0f);
        }
    }

    public static void displayAllKeys(ObjectMap<Character, KeyStatus> keyStatusMap) {
        for(KeyStatus keyStatus: keyStatusMap.values()) {
            keyStatus.setKeyAlpha(1f);
        }
    }

    public static void hideKeysFromList(ObjectMap<Character, KeyStatus> keyStatusMap, List<Character> letters) {
        for(Character letter: letters) {
            KeyStatus keyStatus = keyStatusMap.get(letter);
            keyStatus.setKeyAlpha(0f);
        }
    }

    public static void displayKeysFromList(ObjectMap<Character, KeyStatus> keyStatusMap, List<Character> letters) {
        for(Character letter: letters) {
            KeyStatus keyStatus = keyStatusMap.get(letter);
            keyStatus.setKeyAlpha(1f);
        }
    }
}
