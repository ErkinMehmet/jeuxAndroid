package com.np.spaceshooter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.Random;

public class EnemySpaceship {
    Context context;
    Bitmap enemySpaceship;
    int ex,ey;
    int enemyVelocity;
    Random random;
    public EnemySpaceship(Context context) {
        this.context=context;
        enemySpaceship= BitmapFactory.decodeResource(context.getResources(),R.drawable.rocket2);
        random=new Random();
        resetEnemySpaceship();

    }

    public Bitmap getEnemySpaceship() {
        return enemySpaceship;
    }

    public int getEnemySpaceshipWidth(){
        return enemySpaceship.getWidth();
    }
    public int getEnemySpaceshipHeight(){
        return enemySpaceship.getHeight();
    }

    public void resetEnemySpaceship() {
        ex=200+random.nextInt(400);
        ey=0;
        enemyVelocity=14+random.nextInt(10);

    }
}



