package com.singlemethodgames.wordcurve;

import com.singlemethodgames.wordcurve.services.PlatformResolver;

public class IOSPlatformResolver implements PlatformResolver {
    private static final String APP_ID = "1451579397";

    @Override
    public boolean userManagedInApp() {
        return false;
    }

    @Override
    public String getPremiumSKU() {
        return "com.singlemethodgames.wordcurve.premium";
    }

    @Override
    public String getClassicLifeLeaderboardCode() {
        return "grp.classic_life";
    }

    @Override
    public String getClassicTimeLeaderboardCode() {
        return "grp.classic_time";
    }

    @Override
    public String getClassicCasualLeaderboardCode() {
        return "grp.classic_casual";
    }

    @Override
    public String getSwitchLifeLeaderboardCode() {
        return "grp.switch_life";
    }

    @Override
    public String getSwitchTimeLeaderboardCode() {
        return "grp.switch_time";
    }

    @Override
    public String getSwitchCasualLeaderboardCode() {
        return "grp.switch_casual";
    }

    @Override
    public String getStoreLink() {
        return "https://itunes.apple.com/us/app/id" + APP_ID;
    }

    @Override
    public String getPlatform() {
        return "game_center";
    }

    @Override
    public String getAchievementClassicFirst100() {
        return "grp.classic_first_100";
    }

    @Override
    public String getAchievementClassicFirst500() {
        return "grp.classic_first_500";
    }

    @Override
    public String getAchievementClassicFirst2000() {
        return "grp.classic_first_2000";
    }

    @Override
    public String getAchievementClassicLifeFirst10() {
        return "grp.classic_life_first_10";
    }

    @Override
    public String getAchievementClassicLifeFirst50() {
        return "grp.classic_life_first_50";
    }

    @Override
    public String getAchievementClassicLifeFirst100() {
        return "grp.classic_life_first_100";
    }

    @Override
    public String getAchievementClassicLifeRemoveAllLetters() {
        return "grp.classic_life_remove_all_letters";
    }

    @Override
    public String getAchievementClassicLifeRemoveAllKeys() {
        return "grp.classic_life_remove_all_keys";
    }

    @Override
    public String getAchievementClassicTimeFirst10() {
        return "grp.classic_time_first_10";
    }

    @Override
    public String getAchievementClassicTimeFirst50() {
        return "grp.classic_time_first_50";
    }

    @Override
    public String getAchievementClassicTimeFirst100() {
        return "grp.classic_time_first_100";
    }

    @Override
    public String getAchievementClassicTimeRemoveAllLetters() {
        return "grp.classic_time_remove_all_letters";
    }

    @Override
    public String getAchievementClassicTimeRemoveAllKeys() {
        return "grp.classic_time_remove_all_keys";
    }

    @Override
    public String getAchievementClassicCasualFirst10() {
        return "grp.classic_casual_first_10";
    }

    @Override
    public String getAchievementClassicCasualFirst50() {
        return "grp.classic_casual_first_50";
    }

    @Override
    public String getAchievementClassicCasualFirst100() {
        return "grp.classic_casual_first_100";
    }

    @Override
    public String getAchievementClassicCasualRemoveAllLetters() {
        return "grp.classic_casual_remove_all_letters";
    }

    @Override
    public String getAchievementClassicCasualRemoveAllKeys() {
        return "grp.classic_casual_remove_all_keys";
    }

    @Override
    public String getAchievementClassicChallengesFirstBronze() {
        return "grp.classic_challenges_first_bronze";
    }

    @Override
    public String getAchievementClassicChallengesAllBronze() {
        return "grp.classic_challenges_all_bronze";
    }

    @Override
    public String getAchievementClassicChallengesFirstSilver() {
        return "grp.classic_challenges_first_silver";
    }

    @Override
    public String getAchievementClassicChallengesAllSilver() {
        return "grp.classic_challenges_all_silver";
    }

    @Override
    public String getAchievementClassicChallengesFirstGold() {
        return "grp.classic_challenges_first_gold";
    }

    @Override
    public String getAchievementClassicChallengesAllGold() {
        return "grp.classic_challenges_all_gold";
    }

    @Override
    public String getAchievementClassicChallengesFirstPlatinum() {
        return "grp.classic_challenges_first_platinum";
    }

    @Override
    public String getAchievementClassicChallengesAllPlatinum() {
        return "grp.classic_challenges_all_platinum";
    }

    @Override
    public String getAchievementSwitchFirst100() {
        return "grp.switch_first_100";
    }

    @Override
    public String getAchievementSwitchFirst500() {
        return "grp.switch_first_500";
    }

    @Override
    public String getAchievementSwitchFirst2000() {
        return "grp.switch_first_2000";
    }

    @Override
    public String getAchievementSwitchLifeFirst10() {
        return "grp.switch_life_first_10";
    }

    @Override
    public String getAchievementSwitchLifeFirst50() {
        return "grp.switch_life_first_50";
    }

    @Override
    public String getAchievementSwitchLifeFirst100() {
        return "grp.switch_life_first_100";
    }

    @Override
    public String getAchievementSwitchLifeRemoveAllLetters() {
        return "grp.switch_life_remove_all_letters";
    }

    @Override
    public String getAchievementSwitchLifeRemoveAllKeys() {
        return "grp.switch_life_remove_all_keys";
    }

    @Override
    public String getAchievementSwitchTimeFirst10() {
        return "grp.switch_time_first_10";
    }

    @Override
    public String getAchievementSwitchTimeFirst50() {
        return "grp.switch_time_first_50";
    }

    @Override
    public String getAchievementSwitchTimeFirst100() {
        return "grp.switch_time_first_100";
    }

    @Override
    public String getAchievementSwitchTimeRemoveAllLetters() {
        return "grp.switch_time_remove_all_letters";
    }

    @Override
    public String getAchievementSwitchTimeRemoveAllKeys() {
        return "grp.switch_time_remove_all_keys";
    }

    @Override
    public String getAchievementSwitchCasualFirst10() {
        return "grp.switch_casual_first_10";
    }

    @Override
    public String getAchievementSwitchCasualFirst50() {
        return "grp.switch_casual_first_50";
    }

    @Override
    public String getAchievementSwitchCasualFirst100() {
        return "grp.switch_casual_first_100";
    }

    @Override
    public String getAchievementSwitchCasualRemoveAllLetters() {
        return "grp.switch_casual_remove_all_letters";
    }

    @Override
    public String getAchievementSwitchCasualRemoveAllKeys() {
        return "grp.switch_casual_remove_all_keys";
    }

    @Override
    public String getAchievementSwitchChallengesFirstBronze() {
        return "grp.switch_challenges_first_bronze";
    }

    @Override
    public String getAchievementSwitchChallengesAllBronze() {
        return "grp.switch_challenges_all_bronze";
    }

    @Override
    public String getAchievementSwitchChallengesFirstSilver() {
        return "grp.switch_challenges_first_silver";
    }

    @Override
    public String getAchievementSwitchChallengesAllSilver() {
        return "grp.switch_challenges_all_silver";
    }

    @Override
    public String getAchievementSwitchChallengesFirstGold() {
        return "grp.switch_challenges_first_gold";
    }

    @Override
    public String getAchievementSwitchChallengesAllGold() {
        return "grp.switch_challenges_all_gold";
    }

    @Override
    public String getAchievementSwitchChallengesFirstPlatinum() {
        return "grp.switch_challenges_first_platinum";
    }

    @Override
    public String getAchievementSwitchChallengesAllPlatinum() {
        return "grp.switch_challenges_all_platinum";
    }
}
