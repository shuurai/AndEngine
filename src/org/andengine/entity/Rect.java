package org.andengine.entity;

/**
 * Created by sfdi on 7/12/2015.
 */
public class Rect {
    private float mX = 0;
    private float mY = 0;
    private float mW = 0;
    private float mH = 0;

    public Rect(float pX, float pY, float pW, float pH) {
        mX = pX;
        mY = pY;
        mW = pW;
        mH = pH;
    }

    public float getX() {
        return mX;
    }

    public void setX(float mX) {
        this.mX = mX;
    }

    public float getY() {
        return mY;
    }

    public void setY(float mY) {
        this.mY = mY;
    }

    public float getWidth() {
        return mW;
    }

    public void setWidth(float mW) {
        this.mW = mW;
    }

    public float getHeight() {
        return mH;
    }

    public void setHeight(float mH) {
        this.mH = mH;
    }
}
