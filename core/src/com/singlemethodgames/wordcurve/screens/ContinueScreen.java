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
import com.badlogic.gdx.scenes.scene2d.ui.Button;
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
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Scaling;
import com.singlemethodgames.wordcurve.SaveState;
import com.singlemethodgames.wordcurve.WordCurveGame;
import com.singlemethodgames.wordcurve.actors.gamebar.CasualBar;
import com.singlemethodgames.wordcurve.actors.gamebar.ChallengesBar;
import com.singlemethodgames.wordcurve.actors.gamebar.GameMode;
import com.singlemethodgames.wordcurve.actors.gamebar.LifeBar;
import com.singlemethodgames.wordcurve.actors.gamebar.TimeBar;
import com.singlemethodgames.wordcurve.screens.variants.Classic;
import com.singlemethodgames.wordcurve.screens.variants.Switch;
import com.singlemethodgames.wordcurve.screens.variants.Variant;
import com.singlemethodgames.wordcurve.utils.Assets;
import com.singlemethodgames.wordcurve.utils.Constants;
import com.singlemethodgames.wordcurve.utils.PauseMenuGroup;
import com.singlemethodgames.wordcurve.utils.challenges.ChallengeSet;
import com.singlemethodgames.wordcurve.utils.questionproviders.ChallengeQuestionProvider;
import com.singlemethodgames.wordcurve.utils.questionproviders.QuestionProvider;
import com.singlemethodgames.wordcurve.utils.questionproviders.UnlimitedQuestionProvider;

public class ContinueScreen extends BaseScreen {
    private Stage stage;
    public ContinueScreen(final WordCurveGame game, final SaveState saveState) {
        super(game);
        game.viewport.apply();

        stage = new Stage(game.viewport) {
            @Override
            public boolean keyDown(int keyCode) {
                if (keyCode == Input.Keys.BACK) {
                    goToMainMenu();
                    return true;
                }
                return false;
            }
        };

        I18NBundle bundle = game.getAssetManager().get(Assets.stringsBundle);
        TextureAtlas textureAtlas = game.getAssetManager().get(Assets.finalSelectAtlas);
        BitmapFont font48 = game.getAssetManager().get(Constants.Fonts.SIZE48, BitmapFont.class);
        BitmapFont font96 = game.getAssetManager().get(Constants.Fonts.SIZE96, BitmapFont.class);

        TextureRegion texture = textureAtlas.findRegion(saveState.getVariant().equals(Variant.CLASSIC) ? Constants.TextureRegions.CLASSIC_SMALL : Constants.TextureRegions.SWITCH_SMALL);
        Image selectedType = new Image(texture);

        String textureToLoad = Constants.TextureRegions.NO_SELECT;
        switch (saveState.getGameMode()) {
            case TIME: textureToLoad = Constants.TextureRegions.TIME_SMALL;
                break;
            case LIFE: textureToLoad = Constants.TextureRegions.LIFE_SMALL;
                break;
            case CHALLENGES:
                textureToLoad = Constants.TextureRegions.CHALLENGES_SMALL;
                break;
            case CASUAL: textureToLoad = Constants.TextureRegions.CASUAL_SMALL;
                break;
        }

        TextureRegion selectedModeTexture = textureAtlas.findRegion(textureToLoad);
        Image selectedMode = new Image(selectedModeTexture);

        Label.LabelStyle crumbStyle = new Label.LabelStyle(font48, Color.WHITE);
        Label variantLabel = new Label(bundle.get(saveState.getVariant().toString().toLowerCase() + "_name"), crumbStyle);
        variantLabel.setAlignment(Align.center);

        Label modeLabel = new Label(bundle.get(saveState.getGameMode().toString().toLowerCase() + "_name"), crumbStyle);
        modeLabel.setAlignment(Align.center);

        TextureRegionDrawable normal = new TextureRegionDrawable(textureAtlas.findRegion(Constants.TextureRegions.PLAY_GAME_BUTTON));
        Drawable pressed = normal.tint(Color.LIGHT_GRAY);

        final ImageButton playButton = new ImageButton(normal, pressed, normal);
        playButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                resumeGame(saveState);
            }
        });

        Button continueGame = PauseMenuGroup.getContinueButton(textureAtlas, bundle, font96);
        continueGame.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                resumeGame(saveState);
            }
        });


        NinePatchDrawable mainMenuDrawable = new NinePatchDrawable(textureAtlas.createPatch(Constants.TextureRegions.BUTTON_GRAY));
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle(mainMenuDrawable, mainMenuDrawable.tint(Color.GRAY), mainMenuDrawable, font48);
        textButtonStyle.fontColor = new Color(146 / 255f, 158 / 255f, 180 / 255f, 1f);

        TextButton mainMenuButton = new TextButton(bundle.get("main_menu"), textButtonStyle);
        mainMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.clearSave();
                goToMainMenu();
            }
        });

        Table table = new Table();
        table.setFillParent(true);
        table.align(Align.center | Align.top);
        Table selectedGroup = new Table();
        selectedType.setScaling(Scaling.fit);
        selectedMode.setScaling(Scaling.fit);
        selectedGroup.add(selectedType).center().width(stage.getViewport().getWorldWidth() / 2f);
        selectedGroup.add(selectedMode).center().width(stage.getViewport().getWorldWidth() / 2f);
        selectedGroup.row().padTop(20);
        selectedGroup.add(variantLabel).center();
        selectedGroup.add(modeLabel).center();
        table.add(selectedGroup).center().width(775f).padTop(75f);

        table.row();
        table.add(continueGame).size(650, 250).expandY();
        table.row().center().bottom().size(425f, 100f).padBottom(100f);
        table.add(mainMenuButton);

        stage.addActor(table);
    }

    private void resumeGame(final SaveState saveState) {
        Variant variant = saveState.getVariant();

        QuestionProvider questionProvider;
        if(saveState.getGameMode().equals(GameMode.CHALLENGES)) {
            final ObjectMap<String, ChallengeSet> challenges = game.json.fromJson(ObjectMap.class, Gdx.files.internal(Constants.JsonFiles.CHALLENGES));

            ChallengesBar.ChallengeState challengeState = (ChallengesBar.ChallengeState)saveState.getGameState();
            questionProvider = new ChallengeQuestionProvider(challengeState.getLevelName(), challengeState.getCurrentIndex(), game.random, challenges.get(variant.toString()).getLevels());
        } else {
            UnlimitedQuestionProvider.UnlimitedState unlimitedState;
            if (saveState.getGameState() instanceof LifeBar.LifeState) {
                unlimitedState = ((LifeBar.LifeState) saveState.getGameState()).getUnlimitedState();
            } else if (saveState.getGameState() instanceof TimeBar.TimeState) {
                unlimitedState = ((TimeBar.TimeState) saveState.getGameState()).getUnlimitedState();
            } else {
                unlimitedState = ((CasualBar.CasualState) saveState.getGameState()).getUnlimitedState();
            }
            questionProvider = new UnlimitedQuestionProvider(game.wordLookup, game.random, unlimitedState);
        }

        if(variant.equals(Variant.CLASSIC)) {
            game.setScreen(new Classic(game, saveState, questionProvider));
        } else {
            game.setScreen(new Switch(game, saveState, questionProvider));
        }
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(Constants.Colours.BACKGROUND_COLOUR.r, Constants.Colours.BACKGROUND_COLOUR.g, Constants.Colours.BACKGROUND_COLOUR.b, 1);
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
}
