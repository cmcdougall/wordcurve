package com.singlemethodgames.wordcurve.utils.preferences;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.singlemethodgames.wordcurve.actors.gamebar.GameMode;
import com.singlemethodgames.wordcurve.screens.variants.Variant;

public class Statistics {
    private Preferences stats;
    final private static String STATISTICS = "stats";

    public Statistics() {
        stats = Gdx.app.getPreferences(STATISTICS);
    }

    public void updateAnsweredStatForVariant(final Variant variant, final int answered) {
        int current = getTotalAnsweredForVariant(variant);
        current = current + answered;
        stats.putInteger(variant.toString(), current);
        stats.flush();
    }

    public int getTotalAnsweredForVariant(final Variant variant) {
        return stats.getInteger(variant.toString(), 0);
    }

    public void updateAnsweredStatForVariantMode(final Variant variant, final GameMode gameMode, final int answered) {
        int current = getTotalAnsweredForVariantMode(variant, gameMode);
        current = current + answered;
        stats.putInteger(variant.toString() + "_" + gameMode.toString(), current);
        stats.flush();
    }

    public int getTotalAnsweredForVariantMode(final Variant variant, final GameMode gameMode) {
        return stats.getInteger(variant.toString() + "_" + gameMode.toString(), 0);
    }
}
