package com.singlemethodgames.wordcurve.actors;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.Path;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.singlemethodgames.wordcurve.actions.RemoveWordCurveAction;
import com.singlemethodgames.wordcurve.actions.WordCurveAction;
import com.singlemethodgames.wordcurve.groups.ProgressListener;
import com.singlemethodgames.wordcurve.screens.difficulty.SpeedDifficulty;
import com.singlemethodgames.wordcurve.utils.Constants;
import com.singlemethodgames.wordcurve.utils.QwertyKeyboard;
import com.singlemethodgames.wordcurve.utils.WordCurveActions;
import com.singlemethodgames.wordcurve.utils.WordCurveDetails;
import com.singlemethodgames.wordcurve.utils.WordCurvePath;
import com.singlemethodgames.wordcurve.utils.wordlist.Word;

import java.util.List;

/**
 * Created by cameron on 19/02/2018.
 */

public class WordCurveGroup extends Group implements WordCurveActorAction {
    private WordCurveTailActor wordWaveTailActor;
    private WordCurvePointActor wordWavePointActor;
    private QwertyKeyboard qwertyKeyboard;
    private SpeedDifficulty speedDifficulty;
    private ProgressListener progressListener;

    public WordCurveGroup(final TextureAtlas textureAtlas, final OrthographicCamera camera, final QwertyKeyboard qwertyKeyboard, SpeedDifficulty speedDifficulty) {
        this(textureAtlas, camera, qwertyKeyboard, speedDifficulty, null);
    }

    public WordCurveGroup(final TextureAtlas textureAtlas, final OrthographicCamera camera, final QwertyKeyboard qwertyKeyboard, SpeedDifficulty speedDifficulty, ProgressListener progressListener) {
        wordWaveTailActor = new WordCurveTailActor(textureAtlas, camera, qwertyKeyboard.getScale());
        wordWaveTailActor.setTouchable(Touchable.disabled);
        this.progressListener = progressListener;
        addActor(wordWaveTailActor);

        wordWavePointActor = new WordCurvePointActor(textureAtlas.findRegion(Constants.TextureRegions.POINT), progressListener);
        wordWavePointActor.setTouchable(Touchable.disabled);
        addActor(wordWavePointActor);
        this.qwertyKeyboard = qwertyKeyboard;
        this.speedDifficulty = speedDifficulty;
    }

    @Override
    public void clearActions() {
        wordWavePointActor.clearActions();
        wordWaveTailActor.clearActions();

        super.clearActions();
    }

    public void showWordCurve() {
        wordWaveTailActor.showWordWave();
        wordWavePointActor.reset();
    }

    public void fadeWordCurveOut(float duration) {
        wordWaveTailActor.fadeWordCurveOut(duration);
    }

    public void performWordCurveAction(Word word, boolean removeTail) {
        WordCurveDetails wordCurveDetails = WordCurveDetails.getWordWaveDetails(word, qwertyKeyboard);
        performWordCurveAction(wordCurveDetails, null, null, removeTail);
    }

    public void performWordCurveAction(Word word, boolean removeTail, float delay) {
        WordCurveDetails wordCurveDetails = WordCurveDetails.getWordWaveDetails(word, qwertyKeyboard);
        performWordCurveAction(wordCurveDetails, null, null, removeTail, delay);
    }

    public void performWordCurveAction(Word word, List<RunnableAction> homingOutActions, List<RunnableAction> tailFinishedActions, boolean removeTail) {
        WordCurveDetails wordCurveDetails = WordCurveDetails.getWordWaveDetails(word, qwertyKeyboard);
        performWordCurveAction(wordCurveDetails, homingOutActions, tailFinishedActions, removeTail);
    }

    private void performWordCurveAction(WordCurveDetails wordCurveDetails, List<RunnableAction> homingOutActions, List<RunnableAction> finalActions, boolean removeTail) {
        performWordCurveAction(wordCurveDetails, homingOutActions, finalActions, removeTail, 1f);
    }

    private void performWordCurveAction(WordCurveDetails wordCurveDetails, List<RunnableAction> homingOutActions, List<RunnableAction> finalActions, boolean removeTail, float wait) {
        Vector2[] catmullRomSplinePoints = WordCurvePath.populatePoints(wordCurveDetails);
        final Path<Vector2> spline = new CatmullRomSpline<>(catmullRomSplinePoints, false);

        if(progressListener != null) {
            setProgressPercentages(wordCurveDetails.getWordLength());
        }

        SequenceAction pointSequence = createWordCurveSequence(spline, wordCurveDetails, homingOutActions, finalActions, removeTail, wait);

        clearActions();
        reset();

        wordWavePointActor.addAction(pointSequence);
    }

