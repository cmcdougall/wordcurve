package com.singlemethodgames.wordcurve.utils;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Created by cameron on 19/02/2018.
 */

public class WordCurvePath {
    public static Vector2[] populatePoints(WordCurveDetails wordCurveDetails) {
        // Store the final array of the points
        Array<Vector2> finalPoints = new Array<>();

        Array<Vector2> wordPoints = wordCurveDetails.getCoordinates();

        //Calculate the first control point for the Catmull Rom Spline
        //This point does not appear in the final curve
        if(wordPoints.get(0).equals(wordPoints.get(1))) {
            finalPoints.add(findControlPoint(
                    wordPoints.get(0).cpy(),
                    wordPoints.get(2).cpy()
                    )
            );
        } else {
            finalPoints.add(findControlPoint(
                    wordPoints.get(0).cpy(),
                    wordPoints.get(1).cpy()
                    )
            );
        }

        //Add the word coordinates to the array
        for(Vector2 point: wordPoints) {
            finalPoints.add(point);
        }

        // Calculate the final control point for the Catmull Rom Spline
        // This point does not appear in the final curve
        if(wordPoints.get(wordPoints.size - 1).equals(wordPoints.get(wordPoints.size - 2))) {
            finalPoints.add(findControlPoint(
                    wordPoints.get(wordPoints.size - 1).cpy(),
                    wordPoints.get(wordPoints.size - 3).cpy()
                    )
            );
        } else {
            finalPoints.add(findControlPoint(
                    wordPoints.get(wordPoints.size - 1).cpy(),
                    wordPoints.get(wordPoints.size - 2).cpy()
                    )
            );
        }

        return finalPoints.toArray(Vector2.class);
    }

    public static Vector2 findControlPoint(Vector2 a, Vector2 b) {
        Vector2 c = b.sub(a);
        return a.sub(c);
    }
}
