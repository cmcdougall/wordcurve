package com.singlemethodgames.wordcurve.buttons;

import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;

public class VariantButton extends Button {
    public VariantButton(final Label variantName, final Label variantDescription, Drawable imageUp, Drawable imageDown, Image variantIcon) {
        super(new Button.ButtonStyle(imageUp, imageDown, imageUp));

        variantName.setAlignment(Align.center);
        variantDescription.setAlignment(Align.center);
        variantDescription.setWrap(true);

        variantName.getGlyphLayout().setText(variantName.getStyle().font, variantName.getText());
        float height = variantName.getGlyphLayout().height + 30;
        float width = (height / variantIcon.getPrefHeight()) * variantIcon.getPrefWidth();

        Table table = new Table();

        table.add(variantIcon).right().size(width, height);
        table.add(variantName).left().padLeft(20);
        table.row();
        table.add(variantDescription).center().colspan(2).width(750).padTop(20);

        add(table).fill().expand().center();
    }
}
