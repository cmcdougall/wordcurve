package com.singlemethodgames.wordcurve.utils.challenges;

import com.badlogic.gdx.utils.Array;

public class Level {
    private Array<Array<String>> questions;

    public Level() {
    }

    public Array<Array<String>> getQuestions() {
        return questions;
    }

    public void setQuestions(Array<Array<String>> questions) {
        this.questions = questions;
    }

    @Override
    public String toString() {
        return "Level{" +
                "questions=" + questions +
                '}';
    }
}
