package com.singlemethodgames.wordcurve.buttons;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class UIBinaryButton extends TextButton {

    public UIBinaryButton(final BinaryListener binaryListener, String optionText, final String onText, final String offText, final TextButtonStyle checkedStyle, final TextButtonStyle uncheckedStyle, boolean checked) {
        super(optionText, checked ? checkedStyle : uncheckedStyle);
        super.setChecked(checked);

        final Label.LabelStyle statusOnStyle = new Label.LabelStyle(checkedStyle.font, checkedStyle.fontColor);
        final Label.LabelStyle statusOffStyle = new Label.LabelStyle(uncheckedStyle.font, uncheckedStyle.fontColor);
        final Label statusLabel = new Label(checked ? onText : offText, checked ? statusOnStyle : statusOffStyle);

        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(isChecked()) {
                    setStyle(checkedStyle);
                    statusLabel.setText(onText);
                    statusLabel.setStyle(statusOnStyle);

                    if(binaryListener != null) {
                        binaryListener.on();
                    }
                } else {
                    setStyle(uncheckedStyle);
                    statusLabel.setText(offText);
                    statusLabel.setStyle(statusOffStyle);
                    if(binaryListener != null) {
                        binaryListener.off();
                    }
                }
            }
        });

        clearChildren();
        add(getLabel()).padLeft(30).left();
        add(statusLabel).padRight(30).right().expandX();
    }

    public interface BinaryListener {
        void on();
        void off();
    }
}
