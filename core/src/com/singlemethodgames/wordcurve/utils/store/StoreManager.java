package com.singlemethodgames.wordcurve.utils.store;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.pay.Information;
import com.badlogic.gdx.pay.ItemAlreadyOwnedException;
import com.badlogic.gdx.pay.Offer;
import com.badlogic.gdx.pay.OfferType;
import com.badlogic.gdx.pay.PurchaseManagerConfig;
import com.badlogic.gdx.pay.PurchaseObserver;
import com.badlogic.gdx.pay.Transaction;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
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
import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.StringBuilder;
import com.singlemethodgames.wordcurve.WordCurveGame;
import com.singlemethodgames.wordcurve.actors.WordCurveGroup;
import com.singlemethodgames.wordcurve.screens.BaseScreen;
import com.singlemethodgames.wordcurve.screens.LevelSelectScreen;
import com.singlemethodgames.wordcurve.screens.difficulty.SpeedDifficulty;
import com.singlemethodgames.wordcurve.screens.difficulty.SpeedSetting;
import com.singlemethodgames.wordcurve.utils.Assets;
import com.singlemethodgames.wordcurve.utils.Constants;
import com.singlemethodgames.wordcurve.utils.QwertyKeyboard;
import com.singlemethodgames.wordcurve.utils.Utils;
import com.singlemethodgames.wordcurve.utils.preferences.GamePreferences;

import java.util.UUID;

public class StoreManager extends BaseScreen implements PurchaseObserver {
    private final String PREMIUM_SKU;

    private static final String a = "4! 5# bh56";
    private static final String b = "*#M049gu ";

    private final WordCurveGame game;
    private Stage stage;

    private BaseScreen purchaseSuccessScreen = null;
    private BaseScreen noPurchaseScreen = null;
    private Button buyButton;
    private Label buyLabel;
    private I18NBundle stringsBundle;
    private TextButton restoreButton;
    private BuyingTable buyingTable;

    private Button.ButtonStyle ownButtonStyle;

    private TextButton.TextButtonStyle disabledRestoreButtonStyle;

    private Image cartImage;
    public V v = new V();

