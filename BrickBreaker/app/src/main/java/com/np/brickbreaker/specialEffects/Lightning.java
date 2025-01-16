package com.np.brickbreaker.specialEffects;

import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Color;
import android.os.Handler;
import java.util.Random;

public class Lightning {
    private Paint lightningPaint;
    private Random random;
    private int canvasWidth, canvasHeight;
    private Handler handler;
    private Path lightningPath;
    private boolean isLightningVisible;
    private float flickerAlpha;
    private int currentColor;
    private int[] lightningColors = {Color.WHITE, Color.CYAN, Color.BLUE, Color.MAGENTA, Color.YELLOW};
    private float strokeWidthFactor;
    private int hugeStroke;
    public Lightning() {
        lightningPaint = new Paint();
        //lightningPaint.setColor(Color.CYAN);  // Lightning color (cyan)
        //lightningPaint.setStrokeWidth(8);
        lightningPaint.setStyle(Paint.Style.STROKE);
        lightningPaint.setAntiAlias(true);
        //lightningPaint.setAlpha(255);  // Fully visible initially
        lightningPaint.setMaskFilter(new BlurMaskFilter(5, BlurMaskFilter.Blur.NORMAL));  // Glow effect for lightning

        random = new Random();

        // Initialize the path that will hold the lightning bolt's coordinates
        lightningPath = new Path();

        // Handler to trigger lightning flashes at random intervals
        handler = new Handler();
        //startLightningEffect();
    }

    // Initialize the flash at random intervals
    public void startLightningEffect() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Trigger lightning effect randomly every 2-5 seconds
                createLightningFlash();
                isLightningVisible = true;  // Lightning is now visible

                // make the colors richer
                flickerAlpha = random.nextFloat();  // Random flicker alpha value
                strokeWidthFactor=random.nextInt(5)-random.nextFloat();
                hugeStroke=random.nextInt(2)*random.nextInt(2)*random.nextInt(2)*random.nextInt(2);
                currentColor = lightningColors[random.nextInt(lightningColors.length)];  // Random color
                lightningPaint.setColor(currentColor);  // Set the lightning color
                handler.postDelayed(this, random.nextInt(5000) + 1000);  // Random delay (between 1 and 5 seconds)
            }
        }, random.nextInt(5000) + 1000);  // Start after a random delay
    }

    // Method to simulate a lightning flash
    private void createLightningFlash() {
        // Clear the previous path for new lightning bolt
        lightningPath.reset();

        // Random start position (x-coordinate) for the lightning
        float startX = random.nextInt(canvasWidth);

        // Start position (y-coordinate) from the top of the screen
        float startY = 0;

        // Move the lightning path to the starting point
        lightningPath.moveTo(startX, startY);

        float currentX = startX;
        float currentY = startY;

        // Generate random zigzag movement for the lightning path
        for (int i = 0; i < 5; i++) {
            currentX += random.nextInt(300) - 150;  // Random horizontal movement (-150 to +150)
            currentY += random.nextInt(500) + 100;  // Random vertical movement (100 to 600)
            lightningPath.lineTo(currentX, currentY);
            // Add branches to the lightning bolt for added realism
            if (i % 2 == 0) {  // Add a branch after every other segment
                addBranch(currentX, currentY);
            }
        }

        // End the lightning at the bottom of the screen
        lightningPath.lineTo(currentX, canvasHeight);
    }

    private void addBranch(float x, float y) {
        // Branch will be shorter and grow in a random direction
        float branchLength = random.nextInt(50) + 30;  // Length of the branch (30 to 80 pixels)
        float branchAngle = random.nextFloat() * 360;  // Random direction for the branch (0 to 360 degrees)

        // Calculate the branch's end coordinates using polar coordinates
        float branchX = (float) (x + branchLength * Math.cos(Math.toRadians(branchAngle)));
        float branchY = (float) (y + branchLength * Math.sin(Math.toRadians(branchAngle)));

        // Add the branch to the path
        lightningPath.moveTo(x, y);
        lightningPath.lineTo(branchX, branchY);
    }
    // Call this method in your onDraw() to draw the lightning on the screen
    public void draw(Canvas canvas, int width, int height) {
        // Update canvas width and height
        this.canvasWidth = width;
        this.canvasHeight = height;

        lightningPaint.setAlpha((int) (flickerAlpha * 255));
        lightningPaint.setStrokeWidth(6+strokeWidthFactor*random.nextInt(4)+hugeStroke*48);
        // Only draw the lightning if it's visible
        if (isLightningVisible) {
            canvas.drawPath(lightningPath, lightningPaint);  // Draw the lightning bolt
        }
    }

    // Call this method to make the lightning disappear after a short duration
    public void stopLightningEffect() {
        isLightningVisible = false;
    }
}
