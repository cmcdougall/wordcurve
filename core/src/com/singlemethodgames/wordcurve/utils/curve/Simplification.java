package com.singlemethodgames.wordcurve.utils.curve;

import com.singlemethodgames.wordcurve.actors.WordCurveTailActor.CurvePosition;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class Simplification {
    public static CurvePosition[] simplify(CurvePosition[] points, double tolerance) {
        if (points == null || points.length <= 2) {
            return points;
        }

        double sqTolerance = tolerance * tolerance;
        points = simplifyDouglasPeucker(points, sqTolerance);

        return points;
    }

    private static CurvePosition[] simplifyDouglasPeucker(CurvePosition[] points, double sqTolerance) {

        BitSet bitSet = new BitSet(points.length);
        bitSet.set(0);
        bitSet.set(points.length - 1);

        List<Range> stack = new ArrayList<>();
        stack.add(new Range(0, points.length - 1));

        while (!stack.isEmpty()) {
            Range range = stack.remove(stack.size() - 1);

            int index = -1;
            double maxSqDist = 0f;

            // find index of point with maximum square distance from first and last point
            for (int i = range.first + 1; i < range.last; ++i) {
                double sqDist = getSquareSegmentDistance(points[i], points[range.first], points[range.last]);

                if (sqDist > maxSqDist) {
                    index = i;
                    maxSqDist = sqDist;
                }
            }

            if (maxSqDist > sqTolerance) {
                bitSet.set(index);

                stack.add(new Range(range.first, index));
                stack.add(new Range(index, range.last));
            }
        }

        List<CurvePosition> newPoints = new ArrayList<>(bitSet.cardinality());
        for (int index = bitSet.nextSetBit(0); index >= 0; index = bitSet.nextSetBit(index + 1)) {
            newPoints.add(points[index]);
        }

        CurvePosition[] newList = new CurvePosition[0];
        return newPoints.toArray(newList);
    }

    private static double getSquareSegmentDistance(CurvePosition p0, CurvePosition p1, CurvePosition p2) {
        double x0, y0, x1, y1, x2, y2, dx, dy, t;

        x1 = p1.x;
        y1 = p1.y;
        x2 = p2.x;
        y2 = p2.y;
        x0 = p0.x;
        y0 = p0.y;

        dx = x2 - x1;
        dy = y2 - y1;

        if (dx != 0.0d || dy != 0.0d) {
            t = ((x0 - x1) * dx + (y0 - y1) * dy)
                    / (dx * dx + dy * dy);

            if (t > 1.0d) {
                x1 = x2;
                y1 = y2;
            } else if (t > 0.0d) {
                x1 += dx * t;
                y1 += dy * t;
            }
        }

        dx = x0 - x1;
        dy = y0 - y1;

        return dx * dx + dy * dy;
    }

    private static class Range {
        private Range(int first, int last) {
            this.first = first;
            this.last = last;
        }

        int first;
        int last;
    }
}
