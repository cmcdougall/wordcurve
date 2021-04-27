package com.singlemethodgames.wordcurve.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.I18NBundle;
import com.singlemethodgames.wordcurve.WordCurveGame;
import com.singlemethodgames.wordcurve.services.UserListener;
import com.singlemethodgames.wordcurve.utils.Assets;
import com.singlemethodgames.wordcurve.utils.Constants;

public class InfoScreen extends BaseScreen implements UserListener {
    private Stage stage;
    private I18NBundle stringsBundle;
    private BitmapFont font48;
    private BitmapFont font60;
    private TextureAtlas textureAtlas;
    private Color textColour;
    private TextButton userButton;

    public InfoScreen(final WordCurveGame game, final BaseScreen baseScreen) {
        super(game);
        game.gameServices.updateUserListener(this);
        game.viewport.apply();

        stage = new Stage(game.viewport) {
            @Override
            public boolean keyDown(int keyCode) {
                if (keyCode == Input.Keys.BACK) {
                    game.setScreen(baseScreen);
                    return true;
                }
                return false;
            }
        };

        font48 = game.getAssetManager().get(Constants.Fonts.SIZE48, BitmapFont.class);
        font60 = game.getAssetManager().get(Constants.Fonts.SIZE60, BitmapFont.class);
        BitmapFont font72 = game.getAssetManager().get(Constants.Fonts.SIZE72, BitmapFont.class);

        textureAtlas = game.getAssetManager().get(Assets.mainInfoAtlas);
        Image devLogo = new Image(textureAtlas.findRegion(Constants.TextureRegions.SINGLE_METHOD_GAMES_LOGO));

        stringsBundle = game.getAssetManager().get(Assets.stringsBundle);

        textColour = new Color(146 / 255f, 158 / 255f, 180 / 255f, 1f);
        Label madeBy = new Label(stringsBundle.get("developed_by"), new Label.LabelStyle(font48, textColour));
        Label me = new Label("Cameron McDougall", new Label.LabelStyle(font72, textColour));

        Label license = new Label("https://fontawesome.com/license", new Label.LabelStyle(font48, textColour));
        license.setAlignment(Align.center);
        license.setWrap(true);

        NinePatchDrawable buttonDrawable = new NinePatchDrawable(textureAtlas.createPatch(Constants.TextureRegions.CHANGE_BUTTON));
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle(buttonDrawable, buttonDrawable.tint(Color.LIGHT_GRAY), buttonDrawable, font48);
        textButtonStyle.fontColor = textColour;
        TextButton privacyPolicyButton = new TextButton(stringsBundle.get("privacy_policy"), textButtonStyle);
        privacyPolicyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.net.openURI("http://singlemethodgames.com/privacypolicy.html");
            }
        });

        Drawable drawable = new TextureRegionDrawable(textureAtlas.findRegion(Constants.TextureRegions.EXIT_BUTTON));
        final ImageButton exitButton = new ImageButton(drawable);
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(baseScreen);
            }
        });

        Table table = new Table();
        table.setFillParent(true);
//        table.setDebug(true);
        table.align(Align.center | Align.top);

        table.add(devLogo).center().expandY().width(800f).height(72f);
        table.row();
        table.add(privacyPolicyButton).size(500, 100).expand();
        table.row();
        table.add(createVerticalGroupForActors(madeBy, me)).center().expand();
        table.row();
//        table.add(createVerticalGroupForActors(builtWith, libgdxLogo)).center().expand();
//        table.row();
        table.add(license).expandY();
        table.row();

        if(game.platformResolver.userManagedInApp()) {
            table.add(createUserManagementToTable()).center().expand();
            table.row();
        }

        table.add().expandY();
        table.row().center().bottom().size(150f).padBottom(100f);
        table.add(exitButton);
        stage.addActor(table);
    }

    private VerticalGroup createVerticalGroupForActors(Actor topActor, Actor bottomActor) {
        VerticalGroup verticalGroup = new VerticalGroup();

        Container<Actor> topActorCell = new Container<>(topActor).fill().padBottom(10f);
        Container<Actor> bottomActorCell = new Container<>(bottomActor).fill();
        verticalGroup.expand();
        verticalGroup.addActor(topActorCell);
        verticalGroup.addActor(bottomActorCell);

        return verticalGroup;
    }

    private VerticalGroup createUserManagementToTable() {
        VerticalGroup verticalGroup = new VerticalGroup();
        Label platformHeader = new Label(stringsBundle.get(game.platformResolver.getPlatform()), new Label.LabelStyle(font60, textColour));
        platformHeader.setAlignment(Align.center);
        platformHeader.setWrap(true);

        NinePatchDrawable buttonDrawable = new NinePatchDrawable(textureAtlas.createPatch(Constants.TextureRegions.CHANGE_BUTTON));
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle(buttonDrawable, buttonDrawable.tint(Color.LIGHT_GRAY), buttonDrawable, font48);
        textButtonStyle.fontColor = textColour;
        userButton = new TextButton(getUserStatus(), textButtonStyle);
        userButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(game.gameServices.isSignedIn()) {
                    game.gameServices.signOut();
                } else {
                    game.gameServices.signIn();
                }
            }
        });

        Container<Label> topActorCell = new Container<>(platformHeader).fill().padBottom(30f);
        Container<TextButton> bottomActorCell = new Container<>(userButton).center().top().size(500, 100);
        verticalGroup.expand();
        verticalGroup.addActor(topActorCell);
        verticalGroup.addActor(bottomActorCell);

        return verticalGroup;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(Constants.Colours.SPLASH_BACKGROUND_COLOUR, Constants.Colours.SPLASH_BACKGROUND_COLOUR, Constants.Colours.SPLASH_BACKGROUND_COLOUR, 1);
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
    public void hide() {

    }

    @Override
    public InputProcessor getInputProcessor() {
        return stage;
    }

    private String getUserStatus() {
        return game.gameServices.isSignedIn() ? stringsBundle.get("sign_out") : stringsBundle.get("sign_in");
    }

    @Override
    public void userSignedIn() {
        userButton.setText(stringsBundle.get("sign_out"));
    }

    @Override
    public void userSignedOut() {
        userButton.setText(stringsBundle.get("sign_in"));
    }
    @Override
    public void userSignedInFailed() {
        game.notifyUser(stringsBundle.get("failed_signin"));
    }

    @Override
    public void userSignedOutFailed() {
        game.notifyUser(stringsBundle.get("failed_signout"));
    }
}
