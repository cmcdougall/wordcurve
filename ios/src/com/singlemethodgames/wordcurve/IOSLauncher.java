package com.singlemethodgames.wordcurve;

import com.badlogic.gdx.backends.iosrobovm.IOSApplication;
import com.badlogic.gdx.backends.iosrobovm.IOSApplicationConfiguration;
import com.badlogic.gdx.pay.ios.apple.PurchaseManageriOSApple;
import com.singlemethodgames.wordcurve.services.GameServices;
import com.singlemethodgames.wordcurve.services.UserListener;
import com.singlemethodgames.wordcurve.utils.SecureView;
import com.singlemethodgames.wordcurve.utils.tracking.Tracker;

import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIApplicationLaunchOptions;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIDevice;
import org.robovm.apple.uikit.UIView;

public class IOSLauncher extends IOSApplication.Delegate implements GameServices, SecureView {
    private IOSApplication iosApplication;
    private GameCenterServices gameCenterServices = new GameCenterServices();
    private GameCenterManager gameCenterManager = null;
    private boolean secureView;

    @Override
    protected IOSApplication createApplication() {
        IOSApplicationConfiguration config = new IOSApplicationConfiguration();
        config.orientationPortrait = true;
        config.orientationLandscape = false;
        config.allowIpod = true;
        secureView = false;
        WordCurveGame wordCurveGame = new WordCurveGame(this, new IOSPlatformResolver(), this, getDeviceId());
        wordCurveGame.purchaseManager = new PurchaseManageriOSApple();
        iosApplication = new IOSApplication(wordCurveGame, config);
        return iosApplication;
    }

    @Override
    public boolean didFinishLaunching(UIApplication application, UIApplicationLaunchOptions launchOptions) {
        super.didFinishLaunching(application, launchOptions);
        gameCenterManager = new GameCenterManager(application.getKeyWindow(), gameCenterServices);
        return true;
    }

    private String getDeviceId() {
        UIDevice device = UIDevice.getCurrentDevice();
        return device.getIdentifierForVendor().asString();
    }

    public static void main(String[] argv) {
        NSAutoreleasePool pool = new NSAutoreleasePool();
        UIApplication.main(argv, null, IOSLauncher.class);
        pool.close();
    }

    @Override
    public void willResignActive(UIApplication application) {
        super.willResignActive(application);

        if(secureView) {
            UIView colourView = new UIView(iosApplication.getUIWindow().getFrame());
            colourView.setBackgroundColor(UIColor.fromRGBA(2 / 255f, 17 / 255f, 27 / 255f, 1));
            colourView.setTag(1234);

            iosApplication.getUIWindow().addSubview(colourView);
            iosApplication.getUIWindow().bringSubviewToFront(colourView);
        }
    }

    @Override
    public void didBecomeActive(UIApplication application) {
        super.didBecomeActive(application);
        UIView colourView = iosApplication.getUIWindow().getViewWithTag(1234);
        if(colourView != null) {
            colourView.removeFromSuperview();
        }
    }

    @Override
    public void signIn() {
        if(gameCenterManager != null) {
            gameCenterManager.login();
        }
    }

    @Override
    public void signOut() {

    }

    @Override
    public boolean isSignedIn() {
        return gameCenterManager != null && gameCenterManager.isAuthenticated();
    }

    @Override
    public String getDisplayName() {
        return gameCenterManager.displayName();
    }

    @Override
    public void viewLeaderboards() {
        if(gameCenterManager != null) {
            gameCenterManager.showLeaderboardsView();
        }
    }

    @Override
    public void showLeaderboard(String leaderboardCode) {
        if(gameCenterManager != null) {
            gameCenterManager.showLeaderboardView(leaderboardCode);
        }
    }

    @Override
    public void updateLeaderboard(String leaderboardCode, int score) {
        if(gameCenterManager != null) {
            gameCenterManager.reportScore(leaderboardCode, score);
        }
    }



    @Override
    public void viewAchievements() {
        if(gameCenterManager != null) {
            gameCenterManager.showAchievementsView();
        }
    }

    @Override
    public void unlockAchievement(String achievementCode) {
        if(gameCenterManager != null) {
            gameCenterManager.reportAchievement(achievementCode);
        }
    }

    @Override
    public void incrementAchievement(String achievementCode, int increment, float progress) {
        if(gameCenterManager != null) {
            gameCenterManager.reportAchievement(achievementCode, progress * 100);
        }
    }

    @Override
    public void secureView() {
        secureView = true;
    }

    @Override
    public void removeSecureView() {
        secureView = false;
    }

    @Override
    public void updateUserListener(UserListener userListener) {
        this.gameCenterServices.setUserListener(userListener);
    }

    @Override
    public void recordGame(Tracker tracker, String uid) {

    }
}