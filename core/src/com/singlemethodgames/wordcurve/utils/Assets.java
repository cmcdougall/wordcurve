package com.singlemethodgames.wordcurve.utils;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.I18NBundle;

/**
 * Created by cameron on 19/02/2018.
 */

public class Assets implements Disposable {
    public AssetManager manager = new AssetManager();

    public static final AssetDescriptor<TextureAtlas> ingameAtlas =
            new AssetDescriptor<>("in-game.atlas", TextureAtlas.class);

    public static final AssetDescriptor<TextureAtlas> mainInfoAtlas =
            new AssetDescriptor<>("main-info.atlas", TextureAtlas.class);

    public static final AssetDescriptor<TextureAtlas> modeAtlas =
            new AssetDescriptor<>("mode.atlas", TextureAtlas.class);

    public static final AssetDescriptor<TextureAtlas> finalSelectAtlas =
            new AssetDescriptor<>("finalselect.atlas", TextureAtlas.class);

    public static final AssetDescriptor<TextureAtlas> settingsAtlas =
            new AssetDescriptor<>("settings.atlas", TextureAtlas.class);

    public static final AssetDescriptor<TextureAtlas> levelSelectAtlas =
            new AssetDescriptor<>("level-select.atlas", TextureAtlas.class);

    public static final AssetDescriptor<TextureAtlas> notificationAtlas =
            new AssetDescriptor<>("notification.atlas", TextureAtlas.class);

    public static final AssetDescriptor<I18NBundle> stringsBundle =
            new AssetDescriptor<>("i18n/strings", I18NBundle.class);

    public void load() {
        manager.load(ingameAtlas);
        manager.load(mainInfoAtlas);
        manager.load(modeAtlas);
        manager.load(finalSelectAtlas);
        manager.load(settingsAtlas);
        manager.load(levelSelectAtlas);
        manager.load(notificationAtlas);

        manager.load(stringsBundle);

        // Now to load the Fonts we'll be using
        FileHandleResolver resolver = new InternalFileHandleResolver();
        manager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        manager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));

        manager.load(Constants.Fonts.SIZE34, BitmapFont.class, createFont(34));
        manager.load(Constants.Fonts.SIZE48, BitmapFont.class, createFont(48));
        manager.load(Constants.Fonts.SIZE60, BitmapFont.class, createFont(60));
        manager.load(Constants.Fonts.SIZE72, BitmapFont.class, createFont(72));
        manager.load(Constants.Fonts.SIZE96, BitmapFont.class, createFont(96));
        manager.load(Constants.Fonts.SIZE168, BitmapFont.class, createFont(168));
    }

    private FreetypeFontLoader.FreeTypeFontLoaderParameter createFont(final int size) {
        FreetypeFontLoader.FreeTypeFontLoaderParameter font = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        font.fontFileName = Constants.Fonts.ROBOTO_FONT;
        font.fontParameters.kerning = true;
        font.fontParameters.minFilter = Texture.TextureFilter.Linear;
        font.fontParameters.magFilter = Texture.TextureFilter.Linear;
        font.fontParameters.size = size;

        return font;
    }

    @Override
    public void dispose() {
        manager.dispose();
    }
}

