package com.singlemethodgames.wordcurve.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer20;
import com.badlogic.gdx.math.Path;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.singlemethodgames.wordcurve.utils.Constants;
import com.singlemethodgames.wordcurve.utils.curve.Simplification;

/**
 * Created by cameron on 19/02/2018.
 */

public class WordCurveTailActor extends Actor implements WordCurveActorAction {
    private float start = 0;
    private float current = 0;

    private TextureRegion trailDropOff;

    private float parentX = 0;
    private float parentY = 0;

    private TextureRegion tip;
    private TextureRegion halfTip;
    private Vector2 angle = new Vector2();
    private Vector2 startAngle = new Vector2();
    private float xPoint;
    private float yPoint;
    private float percent;
    private ImmediateModeRenderer20 gl20;

    private Color overrideColor;

    private OrthographicCamera camera;

    private CurvePosition[] wordCurveCoords;
    private Color endColour = Color.CYAN;
    private CurvePosition p = new CurvePosition(0f);
    private CurvePosition p2 = new CurvePosition(0f);
    private Vector2 perp = new Vector2();
    private Vector2 perpPoint = new Vector2();
    private Vector2 endVector = new Vector2();
    private Vector2 sideAMidPoint = new Vector2();
    private Vector2 sideBMidPoint = new Vector2();

    private int startIndex = 0;
    private int currentIndex = 0;

    private float thick;

    private Path<Vector2> curve = null;

