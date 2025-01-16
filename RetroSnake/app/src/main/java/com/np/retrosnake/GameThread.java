package com.np.retrosnake;
import android.graphics.Canvas;
import android.os.SystemClock;
import android.util.Log;
import android.view.SurfaceHolder;
public class GameThread  extends Thread {
    SurfaceHolder surfaceHolder;
    boolean isRunning;
    long startTime,loopTime;
    long DELAY=200;
    boolean startGame=false;
    public GameThread(SurfaceHolder surfaceHolder) {
        this.surfaceHolder=surfaceHolder;
        isRunning=true;

    }

    @Override
    public void run() {
        while (isRunning) {
            startTime= SystemClock.uptimeMillis();
            Canvas canvas=surfaceHolder.lockCanvas(null);
            if (canvas != null) {
                synchronized (surfaceHolder) {
                    if (! startGame) {
                        AppConstants.getGameEngine().generateNewApple();
                        startGame=true;
                    }
                    AppConstants.getGameEngine().moveGrowAndDrawSnake(canvas);
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
            loopTime=SystemClock.uptimeMillis() - startTime;
            if (loopTime < DELAY) {
                try {
                    Thread.sleep(DELAY-loopTime);
                } catch (InterruptedException e) {
                    Log.e("Interrupted","Interrupted while sleeping");
                }
            }
        }
    }

    public boolean isRunning(){
        return isRunning;
    }
    public void setIsRunning(boolean state){
        isRunning=state;
    }
}
