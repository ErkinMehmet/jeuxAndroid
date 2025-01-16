package com.np.retrosnake;

import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

public class AppConstants {
    static GameEngine gameEngine;
    static int SCREEN_WIDTH, SCREEN_HEIGHT;
    static int surfaceViewWidth,surfaceViewHeight;
    static Context gameActivityContext;
    static int pointSize;
    static int defaultTailPoints;
    static int snakeColor=Color.WHITE;
    static int appleColor;

    public static void initialization(Context context) {
        setScreenSize(context);
        AppConstants.gameActivityContext=context;
        setGameConstants();
        gameEngine =new GameEngine();
    }

    public static void setGameConstants() {
        AppConstants.pointSize=24;
        AppConstants.defaultTailPoints=3;
        AppConstants.snakeColor=Color.WHITE;
        AppConstants.appleColor=Color.RED;
    }

    public static GameEngine getGameEngine(){
        return gameEngine;
    }
    public static void setScreenSize(Context context) {
        WindowManager wm=(WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display=wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int width=metrics.widthPixels;
        int height=metrics.heightPixels;
        AppConstants.SCREEN_HEIGHT=height;
        AppConstants.SCREEN_WIDTH=width;

    }
}






