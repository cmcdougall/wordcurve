package com.singlemethodgames.wordcurve.utils.challenges;

import com.badlogic.gdx.graphics.Color;
import com.singlemethodgames.wordcurve.screens.difficulty.LetterSettings;
import com.singlemethodgames.wordcurve.screens.difficulty.SpeedSetting;
import com.singlemethodgames.wordcurve.utils.Constants;

public class ChallengeTiers {
    private int bronze;
    private int silver;
    private int gold;
    private int platinum;

    public ChallengeTiers(final int questions) {
        bronze = calculateBronze(questions);
        silver = calculateSilver(questions);
        gold = calculateGold(questions);
        platinum = calculatePlatinum(questions);
    }

    private int calculateBronze(final int questions) {
        return (SpeedSetting.ONE.points + LetterSettings.LETTERS.maximumPoints) * (int)(questions * 0.75f);
    }

    private int calculateSilver(final int questions) {
        return (SpeedSetting.THREE.points + LetterSettings.NO_LETTERS.maximumPoints) * (int)(questions * 0.75f);
    }

    private int calculateGold(final int questions) {
        return (SpeedSetting.FIVE.points + LetterSettings.NONE.maximumPoints) * (int)(questions * 0.75f);
    }

    private int calculatePlatinum(final int questions) {
        return (SpeedSetting.FIVE.points + LetterSettings.NONE.maximumPoints) * questions;
    }

    public int getBronze() {
        return bronze;
    }

    public int getSilver() {
        return silver;
    }

    public int getGold() {
        return gold;
    }

    public int getPlatinum() {
        return platinum;
    }

    public Color getColourForScore(final int userScore) {
        if (userScore < getBronze()) {
            Color noTrophyColour = Constants.Colours.Keyboard.KEY_COLOUR.cpy();
            noTrophyColour.a = 0.5f;
            return noTrophyColour;
        } else if (userScore < getSilver()) {
            return Constants.Colours.Trophy.BRONZE;
        } else if (userScore < getGold()) {
            return Constants.Colours.Trophy.SILVER;
        } else if (userScore < getPlatinum()) {
            return Color.GOLD;
        }
        return Constants.Colours.Trophy.PLATINUM;
    }

    public Color getNextColourFromScore(final int userScore) {
        if (userScore < getBronze()) {
            return Constants.Colours.Trophy.BRONZE;
        } else if (userScore < getSilver()) {
            return Constants.Colours.Trophy.SILVER;
        } else if (userScore < getGold()) {
            return Color.GOLD;
        }
        return Constants.Colours.Trophy.PLATINUM;
    }

    public int getNextScore(final int userScore) {
        if (userScore < getBronze()) {
            return getBronze();
        } else if (userScore < getSilver()) {
            return getSilver();
        } else if (userScore < getGold()) {
            return getGold();
        }
        return getPlatinum();
    }

    public Trophies getTrophyType(final int userScore) {
        if (userScore < getBronze()) {
            return Trophies.NONE;
        } else if (userScore < getSilver()) {
            return Trophies.BRONZE;
        } else if (userScore < getGold()) {
            return Trophies.SILVER;
        } else if (userScore < getPlatinum()) {
            return Trophies.GOLD;
        }
        return Trophies.PLATINUM;
    }

    public enum Trophies {
        NONE, BRONZE, SILVER, GOLD, PLATINUM
    }
}
