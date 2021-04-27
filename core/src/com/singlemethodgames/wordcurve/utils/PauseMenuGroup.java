package com.singlemethodgames.wordcurve.utils;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.I18NBundle;

/**
 * Created by cameron on 6/03/2018.
 */

public class PauseMenuGroup extends Group {

    public PauseMenuGroup(final GameState gameState, float width, float height, float barHeight, AssetManager assetManager, TextureRegionDrawable backgroundDrawable) {
        super();

        BitmapFont bitmapFont = assetManager.get(Constants.Fonts.SIZE72, BitmapFont.class);
        Label.LabelStyle labelStyleBlack = new Label.LabelStyle(bitmapFont, Color.WHITE);

        Label gamePausedHeaderLabel = new Label(assetManager.get(Assets.stringsBundle).get("pause_header"), labelStyleBlack);
        gamePausedHeaderLabel.setAlignment(Align.center);
        TextureAtlas textureAtlas = assetManager.get(Assets.ingameAtlas);
        Image backgroundImage = new Image(backgroundDrawable.tint(Constants.Colours.BACKGROUND_COLOUR));

        backgroundImage.setBounds(0, 0, width, height - barHeight);

        Button continueButton = PauseMenuGroup.getContinueButton(textureAtlas, assetManager.get(Assets.stringsBundle), assetManager.get(Constants.Fonts.SIZE96, BitmapFont.class));
        continueButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gameState.resumeGame();
            }
        });

        BitmapFont font48 = assetManager.get(Constants.Fonts.SIZE48, BitmapFont.class);
        NinePatchDrawable buttonDrawable = new NinePatchDrawable(textureAtlas.createPatch(Constants.TextureRegions.BUTTON_INCORRECT));
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle(buttonDrawable, buttonDrawable.tint(Color.GRAY), buttonDrawable, font48);
        textButtonStyle.fontColor = Color.WHITE;

        TextButton quitGameButton = new TextButton(assetManager.get(Assets.stringsBundle).get("quit_game"), textButtonStyle);
        quitGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gameState.quitGame();
            }
        });

        Table table = new Table();
        table.setPosition(0f, 0f);
        table.setSize(width, height);
        table.align(Align.center | Align.top);

        table.add(gamePausedHeaderLabel).center().expandX().padTop(120f).expandY();
        table.row();
        table.add(continueButton).size(700f, 250f).center().expandY();

        table.row().center().bottom().size(300f, 100f).padBottom(100f).expandY();
        table.add(quitGameButton);

        addActor(backgroundImage);
        addActor(table);
    }

    public static Button getContinueButton(TextureAtlas textureAtlas, I18NBundle stringsBundle, BitmapFont font) {
        NinePatchDrawable ninePatchDrawable = new NinePatchDrawable(textureAtlas.createPatch(Constants.TextureRegions.BUTTON));
        Drawable ninePatchDrawablePressed = ninePatchDrawable.tint(Color.LIGHT_GRAY);

        Image continueIcon = new Image(textureAtlas.findRegion(Constants.TextureRegions.PLAY_ICON));

        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        Label continueLabel = new Label(stringsBundle.get("continue"), labelStyle);

        Button continueButton = new Button(ninePatchDrawable, ninePatchDrawablePressed);
        continueButton.setSize(700f, 250f);

        continueButton.add(continueIcon).size(131, 150).expandY().left().padLeft(50).padRight(25);
        continueButton.add(continueLabel).expand().center();

        continueButton.setTouchable(Touchable.enabled);

        return continueButton;
    }
}
