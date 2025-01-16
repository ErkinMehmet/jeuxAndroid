package com.np.retrosnake;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity implements SurfaceHolder.Callback {
    GameThread gameThread;
    SurfaceHolder surfaceHolder;
    private SurfaceView surfaceView;
    MediaPlayer btn_click;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        btn_click=MediaPlayer.create(this,R.raw.click);
        surfaceView=findViewById(R.id.surfaceView);
        surfaceView.getHolder().addCallback(this);
        surfaceView.setFocusable(true);
        AppConstants.initialization(GameActivity.this);
        getSupportActionBar().hide();
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        this.surfaceHolder=surfaceHolder;
        gameThread=new GameThread(surfaceHolder);
        if (! gameThread.isRunning()) {
            gameThread=new GameThread(surfaceHolder);
            gameThread.start();
        } else {
            gameThread.start();
        }
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        AppConstants.surfaceViewWidth=surfaceView.getWidth();
        AppConstants.surfaceViewHeight=surfaceView.getHeight();

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        gameThread.setIsRunning(false);
        boolean retry=true;
        while (retry) {
            try {
                gameThread.join();
                retry=false;
            } catch (InterruptedException e) {

            }

        }
    }

    public void changeSnakeDirection(View view) {
        if (btn_click != null) {
            btn_click.start();
        }
        String movement=view.getTag().toString();
        switch (movement) {
            case "top":
                if (!AppConstants.getGameEngine().movingPosition.equals("bottom")) {
                    AppConstants.getGameEngine().movingPosition="top";
                }
                break;
            case "bottom":
                if (!AppConstants.getGameEngine().movingPosition.equals("top")) {
                    AppConstants.getGameEngine().movingPosition="bottom";
                }
                break;
            case "left":
                if (!AppConstants.getGameEngine().movingPosition.equals("right")) {
                    AppConstants.getGameEngine().movingPosition="left";
                }
                break;
            case "right":
                if (!AppConstants.getGameEngine().movingPosition.equals("left")) {
                    AppConstants.getGameEngine().movingPosition="right";
                }
                break;
        }
    }
}
