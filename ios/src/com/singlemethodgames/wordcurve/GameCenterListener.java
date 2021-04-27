package com.singlemethodgames.wordcurve;

import org.robovm.apple.foundation.NSError;
import org.robovm.apple.gamekit.GKAchievement;
import org.robovm.apple.gamekit.GKLeaderboard;

import java.util.ArrayList;

public interface GameCenterListener {
    void playerLoginCompleted();
    void playerLoginFailed(NSError error);
    void achievementReportFailed(NSError error);
    void achievementViewDismissed();
    void leaderboardViewDismissed();
    void leaderboardsLoadFailed(NSError error);
    void leaderboardsLoadCompleted(ArrayList<GKLeaderboard> leaderboards);
    void scoreReportFailed(NSError error);
    void scoreReportCompleted();
    void achievementReportCompleted();
    void achievementsLoadFailed(NSError error);
    void achievementsLoadCompleted(ArrayList<GKAchievement> achievements);
    void achievementsResetCompleted();
    void achievementsResetFailed(NSError error);
}
