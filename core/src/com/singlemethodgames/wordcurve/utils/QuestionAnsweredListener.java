package com.singlemethodgames.wordcurve.utils;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.singlemethodgames.wordcurve.utils.wordlist.Word;

/**
 * Created by cameron on 19/02/2018.
 */

public interface QuestionAnsweredListener {
    void correctAnswer(Actor actor, final Word word, final float x, final float y);
    void incorrectAnswer(Actor actor, final Word word, final float x, final float y);
}
