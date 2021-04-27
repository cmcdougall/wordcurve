package com.singlemethodgames.wordcurve.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Path;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.singlemethodgames.wordcurve.groups.ProgressListener;

/**
 * Created by cameron on 19/02/2018.
 */

public class WordCurvePointActor extends Actor implements WordCurveActorAction {
    private int diameter;
    private TextureRegion point;

    private Path<Vector2> curve;
    private Vector2 currentPoint;

    Color c;
    float radius;

    private ProgressListener progressListener;

    public WordCurvePointActor(TextureRegion pointRegion, ProgressListener progressListener) {
        this.progressListener = progressListener;
        diameter = 200;
        getColor().a = 0;
        point = pointRegion;
        currentPoint = new Vector2();
        setBounds(0, 0, diameter, diameter);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        setWidth(diameter * getScaleX());
        setHeight(diameter * getScaleY());

        c = batch.getColor();
        batch.setColor(Color.CYAN.r, Color.CYAN.g, Color.CYAN.b, getColor().a * parentAlpha);

        radius = getWidth() / 2;

        batch.draw(point, getX() - radius, getY() - radius, getWidth(), getHeight());

        batch.setColor(c.r, c.g, c.b, parentAlpha);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    public void setCurve(Path<Vector2> curve) {
        this.curve = curve;
    }

    @Override
    public void progress(float percent, float duration) {
        curve.valueAt(currentPoint, percent);
        setPosition(currentPoint.x, currentPoint.y);
        if(progressListener != null) {
            progressListener.atPosition(percent, duration);
        }
    }

    @Override
    public void reset() {
        setScale(1);
        getColor().a = 0;
    }
}