    public void startWordCurveFromSpline(final Path<Vector2> spine, WordCurveDetails wordCurveDetails, float delay) {
        SequenceAction pointSequence = createWordCurveSequence(spine, wordCurveDetails, null, null, true, delay);

        clearActions();
        reset();

        wordWavePointActor.addAction(pointSequence);
    }

    private SequenceAction createWordCurveSequence(final Path<Vector2> spline, final WordCurveDetails wordCurveDetails, final List<RunnableAction> homingOutActions, List<RunnableAction> tailFinishedActions, boolean removeTail, float wait) {
        this.wordWaveTailActor.setCurve(spline);
        this.wordWavePointActor.setCurve(spline);

        final WordCurveAction wordCurveAction = new WordCurveAction(wordCurveDetails.getWordLength(), this.speedDifficulty.getSpeed());
        final RemoveWordCurveAction removeWordCurveAction = new RemoveWordCurveAction(wordCurveDetails.getWordLength(), this.speedDifficulty.getSpeed());

        /*
        Sequence Order!
            Reset point and trail
            Move point to start position
            Home in
            Perform word wave on both trail and point
            Begin remove trail and home out for point (run any code depending on game mode)
            Wait for trail to be completely gone!
         */
        SequenceAction wordCurveSequence = new SequenceAction();
        wordCurveSequence.addAction(Actions.run(new Runnable() {
            @Override
            public void run() {
                wordWavePointActor.reset();
                wordWaveTailActor.reset();
                wordCurveAction.restart();
                removeWordCurveAction.restart();
            }
        }));

        wordCurveSequence.addAction(Actions.moveTo(wordCurveDetails.getStartX(), wordCurveDetails.getStartY()));
        wordCurveSequence.addAction(Actions.parallel(
                WordCurveActions.homingInAction(),
                WordCurveActions.fullAlphaAction()
        ));
        wordCurveSequence.addAction(
                Actions.parallel(Actions.run(new Runnable() {
                            @Override
                            public void run() {
                                addAction(wordCurveAction);
                            }
                        }),
                        Actions.delay(wordCurveAction.getDuration())
                )
        );

        ParallelAction homingOutParallel = new ParallelAction();
        homingOutParallel.addAction(WordCurveActions.homingOutAction());
        homingOutParallel.addAction(WordCurveActions.zeroAlphaAction());
        if (homingOutActions != null) {
            for (RunnableAction runnableAction : homingOutActions) {
                homingOutParallel.addAction(runnableAction);
            }
        }
        wordCurveSequence.addAction(homingOutParallel);

        if (removeTail) {
            wordCurveSequence.addAction(Actions.delay(wait));
            wordCurveSequence.addAction(
                    Actions.parallel(Actions.run(new Runnable() {
                                @Override
                                public void run() {
                                    addAction(removeWordCurveAction);
                                }
                            }),
                            Actions.delay(removeWordCurveAction.getDuration() + 0.5f)
                    )
            );

            if (tailFinishedActions != null) {
                final ParallelAction tailFinishedRunnables = new ParallelAction();
                for (RunnableAction runnableAction : tailFinishedActions) {
                    tailFinishedRunnables.addAction(runnableAction);
                }
                wordCurveSequence.addAction(tailFinishedRunnables);
            }
        }
        return wordCurveSequence;
    }

    @Override
    public void progress(float percent, float duration) {
        wordWavePointActor.progress(percent, duration);
        wordWaveTailActor.progress(percent, duration);
    }

    @Override
    public void reset() {
        wordWavePointActor.reset();
        wordWaveTailActor.reset();
    }

    public void setStart(float start) {
        wordWaveTailActor.setStart(start);
    }

    public void hideWordCurve() {
        wordWavePointActor.setVisible(false);
        wordWaveTailActor.setVisible(false);
    }

    public void displayWordCurve() {
        wordWavePointActor.setVisible(true);
        wordWaveTailActor.setVisible(true);
    }

    public void parentPositionChanged(float x, float y) {
        wordWaveTailActor.parentPositionChanged(x, y);
    }

    public float getSpeed() {
        return speedDifficulty.getSpeed();
    }

    private void setProgressPercentages(float length) {
        float percentIncrease = 1f / (length - 1);
        float[] processPoints = new float[(int)length];

        for(int i = 0; i < length; i++) {
            processPoints[i] = (float)i * percentIncrease;
        }

        if(progressListener != null) {
            progressListener.setPointPercentages(processPoints);
        }
    }
}
