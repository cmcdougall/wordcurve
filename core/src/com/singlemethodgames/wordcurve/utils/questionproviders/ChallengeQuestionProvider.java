package com.singlemethodgames.wordcurve.utils.questionproviders;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.singlemethodgames.wordcurve.buttons.WordCurveButton;
import com.singlemethodgames.wordcurve.utils.Utils;
import com.singlemethodgames.wordcurve.utils.challenges.ChallengeTiers;
import com.singlemethodgames.wordcurve.utils.challenges.Level;
import com.singlemethodgames.wordcurve.utils.wordlist.Word;

import java.util.List;
import java.util.Random;

public class ChallengeQuestionProvider implements QuestionProvider {
    private int totalQuestions;
    private int currentIndex;
    private Level level;
    private String levelName;
    private ChallengeTiers challengeTiers;
    private Random random;

    public ChallengeQuestionProvider(String levelName, int currentIndex, Random random, ObjectMap<String, Level> levels) {
        this(levelName, levels.get(levelName), random, currentIndex);
    }

    public ChallengeQuestionProvider(String levelName, Level level, Random random, int currentIndex) {
        this.level = level;
        this.levelName = levelName;
        Utils.shuffleArray(random, level.getQuestions());
        totalQuestions = level.getQuestions().size;
        challengeTiers = new ChallengeTiers(totalQuestions);
        this.random = random;
        this.currentIndex = currentIndex;
    }

    @Override
    public Word nextQuestion(int TOTAL_ANSWERS, List<? extends WordCurveButton> wordCurveButtons) {
        Word nextWord = null;
        if (currentIndex < totalQuestions) {
            Array<String> next = level.getQuestions().get(currentIndex);
            int randomAnswer = Utils.random(random, 0, next.size - 1);
            String nextAnswer = next.get(randomAnswer);
            next.shuffle();
            for (int i = 0; i < TOTAL_ANSWERS; i++) {
                String choice = next.get(i);
                Word word = new Word(choice);
                final WordCurveButton wordButton = wordCurveButtons.get(i);
                if(choice.equals(nextAnswer)) {
                    wordButton.setAnswer(true);
                    nextWord = word;
                } else {
                    wordButton.setAnswer(false);
                }
                wordButton.setWord(word);
            }

            currentIndex++;
        }
        return nextWord;
    }

    @Override
    public boolean hasNextQuestion() {
        return currentIndex < totalQuestions;
    }

    public Level getLevel() {
        return level;
    }

    public String getLevelName() {
        return levelName;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public ChallengeTiers getChallengeTiers() {
        return challengeTiers;
    }

    @Override
    public int totalQuestions() {
        return totalQuestions;
    }

    @Override
    public void reset(Random random) {
        this.random = random;
        Utils.shuffleArray(random, level.getQuestions());
        currentIndex = 0;
    }
}
