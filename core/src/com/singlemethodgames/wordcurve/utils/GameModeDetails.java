package com.singlemethodgames.wordcurve.utils;

/**
 * Created by cameron on 2/03/2018.
 */

public class GameModeDetails {
    final private String name;
    final private String description;
    final private String leaderboardCode;

    public GameModeDetails(String name, String description, String leaderboardCode) {
        this.name = name;
        this.description = description;
        this.leaderboardCode = leaderboardCode;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getLeaderboardCode() {
        return leaderboardCode;
    }
}
