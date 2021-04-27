package com.singlemethodgames.wordcurve.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * Created by cameron on 19/02/2018.
 */

public class QwertyKeyboard {
    private KeyboardDetails details;
    private float keyWidth;
    private float keyHeight;
    private float scale;

    public QwertyKeyboard(float x, float y, float scale) {
        Json json = new Json();
        details = json.fromJson(KeyboardDetails.class, Gdx.files.internal(Constants.JsonFiles.QWERTY_KEYBOARD_COORDINATES));

        keyWidth = details.getKeyWidth();
        keyHeight = details.getKeyHeight();

        this.scale = scale;
    }

    private Vector2 positionPoint(Array<Float> letterCoordinates) {
        return this.positionPoint(letterCoordinates, null);
    }

    private Vector2 positionPoint(Array<Float> letterCoordinates, Vector2 previousCoordinates) {
        // Get bottom left position of key
        float keyX = (letterCoordinates.get(0) * this.scale);
        float keyY = (letterCoordinates.get(1) * this.scale);

        float midX = keyX + (this.keyWidth / 2f);
        float midY = keyY + (this.keyHeight / 2f);

        float minX = midX - (this.keyWidth * (1f/3f));
        float maxX = midX + (this.keyWidth * (1f/3f));

        float thirdHeight = (this.keyHeight * (1f/3f));
        float minY = midY - thirdHeight;
        float maxY = midY + thirdHeight;

        if(previousCoordinates != null) {
            if(previousCoordinates.y >= midY) {
                float newMax = previousCoordinates.y - thirdHeight;
                maxY = newMax <= minY ? minY : newMax;


            } else {
                float newMax = previousCoordinates.y + thirdHeight;
                minY = newMax >= maxY ? maxY : newMax;
            }
        }

        float randX = MathUtils.random(((int) maxX - (int) minX) + 1) + minX;
        float randY = MathUtils.random(((int) maxY - (int) minY) + 1) + minY;

        return new Vector2(randX, randY);
    }

    Array<Vector2> getKeyboardCoordsForWord(String word) {
        word = word.toLowerCase();

        Array<Vector2> wordCoords = new Array<>();

        for (int i = 0; i < word.length(); i++) {
            String currentLetter = String.valueOf(word.charAt(i));
            Array<Float> letterCoordinates = details.getCoordinates().get(currentLetter);

            if (letterCoordinates != null) {
                if (i > 0) {
                    String previousLetter = String.valueOf(word.charAt(i - 1));
                    Array<Float> previousLetterCoordinates = details.getCoordinates().get(previousLetter);

                    Vector2 letterPosition;
                    if(previousLetterCoordinates.get(1).equals(letterCoordinates.get(1))) {
                        letterPosition = positionPoint(letterCoordinates, wordCoords.peek());
                    } else {
                        letterPosition = positionPoint(letterCoordinates);
                    }
                    wordCoords.add(letterPosition);
                } else {
                    wordCoords.add(positionPoint(letterCoordinates));
                }
            }
        }

        return wordCoords;
    }

    public void getKeyPosition(Vector2 keyVector, String letter) {
        Array<Float> coordinates = details.getCoordinates().get(letter);
        keyVector.set(
                coordinates.get(0) * scale,
                coordinates.get(1) * scale
        );
    }

    public float getScale() {
        return scale;
    }

    public static class KeyboardDimensions {
        private float width;
        private float height;

        public KeyboardDimensions(float width, float height) {
            this.width = width;
            this.height = height;
        }

        public float getWidth() {
            return width;
        }

        public float getHeight() {
            return height;
        }
    }

    public static class KeyboardDetails {
        private float width;
        private float height;
        private float keyWidth;
        private float keyHeight;
        private ObjectMap<String, Array<Float>> coordinates;

        public KeyboardDetails() {
            width = 0;
            height = 0;
            keyWidth = 0;
            keyHeight = 0;
            coordinates = new ObjectMap<>();
        }

        public float getWidth() {
            return width;
        }

        public void setWidth(float width) {
            this.width = width;
        }

        public float getHeight() {
            return height;
        }

        public void setHeight(float height) {
            this.height = height;
        }

        public float getKeyWidth() {
            return keyWidth;
        }

        public void setKeyWidth(float keyWidth) {
            this.keyWidth = keyWidth;
        }

        public float getKeyHeight() {
            return keyHeight;
        }

        public void setKeyHeight(float keyHeight) {
            this.keyHeight = keyHeight;
        }

        public ObjectMap<String, Array<Float>> getCoordinates() {
            return coordinates;
        }

        public void setCoordinates(ObjectMap<String, Array<Float>> coordinates) {
            this.coordinates = coordinates;
        }

        @Override
        public String toString() {
            return "KeyboardDetails{" +
                    "width=" + width +
                    ", height=" + height +
                    ", keyWidth=" + keyWidth +
                    ", keyHeight=" + keyHeight +
                    ", coordinates=" + coordinates +
                    '}';
        }
    }
}
