package com.singlemethodgames.wordcurve.utils.preferences;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Base64Coder;
import com.singlemethodgames.wordcurve.actors.gamebar.GameMode;
import com.singlemethodgames.wordcurve.screens.variants.Variant;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

/**
 * Created by cameron on 19/02/2018.
 */

public class HighScores {
    private Preferences highScores;
    final private static String HIGHSCORES_PREF_NAME = "highScoresPref";
    final private static String HIGHSCORE = "highscore";
    final private static String CORRECT = "correct";
    final private static String INCORRECT = "incorrect";

    public HighScores() {
        highScores = Gdx.app.getPreferences(HIGHSCORES_PREF_NAME);
    }

    public void setHighscore(final Variant VARIANT, final GameMode MODE, int score) {
        String encoded = Base64Coder.encodeString(VARIANT + "_" + MODE + "_" + HIGHSCORE);
        this.highScores.putInteger(encoded, score);
        this.highScores.flush();
    }

    public void setCorrectAnswers(final Variant VARIANT, final GameMode MODE, int correctAnswers) {
        String encoded = Base64Coder.encodeString(VARIANT + "_" + MODE + "_" + CORRECT);
        this.highScores.putInteger(encoded, correctAnswers);
        this.highScores.flush();
    }

    public void setIncorrectAnswers(final Variant VARIANT, final GameMode MODE, int incorrectAnswers) {
        String encoded = Base64Coder.encodeString(VARIANT + "_" + MODE + "_" + INCORRECT);
        this.highScores.putInteger(encoded, incorrectAnswers);
        this.highScores.flush();
    }

    public int getHighscore(final Variant VARIANT, final GameMode MODE) {
        String encoded = Base64Coder.encodeString(VARIANT + "_" + MODE + "_" + HIGHSCORE);
        return this.highScores.getInteger(encoded, 0);
    }

    public int getCorrect(final Variant VARIANT, final GameMode MODE) {
        String encoded = Base64Coder.encodeString(VARIANT + "_" + MODE + "_" + CORRECT);
        return this.highScores.getInteger(encoded, 0);
    }

    public int getIncorrect(final Variant VARIANT, final GameMode MODE) {
        String encoded = Base64Coder.encodeString(VARIANT + "_" + MODE + "_" + INCORRECT);
        return this.highScores.getInteger(encoded, 0);
    }

    public void setHighscoreForChallenge(final Variant variant, final String level, final int score) {
        HashMap<String, Integer> highScores = getChallengeHighscores(variant);
        Integer curr = highScores.get(level);
        if(curr == null || score > curr) {
            highScores.put(level, score);

            ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
            try {
                ObjectOutputStream oos = new ObjectOutputStream(byteOutputStream);
                oos.writeObject(highScores);
                oos.close();
                byteOutputStream.close();

                String encoded = String.valueOf(Base64Coder.encode(byteOutputStream.toByteArray()));
                String keyEncoded = Base64Coder.encodeString(variant + "_CHALLENGES");
                this.highScores.putString(keyEncoded, encoded);
                this.highScores.flush();
            } catch (IOException ignore) { }
        }
    }

    public int getHighscoreForChallenge(final Variant variant, final String level) {
        HashMap<String, Integer> highScores = getChallengeHighscores(variant);
        Integer score = highScores.get(level);
        return score != null ? score : 0;
    }

    private HashMap<String, Integer> getChallengeHighscores(final Variant variant) {
        String keyEncoded = Base64Coder.encodeString(variant + "_CHALLENGES");
        String bMap = this.highScores.getString(keyEncoded, "");
        HashMap<String, Integer> map = new HashMap<>();

        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(Base64Coder.decode(bMap));
            ObjectInputStream ois = new ObjectInputStream(bis);
            map = (HashMap) ois.readObject();
            ois.close();
            bis.close();
        } catch (IOException | ClassNotFoundException ignore) { }

        return map;
    }
}
