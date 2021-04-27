package com.singlemethodgames.wordcurve.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.I18NBundle;
import com.singlemethodgames.wordcurve.WordCurveGame;
import com.singlemethodgames.wordcurve.actors.gamebar.GameMode;
import com.singlemethodgames.wordcurve.screens.difficulty.LetterDifficulty;
import com.singlemethodgames.wordcurve.screens.difficulty.LetterSettings;
import com.singlemethodgames.wordcurve.screens.difficulty.SpeedDifficulty;
import com.singlemethodgames.wordcurve.screens.difficulty.SpeedSetting;
import com.singlemethodgames.wordcurve.screens.variants.Variant;

public class KeyboardSettingsTable extends Table {
    private Modified modified;
    public final String difficultyWord = "qzrvump";
    private Label pointsLabel;

    private final Variant variant;
    private LetterDifficulty letterDifficulty;
    private SpeedDifficulty speedDifficulty;
    private WordCurveGame game;
    private I18NBundle myBundle;

    private Slider speedSlider;
    private Slider displaySlider;

    private Label currentDisplaySettingLabel;
    private Label currentSpeedSettingLabel;

    public boolean displaySettingsChanged = false;
    public boolean speedSettingsChanged = false;

    private static final int allVisible = 0;
    private static final int quarterLetterHidden = LetterSettings.DISAPPEARING_LETTERS.start + (int)((LetterSettings.DISAPPEARING_LETTERS.end - LetterSettings.DISAPPEARING_LETTERS.start) * 0.25);;
    private static final int halfLetterHidden = LetterSettings.DISAPPEARING_LETTERS.start + (LetterSettings.DISAPPEARING_LETTERS.end - LetterSettings.DISAPPEARING_LETTERS.start) / 2;
    private static final int threeQuarterLetterHidden = LetterSettings.DISAPPEARING_LETTERS.start + (int)((LetterSettings.DISAPPEARING_LETTERS.end - LetterSettings.DISAPPEARING_LETTERS.start) * 0.75);
    private static final int lettersHidden = LetterSettings.NO_LETTERS.start;
    private static final int quarterKeyHidden = LetterSettings.DISAPPEARING_KEYS.start + (int)((LetterSettings.DISAPPEARING_KEYS.end - LetterSettings.DISAPPEARING_KEYS.start) * 0.25);
    private static final int halfKeyHidden = LetterSettings.DISAPPEARING_KEYS.start + (LetterSettings.DISAPPEARING_KEYS.end - LetterSettings.DISAPPEARING_KEYS.start) / 2;
    private static final int threeQuarterKeyHidden = LetterSettings.DISAPPEARING_KEYS.start + (int)((LetterSettings.DISAPPEARING_KEYS.end - LetterSettings.DISAPPEARING_KEYS.start) * 0.75);
    private static final int allHidden = LetterSettings.NONE.start;

    public KeyboardSettingsTable(final WordCurveGame game, final Modified modified, boolean includeSpeed, final Variant variant, final GameMode gameMode, TextureAtlas textureAtlas) {
        this(game, modified, includeSpeed, variant, gameMode, textureAtlas, -10, -10);
    }