    public WordCurveTailActor(TextureAtlas textureAtlas, final OrthographicCamera camera, float scale) {
        thick = 25f * scale;
        wordCurveCoords = new CurvePosition[0];
        tip = textureAtlas.findRegion(Constants.TextureRegions.HALF_TIP);
        halfTip = textureAtlas.findRegion(Constants.TextureRegions.HALF_TIP);

        trailDropOff = textureAtlas.findRegion(Constants.TextureRegions.GRADIENT);
        trailDropOff.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        gl20 = new ImmediateModeRenderer20(false, true, 1);
        this.camera = camera;

        overrideColor = null;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        startIndex = (int) (this.start * wordCurveCoords.length);
        currentIndex = (int) (this.current * wordCurveCoords.length);
        percent = (float) startIndex / (float) wordCurveCoords.length;
        float endPercent = (float) currentIndex / (float) wordCurveCoords.length;

        // Only draw curve if there are two points available!
        if (percent != endPercent && wordCurveCoords.length > 2) {
            Color color = batch.getColor();
            batch.end();
            batch.begin();
            int index = 0;

            while(index < wordCurveCoords.length && wordCurveCoords[index].percent < percent) {
                index++;
            }

            batch.setColor(wordCurveCoords[index].color);
            xPoint = (wordCurveCoords[index].sideA.x + wordCurveCoords[index].sideB.x) / 2f;
            yPoint = (wordCurveCoords[index].sideA.y + wordCurveCoords[index].sideB.y) / 2f;

            this.curve.derivativeAt(startAngle, percent);
            batch.draw(
                    tip,
                    xPoint - (thick / 2f), yPoint - (thick /2f),
                    (thick / 2f), (thick / 2f),
                    thick, thick,
                    1f, 1f,
                    startAngle.angle() - 180f);

            batch.end();
            trailDropOff.getTexture().bind();
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

            gl20.begin(this.camera.combined, GL20.GL_TRIANGLE_STRIP);

            if(index > 0) {
                findMidPoint(percent, wordCurveCoords[index - 1], wordCurveCoords[index]);

                gl20.color(wordCurveCoords[index].color);
                gl20.texCoord(trailDropOff.getU(), (trailDropOff.getV() + trailDropOff.getV2()) / 2);
                gl20.vertex(parentX + sideAMidPoint.x, parentY + sideAMidPoint.y, 0f);

                gl20.color(wordCurveCoords[index].color);
                gl20.texCoord(trailDropOff.getU2(), (trailDropOff.getV() + trailDropOff.getV2()) / 2);
                gl20.vertex(parentX + sideBMidPoint.x, parentY + sideBMidPoint.y, 0f);
            }

            while(wordCurveCoords[index].percent < endPercent) {
                gl20.color(wordCurveCoords[index].color);
                gl20.texCoord(trailDropOff.getU(), (trailDropOff.getV() + trailDropOff.getV2()) / 2);
                gl20.vertex(parentX + wordCurveCoords[index].sideA.x, parentY + wordCurveCoords[index].sideA.y, 0f);

                gl20.color(wordCurveCoords[index].color);
                gl20.texCoord(trailDropOff.getU2(), (trailDropOff.getV() + trailDropOff.getV2()) / 2);
                gl20.vertex(parentX + wordCurveCoords[index].sideB.x, parentY + wordCurveCoords[index].sideB.y, 0f);

                index++;
            }

            findMidPoint(endPercent, wordCurveCoords[index - 1], wordCurveCoords[index]);

            gl20.color(wordCurveCoords[index].color);
            gl20.texCoord(trailDropOff.getU(), (trailDropOff.getV() + trailDropOff.getV2()) / 2);
            gl20.vertex(parentX + sideAMidPoint.x, parentY + sideAMidPoint.y, 0f);

            gl20.color(wordCurveCoords[index].color);
            gl20.texCoord(trailDropOff.getU2(), (trailDropOff.getV() + trailDropOff.getV2()) / 2);
            gl20.vertex(parentX + sideBMidPoint.x, parentY + sideBMidPoint.y, 0f);

            gl20.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);

            batch.begin();

            if(current == 1f) {
                batch.setColor(endColour);
                batch.draw(
                        halfTip,
                        endVector.x - (thick /2f), endVector.y - (thick /2f),
                        (thick / 2f), (thick / 2f),
                        thick, thick,
                        1f, 1f,
                        angle.angle());
            }

            batch.setColor(color);
        }
    }

    private void findMidPoint(final float currentPercent, final CurvePosition p1, final CurvePosition p2) {
        float m = (currentPercent - p1.percent) / (p2.percent - p1.percent);

        float x = (p2.sideA.x * m) + (p1.sideA.x * (1-m));
        float y = (p2.sideA.y * m) + (p1.sideA.y * (1-m));

        sideAMidPoint.set(x, y);

        x = (p2.sideB.x * m) + (p1.sideB.x * (1-m));
        y = (p2.sideB.y * m) + (p1.sideB.y * (1-m));

        sideBMidPoint.set(x, y);
    }

    private Color getTailColor(float percent) {
        if (overrideColor != null) {
            return new Color(this.overrideColor.r, this.overrideColor.g, this.overrideColor.b, getColor().a);
        }

        return new Color(1 - percent, percent, 1f, getColor().a);
    }

    public void setCurve(Path<Vector2> curve) {
        this.curve = curve;
        int approxPoints = (int) curve.approxLength(100);

        CurvePosition[] before = new CurvePosition[approxPoints + 2];
        float increase = 1f / approxPoints;

        for(int i = 0; i <= approxPoints + 1; i++) {
            float percent = i*increase;
            CurvePosition pointA = new CurvePosition(percent);
            curve.valueAt(pointA, percent);

            before[i] = pointA;
            before[i].color = getTailColor(percent);
        }

        // Set the end Vector to the end of the Curve
        curve.valueAt(endVector, 1f);
        curve.derivativeAt(angle, 1f);
        wordCurveCoords = Simplification.simplify(before, 0.01);
        update(wordCurveCoords);

        getColor().a = 1;
    }

    public void update(CurvePosition[] input) {
        if (input.length > 2) {
            generate(input);
        }
    }

    private void generate(CurvePosition[] input) {
        for (int i = 0; i < input.length; i++) {
            p = input[i];
            int mult = 1;
            if (i < input.length - 1) {
                p2 = input[i + 1];
            } else {
                p2 = input[i - 1];
                mult = -1;
            }

            findPerpendicularPoint(perpPoint, p, p2, mult);
            input[i].sideA.x = perpPoint.x;
            input[i].sideA.y = perpPoint.y;

            findPerpendicularPoint(perpPoint, p, p2, mult * -1);
            input[i].sideB.x = perpPoint.x;
            input[i].sideB.y = perpPoint.y;
        }
    }

    private void findPerpendicularPoint(Vector2 curr, CurvePosition p, CurvePosition p2, int mult) {

        //get direction and normalize it
        perp.set(p).sub(p2).nor();

        //get perpendicular
        perp.set(-perp.y, perp.x);

//            float thick = thickness * (1f-((i)/(float)(input.size)));

        //move outward by thickness
        perp.scl(thick / 2f);

        //decide on which side we are using
        perp.scl(mult);

        curr.set(p.x + perp.x, p.y + perp.y);
    }

    public void setStart(float start) {
        this.start = start;
    }

    @Override
    public void progress(float percent, float duration) {
        this.current = percent;
    }

    @Override
    public void reset() {
        start = 0;
        current = 0;
    }

    public void showWordWave() {
        start = 0;
        current = 1;
    }

    public void showHint(float duration) {
        addAction(
                Actions.sequence(
                        Actions.fadeIn(0f),
                        Actions.delay(duration - Constants.Timing.FADE_TRANSITION_LENGTH),
                        Actions.fadeOut(Constants.Timing.FADE_TRANSITION_LENGTH)
                )
        );
    }

    public void fadeWordCurveOut(float duration) {
        addAction(
                Actions.sequence(
                        Actions.fadeOut(duration)
                )
        );
    }

//    public void setDebugPoints(Vector2[] debugPoints) {
//        this.debugPoints = debugPoints;
//    }

    public Color getOverrideColor() {
        return overrideColor;
    }

    void setOverrideColor(Color overrideColor) {
        this.overrideColor = overrideColor;
    }

    public static class CurvePosition extends Vector2 {
        public float percent;
        Vector2 sideA = new Vector2();
        Vector2 sideB = new Vector2();
        Color color = new Color();

        CurvePosition(float percent) {
            this.percent = percent;
        }

        @Override
        public String toString() {
            return "CurvePosition{" +
                    "percent=" + percent +
                    ", sideA=" + sideA +
                    ", sideB=" + sideB +
                    ", color=" + color +
                    '}';
        }
    }

    public void parentPositionChanged(float parentX, float parentY) {
        this.parentX = parentX;
        this.parentY = parentY;
    }
}
