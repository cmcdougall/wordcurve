package com.singlemethodgames.wordcurve;


import androidx.annotation.StringRes;

import com.singlemethodgames.wordcurve.services.PlatformResolver;

public class AndroidPlatformResolver implements PlatformResolver {

    private final AndroidLauncher androidLauncher;

    AndroidPlatformResolver(AndroidLauncher androidLauncher) {
        this.androidLauncher = androidLauncher;
    }

    @Override
    public boolean userManagedInApp() {
        return true;
    }

    @Override
    public String getPremiumSKU() {
        return "word_curve_premium";
    }

    private String getString(@StringRes int resId) {
        return this.androidLauncher.getString(resId);
    }
    @Override
    public String getClassicLifeLeaderboardCode() {
        return getString(R.string.leaderboard_classic_life);
    }

    @Override
    public String getClassicTimeLeaderboardCode() {
        return getString(R.string.leaderboard_classic_time);
    }

    @Override
    public String getClassicCasualLeaderboardCode() {
        return getString(R.string.leaderboard_classic_casual);
    }

    @Override
    public String getSwitchLifeLeaderboardCode() {
        return getString(R.string.leaderboard_switch_life);
    }

    @Override
    public String getSwitchTimeLeaderboardCode() {
        return getString(R.string.leaderboard_switch_time);
    }

    @Override
    public String getSwitchCasualLeaderboardCode() {
        return getString(R.string.leaderboard_switch_casual);
    }

    @Override
    public String getStoreLink() {
        return "https://play.google.com/store/apps/details?id=com.singlemethodgames.wordcurve";
    }

    @Override
    public String getAchievementClassicFirst100() {
        return getString(R.string.achievement_classic__first_100);
    }

    @Override
    public String getAchievementClassicFirst500() {
        return getString(R.string.achievement_classic__first_500);
    }

    @Override
    public String getAchievementClassicFirst2000() {
        return getString(R.string.achievement_classic__first_2000);
    }

    @Override
    public String getAchievementClassicLifeFirst10() {
        return getString(R.string.achievement_classic_life__first_10);
    }

    @Override
    public String getAchievementClassicLifeFirst50() {
        return getString(R.string.achievement_classic_life__first_50);
    }

    @Override
    public String getAchievementClassicLifeFirst100() {
        return getString(R.string.achievement_classic_life__first_100);
    }

    @Override
    public String getAchievementClassicLifeRemoveAllLetters() {
        return getString(R.string.achievement_classic_life__remove_all_letters);
    }

    @Override
    public String getAchievementClassicLifeRemoveAllKeys() {
        return getString(R.string.achievement_classic_life__remove_all_keys);
    }

    @Override
    public String getAchievementClassicTimeFirst10() {
        return getString(R.string.achievement_classic_time__first_10);
    }

    @Override
    public String getAchievementClassicTimeFirst50() {
        return getString(R.string.achievement_classic_time__first_50);
    }

    @Override
    public String getAchievementClassicTimeFirst100() {
        return getString(R.string.achievement_classic_time__first_100);
    }

    @Override
    public String getAchievementClassicTimeRemoveAllLetters() {
        return getString(R.string.achievement_classic_time__remove_all_letters);
    }

    @Override
    public String getAchievementClassicTimeRemoveAllKeys() {
        return getString(R.string.achievement_classic_time__remove_all_keys);
    }

    @Override
    public String getAchievementClassicCasualFirst10() {
        return getString(R.string.achievement_classic_casual__first_10);
    }

    @Override
    public String getAchievementClassicCasualFirst50() {
        return getString(R.string.achievement_classic_casual__first_50);
    }

    @Override
    public String getAchievementClassicCasualFirst100() {
        return getString(R.string.achievement_classic_casual__first_100);
    }

    @Override
    public String getAchievementClassicCasualRemoveAllLetters() {
        return getString(R.string.achievement_classic_casual__remove_all_letters);
    }

    @Override
    public String getAchievementClassicCasualRemoveAllKeys() {
        return getString(R.string.achievement_classic_casual__remove_all_keys);
    }

    @Override
    public String getAchievementClassicChallengesFirstBronze() {
        return getString(R.string.achievement_classic_challenges__first_bronze);
    }

    @Override
    public String getAchievementClassicChallengesAllBronze() {
        return getString(R.string.achievement_classic_challenges__all_bronze);
    }

    @Override
    public String getAchievementClassicChallengesFirstSilver() {
        return getString(R.string.achievement_classic_challenges__first_silver);
    }

