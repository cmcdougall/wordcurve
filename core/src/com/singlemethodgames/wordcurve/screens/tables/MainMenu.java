package com.singlemethodgames.wordcurve.screens.tables;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.I18NBundle;
import com.singlemethodgames.wordcurve.WordCurveGame;
import com.singlemethodgames.wordcurve.buttons.VariantButton;
import com.singlemethodgames.wordcurve.screens.InfoScreen;
import com.singlemethodgames.wordcurve.screens.MainMenuScreen;
import com.singlemethodgames.wordcurve.screens.SelectModeMenu;
import com.singlemethodgames.wordcurve.screens.tutorial.TutorialStart;
import com.singlemethodgames.wordcurve.screens.variants.Variant;
import com.singlemethodgames.wordcurve.utils.Constants;

public class MainMenu extends Table {
    private final WordCurveGame game;

    public MainMenu(final WordCurveGame game, final I18NBundle myBundle, TextureAtlas textureAtlas, final MainMenuScreen mainMenuScreen) {
        this.game = game;
        align(Align.center | Align.top);
        setFillParent(true);

        BitmapFont font48 = game.getAssetManager().get(Constants.Fonts.SIZE48, BitmapFont.class);
        BitmapFont font96 = game.getAssetManager().get(Constants.Fonts.SIZE96, BitmapFont.class);
        BitmapFont font168 = game.getAssetManager().get(Constants.Fonts.SIZE168, BitmapFont.class);

        Color wordCurveColour = new Color(179 / 255f, 179 / 255f, 179 / 255f, 1f);

        Label gameLogoText = new Label("word   curve", new Label.LabelStyle(font168, wordCurveColour));

        NinePatchDrawable ninePatchDrawable = new NinePatchDrawable(textureAtlas.createPatch("main_new"));
        Drawable ninePatchDrawablePressed = ninePatchDrawable.tint(Color.LIGHT_GRAY);

        Label.LabelStyle labelStyle = new Label.LabelStyle(font96, Constants.Colours.Keyboard.KEY_COLOUR);
        Label classicLabel = new Label(myBundle.get("classic_name"), labelStyle);
        Label classicDescriptionLabel = new Label(myBundle.get("classic_description"), new Label.LabelStyle(font48, Constants.Colours.Keyboard.KEY_COLOUR));
        VariantButton classicVariantButton = new VariantButton(classicLabel, classicDescriptionLabel, ninePatchDrawable, ninePatchDrawablePressed, new Image(textureAtlas.findRegion("classic_icon")));
        classicVariantButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                setScreenForVariant(Variant.CLASSIC);
            }
        });

        Label switchLabel = new Label(myBundle.get("switch_name"), labelStyle);
        Label switchDescriptionLabel = new Label(myBundle.get("switch_description"), new Label.LabelStyle(font48, Constants.Colours.Keyboard.KEY_COLOUR));
        VariantButton switchVariantButton = new VariantButton(switchLabel, switchDescriptionLabel, ninePatchDrawable, ninePatchDrawablePressed,new Image(textureAtlas.findRegion("switch_icon")));
        switchVariantButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                setScreenForVariant(Variant.SWITCH);
            }
        });

        Drawable drawable = new TextureRegionDrawable(textureAtlas.findRegion(Constants.TextureRegions.TUTORIAL_BUTTON));
        final ImageButton tutorialButton = new ImageButton(drawable);
        tutorialButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new TutorialStart(game));
            }
        });

        drawable = new TextureRegionDrawable(textureAtlas.findRegion(Constants.TextureRegions.INFO_BUTTON));
        final ImageButton infoButton = new ImageButton(drawable);
        infoButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new InfoScreen(game, mainMenuScreen));
            }
        });

        NinePatchDrawable buttonDrawable = new NinePatchDrawable(textureAtlas.createPatch(Constants.TextureRegions.MAIN_BUTTON)).tint(Color.LIGHT_GRAY);
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle(buttonDrawable, buttonDrawable.tint(Color.GRAY), buttonDrawable, font48);
        textButtonStyle.fontColor = Constants.Colours.Keyboard.KEY_COLOUR;

        TextButton viewAchievementsButton = new TextButton(myBundle.get("achievements"), textButtonStyle);
        viewAchievementsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(game.gameServices.isSignedIn()) {
                    game.gameServices.viewAchievements();
                } else if(game.platformResolver.userManagedInApp()) {
                    game.gameServices.signIn();
                } else {
                    game.notifyUser(myBundle.format("sign_in_to_view_achievements", myBundle.get(game.platformResolver.getPlatform())));
                }
            }
        });

        TextButton viewLeaderboardsButton = new TextButton(myBundle.get("leaderboards"), textButtonStyle);
        viewLeaderboardsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(game.gameServices.isSignedIn()) {
                    game.gameServices.viewLeaderboards();
                } else if(game.platformResolver.userManagedInApp()) {
                    game.gameServices.signIn();
                } else {
                    game.notifyUser(myBundle.format("sign_in_to_view", myBundle.get(game.platformResolver.getPlatform())));
                }
            }
        });

        drawable = new TextureRegionDrawable(textureAtlas.findRegion(Constants.TextureRegions.BUY));
        ImageButton storeButton = new ImageButton(drawable);
        storeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.storeManager.updateScreens(mainMenuScreen, mainMenuScreen);
                game.setScreen(game.storeManager);
            }
        });

        float height = 300f;
        float width = 800f;

        add(gameLogoText).padTop(120).padBottom(160).center();
        row();
        add(classicVariantButton).width(width).height(height).center().padBottom(100);
        row();
        add(switchVariantButton).width(width).height(height).center();
        row();
        add(viewAchievementsButton).expandY().bottom().padBottom(20).size(width * 3f/4f, 100);
        row();
        add(viewLeaderboardsButton).expandY().top().padTop(20).size(width * 3f/4f, 100);

        Table buttonTable = new Table();
        buttonTable.row().padBottom(100f).size(150f).expand();
        buttonTable.add(tutorialButton).left();
        buttonTable.add(storeButton).center();
        buttonTable.add(infoButton).right();

        row().center().expandX();
        add(buttonTable).width(775f).center().bottom();
    }

    private void setScreenForVariant(Variant variant) {
        game.setScreen(new SelectModeMenu(game, variant));
    }
}
