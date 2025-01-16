package com.np.brickbreaker.specialEffects;

import android.graphics.Canvas;
import android.graphics.Paint;

public class Frost {
    float x, y, size;
    int alpha;

    public Frost(float x, float y, float size, int alpha) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.alpha = alpha;
    }

    public void update() {
        this.size += 0.1f;  // Frost particles expand
        this.alpha -= 2;    // Fade out as they grow
    }

    public void draw(Canvas canvas, Paint paint) {
        paint.setAlpha(alpha);  // Set alpha to simulate fading
        canvas.drawCircle(x, y, size, paint);  // Draw frost particle
    }
}