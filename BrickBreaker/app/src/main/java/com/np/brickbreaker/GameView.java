package com.np.brickbreaker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.MaskFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

import com.np.brickbreaker.specialEffects.Flame;
import com.np.brickbreaker.specialEffects.Frost;
import com.np.brickbreaker.specialEffects.Fuego;
import com.np.brickbreaker.specialEffects.Lightning;
import com.np.brickbreaker.specialEffects.Spiral;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.np.brickbreaker.enums.BrickType;

import com.np.brickbreaker.utils.ImageUtils;

public class GameView extends View {
    long currentTouch = System.currentTimeMillis();
    long lastTouch;
    int counter;
    Context context;
    float ballX,ballY;
    boolean initVelocity=false;
    Velocity velocity=new Velocity(0,0);//25,32
    Handler handler;
    final long UPDATE_MILLIS=30;
    Runnable runnable;
    Paint textPaint=new Paint();
    Paint healthPaint=new Paint();
    Paint brickPaint=new Paint();
    float TEXT_SIZE=50;
    float paddleX,paddleY;
    float oldX,oldPaddleX; //oldPaddleX for onTouch
    float velocityPaddle;
    int points=0;
    final int maxLife=5;
    int life=maxLife;
    Bitmap ball,paddle, ballBefore;
    int dWidth,dHeight;
    int ballWidth,ballHeight;
    MediaPlayer mpHit,mpMiss,mpBreak,mpThunder;
    Random random;

    int numBricks;
    int brokenBricks;
    boolean gameOver=false;

    // Fernando
    float scaleBall=0.05f;
    float scaleBrick=0.5f;

    int brickRows=6*((int) (1/scaleBrick));
    int brickColumns=8*((int) (1/scaleBrick));
    int brickWidth;
    int brickHeight;
    int brickpadding=2;
    int bricksTopMargin=200;
    int colorBrick;
    Integer[] colorsBrick = {
            Color.rgb(51, 51, 153),    // Moderately dim Blue
            Color.rgb(153, 51, 51),    // Moderately dim Red
            Color.rgb(51, 153, 51),    // Moderately dim Green
            Color.rgb(153, 153, 51),   // Moderately dim Yellow
            Color.rgb(51, 153, 153),   // Moderately dim Cyan
            Color.rgb(153, 51, 153)    // Moderately dim Magenta
    };
    int numColors=colorsBrick.length;
    Brick[] bricks=new Brick[brickRows*brickColumns];

    private Flame flame;
    Paint frostPaint = new Paint();
    Paint hpTextPaint=new Paint();

    private List<Lightning> lightningEffects;
    final private Drawable lightningDrawable=getResources().getDrawable(R.drawable.spark_lightning);
    final private Bitmap[] croppedLightningAnimations= ImageUtils.cropDrawableIntoGrid(lightningDrawable,2,4);
    int lightningAnimation=0;
    private Spiral spiral;
    private Fuego fireEffect;
    private boolean isLightningActive = false;
    private boolean isSpiralActive = false;
    private boolean isFireActive = false;
    private boolean isFrostActive = false;
    private boolean isFlameActive=false;

    public GameView(Context context) {
        super(context);
        this.context=context;
        initializeGame();
    }

