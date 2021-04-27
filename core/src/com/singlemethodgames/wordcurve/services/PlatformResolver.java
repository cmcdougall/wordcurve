package com.singlemethodgames.wordcurve.services;

public interface PlatformResolver {
    boolean userManagedInApp();
    String getPremiumSKU();
    String getStoreLink();
    String getPlatform();

    // Leaderboards
    String getClassicLifeLeaderboardCode();
    String getClassicTimeLeaderboardCode();
    String getClassicCasualLeaderboardCode();
    String getSwitchLifeLeaderboardCode();
    String getSwitchTimeLeaderboardCode();
    String getSwitchCasualLeaderboardCode();

    // Achievements
    String getAchievementClassicFirst100();
    String getAchievementClassicFirst500();
    String getAchievementClassicFirst2000();
    String getAchievementClassicLifeFirst10();
    String getAchievementClassicLifeFirst50();
    String getAchievementClassicLifeFirst100();
    String getAchievementClassicLifeRemoveAllLetters();
    String getAchievementClassicLifeRemoveAllKeys();
    String getAchievementClassicTimeFirst10();
    String getAchievementClassicTimeFirst50();
    String getAchievementClassicTimeFirst100();
    String getAchievementClassicTimeRemoveAllLetters();
    String getAchievementClassicTimeRemoveAllKeys();
    String getAchievementClassicCasualFirst10();
    String getAchievementClassicCasualFirst50();
    String getAchievementClassicCasualFirst100();
    String getAchievementClassicCasualRemoveAllLetters();
    String getAchievementClassicCasualRemoveAllKeys();
    String getAchievementClassicChallengesFirstBronze();
    String getAchievementClassicChallengesAllBronze();
    String getAchievementClassicChallengesFirstSilver();
    String getAchievementClassicChallengesAllSilver();
    String getAchievementClassicChallengesFirstGold();
    String getAchievementClassicChallengesAllGold();
    String getAchievementClassicChallengesFirstPlatinum();
    String getAchievementClassicChallengesAllPlatinum();

    String getAchievementSwitchFirst100();
    String getAchievementSwitchFirst500();
    String getAchievementSwitchFirst2000();
    String getAchievementSwitchLifeFirst10();
    String getAchievementSwitchLifeFirst50();
    String getAchievementSwitchLifeFirst100();
    String getAchievementSwitchLifeRemoveAllLetters();
    String getAchievementSwitchLifeRemoveAllKeys();
    String getAchievementSwitchTimeFirst10();
    String getAchievementSwitchTimeFirst50();
    String getAchievementSwitchTimeFirst100();
    String getAchievementSwitchTimeRemoveAllLetters();
    String getAchievementSwitchTimeRemoveAllKeys();
    String getAchievementSwitchCasualFirst10();
    String getAchievementSwitchCasualFirst50();
    String getAchievementSwitchCasualFirst100();
    String getAchievementSwitchCasualRemoveAllLetters();
    String getAchievementSwitchCasualRemoveAllKeys();
    String getAchievementSwitchChallengesFirstBronze();
    String getAchievementSwitchChallengesAllBronze();
    String getAchievementSwitchChallengesFirstSilver();
    String getAchievementSwitchChallengesAllSilver();
    String getAchievementSwitchChallengesFirstGold();
    String getAchievementSwitchChallengesAllGold();
    String getAchievementSwitchChallengesFirstPlatinum();
    String getAchievementSwitchChallengesAllPlatinum();
}
