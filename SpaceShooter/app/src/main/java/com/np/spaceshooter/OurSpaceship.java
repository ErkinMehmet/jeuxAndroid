package com.np.spaceshooter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.Random;

public class OurSpaceship {
    Context context;
    Bitmap ourSpaceship;
    int ox,oy;
    boolean isAlive=true;
    int ourVelocity;
    Random random;
    public OurSpaceship(Context context) {
        this.context=context;
        ourSpaceship= BitmapFactory.decodeResource(this.context.getResources(), R.drawable.rocket1);
        random=new Random();
        resetOurSpaceship();
    }

    public Bitmap getOurSpaceship(){
        return ourSpaceship;
    }
    public int getOurSpaceshipWidth(){
        return ourSpaceship.getWidth();
    }
    public int getOurSpaceshipHeight(){
        return ourSpaceship.getHeight();
    }
    private void resetOurSpaceship() {
        ox=random.nextInt(SpaceShooter.screenWidth);
        oy=SpaceShooter.screenHeight-ourSpaceship.getHeight();
        ourVelocity=10+ random.nextInt(6);
    }

}
