package com.singlemethodgames.wordcurve.utils.questionproviders;

import com.singlemethodgames.wordcurve.buttons.WordCurveButton;
import com.singlemethodgames.wordcurve.utils.wordlist.Word;

import java.util.List;
import java.util.Random;

public interface QuestionProvider {
    Word nextQuestion(final int TOTAL_ANSWERS, List<? extends WordCurveButton> wordCurveButtons);
    boolean hasNextQuestion();
    int totalQuestions();
    void reset(Random random);
}
