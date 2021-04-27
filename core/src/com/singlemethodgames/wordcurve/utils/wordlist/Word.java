package com.singlemethodgames.wordcurve.utils.wordlist;

import com.badlogic.gdx.utils.Array;

import java.util.Objects;

public class Word {
    private String word;
    private Array<Integer> similarWords;

    public Word() {
    }

    public Word(String word) {
        this.word = word;
        similarWords = new Array<>();
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Array<Integer> getSimilarWords() {
        return similarWords;
    }

    public void setSimilarWords(Array<Integer> similarWords) {
        this.similarWords = similarWords;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Word word1 = (Word) o;
        return Objects.equals(word, word1.word) &&
                Objects.equals(similarWords, word1.similarWords);
    }

    @Override
    public int hashCode() {
        return Objects.hash(word, similarWords);
    }

    @Override
    public String toString() {
        return "Word{" +
                "word='" + word + '\'' +
                ", similarWords=" + similarWords +
                '}';
    }
}
