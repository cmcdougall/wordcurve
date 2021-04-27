package com.singlemethodgames.wordcurve;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.pay.android.googlebilling.PurchaseManagerGoogleBilling;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.singlemethodgames.wordcurve.utils.SecureView;

public class AndroidLauncher extends AndroidApplication implements SecureView {
    private static final int RC_UNUSED = 5001;
    private static final int RC_SIGN_IN = 9001;
    private static final int RC_LEADERBOARD_UI = 9004;
    private GooglePlayServices googlePlayServices;

    private WordCurveGame wordCurveGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.googlePlayServices = new GooglePlayServices(this, RC_SIGN_IN, RC_LEADERBOARD_UI, RC_UNUSED);
        wordCurveGame = new WordCurveGame(
                this.googlePlayServices,
                new AndroidPlatformResolver(this),
                this,
                Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID)
        );
        wordCurveGame.purchaseManager = new PurchaseManagerGoogleBilling(this);

        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        RelativeLayout layout = new RelativeLayout(this);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        View gameView = createGameView(config, wordCurveGame);
        layout.addView(gameView);

        setContentView(layout);
    }

    private View createGameView(AndroidApplicationConfiguration config, WordCurveGame wordCurveGame) {
        return initializeForView(wordCurveGame, config);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task =
                    GoogleSignIn.getSignedInAccountFromIntent(intent);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                googlePlayServices.onConnected(account);
            } catch (ApiException apiException) {
                // 12501 indicates the user cancelled sign in
                googlePlayServices.onDisconnected();
                if (apiException.getStatusCode() != 12501) {
                    wordCurveGame.notifyUser("(" + apiException.getStatusCode() + ") " + getString(R.string.signin_other_error));
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Since the state of the signed in user can change when the activity is not active
        // it is recommended to try and sign in silently from when the app resumes.
        googlePlayServices.signInSilently();
    }

    @Override
    public void secureView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
            }
        });
    }

    @Override
    public void removeSecureView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
            }
        });
    }
}
