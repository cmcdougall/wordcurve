package com.singlemethodgames.wordcurve.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.I18NBundle;
import com.singlemethodgames.wordcurve.WordCurveGame;
import com.singlemethodgames.wordcurve.actors.gamebar.GameMode;
import com.singlemethodgames.wordcurve.buttons.VariantButton;
import com.singlemethodgames.wordcurve.screens.variants.Variant;
import com.singlemethodgames.wordcurve.utils.Assets;
import com.singlemethodgames.wordcurve.utils.Constants;

public class SelectModeMenu extends BaseScreen {
    private Stage stage;

    public SelectModeMenu(final WordCurveGame game, final Variant variant) {
        super(game);
        stage = new Stage(game.viewport) {
            @Override
            public boolean keyDown(int keyCode) {
                if(keyCode == Input.Keys.BACK) {
                    goToMainMenu();
                    return true;
                }
                return false;
            }
        };
        I18NBundle bundle = game.getAssetManager().get(Assets.stringsBundle);
        TextureAtlas textureAtlas = game.getAssetManager().get(Assets.modeAtlas);
        TextureRegion texture = textureAtlas.findRegion(variant.equals(Variant.CLASSIC) ? "classic_icon" : "switch_icon");
        Image selectedType = new Image(texture);

        BitmapFont font48 = game.getAssetManager().get(Constants.Fonts.SIZE48, BitmapFont.class);
        BitmapFont font96 = game.getAssetManager().get(Constants.Fonts.SIZE96, BitmapFont.class);

        VariantButton casualButton = createVariantButton(
                textureAtlas,
                "casual_icon",
                bundle.get("casual_name"),
                font96,
                bundle.get("casual_description"),
                font48,
                new Color( 227/255f, 149/255f, 0/255f, 1),
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        game.setScreen(new FinalSelectScreen(game, variant, GameMode.CASUAL));
                    }
                }
        );

        VariantButton lifeButton = createVariantButton(
                textureAtlas,
                "life_icon",
                bundle.get("life_name"),
                font96,
                bundle.get("life_description"),
                font48,
                new Color( 234/255f, 10/255f, 40/255f, 1),
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        game.setScreen(new FinalSelectScreen(game, variant, GameMode.LIFE));
                    }
                }
        );

        VariantButton challengesButton = createVariantButton(
                textureAtlas,
                "challenge_icon",
                bundle.get("challenges_name"),
                font96,
                bundle.get("challenges_description"),
                font48,
                new Color( 20/255f, 105/255f, 242/255f, 1),
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        game.setScreen(new LevelSelectScreen(game, variant));
                    }
                }
        );

        VariantButton timeButton = createVariantButton(
                textureAtlas,
                "time_icon",
                bundle.get("time_name"),
                font96,
                bundle.get("time_description"),
                font48,
                new Color( 49/255f, 182/255f, 0/255f, 1),
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        game.setScreen(new FinalSelectScreen(game, variant, GameMode.TIME));
                    }
                }
        );

        Drawable drawable = new TextureRegionDrawable(textureAtlas.findRegion(Constants.TextureRegions.EXIT_BUTTON));
        ImageButton exitButton = new ImageButton(drawable);
        exitButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                goToMainMenu();
            }
        });


        Table table = new Table();
        table.setFillParent(true);
//        table.setDebug(true);
        table.align(Align.center | Align.top);

        Label.LabelStyle crumbStyle = new Label.LabelStyle(font96, Color.WHITE);
        Label variantLabel = new Label(bundle.get(variant.toString().toLowerCase() + "_name"), crumbStyle);
        variantLabel.setAlignment(Align.center);

        variantLabel.getGlyphLayout().setText(variantLabel.getStyle().font, variantLabel.getText());
        float height = variantLabel.getGlyphLayout().height + 50;
        float width = (height / selectedType.getPrefHeight()) * selectedType.getPrefWidth();

        Table selectedVariant = new Table();
        selectedVariant.add(selectedType).expand().right().padRight(20).size(width, height);
        selectedVariant.add(variantLabel).expand().left();//.padTop(75);

        table.add(selectedVariant).center().padTop(75).padBottom(75);
        table.row();
        table.add(casualButton).center().expandY().size(800, 275);
        table.row();
        table.add(challengesButton).center().expandY().size(800, 275);
        table.row();
        table.add(lifeButton).center().expandY().size(800, 275);
        table.row();
        table.add(timeButton).center().expandY().size(800, 275);
        table.row().center().bottom().size(150f).padBottom(100f).expandY();
        table.add(exitButton);

        stage.addActor(table);
    }

    private static VariantButton createVariantButton(final TextureAtlas textureAtlas, final String region, String modeName, BitmapFont nameFont, String modeDescription, BitmapFont descriptionFont, Color regionColour, ClickListener clickListener) {
        Label.LabelStyle labelStyle = new Label.LabelStyle(nameFont, Color.BLACK);
        Label classicLabel = new Label(modeName, labelStyle);
        Label classicDescriptionLabel = new Label(modeDescription, new Label.LabelStyle(descriptionFont, Color.BLACK));

        NinePatchDrawable background = new NinePatchDrawable(textureAtlas.createPatch("background")).tint(regionColour);
        Drawable pressed = background.tint(regionColour.cpy().mul(Color.LIGHT_GRAY));

        Image iconImage = new Image(textureAtlas.findRegion(region));
        iconImage.setColor(Color.BLACK);

        VariantButton variantButton = new VariantButton(classicLabel, classicDescriptionLabel, background, pressed, iconImage);
        variantButton.addListener(clickListener);

        return variantButton;
    }

    @Override
    public void goToMainMenu() {
        super.goToMainMenu();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(Constants.Colours.BACKGROUND_COLOUR.r, Constants.Colours.BACKGROUND_COLOUR.g, Constants.Colours.BACKGROUND_COLOUR.b, Constants.Colours.BACKGROUND_COLOUR.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.camera.update();

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {
        super.resume();
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    public InputProcessor getInputProcessor() {
        return stage;
    }

}
