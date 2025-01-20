package com.np.brickbreaker.specialEffects;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.List;

public class Firework {
    private int x, y;
    private int color;
    private List<Particle> particles;

    public Firework(int x, int y, int color) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.particles = new ArrayList<>();

        // Generate particles to simulate the explosion
        generateParticles();
    }

    private void generateParticles() {
        for (int i = 0; i < 100; i++) { // Generate 100 particles for explosion effect
            int speedX = (int) (Math.random() * 20 - 10); // Random horizontal speed
            int speedY = (int) (Math.random() * 20 - 10); // Random vertical speed
            int particleColor = Color.argb(255, (int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255)); // Random color

            Particle particle = new Particle(x, y, speedX, speedY, particleColor);
            particles.add(particle);
        }
    }

    public void update() {
        List<Particle> toRemove = new ArrayList<>();

        // Update particles and remove them if they're no longer visible
        for (Particle particle : particles) {
            particle.update();
            if (particle.isFaded()) {
                toRemove.add(particle);
            }
        }

        particles.removeAll(toRemove);
    }

    public void draw(Canvas canvas, Paint paint) {
        for (Particle particle : particles) {
            particle.draw(canvas, paint);
        }
    }

    // Inner Particle class to simulate firework particles
    private static class Particle {
        private int x, y;
        private int speedX, speedY;
        private int color;
        private int radius;
        private int alpha;

        public Particle(int x, int y, int speedX, int speedY, int color) {
            this.x = x;
            this.y = y;
            this.speedX = speedX;
            this.speedY = speedY;
            this.color = color;
            this.radius = 5; // Initial size of the particle
            this.alpha = 255; // Initial opacity
        }

        public void update() {
            x += speedX;
            y += speedY;
            radius += 1; // Increase size to simulate expansion

            // Fade out the particle over time
            if (alpha > 0) {
                alpha -= 5; // Reduce opacity
            }
        }

        public void draw(Canvas canvas, Paint paint) {
            int newColor = Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
            paint.setColor(newColor);
            canvas.drawCircle(x, y, radius, paint);
        }

        public boolean isFaded() {
            return alpha <= 0;
        }
    }
}
