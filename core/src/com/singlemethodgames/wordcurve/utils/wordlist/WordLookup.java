package com.singlemethodgames.wordcurve.utils.wordlist;

import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.ObjectMap;

public class WordLookup {
    private ObjectMap<String, IntArray> indexes;
    private ObjectMap<String, Word> words;

    public WordLookup() {
        indexes = new ObjectMap<>();
        words = new ObjectMap<>();
    }

    public ObjectMap<String, IntArray> getIndexes() {
        return indexes;
    }

    public void setIndexes(ObjectMap<String, IntArray> indexes) {
        this.indexes = indexes;
    }

    public ObjectMap<String, Word> getWords() {
        return words;
    }

    public void setWords(ObjectMap<String, Word> words) {
        this.words = words;
    }

    @Override
    public String toString() {
        return "WordLookup{" +
                "indexes=" + indexes +
                ", words=" + words +
                '}';
    }
}
