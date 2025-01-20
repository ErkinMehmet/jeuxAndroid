package com.np.brickbreaker.utils;

public class MathUtils {
    public static float distance(float x1,float y1, float x2, float y2) {
        float dx=x1-x2;
        float dy=y1-y2;
        return (float) Math.sqrt(dx*dx+dy*dy);
    }
}
