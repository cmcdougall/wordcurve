package com.singlemethodgames.wordcurve.utils;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.BSpline;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Path;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.singlemethodgames.wordcurve.WordCurveGame;
import com.singlemethodgames.wordcurve.actors.WordCurveGroup;
import com.singlemethodgames.wordcurve.actors.gamebar.GameMode;
import com.singlemethodgames.wordcurve.screens.variants.Variant;

import java.util.Random;

public class Utils {
    public static WordCurve createSpline(float yDiff) {
        // 7 points
        // 155 width each
        // 930 total
        // 1080 - 930 = 150 / 2 = 75
        Vector2 startPoint = new Vector2(35, 1700 - yDiff);
        Vector2 secondPoint = new Vector2(135, 1850 - yDiff);
        Vector2 secondLastPoint = new Vector2(945, 1550 - yDiff);
        Vector2 endPoint = new Vector2(1045, 1700 - yDiff);

        Vector2[] controlPoints = new Vector2[]{
                WordCurvePath.findControlPoint(startPoint.cpy(),secondPoint.cpy()),
                startPoint,
                secondPoint,
                new Vector2(435, 1850 - yDiff),
                new Vector2(540, 1700 - yDiff),
                new Vector2(640, 1550 - yDiff),
                secondLastPoint,
                endPoint,
                WordCurvePath.findControlPoint(endPoint.cpy(),secondLastPoint.cpy())
        };

        final Path<Vector2> spline = new BSpline<>(controlPoints, 3, false);
        final Array<Vector2> curvePoints = new Array<>();
        for(int i = 1; i < controlPoints.length - 1; i++) {
            curvePoints.add(controlPoints[i]);
        }

        return new WordCurve(spline, curvePoints);
    }

    public static void startWordCurveAction(Stage stage, final WordCurveGroup wordCurveGroup, final WordCurve wordCurve) {
        stage.addAction(
                Actions.forever(
                        Actions.parallel(
                                Actions.run(new Runnable() {
                                    @Override
                                    public void run() {
                                        wordCurveGroup.startWordCurveFromSpline(wordCurve.path, new WordCurveDetails(wordCurve.curvePoints, 7), 3f);
                                    }
                                }),
                                Actions.delay(8f)
                        )
                )
        );
    }

    public static class WordCurve {
        public Path<Vector2> path;
        public Array<Vector2> curvePoints;

        public WordCurve(Path<Vector2> path, Array<Vector2> curvePoints) {
            this.path = path;
            this.curvePoints = curvePoints;
        }
    }

    public static Array<Character> randomAlphabetList() {
        Array<Character> characterList = new Array<>();

        for (char character = 'a'; character <= 'z'; character++) {
            characterList.add(character);
        }

        characterList.shuffle();
        return characterList;
    }

    public static void fixWhitePixelRegion(TextureRegion whitePixelRegion) {
        float u = whitePixelRegion.getU();
        float u2 = whitePixelRegion.getU2();
        float v = whitePixelRegion.getV();
        float v2 = whitePixelRegion.getV2();
        float uDiff = (u2 - u) * 0.25f;
        float vDiff = (v2 - v) * 0.25f;
        whitePixelRegion.setRegion(u + uDiff, v + vDiff, u2 - uDiff, v2 - vDiff);
    }

    public static int random (Random random, int range) {
        return random.nextInt(range + 1);
    }

    public static int random (Random random, int start, int end) {
        return start + random.nextInt(end - start + 1);
    }

    public static int gaussianRandom(Random random, int start, int end) {
        double nextGaussian = random.nextGaussian() * 2;
        double next = nextGaussian + MathUtils.floor((start + end) / 2f);

        if(next < start) {
            next = start;
        }
        if (next > end) {
            next = end;
        }

        return (int)next;
    }

    public static void shuffleIntArray(Random random, IntArray intArray) {
        int[] items = intArray.items;
        for (int i = intArray.size - 1; i >= 0; i--) {
            int ii = random(random, i);
            int temp = items[i];
            items[i] = items[ii];
            items[ii] = temp;
        }
    }

    public static <T> void shuffleArray(Random random, Array<T> array) {
        T[] items = array.items;
        for (int i = array.size - 1; i >= 0; i--) {
            int ii = random(random, i);
            T temp = items[i];
            items[i] = items[ii];
            items[ii] = temp;
        }
    }

    public static String getLeaderboardCode(WordCurveGame game, Variant variant, GameMode gameMode) {
        String leaderboardCode = "";
        if(variant.equals(Variant.CLASSIC)) {
            if(gameMode.equals(GameMode.LIFE)) {
                leaderboardCode = game.platformResolver.getClassicLifeLeaderboardCode();
            } else if(gameMode.equals(GameMode.TIME)) {
                leaderboardCode = game.platformResolver.getClassicTimeLeaderboardCode();
            } else if(gameMode.equals(GameMode.CASUAL)) {
                leaderboardCode = game.platformResolver.getClassicCasualLeaderboardCode();
            }
        } else if (variant.equals(Variant.SWITCH)) {
            if(gameMode.equals(GameMode.LIFE)) {
                leaderboardCode = game.platformResolver.getSwitchLifeLeaderboardCode();
            } else if(gameMode.equals(GameMode.TIME)) {
                leaderboardCode = game.platformResolver.getSwitchTimeLeaderboardCode();
            } else if(gameMode.equals(GameMode.CASUAL)) {
                leaderboardCode = game.platformResolver.getSwitchCasualLeaderboardCode();
            }
        }

        return leaderboardCode;
    }

    public static boolean showLeaderboard(WordCurveGame game, Variant variant, GameMode gameMode) {
        String leaderboardCode = getLeaderboardCode(game, variant, gameMode);
        if (game.gameServices.isSignedIn() && leaderboardCode != null && !leaderboardCode.isEmpty()) {
            game.gameServices.showLeaderboard(leaderboardCode);
            return true;
        }

        return false;
    }
}
