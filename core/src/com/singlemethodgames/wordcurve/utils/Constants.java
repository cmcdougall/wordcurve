package com.singlemethodgames.wordcurve.utils;

import com.badlogic.gdx.graphics.Color;

/**
 * Created by cameron on 19/02/2018.
 */

public class Constants {
    public static class Colours {
        public static final Color BACKGROUND_COLOUR = new Color(2 / 255f, 17 / 255f, 27 / 255f, 1);
        public static final float SPLASH_BACKGROUND_COLOUR = 34f / 255f;
        public static final Color TOP_BAR = new Color(10 / 255f, 36 / 255f, 99 / 255f, 1f);
        public static final Color CLOCK_COLOUR = new Color(138 / 255f, 205 / 255f, 234 / 255f, 1f);

        public static final Color INCORRECT_RED = new Color(146/255f,20/255f,12/255f,1f);
        public static final Color INCORRECT_KEY_RED = new Color(175 / 255f, 20 / 255f, 12 / 255f, 1f);
        public static final Color CORRECT_GREEN = new Color(69/255f,135/255f,34/255f,1f);

        public static class Keyboard {
            public static final Color DEFAULT_PRESSED_TINT = new Color(.9f, .9f, .9f, 1f);
            public static final Color KEY_COLOUR = new Color(62 / 255f, 146 / 255f, 204 / 255f, 1f);
            public static final Color KEY_COLOUR_ZERO = new Color(62 / 255f, 146 / 255f, 204 / 255f, 0f);
            public static final Color KEY_COLOUR_PRESSED = new Color(11 / 255f, 95 / 255f, 153 / 255f, 1f);
            public static final Color BASE_COLOUR = new Color(10 / 255f, 36 / 255f, 99 / 255f, 1f);
            public static final Color BASE_COLOUR_PRESSED = new Color(0 / 255f, 0 / 255f, 48 / 255f, 1f);
            public static final Color LETTER_COLOUR = new Color(2 / 255f, 17 / 255f, 27 / 255f, 1f);

            public static class Button {
                public static final Color CORRECT = new Color(98 / 255f, 193 / 255f, 34 / 255f, 1f);
                public static final Color INCORRECT = new Color(175 / 255f, 84 / 255f, 78 / 255f, 1f);
            }
        }

        public static class Heart {
            public static final Color HEART_COLOUR = Color.RED.cpy();
            public static final Color FADED_HEART_COLOUR = new Color(1, 1, 1, 0.25f);
        }

        public static class Score {
            public static final Color NORMAL = Color.WHITE.cpy();
            public static final Color CORRECT = Color.GREEN.cpy();
            public static final Color INCORRECT = Color.RED.cpy();
        }

        public static class Timer {
            public static final Color NORMAL = Color.WHITE;
            public static final Color CORRECT = Color.GREEN;
        }

        public static class Trophy {
            public static final Color BRONZE = new Color (163 / 255f, 113 / 255f, 100 / 255f, 1f);
            public static final Color SILVER = new Color (192 / 255f, 192 / 255f, 192 / 255f, 1f);
            public static final Color PLATINUM  = new Color (202 / 255f, 228 / 255f, 226 / 255f, 1f);
//            public static final Color PLATINUM  = new Color (229 / 255f, 212 / 255f, 237 / 255f, 1f);
        }
    }

    public static class Conditions {
        public static final int START_LIVES = 3;
    }

    public static class Fonts {
        public static final String ROBOTO_FONT = "fonts/Roboto-Regular.ttf";
        public static final String SIZE34 = "size34.ttf";
        public static final String SIZE48 = "size48.ttf";
        public static final String SIZE60 = "size60.ttf";
        public static final String SIZE72 = "size72.ttf";
        public static final String SIZE96 = "size96.ttf";
        public static final String SIZE168 = "size168.ttf";
    }

    public static class TextureRegions {
        public static final String GRADIENT = "gradient";

        // Main Menu Buttons
        public static final String CLASSIC_SMALL = "classic_small";
        public static final String SWITCH_SMALL = "switch_small";
        public static final String TUTORIAL_BUTTON = "tutorial";
        public static final String INFO_BUTTON = "info";