    private void initializeGame(){
        brokenBricks=0;
        numBricks=0;
        life=maxLife;

        Display display=((Activity) getContext()).getWindowManager().getDefaultDisplay();
        Point size=new Point();
        display.getSize(size);
        dWidth=size.x;
        dHeight=size.y;
        random=new Random();

        // pour changer les tailles
        Matrix matrix = new Matrix();
        matrix.postScale(scaleBall, scaleBall);

        ballBefore= BitmapFactory.decodeResource(getResources(),R.drawable.globe);
        ball=Bitmap.createBitmap(ballBefore,0,0,ballBefore.getWidth(),ballBefore.getHeight(),matrix,true);
        paddle=BitmapFactory.decodeResource(getResources(),R.drawable.sprites);
        handler=new Handler();
        runnable=new Runnable() {
            @Override
            public void run() {
                invalidate();
            }
        };
        mpHit=MediaPlayer.create(context,R.raw.shot);
        mpMiss=MediaPlayer.create(context,R.raw.miss);
        mpBreak=MediaPlayer.create(context,R.raw.breaking);
        mpThunder=MediaPlayer.create(context,R.raw.rock_breaking);

        textPaint.setColor(Color.parseColor("#4997D0"));
        textPaint.setTextSize(TEXT_SIZE);
        textPaint.setTextAlign(Paint.Align.LEFT);
        int[] healthColors = {
                Color.parseColor("#FF0000"),
                Color.parseColor("#FFD700"),
                Color.parseColor("#32CD32")
        };

        float[] hpPositions = {0f, 0.7f, 1f}; // Positions: Green at 0, Gold at 0.5, Red at 1
        LinearGradient hpGradient = new LinearGradient(dWidth-400, 0, dWidth-20, 0, healthColors, hpPositions, Shader.TileMode.CLAMP);
        healthPaint.setShader(hpGradient);
        healthPaint.setAntiAlias(true);
        hpTextPaint = new Paint();
        hpTextPaint.setColor(Color.WHITE);  // Text color
        hpTextPaint.setTextSize(40f);  // Text size
        hpTextPaint.setTextAlign(Paint.Align.CENTER);
        brickPaint.setAntiAlias(true);

        paddleY=dHeight*9/10;
        paddleX=dWidth/2-paddle.getWidth()/2;

        ballX=paddleX+paddle.getWidth()/2-ball.getWidth()/2;//random.nextInt(dWidth-50);
        ballY=paddleY-ball.getHeight();//dHeight/3;
        ballWidth=ball.getWidth();
        ballHeight=ball.getHeight();
        createBricks();

        lightningEffects = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Lightning lightningEffect = new Lightning();
            lightningEffects.add(lightningEffect);
        }


        spiral = new Spiral();
        spiral.setSpiralParameters(500f, 500f, 300f, 10, 5f);

