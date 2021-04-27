package com.singlemethodgames.wordcurve.utils.challenges;

import com.badlogic.gdx.utils.ObjectMap;

public class ChallengeSet {
    private ObjectMap<String, Level> levels;

    public ChallengeSet() { }

    public ObjectMap<String, Level> getLevels() {
        return levels;
    }

    public void setLevels(ObjectMap<String, Level> levels) {
        this.levels = levels;
    }

    @Override
    public String toString() {
        return "ChallengeSet{" +
                "levels=" + levels +
                '}';
    }
}
