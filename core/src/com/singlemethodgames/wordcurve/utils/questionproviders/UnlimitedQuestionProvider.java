package com.singlemethodgames.wordcurve.utils.questionproviders;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.ObjectMap;
import com.singlemethodgames.wordcurve.buttons.WordCurveButton;
import com.singlemethodgames.wordcurve.utils.Utils;
import com.singlemethodgames.wordcurve.utils.wordlist.Word;
import com.singlemethodgames.wordcurve.utils.wordlist.WordLookup;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class UnlimitedQuestionProvider implements QuestionProvider {
    private WordLookup wordLookup;
    private ObjectMap<Integer, LengthLookup> shuffledMap;
    public static final int MINIMUM_LENGTH = 3;
    public static final int MAXIMUM_LENGTH = 11;
    private int currentMinimum;
    private int currentMaximum;
    private Random random;

    public UnlimitedQuestionProvider(WordLookup wordLookup, Random random, UnlimitedState unlimitedState) {
        this.wordLookup = wordLookup;
        this.random = random;
        this.currentMinimum = unlimitedState.currentMinimum;
        this.currentMaximum = unlimitedState.currentMaximum;

        shuffledMap = new ObjectMap<>();
        ObjectMap<String, IntArray> indexMap = wordLookup.getIndexes();
        for(int key = MINIMUM_LENGTH; key <= MAXIMUM_LENGTH; key++) {
            IntArray indexes = indexMap.get(String.valueOf(key));
            int startIndex = indexes.get(0);
            int endIndex = indexes.get(1);
            IntArray list = new IntArray(false, endIndex - startIndex);
            for(int i = startIndex; i <= endIndex; i++) {
                list.add(i);
            }
            Utils.shuffleIntArray(random, list);
            LengthLookup lengthLookup = new LengthLookup(unlimitedState.indexStates.get(String.valueOf(key)), list, random);

            shuffledMap.put(key, lengthLookup);
        }
    }

    public void setLengths(final int minimumLength, final int maximumLength) {
        this.currentMinimum = minimumLength;
        this.currentMaximum = maximumLength;
    }

    @Override
    public Word nextQuestion(int TOTAL_ANSWERS, List<? extends WordCurveButton> wordCurveButtons) {
        int index = Utils.gaussianRandom(random, currentMinimum, currentMaximum);
        shuffledMap.get(index).checkIndex();
        Integer id = shuffledMap.get(index).getNextIndex();
        Word word = wordLookup.getWords().get(String.valueOf(id));
        word.getSimilarWords().shuffle();

        Array<Word> choices = new Array<>();
        choices.add(word);
        for(int i = 0; i < TOTAL_ANSWERS - 1; i++) {
            Word similarWord = wordLookup.getWords().get(String.valueOf(word.getSimilarWords().get(i)));
            choices.add(similarWord);
        }
        choices.shuffle();
        for (int i = 0; i < TOTAL_ANSWERS; i++) {
            final WordCurveButton wordButton = wordCurveButtons.get(i);
            Word choice = choices.get(i);
            wordButton.setAnswer(choice.equals(word));
            wordButton.setWord(choice);
        }

        return word;
    }

    @Override
    public boolean hasNextQuestion() {
        return true;
    }

    @Override
    public int totalQuestions() {
        return -1;
    }

    @Override
    public void reset(Random random) {

    }

    public UnlimitedState getState() {
        Map<String, Integer> indexStates = new HashMap<>();

        for(int key = MINIMUM_LENGTH; key <= MAXIMUM_LENGTH; key++) {
            int currentIndex = shuffledMap.get(key).index;
            indexStates.put(String.valueOf(key), currentIndex);
        }

        return new UnlimitedState(currentMinimum, currentMaximum, indexStates);
    }

    public static class LengthLookup {
        private int index;
        private IntArray lookup;
        private Random random;

        LengthLookup(int index, IntArray lookup, Random random) {
            this.index = index;
            this.lookup = lookup;
            this.random = random;
        }

        int getNextIndex() {
            return lookup.get(index++);
        }

        void checkIndex() {
            index = 0;
            Utils.shuffleIntArray(random, lookup);
        }
    }

    public static class UnlimitedState implements Serializable {
        private int currentMinimum;
        private int currentMaximum;
        Map<String, Integer> indexStates;

        public UnlimitedState() {
            this.currentMinimum = 3;
            this.currentMaximum = 4;

            Map<String, Integer> indexStates = new HashMap<>();
            for(int key = MINIMUM_LENGTH; key <= MAXIMUM_LENGTH; key++) {
                indexStates.put(String.valueOf(key), 0);
            }
            this.indexStates = indexStates;
        }

        public UnlimitedState(int currentMinimum, int currentMaximum, Map<String, Integer> indexStates) {
            this.currentMinimum = currentMinimum;
            this.currentMaximum = currentMaximum;
            this.indexStates = indexStates;
        }

        public int getCurrentMinimum() {
            return currentMinimum;
        }

        public void setCurrentMinimum(int currentMinimum) {
            this.currentMinimum = currentMinimum;
        }

        public int getCurrentMaximum() {
            return currentMaximum;
        }

        public void setCurrentMaximum(int currentMaximum) {
            this.currentMaximum = currentMaximum;
        }

        public Map<String, Integer> getIndexStates() {
            return indexStates;
        }

        public void setIndexStates(Map<String, Integer> indexStates) {
            this.indexStates = indexStates;
        }

        @Override
        public String toString() {
            return "UnlimitedState{" +
                    "currentMinimum=" + currentMinimum +
                    ", currentMaximum=" + currentMaximum +
                    ", indexStates=" + indexStates +
                    '}';
        }
    }
}
