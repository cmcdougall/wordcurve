package com.singlemethodgames.wordcurve.buttons;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.singlemethodgames.wordcurve.utils.wordlist.Word;

public interface WordCurveButton {
    void setAnswer(boolean isAnswer);
    boolean isAnswer();
    void setWord(Word word);
    Word getWord();
    Actor getActor();
}