        fireEffect = new Fuego();
        flame = new Flame();
    }
    private void createBricks() {
        numBricks=0;
        int currentColorIndex;
        brickWidth= (int) (((float) dWidth)/8.0f * scaleBrick);
        brickHeight=(int) (((float) dHeight)/16.0f * scaleBrick);
        colorBrick=colorsBrick[0];
        brickPaint.setShadowLayer(10, 5, 5, Color.BLUE);
        for (int column=0;column<brickColumns;column++) {
            for (int row=0;row<brickRows;row++) {
                int colorDecider=random.nextInt(7);

                if (numBricks>brickColumns && colorDecider==2){
                    colorBrick=bricks[numBricks-brickColumns].color;
                } else if (colorDecider==3) {
                    currentColorIndex=Arrays.asList(colorsBrick).indexOf(colorBrick);
                    if (currentColorIndex==numColors-1){
                        currentColorIndex=-1;
                    }
                    colorBrick=colorsBrick[currentColorIndex+1];
                }

                Brick newBrick=new Brick(row,column,brickWidth,brickHeight,colorBrick);

                bricks[numBricks]=newBrick;
                newBrick.left = column * brickWidth +brickpadding;
                newBrick.top = row * brickHeight + bricksTopMargin+brickpadding;
                newBrick.right = (1 + column) * brickWidth-brickpadding;
                newBrick.bottom = (row + 1) * brickHeight+ bricksTopMargin-brickpadding;
                newBrick.gradient  = new LinearGradient(
                        newBrick.left,  newBrick.top,  newBrick.right,  newBrick.bottom,
                        colorBrick, Color.WHITE,  // Colors for the gradient
                        Shader.TileMode.CLAMP);


                if (row==brickRows-1){
                    int decidePutLightning=random.nextInt(brickRows); //brickRows*3
                    if (decidePutLightning==1){
                        bricks[numBricks].brickType=BrickType.LIGHTNING;
                    }
                }
                //Log.e("info",newBrick.toString());
                numBricks++;
            }
        }
        //Log.e("info","There are "+String.valueOf(numBricks)+" bricks");
    }
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (isLightningActive) {
            for (Lightning lightningEffect : lightningEffects) {
                lightningEffect.startLightningEffect();
            }
        }
        handler.post(runnable);
    }


    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        boolean breakAny=false; // initialize this to false then check if the ball breaks any bricks

        counter++;
        super.onDraw(canvas);
        canvas.drawColor(Color.BLACK);
        if (initVelocity) {
            ballX+=velocity.getX();
            ballY+=velocity.getY();
        }

        if (ballX>=dWidth-ball.getWidth() || ballX<=0) {
            ballX-=velocity.getX(); //recover
            velocity.setX(velocity.getX()*-1);
        }
        if (ballY<=0) {
            ballY-=velocity.getY();
            velocity.setY(velocity.getY()*-1);
        }
        // miss
        if (ballY>=dHeight-1) {
            if (mpMiss!=null) {
                mpMiss.start();
            }
            velocity.setX(xVelocity());
            velocity.setY(32);
            life--;
            if (life==0) {
                gameOver=true;
                launchGameOver();
            }

            velocity.setX(0);
            velocity.setY(0);
            ballX=paddleX+paddle.getWidth()/2-ball.getWidth()/2;
            ballY=paddleY-ball.getHeight();//dHeight/3;
            initVelocity=false;
        }

        if ((ballX+ball.getWidth()>paddleX) && (ballX<paddleX+paddle.getWidth())
                && (ballY+ball.getHeight()>=paddleY)
                && (ballY<paddleY+paddle.getHeight())
                && initVelocity) {
            if (mpHit!=null) {
                mpHit.start();
            }
            ballX-=velocity.getX();
            ballY-=velocity.getY();
            float momentum=velocityPaddle*2;
            velocity.setX(velocity.getX()+(int) momentum);
            velocity.setY((velocity.getY()+1)*-1);

        }

        canvas.drawBitmap(paddle,paddleX,paddleY,null);
        canvas.drawBitmap(ball,ballX,ballY,null);
        for (int i=0; i<numBricks;i++){
            if (isLightningActive) {
                int decideStruckByLightning=random.nextInt(brickColumns*5);
                if (decideStruckByLightning==1 && brokenBricks<=0.5*numBricks) {
                    bricks[i].setInvisible();
                    points+=15;
                    brokenBricks++;
                }
            }
            if (bricks[i].getVisibility()) {
                brickPaint.setShader(bricks[i].gradient);
                //Log.e("info",bricks[i].toString());
                canvas.drawRect(bricks[i].left, bricks[i].top, bricks[i].right, bricks[i].bottom, brickPaint);

                if (bricks[i].brickType==BrickType.LIGHTNING) {
                    if (lightningAnimation>=croppedLightningAnimations.length){
                        lightningAnimation=0;
                    }
                    Bitmap bitmapToDraw = croppedLightningAnimations[lightningAnimation];
                    float bitmapWidth = bitmapToDraw.getWidth();
                    float bitmapHeight = bitmapToDraw.getHeight();
                    float scaleX = (brickWidth-2*brickpadding) / bitmapWidth;
                    float scaleY = (brickHeight-2*brickpadding) / bitmapHeight;
                    Matrix matrix = new Matrix();
                    matrix.postScale(scaleX, scaleY);
                    float translateX =  bricks[i].left + (brickWidth-2*brickpadding - bitmapWidth * scaleX) / 2;
                    float translateY =  bricks[i].top + (brickHeight-2*brickpadding - bitmapHeight * scaleY) / 2;
                    matrix.postTranslate(translateX, translateY);
                    canvas.drawBitmap(bitmapToDraw, matrix, null);
                }
            }
        }
        lightningAnimation++; // to have the same animation for all the lightning bricks

        canvas.drawText(""+points,20,TEXT_SIZE+60,textPaint);
        canvas.drawRoundRect(dWidth-400,70,(dWidth-400)+ ((float) life/maxLife)*380,120,15,15,healthPaint);

        String healthText = "Health: " + (int) life + " / "+(int) maxLife;  // Text to display (you can adjust this)
        float textX = dWidth-400 + 190;  // X coordinate (centered)
        float textY = 70 + 25 - ((hpTextPaint.descent() + hpTextPaint.ascent()) / 2);  // Y coordinate (centered vertically)
        canvas.drawText(healthText, textX, textY, hpTextPaint);

        for (int i=numBricks-1;i>=0;i--){
            if (bricks[i].getVisibility()) {
                if (((ballX>=bricks[i].left-brickpadding
                        && ballX<=bricks[i].right+brickpadding)
                        ||(ballX+ball.getWidth()>=bricks[i].left-brickpadding
                        && ballX+ball.getWidth()<=bricks[i].right+brickpadding))
                        && ballY>=bricks[i].top-brickpadding
                        && ballY<=bricks[i].bottom+brickpadding) {
                    breakAny=true;
                    bricks[i].setInvisible();
                    points+=10;
                    brokenBricks++;
                    //lightning
                    if (bricks[i].brickType==BrickType.LIGHTNING){
                        isLightningActive=true;
                        for (Lightning lightningEffect : lightningEffects) {
                            lightningEffect.startLightningEffect();
                        }
                        if (mpThunder!= null) {
                            mpThunder.start();
                        }

                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                for (Lightning lightningEffect : lightningEffects) {
                                    lightningEffect.stopLightningEffect();
                                }
                                if (mpThunder.isPlaying()) {
                                    mpThunder.stop();
                                    mpThunder.release(); // Release resources
                                }
                                isLightningActive = false;
                            }
                        }, 7000);
                    }
                    if (brokenBricks==brickRows*brickColumns) {
                        launchGameOver();
                    }
                }
            }
        }
        // several bricks can break but the change for the ball only happen once
        if (breakAny){
            //Log.e("info","break now");
            if (mpBreak!=null) {
                mpBreak.start();
            }
            // the ball will not advance if there is collison
            ballX-=velocity.getX();
            ballY-=velocity.getY();
            velocity.setY((velocity.getY()+1)*-1);
        }

        // draw flame
        if (isFlameActive) {
            flame.draw(canvas);
        }

        // frosts
        if (isFrostActive) {
            frostPaint.setAntiAlias(true);  // Smooth edges for frost particles
            frostPaint.setColor(Color.CYAN);  // Light blue color for icy effect (can use Color.WHITE for white frost)
            frostPaint.setAlpha(180);  // Slightly transparent for a soft frosty look
            frostPaint.setStyle(Paint.Style.FILL);  // Solid fill style for the frost particles
            RadialGradient gradient2 = new RadialGradient(
                    0, 0, 50,  // Center and radius of the gradient
                    Color.CYAN, Color.WHITE,  // Gradient from cyan to white (icy effect)
                    Shader.TileMode.CLAMP);  // Use the gradient for the paint
            frostPaint.setShader(gradient2);
            MaskFilter blurFilter = new BlurMaskFilter(10, BlurMaskFilter.Blur.NORMAL);  // Adjust radius (10) for effect
            frostPaint.setMaskFilter(blurFilter);
            List<Frost> frostParticles = new ArrayList<>();
            for (int i = 0; i < 100; i++) {
                float x = random.nextFloat() * dWidth;
                float y = random.nextFloat() * dHeight;
                float size = random.nextFloat() * 5 + 5; // Random size
                int alpha = 255;  // Fully visible at the start
                frostParticles.add(new Frost(x, y, size, alpha));
            }
            for (Frost p : frostParticles) {
                p.update();  // Update particle properties (e.g., size, alpha)
                p.draw(canvas, frostPaint);  // Draw the particle on canvas
            }
        }

        if (isLightningActive) {
            for (Lightning lightningEffect : lightningEffects) {
                lightningEffect.draw(canvas, getWidth(), getHeight());
            }
        }
        if (isSpiralActive){
            spiral.draw(canvas);
        }
        if (isFireActive) {
            fireEffect.draw(canvas, getWidth(), getHeight());
        }

        // game loop
        if (brokenBricks==numBricks) {
            //gameOver=true;
            initializeGame();
            invalidate();
        }
        if (!gameOver) {
            handler.postDelayed(runnable,UPDATE_MILLIS);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX=event.getX();
        float touchY=event.getY();
        if (touchY>=paddleY-200) {
            int action=event.getAction();
            if (action==MotionEvent.ACTION_DOWN) {
                oldX=event.getX();
                oldPaddleX=paddleX;
                if (!initVelocity) { // ontap initialize the velocity
                    velocity.setX(25);
                    velocity.setY(-32);
                    initVelocity=true;
                    //Log.e("info","started");
                }
                lastTouch=currentTouch;
            }
            if (action==MotionEvent.ACTION_MOVE) {
                float shift=oldX-touchX;
                float newPaddleX=oldPaddleX-shift;
                if (newPaddleX<=0){
                    paddleX=0;
                } else if (newPaddleX>dWidth-paddle.getWidth()) {
                    paddleX=dWidth-paddle.getWidth();
                } else  {
                    paddleX=newPaddleX;
                }
                currentTouch=System.currentTimeMillis();
                long timeElapsed = currentTouch - lastTouch;
                if (timeElapsed > 0 && shift!=0) {
                    velocityPaddle = (-shift) / timeElapsed;
                }
                lastTouch = currentTouch;
            }
        }
        return true;
    }

    private void launchGameOver() {
        handler.removeCallbacksAndMessages(null);
        Intent intent = new Intent(context, GameOver.class);
        intent.putExtra("points", points);
        context.startActivity(intent);
        ((Activity) context).finish();
    }

    private int xVelocity(){
        int[] values={-35,-30,-25,25,30,35};
        int index=random.nextInt(6);
        return values[index];
    }
}







