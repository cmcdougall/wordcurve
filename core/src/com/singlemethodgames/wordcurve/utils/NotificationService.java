package com.singlemethodgames.wordcurve.utils;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.singlemethodgames.wordcurve.WordCurveGame;
import com.singlemethodgames.wordcurve.screens.BaseScreen;

public class NotificationService {
    private final WordCurveGame game;
    private NotificationTable notificationTable;

    public NotificationService(final WordCurveGame game) {
        this.game = game;

        notificationTable = new NotificationTable(game);
        notificationTable.setPosition(-1000, -1000);
    }

    public void displayNotificationWithMessage(String message) {
        Screen screen = this.game.getScreen();
        if(screen instanceof BaseScreen) {
            InputProcessor inputProcessor = ((BaseScreen)screen).getInputProcessor();
            if(inputProcessor instanceof Stage) {
                final Stage stage = (Stage)inputProcessor;
                notificationTable.setLabelMessage(message);
                notificationTable.pack();
                notificationTable.clearActions();
                notificationTable.addAction(
                        Actions.sequence(
                                Actions.moveTo((stage.getViewport().getWorldWidth() - notificationTable.getWidth()) / 2f, stage.getViewport().getWorldHeight() + notificationTable.getHeight()),
                                Actions.parallel(
                                        Actions.fadeIn(0.2f),
                                        Actions.moveTo((stage.getViewport().getWorldWidth() - notificationTable.getWidth()) / 2f, stage.getViewport().getWorldHeight() - notificationTable.getHeight() - 30f, 0.2f)
                                ),
                                Actions.delay(3f, Actions.run(new Runnable() {
                                    @Override
                                    public void run() {
                                        removeNotificationFromView();
                                    }
                                }))
                        )
                );
                stage.addActor(notificationTable);
                notificationTable.clearListeners();
                notificationTable.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        removeNotificationFromView();
                    }
                });
            }
        }
    }

    private void removeNotificationFromView() {
        notificationTable.clearActions();
        notificationTable.addAction(
                Actions.sequence(
                        Actions.parallel(
                                Actions.moveBy(0,  notificationTable.getHeight() + 30f, 0.2f),
                                Actions.fadeOut(0.2f)
                        ),
                        Actions.run(new Runnable() {
                            @Override
                            public void run() {
                                notificationTable.remove();
                            }
                        })
                )
        );
    }

    public NotificationTable getNotificationTable() {
        return notificationTable;
    }

    public static class NotificationTable extends Table {
        private Label messageLabel;
        private Image removeImage;
        public NotificationTable(final WordCurveGame game) {
            TextureAtlas textureAtlas = game.getAssetManager().get(Assets.notificationAtlas);
            BitmapFont font48 = game.getAssetManager().get(Constants.Fonts.SIZE48, BitmapFont.class);

            Color mainColour = new Color(146 / 255f, 158 / 255f, 180 / 255f, 1f);
            Color secondColour = new Color(Constants.Colours.SPLASH_BACKGROUND_COLOUR, Constants.Colours.SPLASH_BACKGROUND_COLOUR, Constants.Colours.SPLASH_BACKGROUND_COLOUR, 1);

            messageLabel = new Label("", new Label.LabelStyle(font48, secondColour));
            messageLabel.setAlignment(Align.center);
            messageLabel.setWrap(true);
            removeImage = new Image(textureAtlas.findRegion(Constants.TextureRegions.REMOVE_ICON));
            removeImage.setColor(secondColour);
            removeImage.setSize(50, 50);
            removeImage.setOrigin(Align.center);

            NinePatchDrawable tableBackground = new NinePatchDrawable(textureAtlas.createPatch(Constants.TextureRegions.KEYBOARD_BASE));
            background(tableBackground.tint(mainColour));
            add(messageLabel).pad(20f).width(800);
            add(removeImage).size(50).center().padRight(20f).padLeft(20f);
        }

        public void setLabelMessage(String message) {
            messageLabel.setText(message);
            pack();
        }
    }
}
