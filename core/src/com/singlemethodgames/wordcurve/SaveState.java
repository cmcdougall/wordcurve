package com.singlemethodgames.wordcurve;

import com.singlemethodgames.wordcurve.actors.gamebar.GameBar;
import com.singlemethodgames.wordcurve.actors.gamebar.GameMode;
import com.singlemethodgames.wordcurve.screens.variants.Variant;
import com.singlemethodgames.wordcurve.utils.tracking.Tracker;

import java.io.Serializable;

public class SaveState implements Serializable {
    private long seed;
    private Variant variant;
    private GameMode gameMode;
    private GameBar.State gameState;
    private Tracker tracker;
    private boolean training;

    public SaveState() { }

    public SaveState(long seed, Variant variant, GameMode gameMode, Tracker tracker, boolean training) {
        this.seed = seed;
        this.variant = variant;
        this.gameMode = gameMode;
        this.tracker = tracker;
        this.training = training;
    }

    public SaveState(long seed, Variant variant, GameMode gameMode, GameBar.State gameState, Tracker tracker, boolean training) {
        this.seed = seed;
        this.variant = variant;
        this.gameMode = gameMode;
        this.gameState = gameState;
        this.tracker = tracker;
        this.training = training;
    }

    public long getSeed() {
        return seed;
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }

    public GameBar.State getGameState() {
        return gameState;
    }

    public void setGameState(GameBar.State gameState) {
        this.gameState = gameState;
    }

    public Variant getVariant() {
        return variant;
    }

    public void setVariant(Variant variant) {
        this.variant = variant;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    public Tracker getTracker() {
        return tracker;
    }

    public void setTracker(Tracker tracker) {
        this.tracker = tracker;
    }

    public boolean isTraining() {
        return training;
    }

    public void setTraining(boolean training) {
        this.training = training;
    }

    @Override
    public String toString() {
        return "SaveState{" +
                "seed=" + seed +
                ", variant=" + variant +
                ", gameMode=" + gameMode +
                ", gameState=" + gameState +
                ", tracker=" + tracker +
                ", training=" + training +
                '}';
    }
}
