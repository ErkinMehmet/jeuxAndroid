package com.np.brickbreaker.specialEffects;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;

public class Fuego {

    private Paint paint;
    private Path firePath;
    private int fireAlpha;
    private float fireScale;
    private float angle;
    private ValueAnimator animator;

    public Fuego() {
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

        firePath = new Path();
        fireAlpha = 255;
        fireScale = 1f;
        angle = 0f;

        // Animator to create a dynamic fire effect with random alpha and scale
        animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(2000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.addUpdateListener(animation -> {
            fireAlpha = (int) (Math.random() * 255); // Random alpha value
            fireScale = 1f + (float) (Math.random() * 0.3); // Random scale
            angle += 5f; // Increment the rotation angle
            // Trigger redraw
        });
        animator.start();
    }

    // Generate the path for the fire effect
    private void generateFirePath(int width, int height) {
        firePath.reset();
        float centerX = width / 2f;
        float centerY = height /2f;
        float radius = 150f * fireScale;

        // Create the path representing the fire's flicker
        for (int i = 0; i < 360; i += 20) {
            float angleInRad = (float) Math.toRadians(i + angle);
            float x = centerX + (float) Math.cos(angleInRad) * radius;
            float y = centerY + (float) Math.sin(angleInRad) * radius;
            if (i == 0) {
                firePath.moveTo(x, y);
            } else {
                firePath.lineTo(x, y);
            }
        }
        firePath.close();
    }

    // Draw the fire effect
    public void draw(Canvas canvas, int width, int height) {
        // Generate dynamic fire path
        generateFirePath(width, height);

        // Create radial gradient for the fire effect
        RadialGradient gradient = new RadialGradient(
                width / 2f, height / 2f, 200f * fireScale,
                new int[]{Color.RED, Color.YELLOW, Color.TRANSPARENT},
                null, Shader.TileMode.CLAMP);

        paint.setShader(gradient);
        paint.setAlpha(fireAlpha); // Set transparency for the fire

        // Apply rotation and draw the fire
        canvas.save();
        canvas.rotate(angle, width / 2f, height / 2f);
        canvas.drawPath(firePath, paint);
        canvas.restore();

        // Trigger invalidation for animation loop (if needed)
        // invalidate(); // Uncomment this in your GameView if necessary
    }
}
