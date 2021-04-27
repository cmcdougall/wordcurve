package com.singlemethodgames.wordcurve.services;

public interface UserListener {
    void userSignedIn();
    void userSignedInFailed();
    void userSignedOut();
    void userSignedOutFailed();
}
