package com.singlemethodgames.wordcurve.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
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
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Scaling;
import com.singlemethodgames.wordcurve.WordCurveGame;
import com.singlemethodgames.wordcurve.actors.gamebar.ChallengesBar;
import com.singlemethodgames.wordcurve.actors.gamebar.GameMode;
import com.singlemethodgames.wordcurve.screens.difficulty.ChallengeDifficultyScreen;
import com.singlemethodgames.wordcurve.screens.variants.Variant;
import com.singlemethodgames.wordcurve.utils.Assets;
import com.singlemethodgames.wordcurve.utils.Constants;
import com.singlemethodgames.wordcurve.utils.challenges.ChallengeSet;
import com.singlemethodgames.wordcurve.utils.challenges.ChallengeTiers;
import com.singlemethodgames.wordcurve.utils.challenges.Level;

public class LevelSelectScreen extends BaseScreen {
    private Stage stage;
    private ObjectMap<String, Level> levels;
    public final Variant variant;

    public LevelSelectScreen(final WordCurveGame game, final Variant variant) {
        super(game);

        Json json = new Json();
        final ObjectMap<String, ChallengeSet> challenges = json.fromJson(ObjectMap.class, Gdx.files.internal(Constants.JsonFiles.CHALLENGES));
        this.variant = variant;
        this.levels = challenges.get(variant.toString()).getLevels();
        I18NBundle myBundle = game.getAssetManager().get(Assets.stringsBundle);
        stage = new Stage(game.viewport) {
            @Override
            public boolean keyDown(int keyCode) {
                if(keyCode == Input.Keys.BACK) {
                    game.setScreen(new SelectModeMenu(game, variant));
                    return true;
                }
                return false;
            }
        };
        TextureAtlas textureAtlas = game.getAssetManager().get(Assets.levelSelectAtlas);
        BitmapFont font48 = game.getAssetManager().get(Constants.Fonts.SIZE48, BitmapFont.class);
        BitmapFont font96 = game.getAssetManager().get(Constants.Fonts.SIZE96, BitmapFont.class);

        Image selectedVariant = new Image(textureAtlas.findRegion(variant.equals(Variant.CLASSIC) ? Constants.TextureRegions.CLASSIC_SMALL : Constants.TextureRegions.SWITCH_SMALL));
        selectedVariant.setScaling(Scaling.fit);
        Image challengesImage = new Image(textureAtlas.findRegion(Constants.TextureRegions.CHALLENGES_SMALL));
        challengesImage.setScaling(Scaling.fit);

        Drawable drawable = new TextureRegionDrawable(textureAtlas.findRegion(Constants.TextureRegions.EXIT_BUTTON));
        ImageButton exitButton = new ImageButton(drawable);
        exitButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new SelectModeMenu(game, variant));
            }
        });

        Table pageTable = new Table();
        pageTable.setFillParent(true);
        pageTable.align(Align.center | Align.top);

        Label.LabelStyle crumbStyle = new Label.LabelStyle(font48, Color.WHITE);
        Label variantLabel = new Label(myBundle.get(variant.toString().toLowerCase() + "_name"), crumbStyle);
        variantLabel.setAlignment(Align.center);

        Label modeLabel = new Label(myBundle.get(GameMode.CHALLENGES.toString().toLowerCase() + "_name"), crumbStyle);
        modeLabel.setAlignment(Align.center);

        Table selectedGroup = new Table();
        selectedVariant.setScaling(Scaling.fit);
        selectedVariant.setScaling(Scaling.fit);
        selectedGroup.add(selectedVariant).center().width(stage.getViewport().getWorldWidth() / 2f);
        selectedGroup.add(challengesImage).center().width(stage.getViewport().getWorldWidth() / 2f);
        selectedGroup.row().padTop(20);
        selectedGroup.add(variantLabel).center();
        selectedGroup.add(modeLabel).center();
        pageTable.add(selectedGroup).center().width(775f).padTop(75f).padBottom(50f);
        pageTable.row();

        Table levelsTable = createLevelTable(textureAtlas, font96);
        pageTable.row();
        pageTable.add(levelsTable).center().bottom().width(900f).padBottom(100f).fill().expand();

        pageTable.row().center().bottom().size(150f).padBottom(100f);
        pageTable.add(exitButton);

        stage.addActor(pageTable);
    }

    private Table createLevelTable(TextureAtlas atlas, BitmapFont font96) {
        Table table = new Table();

        TextureRegionDrawable trophyDrawable = new TextureRegionDrawable(atlas.findRegion(Constants.TextureRegions.TROPHY));
        TextureRegionDrawable lockDrawable = new TextureRegionDrawable(atlas.findRegion(Constants.TextureRegions.LOCK));
        TextureRegionDrawable starDrawable = new TextureRegionDrawable(atlas.findRegion(Constants.TextureRegions.STAR));

        NinePatchDrawable levelButtonDrawable = new NinePatchDrawable(atlas.createPatch(Constants.TextureRegions.LEVEL_BUTTON));
        for(int i = 1; i <= 5; i++) {
            int highScore = game.highScores.getHighscoreForChallenge(variant, String.valueOf(i));
            ChallengeTiers challengeTiers = new ChallengeTiers(this.levels.get(String.valueOf(i)).getQuestions().size);
            Color highScoreColor = challengeTiers.getColourForScore(highScore).cpy();
            final int current = i;
            ClickListener clickListener = new ClickListener() {
                public void clicked(InputEvent event, float x, float y) {
                    game.setScreen(new ChallengeDifficultyScreen(game, variant, String.valueOf(current), levels.get(String.valueOf(current)), LevelSelectScreen.this));
                }
            };
            Image buttonImage = new Image(trophyDrawable);

            buttonImage.setSize(150, 150);
            buttonImage.setScaling(Scaling.fit);
            buttonImage.setColor(highScoreColor);

            NinePatchDrawable drawable = levelButtonDrawable.tint(highScoreColor);
            Drawable pressed = levelButtonDrawable.tint(highScoreColor.cpy().mul(Color.LIGHT_GRAY));
            Button button = new Button(drawable, pressed);
            button.addListener(clickListener);

            Image starImage = new Image(starDrawable.tint(highScoreColor));
            starImage.setScaling(Scaling.fit);
            starImage.setWidth(50);

            Label.LabelStyle levelStyle = new Label.LabelStyle(font96, highScoreColor);
            Label highScoreValueLabel = new Label(String.valueOf(highScore), levelStyle);
            highScoreValueLabel.setAlignment(Align.right);

            Label levelLabel = new Label(String.valueOf(i), levelStyle);
            levelLabel.setAlignment(Align.center);

            button.add(buttonImage).size(150).center().padLeft(50).padRight(50);
            button.add(levelLabel).left();
            button.add(starImage).expand().right();
            button.add(highScoreValueLabel).right().padLeft(25).padRight(50);

            table.add(button).fill().expand().pad(30);
            table.row();
        }

        return table;
    }

    @Override
    public void show() {
        ChallengesBar.unlockChallengeAchievements(game, variant, levels);
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
    public void hide() {

    }

    @Override
    public InputProcessor getInputProcessor() {
        return stage;
    }
}
