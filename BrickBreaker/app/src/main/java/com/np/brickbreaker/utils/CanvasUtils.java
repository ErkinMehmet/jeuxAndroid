package com.np.brickbreaker.utils;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.np.brickbreaker.specialEffects.Firework;

import java.util.List;

public class CanvasUtils {
    public static void drawCongratulationText(Canvas canvas, String message, int screenWidth, int screenHeight, long elapsedTime) {
        Paint paint = new Paint();
        paint.setColor(Color.YELLOW);  // Use Color constants like Color.YELLOW
        paint.setTextSize(50);
        paint.setTextAlign(Paint.Align.CENTER);

        // Calculate the position to center the text
        float x = screenWidth / 2;
        float y = screenHeight / 2;

        // Animate the text size based on elapsed time
        float scale = Math.min(1.0f + elapsedTime / 1000.0f, 2.0f); // Scale the text up
        canvas.save();
        canvas.scale(scale, scale, x, y); // Apply scale to the canvas

        canvas.drawText(message, x, y,paint);

        canvas.restore(); // Restore canvas to original state
    }

    public static void drawFireworks(Canvas canvas, List<Firework> fireworks) {
        Paint paint = new Paint();

        for (Firework firework : fireworks) {
            firework.update();
            firework.draw(canvas, paint);
        }
    }

}
