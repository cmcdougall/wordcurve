package com.singlemethodgames.wordcurve.services;

import com.singlemethodgames.wordcurve.utils.tracking.Tracker;

public interface GameServices {
    void signIn();
    void signOut();
    boolean isSignedIn();
    String getDisplayName();
    void updateUserListener(UserListener userListener);

    void viewLeaderboards();
    void showLeaderboard(final String leaderboardCode);
    void updateLeaderboard(final String leaderboardCode, final int score);

    void recordGame(Tracker tracker, String uid);

    void viewAchievements();
    void unlockAchievement(final String achievementCode);
    void incrementAchievement(final String achievementCode, final int increment, final float progress);
}
