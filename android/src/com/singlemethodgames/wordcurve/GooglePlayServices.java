package com.singlemethodgames.wordcurve;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.games.AchievementsClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesClient;
import com.google.android.gms.games.LeaderboardsClient;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.PlayersClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.singlemethodgames.wordcurve.services.GameServices;
import com.singlemethodgames.wordcurve.services.UserListener;
import com.singlemethodgames.wordcurve.utils.tracking.Tracker;

/**
 * Created by cameron on 27/02/2018.
 */

public class GooglePlayServices implements GameServices {

//    // Client variables
    private AchievementsClient mAchievementsClient;
//    private DatabaseReference mDatabase;

    private UserListener userListener = null;

    private String displayName;
    private static final String TAG = "WCC";
    private final int RC_SIGN_IN;
    private final int RC_LEADERBOARD_UI;
    private final int RC_ACHIEVEMENT_UI = 9003;
    private final int RC_UNUSED;

    // Google Sign In
    private GoogleSignInClient mGoogleSignInClient;
    private PlayersClient mPlayersClient;

    // Leaderboards
    private LeaderboardsClient mLeaderboardsClient;

    private final AndroidLauncher androidLauncher;
    private GoogleSignInOptions gso;

    GooglePlayServices(AndroidLauncher androidLauncher, final int RC_SIGN_IN, final int RC_LEADERBOARD_UI, final int RC_UNUSED) {
        this.androidLauncher = androidLauncher;
        this.RC_SIGN_IN = RC_SIGN_IN;
        this.RC_LEADERBOARD_UI = RC_LEADERBOARD_UI;
        this.RC_UNUSED = RC_UNUSED;

        // Create the client used to sign in to Google services.
         gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(androidLauncher, gso);

        displayName = "-";
    }

    void onConnected(GoogleSignInAccount googleSignInAccount) {
        mAchievementsClient = Games.getAchievementsClient(androidLauncher, googleSignInAccount);
        mLeaderboardsClient = Games.getLeaderboardsClient(androidLauncher, googleSignInAccount);
//        mEventsClient = Games.getEventsClient(androidLauncher, googleSignInAccount);
        mPlayersClient = Games.getPlayersClient(androidLauncher, googleSignInAccount);

        GamesClient gamesClient = Games.getGamesClient(androidLauncher, googleSignInAccount);
        gamesClient.setViewForPopups(androidLauncher.findViewById(android.R.id.content));
        gamesClient.setGravityForPopups(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
//
//        // Set the greeting appropriately on main menu
        mPlayersClient.getCurrentPlayer()
                .addOnCompleteListener(new OnCompleteListener<Player>() {
                    @Override
                    public void onComplete(@NonNull Task<Player> task) {
                        if (task.isSuccessful()) {
                            displayName = task.getResult().getDisplayName();
                            if(userListener != null) {
                                userListener.userSignedIn();
                            }
                        } else {
                            if (userListener!= null) {
                                userListener.userSignedInFailed();
                            }
                        }
                    }
                });
    }

    void onDisconnected() {
        mPlayersClient = null;
        mLeaderboardsClient = null;
        mAchievementsClient = null;
    }

    void signInSilently() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(androidLauncher);
        if(GoogleSignIn.hasPermissions(account, gso.getScopeArray())) {
            onConnected(account);
        } else {
            mGoogleSignInClient.silentSignIn().addOnCompleteListener(this.androidLauncher,
            new OnCompleteListener<GoogleSignInAccount>() {
                @Override
                public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                    if(task.isSuccessful()) {
                        onConnected(task.getResult());
                    }
                    else {
                        onDisconnected();
                    }
                }
            });
        }
    }

    @Override
    public void signIn() {
        androidLauncher.startActivityForResult(mGoogleSignInClient.getSignInIntent(), RC_SIGN_IN);
    }

    @Override
    public void signOut() {
        if (isSignedIn()) {
            mGoogleSignInClient.signOut().addOnCompleteListener(androidLauncher,
                    new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            boolean successful = task.isSuccessful();

                            if(userListener != null) {
                                if(successful) {
                                    userListener.userSignedOut();
                                } else {
                                    userListener.userSignedOutFailed();
                                }
                            }

                            onDisconnected();
                        }
                    });
        }
    }

    @Override
    public boolean isSignedIn() {
        return GoogleSignIn.getLastSignedInAccount(this.androidLauncher) != null;
    }

    @Override
    public String getDisplayName() {
        return this.displayName;
    }

    @Override
    public void viewLeaderboards() {
        mLeaderboardsClient.getAllLeaderboardsIntent()
                .addOnSuccessListener(new OnSuccessListener<Intent>() {
                    @Override
                    public void onSuccess(Intent intent) {
                        androidLauncher.startActivityForResult(intent, RC_LEADERBOARD_UI);
                    }
                });
    }

    @Override
    public void showLeaderboard(String leaderboardCode) {
        mLeaderboardsClient.getLeaderboardIntent(leaderboardCode)
                .addOnSuccessListener(new OnSuccessListener<Intent>() {
                    @Override
                    public void onSuccess(Intent intent) {
                        androidLauncher.startActivityForResult(intent, RC_LEADERBOARD_UI);
                    }
                });
    }

    @Override
    public void updateLeaderboard(String leaderboardCode, int score) {
        Bundle bundle = new Bundle();
        bundle.putString("leaderboard_id", leaderboardCode);
        if(mLeaderboardsClient != null) {
            mLeaderboardsClient.submitScore(leaderboardCode, score);
        }
    }

    @Override
    public void updateUserListener(UserListener userListener) {
        this.userListener = userListener;
    }

    @Override
    public void recordGame(Tracker tracker, String uid) {
//        String randomUid = UUID.randomUUID().toString();
//        mDatabase.child(tracker.getVariant()).child(tracker.getMode()).child(randomUid).setValue(tracker);
    }

    @Override
    public void viewAchievements() {
        if(mAchievementsClient != null) {
            mAchievementsClient.getAchievementsIntent().addOnSuccessListener(
                    new OnSuccessListener<Intent>() {
                        @Override
                        public void onSuccess(Intent intent) {
                            androidLauncher.startActivityForResult(intent, RC_ACHIEVEMENT_UI);
                        }
                    }
            );
        }
    }

    @Override
    public void unlockAchievement(final String achievementCode) {
        androidLauncher.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mAchievementsClient != null) {
                    mAchievementsClient.unlock(achievementCode);
                }
            }
        });
    }

    @Override
    public void incrementAchievement(final String achievementCode, final int increment, final float progress) {
        androidLauncher.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mAchievementsClient != null) {
                    mAchievementsClient.increment(achievementCode, increment);
                }
            }
        });
    }
}