    public StoreManager(final WordCurveGame game) {
        super(game);
        this.PREMIUM_SKU = game.platformResolver.getPremiumSKU();
        this.game = game;
        stage = new Stage(game.viewport) {
            @Override
            public boolean keyDown(int keyCode) {
                if (keyCode == Input.Keys.BACK) {
                    leaveStore();
                    return true;
                }

                return false;
            }
        };

        TextureAtlas textureAtlas = game.getAssetManager().get(Assets.mainInfoAtlas);
        stringsBundle = game.getAssetManager().get(Assets.stringsBundle);

        buyingTable = new BuyingTable(game, textureAtlas, stringsBundle);
        buyingTable.addAction(Actions.alpha(0));

        BitmapFont font48 = game.getAssetManager().get(Constants.Fonts.SIZE48, BitmapFont.class);
        BitmapFont font96 = game.getAssetManager().get(Constants.Fonts.SIZE96, BitmapFont.class);

        Label.LabelStyle labelStyle48 = new Label.LabelStyle(font48, Color.WHITE);
        Label.LabelStyle labelStyle96 = new Label.LabelStyle(font96, Color.WHITE);

        QwertyKeyboard qwertyKeyboard = new QwertyKeyboard(0,0, 1f);
        WordCurveGroup wordCurveGroup = new WordCurveGroup(game.getAssetManager().get(Assets.mainInfoAtlas), game.camera, qwertyKeyboard, new SpeedDifficulty(SpeedSetting.THREE.start, SpeedSetting.THREE.start));
        Utils.WordCurve wordCurve = Utils.createSpline(0f);

        Color ourGray = new Color(179 / 255f, 179 / 255f, 179 / 255f, 1f);
        Color ourGold = ourGray.cpy().mul(Color.GOLD);

        Table table = new Table();
        table.setFillParent(true);
//        table.setDebug(true);
        table.align(Align.center | Align.top);

        BitmapFont font168 = game.getAssetManager().get(Constants.Fonts.SIZE168, BitmapFont.class);
        Label gameLogoLabel = new Label("word   curve", new Label.LabelStyle(font168, ourGray));
        table.add(gameLogoLabel).padTop(120).center().width(900f);
        table.row();

        Label premiumLabel = new Label(stringsBundle.get("premium"), new Label.LabelStyle(font96, ourGold));
        premiumLabel.setAlignment(Align.center);
        premiumLabel.setWrap(true);
        table.add(premiumLabel).center().expandY();
        table.row();

        Label premiumDescriptionLabel = new Label(stringsBundle.get("premium_description"), labelStyle48);
        premiumDescriptionLabel.setAlignment(Align.center);
        premiumDescriptionLabel.setWrap(true);
        table.add(premiumDescriptionLabel).center().width(900f).expandY();
        table.row();

        // Buy Button goes here
        NinePatchDrawable buyButtonDrawable = new NinePatchDrawable(textureAtlas.createPatch(Constants.TextureRegions.BUTTON));
        NinePatchDrawable buyButtonDrawablePressed = buyButtonDrawable.tint(Color.LIGHT_GRAY);

        NinePatchDrawable disabledButtonDrawable = new NinePatchDrawable(textureAtlas.createPatch(Constants.TextureRegions.BUTTON_GRAY));

        Button.ButtonStyle buyButtonStyle = new Button.ButtonStyle(buyButtonDrawable, buyButtonDrawablePressed, buyButtonDrawable);
        ownButtonStyle = new Button.ButtonStyle(disabledButtonDrawable, disabledButtonDrawable, disabledButtonDrawable);

        buyButton = new Button(buyButtonStyle);
        buyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.purchaseManager.purchase(PREMIUM_SKU);
                stage.addActor(buyingTable);
                buyingTable.addAction(Actions.fadeIn(0.2f));
            }
        });

        buyLabel = new Label(stringsBundle.get("loading_text"), labelStyle96);
        buyLabel.setAlignment(Align.center);
        cartImage = new Image(textureAtlas.findRegion(Constants.TextureRegions.CART));
        cartImage.setSize(150, 150);
        cartImage.setScaling(Scaling.fit);

        buyButton.add(cartImage).size(150).expandY().left().padLeft(50).padRight(50);
        buyButton.add(buyLabel).expand().center();

        table.add(buyButton).fill().expandY().pad(80, 30, 80, 30);
        table.row();

        TextButton.TextButtonStyle restoreButtonStyle = new TextButton.TextButtonStyle(buyButtonDrawable, buyButtonDrawablePressed, buyButtonDrawable, font48);
        disabledRestoreButtonStyle  = new TextButton.TextButtonStyle(disabledButtonDrawable, disabledButtonDrawable, disabledButtonDrawable, font48);
        restoreButton = new TextButton(stringsBundle.get("restore_purchase"), restoreButtonStyle);
        restoreButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                displayError(stringsBundle.get("restoring_purchases"));
                game.purchaseManager.purchaseRestore();
            }
        });

        table.add(restoreButton).size(600, 100).center().top().expandY();

        Drawable drawable = new TextureRegionDrawable(textureAtlas.findRegion(Constants.TextureRegions.EXIT_BUTTON));
        final ImageButton exitButton = new ImageButton(drawable);
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                leaveStore();
            }
        });
        table.row().center().bottom().size(150f).padBottom(100f);
        table.add(exitButton);

        Utils.startWordCurveAction(stage, wordCurveGroup, wordCurve);

        stage.addActor(wordCurveGroup);
        stage.addActor(table);

        verifyPurchase();
        if(v.isV()) {
            disableBuyButton();
            disableRestoreButton();
        }

        PurchaseManagerConfig pmc = new PurchaseManagerConfig();
        pmc.addOffer(new Offer().setType(OfferType.ENTITLEMENT).setIdentifier(PREMIUM_SKU));
        game.purchaseManager.install(this, pmc, true);
    }

    public void updateScreens(BaseScreen purchaseSuccessScreen, BaseScreen noPurchaseScreen) {
        this.purchaseSuccessScreen = purchaseSuccessScreen;
        this.noPurchaseScreen = noPurchaseScreen;
    }

    private void leaveStore() {
        if(purchaseSuccessScreen == null && noPurchaseScreen instanceof LevelSelectScreen) {
            game.setScreen(new LevelSelectScreen(game, ((LevelSelectScreen) noPurchaseScreen).variant));
        } else if(v.isV() || noPurchaseScreen == null) {
            if(purchaseSuccessScreen instanceof PurchaseSuccessListener && v.isV()) {
                ((PurchaseSuccessListener)purchaseSuccessScreen).purchased();
            }
            game.setScreen(purchaseSuccessScreen);
        } else {
            game.setScreen(noPurchaseScreen);
        }
    }

    private void disableBuyButton() {
        buyButton.setStyle(ownButtonStyle);
        buyButton.setDisabled(true);
        buyButton.setTouchable(Touchable.disabled);
        buyLabel.setText(stringsBundle.get("purchased"));
        buyLabel.setColor(Color.LIGHT_GRAY);
        cartImage.setColor(Color.LIGHT_GRAY);
    }

    private void disableRestoreButton() {
        restoreButton.setDisabled(true);
        restoreButton.setTouchable(Touchable.disabled);
        restoreButton.setStyle(disabledRestoreButtonStyle);
        restoreButton.getLabel().setColor(Color.LIGHT_GRAY);
    }

    private void updateGuiWhenPurchaseManInstalled(String errorMessage) {
        if (game.purchaseManager.installed() && errorMessage == null) {
            setLabelToPrice();
        } else {
            buyButton.setTouchable(Touchable.disabled);
            buyLabel.setText(stringsBundle.get("unavailable"));
        }
    }

    private void setLabelToPrice() {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                if (game.purchaseManager.installed()) {
                    Information skuInformation = game.purchaseManager.getInformation(PREMIUM_SKU);
                    if (!buyButton.isDisabled() && !skuInformation.equals(Information.UNAVAILABLE)) {
                        buyLabel.setText(skuInformation.getLocalPricing());
                    }
                }
            }
        });
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(Constants.Colours.BACKGROUND_COLOUR.r, Constants.Colours.BACKGROUND_COLOUR.g, Constants.Colours.BACKGROUND_COLOUR.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.camera.update();

        game.batch.setProjectionMatrix(game.camera.combined);

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

    @Override
    public void handleInstall() {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                updateGuiWhenPurchaseManInstalled(null);
            }
        });
    }

    @Override
    public void handleInstallError(final Throwable e) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                updateGuiWhenPurchaseManInstalled(e.getMessage());
            }
        });
    }

    @Override
    public void handleRestore(final Transaction[] transactions) {
        if (transactions != null && transactions.length > 0) {
            for (Transaction t : transactions) {
                handlePurchase(t, true);
            }
        } else {
            displayError(stringsBundle.get("no_purchases"));
        }
    }

    @Override
    public void handleRestoreError(Throwable e) {
    }

    @Override
    public void handlePurchase(final Transaction transaction) {
        handlePurchase(transaction, false);
    }

    private void handlePurchase(final Transaction transaction, final boolean fromRestore) {
        removeBuyingTableFromStage();
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    if (transaction.isPurchased()) {
                        if (transaction.getIdentifier().equals(PREMIUM_SKU)) {
                            storePurchase(transaction.toString());
                            verifyPurchase();
                            disableBuyButton();
                            disableRestoreButton();
                        }
                    }
                }
            });
    }

    private void storePurchase(String t) {
        Preferences prefs = Gdx.app.getPreferences(GamePreferences.GAME_PREF_NAME);

        String id = prefs.getString("q", "");
        if(id.isEmpty()) {
            id = UUID.randomUUID().toString();
            prefs.putString("q", Base64Coder.encodeString(id));
        } else {
            id = Base64Coder.decodeString(id);
        }

        String x = construction(a + WordCurveGame.c, t.length());
        String resultOne = action(t, x);
        String encodedOne = Base64Coder.encodeString(resultOne);
        prefs.putString("t", encodedOne);

        String y = construction(b + WordCurveGame.d, t.length());
        String resultTwo = action(t, y);
        String encodedTwo = WordCurveGame.checksum(resultTwo + id);
        prefs.putString("u", encodedTwo);

        prefs.flush();
    }

    private void verifyPurchase() {
        Preferences prefs = Gdx.app.getPreferences(GamePreferences.GAME_PREF_NAME);

        String id = prefs.getString("q", "");
        if(id.isEmpty()) {
            id = UUID.randomUUID().toString();
            prefs.putString("q", Base64Coder.encodeString(id));
        } else {
            try {
                id = Base64Coder.decodeString(id);
            } catch(Exception ignore) {}
        }

        String t = prefs.getString("t", "");

        if(!t.isEmpty()) {
            String result = Base64Coder.decodeString(t);
            String x = construction(a + WordCurveGame.c, result.length());
            String o = action(result, x);

            String y = construction(b + WordCurveGame.d, o.length());
            String d = action(o, y);

            String e = WordCurveGame.checksum(d + game.deviceId);
            String f = WordCurveGame.checksum(d + id);

            String z = prefs.getString("u", "");
            if(!z.isEmpty()) {
                if(z.equals(e)) {
                    v.setV(true);
                    prefs.putString("u", f);
                } else if (z.equals(f)) {
                    v.setV(true);
                } else {
                    prefs.putString("t", "");
                    prefs.putString("u", "");
                    prefs.putString("q", "");
                }
            }
        }

        prefs.flush();
    }

    @Override
    public void handlePurchaseError(Throwable e) {
        removeBuyingTableFromStage();

        if (e instanceof ItemAlreadyOwnedException) {
            displayError(stringsBundle.get("already_own"));
        } else {
            displayError(stringsBundle.get("purchase_error"));
        }
    }

    @Override
    public void handlePurchaseCanceled() {
        removeBuyingTableFromStage();
    }

    private void displayError(final String message) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                game.notifyUser(message);
            }
        });
    }

    private void removeBuyingTableFromStage() {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                buyingTable.clearActions();
                buyingTable.addAction(
                        Actions.sequence(
                                Actions.fadeOut(0.2f),
                                Actions.run(new Runnable() {
                                    @Override
                                    public void run() {
                                        buyingTable.remove();
                                    }
                                })
                        )
                );
            }
        });
    }

    private static String construction(String s, int length) {
        int t = length / s.length();
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < t; i++) {
            sb.append(s);
        }

        int v = length % s.length();
        String sub = s.substring(0, v);
        sb.append(sub);

        return sb.toString();
    }

    private static String action(String one, String two) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < one.length(); i++) {
            int c = one.charAt(i) ^ two.charAt(i);
            sb.append((char)c);
        }

        return sb.toString();
    }

    public static class V {
        private boolean v = false;

        public boolean isV() {
            return v;
        }

        void setV(boolean v) {
            this.v = v;
        }
    }

    public interface PurchaseSuccessListener {
        void purchased();
    }
}
