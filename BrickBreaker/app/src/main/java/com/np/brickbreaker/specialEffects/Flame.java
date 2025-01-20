package com.np.brickbreaker.specialEffects;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import java.util.Random;
public class Flame {
    private Path path;
    private Path pathOuter;
    private Path pathOuter2;
    private Paint flamePaint;
    private Paint outerFlamePaint;
    private Paint outerFlamePaint2;
    private Random random;
    public float x=400f;
    public float y=550f;
    public float flameSizeOrig=150.0f;
    public float flameDecay=0.1f;
    public float flameThreshold=100;
    private int[] flameColors = {
            0xFFFF4500, // Orange (Flame color)
            0xFFFF6347, // Tomato Red
            0xFFFF0000, // Red
            0xFFFFD700, // Gold (for hotter parts)
            0xFFFFA500  // Orange
    };
    public Flame() {
        this.random = new Random();
        // randomize x and y
        x+=random.nextFloat()*200;
        y+=random.nextFloat()*300;

        this.path = new Path();
        this.pathOuter=new Path();
        this.pathOuter2=new Path();

        this.flamePaint = new Paint();
        this.outerFlamePaint = new Paint();
        this.outerFlamePaint2 = new Paint();
        int color = flameColors[random.nextInt(flameColors.length)];
        int colorOuter = flameColors[random.nextInt(flameColors.length)];
        int colorOuter2 = flameColors[random.nextInt(flameColors.length)];
        flamePaint.setColor(color);
        flamePaint.setStyle(Paint.Style.FILL);
        outerFlamePaint.setColor(colorOuter);
        outerFlamePaint.setStyle(Paint.Style.FILL);
        outerFlamePaint2.setColor(colorOuter2);
        outerFlamePaint2.setStyle(Paint.Style.FILL);
    }
    public void draw(Canvas canvas) {
        path.reset();
        path.moveTo(x,y);
        pathOuter.reset();
        pathOuter.moveTo(x,y);
        pathOuter2.reset();
        pathOuter2.moveTo(x,y);

        // Create random flame wiggle effect
        for (int i = 0; i < 200; i++) {
            float flameSize= flameSizeOrig* (float) Math.exp(-flameDecay * (i - flameThreshold));
            if (flameSize>flameSizeOrig) {
                flameSize=flameSizeOrig;
            }
            float deltay1=- i * 1 + random.nextFloat() * 20;
            float deltax1= random.nextFloat() * flameSize/2*2 - flameSize/2;
            float y1=y +deltay1;
            float x1= x+deltax1;
            float deltax2= random.nextFloat() * flameSize*2 - flameSize;
            float deltay2= deltay1*2;
            float y2 = y + deltay2;
            float x2 = x + deltax2;
            float deltax3= random.nextFloat() * flameSize*4 - flameSize*2;
            float y3=y+deltay2*2;
            float x3=x+deltax3;
            float deltax4=random.nextFloat() * flameSize*8 - flameSize*4;
            float y4=y+deltay2*4;
            float x4=x+deltax4;
            path.cubicTo(x1, y1,x2,y2,x,y);
            pathOuter.cubicTo(x2,y2,x3,y3,x,y);
            pathOuter2.cubicTo(x3,y3,x4,y4,x,y);
        }

        path.close();
        pathOuter.close();
        pathOuter2.close();
        canvas.drawPath(pathOuter2, outerFlamePaint2);
        canvas.drawPath(pathOuter, outerFlamePaint);
        canvas.drawPath(path, flamePaint);
    }
}