    public KeyboardSettingsTable(final WordCurveGame game, final Modified modified, boolean includeSpeed, final Variant variant, final GameMode gameMode, TextureAtlas textureAtlas, final int currentCorrectDisplay, final int currentCorrectSpeed) {
        this.game = game;

        this.letterDifficulty = new LetterDifficulty(
                currentCorrectDisplay != -10 ? currentCorrectDisplay : game.gamePreferences.getLetterDifficulty(variant),
                0);

        this.speedDifficulty = new SpeedDifficulty(
                currentCorrectSpeed != -10 ? currentCorrectSpeed : game.gamePreferences.getSpeedDifficulty(variant).start,
                0);

        BitmapFont font48 = game.getAssetManager().get(Constants.Fonts.SIZE48, BitmapFont.class);
        BitmapFont font72 = game.getAssetManager().get(Constants.Fonts.SIZE72, BitmapFont.class);

        Label.LabelStyle labelStyle = new Label.LabelStyle(font48, Color.WHITE);
        Label.LabelStyle largeLabelStyle = new Label.LabelStyle(font72, Color.WHITE);
        this.modified = modified;
        myBundle = game.getAssetManager().get(Assets.stringsBundle);
        this.variant = variant;

        currentDisplaySettingLabel = new Label("", largeLabelStyle);
        currentSpeedSettingLabel = new Label("", largeLabelStyle);

        String headerString = gameMode.equals(GameMode.CASUAL) || gameMode.equals(GameMode.CHALLENGES) ? myBundle.get("difficulty") : myBundle.get("starting_difficulty");
        Label headerLabel = new Label(headerString, largeLabelStyle);
        Label keyboardSettingsLabel = new Label(myBundle.get("settings_keys"), labelStyle);
        Label speedLabel = new Label(myBundle.get("settings_speed"), labelStyle);

        TextureRegionDrawable buttonDrawable = new TextureRegionDrawable(textureAtlas.findRegion("display_button"));
        Slider.SliderStyle speedSliderStyle = new Slider.SliderStyle(new TextureRegionDrawable(textureAtlas.findRegion("speed_slider")),
                buttonDrawable
        );

        speedSlider = new Slider(1, 5, 1, false, speedSliderStyle);
        setSpeedSliderForDifficulty(speedDifficulty.getSpeedSetting());
        speedSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Slider slider = (Slider) actor;

                Number value = slider.getValue();
                switch(value.intValue()) {
                    case 1: speedChanged(SpeedSetting.ONE);
                        break;
                    case 2: speedChanged(SpeedSetting.TWO);
                        break;
                    case 3: speedChanged(SpeedSetting.THREE);
                        break;
                    case 4: speedChanged(SpeedSetting.FOUR);
                        break;
                    case 5: speedChanged(SpeedSetting.FIVE);
                        break;
                }
                updateCurrentSpeedLabel();
                saveSettings();
                speedSettingsChanged = true;
            }
        });

        Slider.SliderStyle displaySliderStyle = new Slider.SliderStyle(new TextureRegionDrawable(textureAtlas.findRegion("display_slider")),
                buttonDrawable
        );

        displaySlider = new Slider(1, 9, 1, false, displaySliderStyle);
        setDisplaySliderForDifficulty(letterDifficulty.getCurrentCorrect());
        displaySlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Slider slider = (Slider) actor;

                Number value = slider.getValue();
                int currentSettings = allVisible;
                switch(value.intValue()) {
                    case 2: currentSettings = quarterLetterHidden;
                        break;
                    case 3: currentSettings = halfLetterHidden;
                        break;
                    case 4: currentSettings = threeQuarterLetterHidden;
                        break;
                    case 5: currentSettings = lettersHidden;
                        break;
                    case 6: currentSettings = quarterKeyHidden;
                        break;
                    case 7: currentSettings = halfKeyHidden;
                        break;
                    case 8: currentSettings = threeQuarterKeyHidden;
                        break;
                    case 9: currentSettings = allHidden;
                        break;
                }

                letterDifficulty.setCurrentCorrect(currentSettings);
                updateKeysSettings(currentSettings);
                updateCurrentDisplayLabel();
                saveSettings();
                displaySettingsChanged = true;
            }
        });

        updateCurrentDisplayLabel();
        updateCurrentSpeedLabel();

        Image pointsImage = new Image(textureAtlas.findRegion(Constants.TextureRegions.STAR));
        pointsImage.setColor(Color.YELLOW);
        pointsImage.setOrigin(Align.center);

        pointsLabel = new Label("0", largeLabelStyle);
        pointsLabel.setAlignment(Align.center);

        Table pointsTable = new Table();
        pointsTable.add(pointsImage).width(100).center().expandX().expandY().right().padRight(15f);
        pointsTable.add(pointsLabel).center().expandX().expandY().left().padLeft(15f);

        Table displayTable = new Table();
        displayTable.align(Align.center);
        displayTable.add(keyboardSettingsLabel).center();
        displayTable.row();
        displayTable.add(currentDisplaySettingLabel).center().pad(20);
        displayTable.row();
        displayTable.add(displaySlider).center().size(900, 100);

        Table speedTable = new Table();
        speedTable.align(Align.center);
        speedTable.add(speedLabel);
        speedTable.row();
        speedTable.add(currentSpeedSettingLabel).center().pad(20);
        speedTable.row();
        speedTable.add(speedSlider).center().size(900, 100).expandX();

        setFillParent(true);
        align(Align.center|Align.top);

        add(headerLabel).center().padTop(50f).padBottom(400f);
        row();
        add(pointsTable).center().expandY();
        row();
        add(displayTable).center().width(1000).expand();
        row();
        add(speedTable).center().width(1000).expand();

        updatePointsLabel();
    }

    private void setDisplaySliderForDifficulty(final int count) {
        if (count < LetterSettings.LETTERS.end) {
            displaySlider.setValue(1);
        } else if (count < LetterSettings.DISAPPEARING_LETTERS.end) {
            if(count <= (quarterLetterHidden + halfLetterHidden) / 2) {
                displaySlider.setValue(2);
            } else if(count <= (halfLetterHidden + threeQuarterLetterHidden) / 2) {
                displaySlider.setValue(3);
            } else {
                displaySlider.setValue(4);
            }
        } else if (count < LetterSettings.NO_LETTERS.end) {
            displaySlider.setValue(5);
        } else if(count < LetterSettings.DISAPPEARING_KEYS.end) {
            if(count <= (quarterKeyHidden + halfKeyHidden) / 2) {
                displaySlider.setValue(6);
            } else if(count <= (halfKeyHidden + threeQuarterKeyHidden) / 2) {
                displaySlider.setValue(7);
            } else {
                displaySlider.setValue(8);
            }
        } else {
            displaySlider.setValue(9);
        }
    }

    private void setSpeedSliderForDifficulty(SpeedSetting speedSetting) {
        switch(speedSetting) {
            case ONE: speedSlider.setValue(1);
                break;
            case TWO: speedSlider.setValue(2);
                break;
            case THREE: speedSlider.setValue(3);
                break;
            case FOUR: speedSlider.setValue(4);
                break;
            case FIVE: speedSlider.setValue(5);
                break;
        }
    }

    private void updateCurrentSpeedLabel() {
        currentSpeedSettingLabel.setText("×" + myBundle.get("speed_"+speedDifficulty.getSpeedSetting().toString().toLowerCase()));
    }

    private void updateCurrentDisplayLabel() {
        switch (letterDifficulty.getLetterSettings()) {
            case LETTERS: currentDisplaySettingLabel.setText(myBundle.get("display_all_visible"));
                break;
            case DISAPPEARING_LETTERS: currentDisplaySettingLabel.setText(myBundle.get("display_letters_disappearing"));
                break;
            case NO_LETTERS: currentDisplaySettingLabel.setText(myBundle.get("display_letters_hidden"));
                break;
            case DISAPPEARING_KEYS: currentDisplaySettingLabel.setText(myBundle.get("display_keys_disappearing"));
                break;
            case NONE: currentDisplaySettingLabel.setText(myBundle.get("display_all_hidden"));
                break;
        }
    }

    private void updatePointsLabel() {
        pointsLabel.clearActions();
        pointsLabel.setText(String.valueOf(getPoints()));
    }

    private void updateKeysSettings(int start) {
        modified.performWordWave(this.difficultyWord, this.speedDifficulty.getSpeedSetting());
        modified.settingsChanged(start);
        updatePointsLabel();
    }

    private void speedChanged(final SpeedSetting SPEED) {
        this.speedDifficulty.setCurrentCorrect(SPEED.start);
        modified.performWordWave(this.difficultyWord, SPEED);
        updatePointsLabel();
    }

    public interface Modified {
        void settingsChanged(int start);
        void performWordWave(String word, SpeedSetting speedSetting);
    }

    public int getPoints() {
        return letterDifficulty.getPoints() + speedDifficulty.getPoints();
    }

    private void saveSettings() {
        game.gamePreferences.saveSettings(variant, this.letterDifficulty.getCurrentCorrect(), this.speedDifficulty.getSpeedSetting());
    }

    public LetterDifficulty getLetterSettings() {
        return letterDifficulty;
    }

    public SpeedDifficulty getSpeedDifficulty() {
        return speedDifficulty;
    }
}