    @Override
    public String getAchievementClassicChallengesAllSilver() {
        return getString(R.string.achievement_classic_challenges__all_silver);
    }

    @Override
    public String getAchievementClassicChallengesFirstGold() {
        return getString(R.string.achievement_classic_challenges__first_gold);
    }

    @Override
    public String getAchievementClassicChallengesAllGold() {
        return getString(R.string.achievement_classic_challenges__all_gold);
    }

    @Override
    public String getAchievementClassicChallengesFirstPlatinum() {
        return getString(R.string.achievement_classic_challenges__first_platinum);
    }

    @Override
    public String getAchievementClassicChallengesAllPlatinum() {
        return getString(R.string.achievement_classic_challenges__all_platinum);
    }

    @Override
    public String getAchievementSwitchFirst100() {
        return getString(R.string.achievement_switch__first_100);
    }

    @Override
    public String getAchievementSwitchFirst500() {
        return getString(R.string.achievement_switch__first_500);
    }

    @Override
    public String getAchievementSwitchFirst2000() {
        return getString(R.string.achievement_switch__first_2000);
    }

    @Override
    public String getAchievementSwitchLifeFirst10() {
        return getString(R.string.achievement_switch_life__first_10);
    }

    @Override
    public String getAchievementSwitchLifeFirst50() {
        return getString(R.string.achievement_switch_life__first_50);
    }

    @Override
    public String getAchievementSwitchLifeFirst100() {
        return getString(R.string.achievement_switch_life__first_100);
    }

    @Override
    public String getAchievementSwitchLifeRemoveAllLetters() {
        return getString(R.string.achievement_switch_life__remove_all_letters);
    }

    @Override
    public String getAchievementSwitchLifeRemoveAllKeys() {
        return getString(R.string.achievement_switch_life__remove_all_keys);
    }

    @Override
    public String getAchievementSwitchTimeFirst10() {
        return getString(R.string.achievement_switch_time__first_10);
    }

    @Override
    public String getAchievementSwitchTimeFirst50() {
        return getString(R.string.achievement_switch_time__first_50);
    }

    @Override
    public String getAchievementSwitchTimeFirst100() {
        return getString(R.string.achievement_switch_time__first_100);
    }

    @Override
    public String getAchievementSwitchTimeRemoveAllLetters() {
        return getString(R.string.achievement_switch_time__remove_all_letters);
    }

    @Override
    public String getAchievementSwitchTimeRemoveAllKeys() {
        return getString(R.string.achievement_switch_time__remove_all_keys);
    }

    @Override
    public String getAchievementSwitchCasualFirst10() {
        return getString(R.string.achievement_switch_casual__first_10);
    }

    @Override
    public String getAchievementSwitchCasualFirst50() {
        return getString(R.string.achievement_switch_casual__first_50);
    }

    @Override
    public String getAchievementSwitchCasualFirst100() {
        return getString(R.string.achievement_switch_casual__first_100);
    }

    @Override
    public String getAchievementSwitchCasualRemoveAllLetters() {
        return getString(R.string.achievement_switch_casual__remove_all_letters);
    }

    @Override
    public String getAchievementSwitchCasualRemoveAllKeys() {
        return getString(R.string.achievement_switch_casual__remove_all_keys);
    }

    @Override
    public String getAchievementSwitchChallengesFirstBronze() {
        return getString(R.string.achievement_switch_challenges__first_bronze);
    }

    @Override
    public String getAchievementSwitchChallengesAllBronze() {
        return getString(R.string.achievement_switch_challenges__all_bronze);
    }

    @Override
    public String getAchievementSwitchChallengesFirstSilver() {
        return getString(R.string.achievement_switch_challenges__first_silver);
    }

    @Override
    public String getAchievementSwitchChallengesAllSilver() {
        return getString(R.string.achievement_switch_challenges__all_silver);
    }

    @Override
    public String getAchievementSwitchChallengesFirstGold() {
        return getString(R.string.achievement_switch_challenges__first_gold);
    }

    @Override
    public String getAchievementSwitchChallengesAllGold() {
        return getString(R.string.achievement_switch_challenges__all_gold);
    }

    @Override
    public String getAchievementSwitchChallengesFirstPlatinum() {
        return getString(R.string.achievement_switch_challenges__first_platinum);
    }

    @Override
    public String getAchievementSwitchChallengesAllPlatinum() {
        return getString(R.string.achievement_switch_challenges__all_platinum);
    }

    @Override
    public String getPlatform() {
        return "google_play_games";
    }
}
