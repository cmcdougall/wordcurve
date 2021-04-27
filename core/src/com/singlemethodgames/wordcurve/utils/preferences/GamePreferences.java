package com.singlemethodgames.wordcurve.utils.preferences;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.singlemethodgames.wordcurve.screens.difficulty.SpeedSetting;
import com.singlemethodgames.wordcurve.screens.variants.Variant;

/**
 * Created by cameron on 19/02/2018.
 */

public class GamePreferences {
    private Preferences keyboardPref;
    private Preferences trailPref;
    private Preferences gamePref;

    final private static String KEYBOARD_PREF_NAME = "keyboardPref";
    final private static String TRAIL_PREF_NAME = "trailPref";
    final public static String GAME_PREF_NAME = "gamePref";

    final private static String KEYBOARD = "keyboard";
    final private static String SPEED = "speed";

    final private static String SHOW_TUTORIAL = "showTutorial";
    final private static String INCREASE_DIFFICULTY = "increaseDifficulty";
    final private static String GAME_TRAINING = "gameTraining";

    public GamePreferences() {
        keyboardPref = Gdx.app.getPreferences(KEYBOARD_PREF_NAME);
        trailPref = Gdx.app.getPreferences(TRAIL_PREF_NAME);
        gamePref = Gdx.app.getPreferences(GAME_PREF_NAME);
    }

    public void saveSettings(final Variant VARIANT, final int displaySettings, final SpeedSetting speedSetting) {
        this.keyboardPref.putInteger(VARIANT + "_" + GamePreferences.KEYBOARD, displaySettings);
        this.keyboardPref.flush();

        this.trailPref.putString(VARIANT + "_" + GamePreferences.SPEED, speedSetting.toString());
        this.trailPref.flush();
    }

    public int getLetterDifficulty(final Variant VARIANT) {
        int difficulty;
        try {
            difficulty = this.keyboardPref.getInteger(VARIANT + "_" + GamePreferences.KEYBOARD, 0);
        } catch (Exception e) {
            difficulty = 0;
        }
        return difficulty;
    }

    public SpeedSetting getSpeedDifficulty(final Variant VARIANT) {
        SpeedSetting speedSetting;
        try {
            speedSetting = SpeedSetting.valueOf(this.trailPref.getString(VARIANT + "_" + GamePreferences.SPEED, SpeedSetting.ONE.toString()));
        } catch (IllegalArgumentException ex) {
            speedSetting = SpeedSetting.ONE;
        }

        return speedSetting;
    }

    public void updateTutorialPreference(final boolean showTutorial) {
        this.gamePref.putBoolean(SHOW_TUTORIAL, showTutorial);
        this.gamePref.flush();
    }

    public boolean showTutorial() {
        return this.gamePref.getBoolean(SHOW_TUTORIAL, true);
    }

    public void updateIncreaseDifficultyPreference(final Variant variant, final boolean increaseDifficulty) {
        this.gamePref.putBoolean(variant + "_" + INCREASE_DIFFICULTY, increaseDifficulty);
        this.gamePref.flush();
    }

    public boolean increaseDifficulty(final Variant variant) {
        return this.gamePref.getBoolean(variant + "_" + INCREASE_DIFFICULTY, true);
    }

    public boolean gameTraining(final Variant variant) {
        return this.gamePref.getBoolean(variant + "_" + GAME_TRAINING, false);
    }

    public void updateTrainingModePreference(final Variant variant, final boolean training) {
        this.gamePref.putBoolean(variant + "_" + GAME_TRAINING, training);
        this.gamePref.flush();
    }
}
