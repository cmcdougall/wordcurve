package com.singlemethodgames.wordcurve.utils.tracking;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Tracker implements Serializable {
    private String variant;
    private String mode;
    private int letterStart;
    private int speedStart;
    private List<Answer> answers;

    public Tracker() {
    }

    public Tracker(String variant, String mode, int letterStart, int speedStart) {
        this.variant = variant;
        this.mode = mode;
        this.letterStart = letterStart;
        this.speedStart = speedStart;
        answers = new ArrayList<>();
    }

    public void addAnswer(Answer answer) {
        answers.add(answer);
    }

    public String getVariant() {
        return variant;
    }

    public String getMode() {
        return mode;
    }

    public int getLetterStart() {
        return letterStart;
    }

    public int getSpeedStart() {
        return speedStart;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    @Override
    public String toString() {
        return "Tracker{" +
                "variant='" + variant + '\'' +
                ", mode='" + mode + '\'' +
                ", letterStart=" + letterStart +
                ", speedStart=" + speedStart +
                ", answers=" + answers +
                '}';
    }

    public static class Answer implements Serializable {
        private List<String> options;
        private String answer;
        private String answered;
        private boolean correct;
        private float time;

        public Answer() {

        }

        public Answer(List<String> options, String answer, String answered, boolean correct, float time) {
            this.options = options;
            this.answer = answer;
            this.answered = answered;
            this.correct = correct;
            this.time = time;
        }

        public List<String> getOptions() {
            return options;
        }

        public void setOptions(List<String> options) {
            this.options = options;
        }

        public String getAnswer() {
            return answer;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }

        public String getAnswered() {
            return answered;
        }

        public void setAnswered(String answered) {
            this.answered = answered;
        }

        public boolean isCorrect() {
            return correct;
        }

        public void setCorrect(boolean correct) {
            this.correct = correct;
        }

        public float getTime() {
            return time;
        }

        public void setTime(float time) {
            this.time = time;
        }

        @Override
        public String toString() {
            return "Answer{" +
                    "options=" + options +
                    ", answer='" + answer + '\'' +
                    ", answered='" + answered + '\'' +
                    ", correct=" + correct +
                    ", time=" + time +
                    '}';
        }
    }
}
