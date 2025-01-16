package com.np.brickbreaker.specialEffects;

import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;

public class Spiral {

    private Paint paint;
    private Path path;
    private Matrix matrix;
    private float angle = 0f;
    private float centerX = 500f;
    private float centerY = 500f;
    private float maxRadius = 500f;
    private int turns = 10;
    private float angleStep = 5f;
    private float time = 0f;
    public Spiral() {
        paint = new Paint();
        paint.setColor(Color.CYAN);
        paint.setStrokeWidth(5f);
        paint.setStyle(Paint.Style.STROKE);
        paint.setMaskFilter(new BlurMaskFilter(10, BlurMaskFilter.Blur.NORMAL));
        path = new Path();
        matrix = new Matrix();
        generateSpiralPath();
    }

    public void setSpiralParameters(float centerX, float centerY, float maxRadius, int turns, float angleStep) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.maxRadius = maxRadius;
        this.turns = turns;
        this.angleStep = angleStep;
        generateSpiralPath();
    }
    private void generateSpiralPath() {
        path.reset();
        float radius = 0f;

        path.moveTo(centerX, centerY);

        for (int i = 0; i < (turns * 360 / angleStep); i++) {
            float angleInRad = (float) Math.toRadians(i * angleStep);
            radius = (float) (Math.sin(time + i * 0.1) * maxRadius *7/ 8 + maxRadius / 8);
            radius = Math.min(radius, maxRadius); // Limit radius to maxRadius
            float x = centerX + radius * (float) Math.cos(angleInRad);
            float y = centerY + radius * (float) Math.sin(angleInRad);
            path.lineTo(x, y);
        }
    }
    public void draw(Canvas canvas) {
        // Apply rotation transformation
        matrix.setRotate(angle, canvas.getWidth() / 2f, canvas.getHeight() / 2f);
        canvas.save();
        canvas.concat(matrix);

        LinearGradient gradient = new LinearGradient(centerX - maxRadius, centerY - maxRadius,
                centerX + maxRadius, centerY + maxRadius,
                new int[] { Color.CYAN, Color.TRANSPARENT, Color.CYAN },
                null, Shader.TileMode.CLAMP);
        paint.setShader(gradient);
        canvas.drawPath(path, paint);

        canvas.restore();

        // Update the rotation angle
        angle += 5f;
        if (angle >= 360f) {
            angle = 0f;
        }
        time += 0.01f;
        if (time > 2 * Math.PI) {
            time = 0f;
        }
    }
}
