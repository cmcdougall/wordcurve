package com.singlemethodgames.wordcurve;

import com.singlemethodgames.wordcurve.services.UserListener;

import org.robovm.apple.foundation.NSError;
import org.robovm.apple.gamekit.GKAchievement;
import org.robovm.apple.gamekit.GKLeaderboard;

import java.util.ArrayList;

public class GameCenterServices implements GameCenterListener {

    private UserListener userListener = null;

    public void setUserListener(UserListener userListener) {
        this.userListener = userListener;
    }

    @Override
    public void playerLoginCompleted() {
        if(userListener != null) {
            userListener.userSignedIn();
        }
    }

    @Override
    public void playerLoginFailed(NSError error) {
        if(userListener != null && error.getCode() != 2 && error.getCode() != 6) {
            userListener.userSignedInFailed();
        }
    }

    @Override
    public void achievementReportFailed(NSError error) {
    }

    @Override
    public void achievementViewDismissed() {
    }

    @Override
    public void leaderboardViewDismissed() {
    }

    @Override
    public void leaderboardsLoadFailed(NSError error) {
    }

    @Override
    public void leaderboardsLoadCompleted(ArrayList<GKLeaderboard> leaderboards) {
    }

    @Override
    public void scoreReportFailed(NSError error) {
    }

    @Override
    public void scoreReportCompleted() {
    }

    @Override
    public void achievementReportCompleted() {
    }

    @Override
    public void achievementsLoadFailed(NSError error) {
    }

    @Override
    public void achievementsLoadCompleted(ArrayList<GKAchievement> achievements) {
    }

    @Override
    public void achievementsResetCompleted() {
    }

    @Override
    public void achievementsResetFailed(NSError error) {
    }
}