        // Mode Selection Buttons
        public static final String LIFE_MODE = "life_mode";
        public static final String LIFE_SMALL = "life_small";
        public static final String TIME_MODE = "time_mode";
        public static final String TIME_SMALL = "time_small";
        public static final String CHALLENGES_MODE = "challenges_mode";
        public static final String CHALLENGES_SMALL = "challenges_small";
        public static final String CASUAL_MODE = "casual_mode";
        public static final String CASUAL_SMALL = "casual_small";
        public static final String NO_SELECT = "no_select";

        // Word Buttons
        public static final String BUTTON = "button";
        public static final String BUTTON_PRESSED = "button_pressed";
        public static final String BUTTON_CORRECT = "button_correct";
        public static final String BUTTON_INCORRECT = "button_incorrect";
        public static final String BUTTON_GRAY = "button_gray";
        public static final String CHANGE_BUTTON = "change_button";
        public static final String PAUSE_BUTTON = "pause";
        public static final String EXIT_APP_BUTTON = "exit_game";
        public static final String PLAY_GAME_BUTTON = "play_game";
        public static final String LEVEL_BUTTON = "level_button";
        public static final String MAIN_BUTTON = "main_button";

        public static final String HELP_BUTTON = "help";

        public static final String INCREASE = "increase";
        public static final String DECREASE = "decrease";

        public static final String KEYBOARD_SETTINGS_ICON = "keyboard-settings";

        // Key Settings
        public static final String KEYBOARD_BASE = "keyboard_base";
        public static final String BASE = "base";

        public static final String KEYBOARD_KEY_SETTINGS_WIDE = "keyboard-key-settings-wide";
        public static final String KEYBOARD_KEY_NONE_WIDE = "keyboard-key-none-wide";
        public static final String KEYBOARD_KEY_WITH_LETTER_WIDE = "keyboard-key-with-letter-wide";
        public static final String KEYBOARD_KEY_DISAPPEARING_LETTER_WIDE = "keyboard-key-disappearing-letter-wide";
        public static final String KEYBOARD_KEY_WITH_KEY_DISAPPEARING = "keyboard-key-with-key-disappearing";

        public static final String CORRECT_ICON = "correct";

        // TextureArea
        public static final String POINT = "point";
        public static final String TIP = "tip";
        public static final String HALF_TIP = "half_tip";
        public static final String PREVIOUS_BUTTON = "previous";
        public static final String NEXT_BUTTON = "next";
        public static final String EXIT_BUTTON = "exit";

        public static final String KEYBOARD_KEY = "key";

        public static final String REPLAY_BUTTON = "replay";
        public static final String HEART = "heart";
        public static final String HEART_BROKEN = "heart-broken";
        public static final String STAR = "star";
        public static final String CLOCK = "clock";
        public static final String TROPHY = "trophy";
        public static final String LOCK = "lock";
        public static final String UNLOCK = "unlock";
        public static final String CART = "cart";
        public static final String VIDEO = "video";
        public static final String NO_VIDEO = "no_video";
        public static final String SPINNER = "spinner";
        public static final String REMOVE_ICON = "remove_icon";
        public static final String CHECKED = "checked";
        public static final String UNCHECKED = "unchecked";

        public static final String SINGLE_METHOD_GAMES_LOGO = "singlemethodgameslogo";

        public static final String LEADERBOARD_ICON = "leaderboard";

        public static final String BUY = "buy";
        public static final String USER_ICON = "user";

        public static final String CONTINUE_BUTTON = "play";
        public static final String PLAY_ICON = "play_icon";

        public static final String WHITE_PIXEL = "white";
        public static final String LIBGDX_LOGO = "libgdx";
    }

    public static class Timing {
        public static final float BUTTON_MOVEMENT = 0.2f;
        public static final float INCORRECT_GUESS_MOVEMENT = 0.1f;
        public static final float SHOW_EXIT_MENU = 0.3f;
        public static final float INCORRECT_GUESS = 10f;
        public static final float FADE_TRANSITION_LENGTH = 0.2f;
    }

    public static class JsonFiles {
        public static final String QWERTY_KEYBOARD_COORDINATES = "json/qwerty-coordinates.json";
        public static final String WORD_DICT = "json/dict.json";
        public static final String CHALLENGES = "json/challenges.json";
    }

    public static class Sizes {
        public static final float ANSWER_BUTTON_WIDTH = 800f;
        public static final float ANSWER_BUTTON_HEIGHT = 200f;
    }
}
