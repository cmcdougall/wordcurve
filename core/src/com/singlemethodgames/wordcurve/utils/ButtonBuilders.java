package com.singlemethodgames.wordcurve.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class ButtonBuilders {
    public static ImageButton buildLifeModeButton(String text, Label.LabelStyle labelStyle, TextureAtlas atlas) {
        TextureRegionDrawable drawable = new TextureRegionDrawable(atlas.findRegion(Constants.TextureRegions.LIFE_MODE));
        Drawable drawablePressed = drawable.tint(Color.LIGHT_GRAY);
        return buildButton(text, drawable, drawablePressed, labelStyle);
    }

    public static ImageButton buildTimeModeButton(String text, Label.LabelStyle labelStyle, TextureAtlas atlas) {
        TextureRegionDrawable drawable = new TextureRegionDrawable(atlas.findRegion(Constants.TextureRegions.TIME_MODE));
        Drawable drawablePressed = drawable.tint(Color.LIGHT_GRAY);
        return buildButton(text, drawable, drawablePressed, labelStyle);
    }

    public static ImageButton buildChallengeModeButton(String text, Label.LabelStyle labelStyle, TextureAtlas atlas) {
        TextureRegionDrawable drawable = new TextureRegionDrawable(atlas.findRegion(Constants.TextureRegions.CHALLENGES_MODE));
        Drawable drawablePressed = drawable.tint(Color.LIGHT_GRAY);
        return buildButton(text, drawable, drawablePressed, labelStyle);
    }

    public static ImageButton buildCasualModeButton(String text, Label.LabelStyle labelStyle, TextureAtlas atlas) {
        TextureRegionDrawable drawable = new TextureRegionDrawable(atlas.findRegion(Constants.TextureRegions.CASUAL_MODE));
        Drawable drawablePressed = drawable.tint(Color.LIGHT_GRAY);
        return buildButton(text, drawable, drawablePressed, labelStyle);
    }

    private static ImageButton buildButton(String text, Drawable drawable, Drawable pressed, Label.LabelStyle labelStyle) {
        ImageButton.ImageButtonStyle lifeModeButtonStyle = new ImageButton.ImageButtonStyle(new Button.ButtonStyle(drawable, pressed, drawable));
        final ImageButton lifeModeButton = new ImageButton(lifeModeButtonStyle);

        Label lifeLabel = new Label(text, labelStyle);
        lifeModeButton.add(lifeLabel);
        lifeModeButton.getCell(lifeLabel).padLeft(200f);

        return lifeModeButton;
    }
}
