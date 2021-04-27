package com.singlemethodgames.wordcurve.utils.progress;

import java.io.Serializable;

/**
 * Created by cameron on 19/02/2018.
 */

public class GameProgress implements Serializable {
    private int score;
    private int correctAnswers;
    private int incorrectAnswers;

    public GameProgress() {
        score = 0;
        correctAnswers = 0;
        incorrectAnswers = 0;
    }

    public GameProgress(int score, int correctAnswers, int incorrectAnswers) {
        this.score = score;
        this.correctAnswers = correctAnswers;
        this.incorrectAnswers = incorrectAnswers;
    }

    public void correctAnswer(int increase) {
        correctAnswers++;
        score += increase;
    }

    public void incorrectAnswer() {
        incorrectAnswers++;
    }

    public int getScore() {
        return score;
    }

    public int getMatched() {
        return correctAnswers;
    }
    public int getIncorrectAnswers() {
        return incorrectAnswers;
    }

    @Override
    public String toString() {
        return "GameProgress{" +
                "score=" + score +
                ", correctAnswers=" + correctAnswers +
                ", incorrectAnswers=" + incorrectAnswers +
                '}';
    }
}
