package com.singlemethodgames.wordcurve.utils.store;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.I18NBundle;
import com.singlemethodgames.wordcurve.WordCurveGame;
import com.singlemethodgames.wordcurve.utils.Constants;
import com.singlemethodgames.wordcurve.utils.Utils;

public class BuyingTable extends Table {
    public BuyingTable(final WordCurveGame game, final TextureAtlas textureAtlas, final I18NBundle bundle) {
        super();
        BitmapFont font60 = game.getAssetManager().get(Constants.Fonts.SIZE60, BitmapFont.class);

        setFillParent(true);

        Color backgroundColour = Constants.Colours.BACKGROUND_COLOUR.cpy();
        backgroundColour.a = .9f;

        TextureRegion whitePixelRegion = textureAtlas.findRegion(Constants.TextureRegions.WHITE_PIXEL);
        Utils.fixWhitePixelRegion(whitePixelRegion);

        TextureRegionDrawable backgroundDrawable = new TextureRegionDrawable(whitePixelRegion);
        background(backgroundDrawable.tint(backgroundColour));

        Label buyingLabel = new Label(bundle.get("buying_premium"), new Label.LabelStyle(font60, Color.WHITE));
        buyingLabel.setAlignment(Align.center);
        buyingLabel.setWrap(true);

        add(buyingLabel).center().expand().fill();

        setTouchable(Touchable.enabled);
    }
}
