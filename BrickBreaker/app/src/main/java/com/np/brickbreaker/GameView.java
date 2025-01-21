package com.np.brickbreaker;

import static android.content.Context.MODE_PRIVATE;
import static com.np.brickbreaker.utils.CanvasUtils.drawFireworks;

import android.content.SharedPreferences;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.MaskFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;

import androidx.annotation.NonNull;

import com.np.brickbreaker.models.Brick;
import com.np.brickbreaker.models.GameState;
import com.np.brickbreaker.models.Level;
import com.np.brickbreaker.models.Velocity;
import com.np.brickbreaker.specialEffects.Firework;
import com.np.brickbreaker.specialEffects.Flame;
import com.np.brickbreaker.specialEffects.Frost;
import com.np.brickbreaker.specialEffects.Fuego;
import com.np.brickbreaker.specialEffects.Lightning;
import com.np.brickbreaker.specialEffects.Spiral;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.np.brickbreaker.enums.BrickType;

import com.np.brickbreaker.utils.CanvasUtils;
import com.np.brickbreaker.utils.MathUtils;

public class GameView extends View {
    private MyApp app;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private int levelNum=1; // 50 levels for the game
    private int maxLevelNum=5;
    int[][] brickMap;

    private MainActivity mainActivity;
    private long currentTouch = System.currentTimeMillis();
    private long lastTouch;
    private int counter;
    Context context;
    private int ballX,ballY,ballLeft,ballRight,ballTop,ballBottom,ballCenterX,ballCenterY,ballRadius;
    private int precisionAcceptance;
    private boolean initVelocity=false;
    private Velocity velocity=new Velocity(0,0);//25,32
    Handler handler;
    final private long UPDATE_MILLIS=15;
    private Runnable runnable;
    private Paint textPaint=new Paint();
    private Paint healthPaint=new Paint();
    private Integer[] colorsBrick = {
            Color.rgb(144, 238, 144),    // Light Green (Easy)
            Color.rgb(255, 223, 186),    // Light Peach (Easy)
            Color.rgb(173, 216, 230),    // Light Blue (Medium)
            Color.rgb(255, 255, 102),    // Soft Yellow (Medium)
            Color.rgb(255, 99, 71),      // Tomato Red (Hard)
            Color.rgb(139, 0, 0)         // Dark Red (Very Hard)
    };
    private Paint[] brickPaints = new Paint[colorsBrick.length];
    private float TEXT_SIZE=50;
    private int paddleX,paddleY;
    private boolean hasMagnet=false;
    private Bitmap[] magnetEffects;
    private int magnetEffectWidth,magnetEffectHeight;
    private int magnetFrame=0;
    private int oldX,oldPaddleX; //oldPaddleX for onTouch
    private float velocityPaddle;
    public int points;
    final private int maxLife=3;
    private int life;
    private Bitmap ball,paddle, ballBefore;
    private Bitmap[] iceBalls;
    private int ballAttack;
    int ballSizeChangeFactor;
    int dWidth,dHeight;
    int ballWidth,ballHeight;
    MediaPlayer mpHit,mpMiss,mpBreak,mpThunder,mpFlame,mpFreeze,mpMagnet,mpHeal;
    Random random;

    int numBricks,brokenBricks,iniNumBricks;
    boolean gameOver=false;

    // Fernando
    float scaleBall=0.05f;
    float scaleBrick=0.5f;

    int brickRows=10;//6*((int) (1/scaleBrick));
    int brickColumns=12;//8*((int) (1/scaleBrick));
    int brickWidth,brickHeight,brickRadius;
    int brickpadding;
    int bricksTopMargin=600;
    int colorBrick;

    private int numColors=colorsBrick.length;
    private Brick[] bricks=new Brick[brickRows*brickColumns];

    private Flame flame;
    private Bitmap brickFlame,brickMagnet,brickHP;
    private boolean flameOneTimeEffect=false;

    Paint frostPaint = new Paint();
    private Bitmap brickFrost;
    Paint hpTextPaint=new Paint();

    private List<Lightning> lightningEffects;
    int lightningAnimation=0;
    private Bitmap[] croppedLightningAnimations;
    private Spiral spiral;
    private Fuego fireEffect;
    private boolean isLightningActive = false;
    private boolean isSpiralActive = false;
    private boolean isFireActive = false;
    private boolean isFrostActive = false;
    private boolean isFlameActive=false;

    private boolean isInvincible=true;


    // buttons
    public boolean gamePaused=false;
    /*
    private Bitmap restartIcon,resumeIcon,menuIcon,pauseIcon,quitIcon;
    private float iconSize = 90; // Size of each icon
    private float iconPadding = 30;  // Padding between icons
    */
    // target line
    private int targetArrowX,targetArrowY;
    private Paint arrowPaint=new Paint();

    // action zones
    private Paint dashedLinePaint = new Paint();
    private int dashedPos;

    // level passing animation and congratulations
    private List<Firework> fireworks = new ArrayList<>();
    private long startTimeFirework;
    private boolean showCongratulatoryMessage = false;

    // ads
    private boolean loadAd=false;


    // bg
    private Bitmap bg;
    private Paint canvasPaint;
    private int bgNum;

    public GameView(Context context,MainActivity mainActivity) {
        super(context);
        this.mainActivity=mainActivity;
        this.context=context;
        sharedPreferences = context.getSharedPreferences("GameData", MODE_PRIVATE);
        editor=sharedPreferences.edit();
        initializeGame();
    }

    public GameView(Context context) {
        super(context);
        this.context=context;
        if (context instanceof MainActivity) {
            this.mainActivity = (MainActivity) context;
        }
        sharedPreferences = context.getSharedPreferences("GameData", MODE_PRIVATE);
        editor=sharedPreferences.edit();
        initializeGame();
    }

    public void initializeGame() {
        if (mainActivity != null) {
            app = (MyApp) mainActivity.getApplicationContext();
        }
        initiateBitmaps();
        initiateParams();
        initiateMedias();
        initiatePaints();
        createBricks();
        createSpecialEffects();
    }

    private void initiateParams() {
        brokenBricks = 0;
        numBricks = 0;
        if (levelNum==1) {
            life = maxLife;
        } else {
            if (life<maxLife){
                life++;
            }
        }
        counter=0;
        ballSizeChangeFactor=1;
        gameOver=false;
        gamePaused=false;
        isLightningActive = false;
        isSpiralActive = false;
        isFireActive = false;
        isFrostActive = false;
        isFlameActive=false;
        isInvincible=false;
        Display display = ((Activity) getContext()).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        dWidth = size.x;
        dHeight = size.y;
        paddleY = dHeight * 4 / 5;
        paddleX = dWidth / 2 - paddle.getWidth() / 2;
        ballX = paddleX + paddle.getWidth() / 2 - ball.getWidth() / 2;//random.nextInt(dWidth-50);
        ballY = paddleY - ball.getHeight();//dHeight/3;
        ballWidth = ball.getWidth();
        ballHeight = ball.getHeight();
        ballAttack=1+levelNum/10;
        precisionAcceptance=1;
        targetArrowX=dWidth/2;
        targetArrowY=550;
        dashedPos=paddleY-50;
        initVelocity=false;
        velocity.setX(0);
        velocity.setY(0);
        velocityPaddle=0;
        brickWidth= (int) (((float) dWidth)/brickColumns);
        brickHeight=(int) (((float) dHeight)/12.0f * scaleBrick);

        // overwrite game state
        String loadedGameState=sharedPreferences.getString("loaded_game_state", null);
        if (loadedGameState!=null){
            Gson gson = new Gson();
            GameState gameState = gson.fromJson(loadedGameState, GameState.class);
            life=gameState.life;
            levelNum=gameState.levelNum;
            points=gameState.points;
            Log.e("info","game loaded in the gameview");
            editor.remove("loaded_game_state");
            editor.apply();
        }
    }

    public void initiateEverything(){
        life=maxLife;
        levelNum=1;
        points=0;
    }
    private void initiateBitmaps(){
        random = new Random();
        Matrix matrix = new Matrix();
        matrix.postScale(scaleBall, scaleBall);
        ballBefore = BitmapFactory.decodeResource(getResources(), R.drawable.ball2);
        ball = Bitmap.createBitmap(ballBefore, 0, 0, ballBefore.getWidth(), ballBefore.getHeight(), matrix, true);
        iceBalls =new Bitmap[5];
        for (int i=0;i<5;i++){
            iceBalls[i]=Bitmap.createScaledBitmap(ball, ball.getWidth() * (i+2), ball.getHeight()  * (i+2), false); // frost
        }

        paddle = BitmapFactory.decodeResource(getResources(), R.drawable.sprites);
        // brick lightning animation
        croppedLightningAnimations = new Bitmap[7];
        croppedLightningAnimations[0] = BitmapFactory.decodeResource(getResources(), R.drawable.li);
        croppedLightningAnimations[1] = BitmapFactory.decodeResource(getResources(), R.drawable.li2);
        croppedLightningAnimations[2] = BitmapFactory.decodeResource(getResources(), R.drawable.li3);
        croppedLightningAnimations[3] = BitmapFactory.decodeResource(getResources(), R.drawable.li4);
        croppedLightningAnimations[4] = BitmapFactory.decodeResource(getResources(), R.drawable.li5);
        croppedLightningAnimations[5] = BitmapFactory.decodeResource(getResources(), R.drawable.li6);
        croppedLightningAnimations[6] = BitmapFactory.decodeResource(getResources(), R.drawable.li7);

        // brick flame pic, frost
        brickFlame = BitmapFactory.decodeResource(getResources(), R.drawable.blue_fire);
        brickFrost = BitmapFactory.decodeResource(getResources(), R.drawable.snow_flake);
        brickMagnet = BitmapFactory.decodeResource(getResources(), R.drawable.magnes);
        brickHP = BitmapFactory.decodeResource(getResources(), R.drawable.love_shield);
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                invalidate();
            }
        };

        // magnet effects
        magnetEffects= new Bitmap[5];
        magnetEffects[0]=BitmapFactory.decodeResource(getResources(), R.drawable.ele1);
        magnetEffects[1]=BitmapFactory.decodeResource(getResources(), R.drawable.ele2);
        magnetEffects[2]=BitmapFactory.decodeResource(getResources(), R.drawable.ele3);
        magnetEffects[3]=BitmapFactory.decodeResource(getResources(), R.drawable.ele4);
        magnetEffects[4]=BitmapFactory.decodeResource(getResources(), R.drawable.ele5);
        magnetEffectWidth=magnetEffects[0].getWidth();
        magnetEffectHeight=magnetEffects[0].getHeight();

        /*
        restartIcon = BitmapFactory.decodeResource(getResources(), R.drawable.restart);
        resumeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.resume);
        pauseIcon = BitmapFactory.decodeResource(getResources(), R.drawable.pause2);
        quitIcon = BitmapFactory.decodeResource(getResources(), R.drawable.exit);
        menuIcon = BitmapFactory.decodeResource(getResources(), R.drawable.menu);
        restartIcon = Bitmap.createScaledBitmap(
                restartIcon,
                restartIcon.getWidth() /2,
                restartIcon.getHeight() /2,
                false
        );

        resumeIcon = Bitmap.createScaledBitmap(
                resumeIcon,
                resumeIcon.getWidth()/2,
                resumeIcon.getHeight()/2,
                false
        );

        pauseIcon = Bitmap.createScaledBitmap(
                pauseIcon,
                pauseIcon.getWidth()/2,
                pauseIcon.getHeight() /2,
                false
        );

        quitIcon = Bitmap.createScaledBitmap(
                quitIcon,
                quitIcon.getWidth() /2,
                quitIcon.getHeight() /2,
                false
        );

        menuIcon = Bitmap.createScaledBitmap(
                menuIcon,
                menuIcon.getWidth()/2,
                menuIcon.getHeight()/2,
                false
        );*/

        // bg
        bgNum=random.nextInt(16)+1;
        int resId = getResources().getIdentifier("bg_level" + bgNum, "drawable", getContext().getPackageName());
        if (resId != 0) {
            bg = BitmapFactory.decodeResource(getResources(), resId);
        } else {
            bg = BitmapFactory.decodeResource(getResources(), R.drawable.bg_level1);
        }

    }
    private void createSpecialEffects() {
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

    private void initiatePaints() {
        textPaint.setColor(Color.parseColor("#4997D0"));
        textPaint.setTextSize(TEXT_SIZE);
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);
        int[] healthColors = {
                Color.parseColor("#FF0000"),
                Color.parseColor("#FFD700"),
                Color.parseColor("#32CD32")
        };
        float[] hpPositions = {0f, 0.7f, 1f}; // Positions: Green at 0, Gold at 0.5, Red at 1
        LinearGradient hpGradient = new LinearGradient(dWidth - 400, 0, dWidth - 20, 0, healthColors, hpPositions, Shader.TileMode.CLAMP);
        healthPaint.setShader(hpGradient);
        healthPaint.setAntiAlias(true);
        hpTextPaint = new Paint();
        hpTextPaint.setColor(Color.WHITE);  // Text color
        hpTextPaint.setTextSize(40f);  // Text size
        hpTextPaint.setTextAlign(Paint.Align.CENTER);
        //brickPaint.setAntiAlias(true);
        // arrow to target
        arrowPaint.setColor(Color.GRAY);
        arrowPaint.setAntiAlias(true);
        arrowPaint.setStyle(Paint.Style.FILL);
        // action zone
        dashedLinePaint.setColor(Color.GRAY); // Set the color of the dashed line
        dashedLinePaint.setStyle(Paint.Style.STROKE); // Stroke style for lines
        dashedLinePaint.setStrokeWidth(5f); // Width of the line
        dashedLinePaint.setAntiAlias(true);
        dashedLinePaint.setPathEffect(new DashPathEffect(new float[]{20, 10}, 0));
        // bricks
        for (int i = 0; i < colorsBrick.length; i++) {
            RadialGradient gradientBrick = new RadialGradient(
                    dWidth / 2, -dHeight*100, brickHeight/2,
                    colorsBrick[i],Color.WHITE,
                    Shader.TileMode.MIRROR
            );
            brickPaints[i] = new Paint();
            brickPaints[i].setShader(gradientBrick);
        }

        // bg
        canvasPaint= new Paint();
        canvasPaint.setAlpha(128);
    }

    private void initiateMedias() {
        mpHit = MediaPlayer.create(context, R.raw.shot);
        mpMiss = MediaPlayer.create(context, R.raw.miss);
        mpBreak = MediaPlayer.create(context, R.raw.breaking);
        mpThunder = MediaPlayer.create(context, R.raw.rock_breaking);
        mpFlame = MediaPlayer.create(context, R.raw.flame);
        mpFreeze = MediaPlayer.create(context, R.raw.freeze);
        mpMagnet = MediaPlayer.create(context, R.raw.magnet);
        mpHeal = MediaPlayer.create(context, R.raw.heal);
    }

    private void createBricks() {
        numBricks=0;

        // load level
        Brick emptyBrickPlaceHolder=new Brick(0,0,0,0,Color.BLACK);
        //Log.e("info","level num is "+levelNum);
        Level level =loadLevel("levels/level"+levelNum+".json");
        if (level !=null){
            brickMap=level.getBrickMap();
            //level.printBrickMap();
        }

        int currentColorIndex;

        brickpadding=2;
        brickRadius=(int) MathUtils.distance(0,0,(brickWidth)/2+brickpadding,brickHeight/2+brickpadding);
        //colorBrick=colorsBrick[0];
        //brickPaint.setShadowLayer(10, 5, 5, Color.BLUE);
        for (int column=0;column<brickColumns;column++) {
            for (int row=0;row<brickRows;row++) {
                Brick newBrick;
                if (brickMap[row][column]==0) {
                    newBrick=emptyBrickPlaceHolder;
                    newBrick.setInvisible();
                    brokenBricks++;
                } else {
                    newBrick=new Brick(row,column,brickWidth,brickHeight,colorBrick);
                    newBrick.hp=brickMap[row][column];//initialize the hp of brick
                    /*
                    int colorDecider=random.nextInt(7);
                    if (numBricks>brickColumns && colorDecider==2){
                        colorBrick=bricks[numBricks-brickColumns].color;
                    } else if (colorDecider==3) {
                        currentColorIndex=Arrays.asList(colorsBrick).indexOf(colorBrick);
                        if (currentColorIndex==numColors-1){
                            currentColorIndex=-1;
                        }
                        colorBrick=colorsBrick[currentColorIndex+1];
                    }*/
                    newBrick.left = column * brickWidth +brickpadding;
                    newBrick.top = row * brickHeight + bricksTopMargin+brickpadding;
                    newBrick.right = (1 + column) * brickWidth-brickpadding;
                    newBrick.bottom = (row + 1) * brickHeight+ bricksTopMargin-brickpadding;
                    /*newBrick.gradient  = new LinearGradient(
                            newBrick.left,  newBrick.top,  newBrick.right,  newBrick.bottom,
                            colorBrick, Color.WHITE,  // Colors for the gradient
                            Shader.TileMode.CLAMP);*/
                    if (row % 2 !=1){
                        int decidePutLightning=random.nextInt(brickRows); //brickRows*3
                        if (decidePutLightning==1){
                            newBrick.brickType=BrickType.LIGHTNING;
                        }
                        else if (decidePutLightning==2 & levelNum>3){
                            newBrick.brickType=BrickType.FIRE_BALL;
                        }
                        else if (decidePutLightning==3 & levelNum>1){
                            newBrick.brickType=BrickType.ICE_BALL;
                        }
                        else if (decidePutLightning==4 & levelNum>2){
                            newBrick.brickType=BrickType.MAGNET_PADDLE;
                        }
                    } else  {
                        int decidePutLightning2=random.nextInt(brickRows*2);
                        if (decidePutLightning2==5 & levelNum>2){
                            newBrick.brickType=BrickType.GET_HP;
                        }
                    }

                }
                bricks[numBricks]=newBrick;
                numBricks++;
            }
        }
        iniNumBricks=numBricks-brokenBricks;
        //.e("info","There are "+String.valueOf(numBricks)+" bricks"+"broken"+brokenBricks+"ok"+iniNumBricks);
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
        //Log.e("info",""+dWidth+dHeight+"bitmap"+bgNum);
        canvas.drawBitmap(bg, null, new Rect(0, 0, dWidth, dHeight), canvasPaint);
        if (initVelocity) {
            //Log.e("info","v"+velocity.getX()+"y"+velocity.getY());
            ballX+=velocity.getX();
            ballY+=velocity.getY();
        }
        calculateBallPos();

        if (ballLeft<=1) {
            ballX=(ballSizeChangeFactor-1)*ballWidth; //recover
            velocity.setX(Math.abs(velocity.getX()));
        } else if (ballRight>=dWidth ){
            ballX=dWidth-ballWidth*ballSizeChangeFactor;
            velocity.setX(-Math.abs(velocity.getX()));
        }

        if (ballTop<=1) {
            ballY=(ballSizeChangeFactor-1)*ballHeight;
            velocity.setY(Math.abs(velocity.getY()));
        }

        // miss
        if (ballBottom>=dHeight-precisionAcceptance) {
            if (mpMiss!=null && !mpMiss.isPlaying()) {
                mpMiss.start();
            }
            life--;
            if (life==0) {
                gameOver=true;
                launchGameOver();
            }
            velocity.setX(0);
            velocity.setY(0);
            ballX=paddleX+paddle.getWidth()/2-ballWidth/2;
            ballY=paddleY-ballHeight*(ballSizeChangeFactor+1)/2;
            initVelocity=false;
        }

        isInvincible=false;
        if ( (isInvincible && velocity.getY() >0) ||  ((
                (
                (ballRight>paddleX
                        && ballRight<paddleX+paddle.getWidth())
                || (ballLeft>paddleX
                        && ballLeft<paddleX+paddle.getWidth())
                )
                && (ballBottom>=paddleY)
                && (ballBottom-velocity.getY()<paddleY)
                ))
                && initVelocity && velocity.getY() >0) {

            if (hasMagnet) {
                // if has magnet, reduce velocity to 0, fix ball on paddle, velinit=false
                initVelocity=false;
                velocity.setX(0);
                velocity.setY(0);
                ballX=paddleX+paddle.getWidth()/2-ball.getWidth()/2;
                ballY=paddleY-ballHeight*(ballSizeChangeFactor+1)/2;
            } else { // otherwise go ahead and treat the collison normally by reflecting the ball
                if (mpHit!=null && !mpHit.isPlaying()) {
                    mpHit.start();
                }
                ballY=paddleY-ballHeight*(ballSizeChangeFactor+1)/2;
                int newVx=velocity.getX();
                int newVy=(velocity.getY())*-1;
                //  need logic to control them so that they are not too big
                int combinedV=(int) MathUtils.distance(0,0,newVx,newVy);
                if (combinedV>40){
                    newVx=40*newVx/combinedV;
                    newVy=40*newVy/combinedV;
                }
                velocity.setX(newVx+(int) velocityPaddle/2);
                velocity.setY(newVy);
            }
        }

        canvas.drawBitmap(paddle,paddleX,paddleY,null);
        if (hasMagnet) {
            canvas.drawBitmap(magnetEffects[magnetFrame],paddleX+(paddle.getWidth()-magnetEffectWidth)*(magnetFrame-1)/3+20
                    ,paddleY-magnetEffectHeight,null);
            magnetFrame++;
            if (magnetFrame>4) magnetFrame=0;
        }

        for (int i=0; i<numBricks;i++){
            if (bricks[i].getVisibility()) {
                if (isLightningActive) {
                    int decideStruckByLightning=random.nextInt(12);
                    if (brokenBricks>=0.9*numBricks) {
                        if (bricks[i].hp<=1){
                            bricks[i].setInvisible();
                            points+=20;
                            brokenBricks++;
                            //Log.e("info","brick"+i+"brokenBricks"+brokenBricks);
                        }
                    }
                    if (decideStruckByLightning==1 && brokenBricks<=0.8*numBricks && bricks[i].hp<=1
                            && bricks[i].brickType==BrickType.COMMON) {
                        bricks[i].setInvisible();
                        points+=15;
                        brokenBricks++;
                       // Log.e("info","type 2 brick"+i+"brokenBricks"+brokenBricks);
                    }
                }

                if (flameOneTimeEffect) {
                    //Log.e("info","Start effect for flame");
                    int decideStruckByFlame=random.nextInt(3);
                    if (bricks[i].left>=flame.x-55 && bricks[i].left<=flame.x+55 && bricks[i].bottom<flame.y+20) {
                        bricks[i].setInvisible();
                        brokenBricks++;
                    } else if (bricks[i].left>=flame.x-250 && bricks[i].left<=flame.x+250 && bricks[i].bottom<flame.y+40) {
                        if (decideStruckByFlame==1) {
                            bricks[i].setInvisible();
                            brokenBricks++;
                        }
                    }
                }
                //brickPaint.setShader(bricks[i].gradient);
                //Log.e("info",bricks[i].toString());
                //brickPaint.setShader(gradientArrayBrick[bricks[i].hp-1]);
                canvas.drawRect(bricks[i].left, bricks[i].top, bricks[i].right, bricks[i].bottom, brickPaints[bricks[i].hp-1]);

                float bitmapWidth,bitmapHeight,scaleX,scaleY,translateX,translateY;

                if (bricks[i].brickType==BrickType.LIGHTNING) {
                    if (lightningAnimation>=croppedLightningAnimations.length){
                        lightningAnimation=0;
                    }
                    Bitmap bitmapToDraw = croppedLightningAnimations[lightningAnimation];
                    bitmapWidth = bitmapToDraw.getWidth();
                    bitmapHeight = bitmapToDraw.getHeight();
                    scaleX = (brickWidth-2*brickpadding) / bitmapWidth;
                    scaleY = (brickHeight-2*brickpadding) / bitmapHeight;
                    Matrix matrixLightning = new Matrix();
                    matrixLightning.postScale(scaleX, scaleY);
                    translateX =  bricks[i].left + (brickWidth-2*brickpadding - bitmapWidth * scaleX) / 2;
                    translateY =  bricks[i].top + (brickHeight-2*brickpadding - bitmapHeight * scaleY) / 2;
                    matrixLightning.postTranslate(translateX, translateY);
                    canvas.drawBitmap(bitmapToDraw, matrixLightning, null);
                } else if (bricks[i].brickType==BrickType.FIRE_BALL) {
                    bitmapWidth=brickFlame.getWidth();
                    bitmapHeight=brickFlame.getHeight();
                    scaleX = (brickWidth-2*brickpadding) / bitmapWidth;
                    scaleY = (brickHeight-2*brickpadding) / bitmapHeight;
                    Matrix matrixFire = new Matrix();
                    matrixFire.postScale(scaleX, scaleY);
                    translateX =  bricks[i].left + (brickWidth-2*brickpadding - bitmapWidth * scaleX) / 2;
                    translateY =  bricks[i].top + (brickHeight-2*brickpadding - bitmapHeight * scaleY) / 2;
                    matrixFire.postTranslate(translateX, translateY);
                    canvas.drawBitmap(brickFlame, matrixFire, null);
                } else if (bricks[i].brickType==BrickType.ICE_BALL) {
                    bitmapWidth=brickFrost.getWidth();
                    bitmapHeight=brickFrost.getHeight();
                    scaleX = (brickWidth-2*brickpadding) / bitmapWidth;
                    scaleY = (brickHeight-2*brickpadding) / bitmapHeight;
                    Matrix matrixFrost = new Matrix();
                    matrixFrost.postScale(scaleX, scaleY);
                    translateX =  bricks[i].left + (brickWidth-2*brickpadding - bitmapWidth * scaleX) / 2;
                    translateY =  bricks[i].top + (brickHeight-2*brickpadding - bitmapHeight * scaleY) / 2;
                    matrixFrost.postTranslate(translateX, translateY);
                    canvas.drawBitmap(brickFrost, matrixFrost, null);
                } else if (bricks[i].brickType==BrickType.MAGNET_PADDLE) {
                    bitmapWidth=brickMagnet.getWidth();
                    bitmapHeight=brickMagnet.getHeight();
                    scaleX = (brickWidth-2*brickpadding) / bitmapWidth;
                    scaleY = (brickHeight-2*brickpadding) / bitmapHeight;
                    Matrix matrixMagnet = new Matrix();
                    matrixMagnet.postScale(scaleX, scaleY);
                    translateX =  bricks[i].left + (brickWidth-2*brickpadding - bitmapWidth * scaleX) / 2;
                    translateY =  bricks[i].top + (brickHeight-2*brickpadding - bitmapHeight * scaleY) / 2;
                    matrixMagnet.postTranslate(translateX, translateY);
                    canvas.drawBitmap(brickMagnet, matrixMagnet, null);
                } else if (bricks[i].brickType==BrickType.GET_HP) {
                    bitmapWidth=brickHP.getWidth();
                    bitmapHeight=brickHP.getHeight();
                    scaleX = (brickWidth-2*brickpadding) / bitmapWidth;
                    scaleY = (brickHeight-2*brickpadding) / bitmapHeight;
                    Matrix matrixHP = new Matrix();
                    matrixHP.postScale(scaleX, scaleY);
                    translateX =  bricks[i].left + (brickWidth-2*brickpadding - bitmapWidth * scaleX) / 2;
                    translateY =  bricks[i].top + (brickHeight-2*brickpadding - bitmapHeight * scaleY) / 2;
                    matrixHP.postTranslate(translateX, translateY);
                    canvas.drawBitmap(brickHP, matrixHP, null);
                }
            }
        }
        lightningAnimation++; // to have the same animation for all the lightning bricks
        // deactivate after one effect
        if (flameOneTimeEffect) {
            flameOneTimeEffect=false;
        }

        canvas.drawText("Points: "+points,20,TEXT_SIZE+190,textPaint);
        canvas.drawRoundRect(dWidth-400,200,(dWidth-400)+ ((float) life/maxLife)*380,250,15,15,healthPaint);

        String healthText = "Health: " + (int) life + " / "+(int) maxLife;  // Text to display (you can adjust this)
        float textX = dWidth-400 +190;  // X coordinate (centered)
        float textY = 225 - ((hpTextPaint.descent() + hpTextPaint.ascent()) / 2);  // Y coordinate (centered vertically)
        canvas.drawText(healthText, textX, textY, hpTextPaint);

        for (int i=numBricks-1;i>=0;i--){
            if (bricks[i].getVisibility()) {
                if (MathUtils.distance(ballX+ballWidth/2,ballY+ballWidth/2,
                        (bricks[i].left+bricks[i].right)/2,(bricks[i].top+bricks[i].bottom)/2)
                    <=ballRadius+brickRadius+precisionAcceptance) // add a margin
                {
                    //Log.e("info",""+brokenBricks+"total"+numBricks);
                    ballX-=velocity.getX();
                    ballY-= velocity.getY();
                    breakAny=true;

                    bricks[i].hp-=ballAttack;
                    if (bricks[i].hp<=0){
                        bricks[i].setInvisible();
                        points+=10;
                        brokenBricks++;
                    }

                    //lightning
                    if (bricks[i].brickType==BrickType.LIGHTNING){
                        isLightningActive=true;
                        for (Lightning lightningEffect : lightningEffects) {
                            lightningEffect.startLightningEffect();
                        }
                        if (mpThunder!= null && !mpThunder.isPlaying()) {
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
                                    //mpThunder.release();
                                }
                                isLightningActive = false;
                            }
                        }, 7000);
                    } else if (bricks[i].brickType==BrickType.FIRE_BALL) {
                        isFlameActive=true;
                        flameOneTimeEffect=true;
                        if (isFrostActive) {
                            isFrostActive=false;
                        }
                        ballSizeChangeFactor=4;
                        ballAttack=4;
                        if (mpFlame!= null && !mpFlame.isPlaying()) {
                            mpFlame.start();
                        }

                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                isFlameActive = false;
                            }
                        }, 10000);
                    } else if (bricks[i].brickType==BrickType.ICE_BALL) {
                        isFrostActive=true;
                        if (isFireActive) {
                            isFireActive=false;
                            ballSizeChangeFactor=1;
                            ballAttack=1;
                        }
                        if (ballSizeChangeFactor<7){
                            ballSizeChangeFactor++;
                            ballAttack++;
                        }
                        if (mpFreeze!= null && !mpFreeze.isPlaying()) {
                            mpFreeze.start();
                        }
                    } else if (bricks[i].brickType==BrickType.MAGNET_PADDLE) {
                        hasMagnet=true;
                        if (mpMagnet!= null && !mpMagnet.isPlaying()) {
                            mpMagnet.start();
                        }
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                hasMagnet = false;
                            }
                        }, 30000);
                    } else if (bricks[i].brickType==BrickType.GET_HP) {
                        if (mpHeal!= null && !mpHeal.isPlaying()) {
                            mpHeal.start();
                        }
                        life+=2;
                        if (life>5){
                            life=5;
                        }
                    }
                }
            }
        }
        // several bricks can break but the change for the ball only happen once
        if (breakAny){
            //Log.e("info","break now");
            if (mpBreak!=null && !mpBreak.isPlaying()) {
                mpBreak.start();
            }
            // the ball will not advance if there is collison
            ballX-=velocity.getX();
            ballY-=velocity.getY();
            velocity.setY((velocity.getY()+1)*-1);
        }

        // draw the ball at the end
        if (isFrostActive) {
            canvas.drawBitmap(iceBalls[ballSizeChangeFactor-2],ballLeft,ballTop,null);
        } else {
            canvas.drawBitmap(ball,ballX,ballY,null);
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

        // on the ball
        if (isFlameActive) {
            fireEffect.draw(canvas, ballCenterX*2, ballCenterY*2);
        }


        // draw buttons
        //drawButtons(canvas);
        // target arrow
        if (!initVelocity){
            // if not then target with the targetArrow
            //Log.e("info","targetting"+targetArrowY+"paddle Y"+paddleY);
            if (targetArrowY<ballY) {
                float startX=ballX+ball.getWidth()/2;
                float startY=ballY+ball.getHeight()/2;
                float dx = (targetArrowX-startX) ;
                float dy = (targetArrowY-startY);

                float distance = (float) Math.sqrt(dx * dx + dy * dy);
                int numBalls = (int) (distance / 60);

                for (int i = 1; i < numBalls; i++) {
                    float arrowX = startX + i * dx / numBalls;
                    float arrowY = startY + i * dy / numBalls;
                    int alphaArrow = (int) (255 * (1 - (float) i / numBalls));
                    if (alphaArrow < 50) alphaArrow = 50;
                    arrowPaint.setAlpha(alphaArrow);
                    canvas.drawCircle(arrowX, arrowY, 15, arrowPaint);
                    if (arrowY<300) break;
                }
            }
        }

        canvas.drawLine(0, paddleY - 50, dWidth, dashedPos, dashedLinePaint);

        if (brokenBricks>=numBricks) {
            if (startTimeFirework ==0){
                levelNum++;
                //Log.e("info","game over, now going to "+levelNum);
                if (levelNum>maxLevelNum){// max level
                    levelNum=1;
                }
                initializeGame();
                invalidate();
                startTimeFirework = System.currentTimeMillis();
                for (int i = 0; i < 5; i++) {
                    fireworks.add(new Firework(getWidth() / 2, getHeight() / 2, Color.RED));
                }
            }
            showCongratulatoryMessage =true;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showCongratulatoryMessage = false;  // Set flag to show the message
                    loadAd=true;
                    startTimeFirework=0;
                    invalidate();  // Force a redraw of the view
                }
            }, 3000);
        }
        //Log.e("info",""+ballX+"y"+ballY+"vx"+velocity.getX()+"vy"+velocity.getY());
        if (showCongratulatoryMessage) {
            long elapsedTime = System.currentTimeMillis() - startTimeFirework;
            CanvasUtils.drawCongratulationText(canvas, "CONGRATULATIONS!", dWidth/2,dHeight/2, elapsedTime);
            CanvasUtils.drawFireworks(canvas, fireworks);
            if (loadAd){
                if (app != null) {
                    app.showInterstitialAd();
                } else {
                    //Log.e("GameView", "MyApp instance is null, cannot show interstitial ad.");
                }
                loadAd=false;
            }

        }

        if (!gameOver) {
            if (! gamePaused) {
                app.showInterstitialAd();
                handler.postDelayed(runnable,UPDATE_MILLIS);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!showCongratulatoryMessage) {
            int action;
            int touchX=(int) event.getX();
            int touchY=(int) event.getY();
            if (touchY>=dashedPos) { // this condition might not be useful
                action=event.getAction();
                if (action==MotionEvent.ACTION_DOWN) {
                    oldX=(int) event.getX();
                    oldPaddleX=paddleX;
                    lastTouch=currentTouch;
                    /*
                    float startIconY = dHeight - iconSize - iconPadding;

                    // Restart button bounds
                    float restartLeft = iconPadding;
                    float restartRight = restartLeft + iconSize;
                    if (touchX >= restartLeft && touchX <= restartRight && touchY >= startIconY && touchY <= startIconY + iconSize) {
                        restartGame();
                        return true;
                    }

                    // Resume/Pause button bounds
                    float resumePauseLeft = iconPadding + iconSize + iconPadding;
                    float resumePauseRight = resumePauseLeft + iconSize;
                    if (touchX >= resumePauseLeft && touchX <= resumePauseRight && touchY >= startIconY && touchY <= startIconY + iconSize) {
                        toggleGamePause();
                        return true;
                    }

                    // Quit button bounds
                    float quitLeft = resumePauseLeft + iconSize + iconPadding;
                    float quitRight = quitLeft + iconSize;
                    if (touchX >= quitLeft && touchX <= quitRight && touchY >= startIconY && touchY <= startIconY + iconSize) {
                        // Quit game logic
                        quitGame();
                        return true;
                    }

                    // Menu button bounds
                    float menuLeft = quitLeft + iconSize + iconPadding;
                    float menuRight = menuLeft + iconSize;
                    if (touchX >= menuLeft && touchX <= menuRight && touchY >= startIconY && touchY <= startIconY + iconSize) {
                        // Open menu logic
                        if (mainActivity != null) {
                            mainActivity.openMenu(null); // Call openMenu() in MainActivity
                        }
                        return true;
                    }*/

                    // on finger tap
                    if (!initVelocity) {
                        velocityPaddle=0;
                        int velo=getRandomVelocity();
                        int dx=targetArrowX-ballCenterX;
                        int dy=targetArrowY-ballCenterY;
                        float modeArrow= MathUtils.distance(0,0,dx,dy);
                        float  cosX=dx/modeArrow;
                        float  sinY=dy/modeArrow;
                        velocity.setX((int) (velo*cosX));
                        velocity.setY((int) (velo*sinY));
                        initVelocity=true;
                    }

                }
                if (action==MotionEvent.ACTION_UP) {


                }
                if (action==MotionEvent.ACTION_MOVE) {
                    int shift=oldX-touchX;
                    int newPaddleX=oldPaddleX-shift;
                    if (newPaddleX<=0){
                        paddleX=0;
                    } else if (newPaddleX>dWidth-paddle.getWidth()) {
                        paddleX=dWidth-paddle.getWidth();
                    } else  {
                        paddleX=newPaddleX;
                    }

                    // if vel not set move the ball too
                    if (!initVelocity) {
                        ballX=paddleX+paddle.getWidth()/2-ball.getWidth()-2;
                        // ballY=paddleY-ball.getHeight(); no need cuz the ball is by default on the paddle
                    }

                    // calculate the velocity of the paddle to give a push to the ball
                    currentTouch=System.currentTimeMillis();
                    long timeElapsed = currentTouch - lastTouch;
                    if (timeElapsed > 0 && shift!=0) {
                        velocityPaddle = (-shift) / timeElapsed;
                        //Log.e("info","tl"+timeElapsed+"sh"+shift);
                    }
                    if (hasMagnet || !initVelocity) velocityPaddle=0;
                    lastTouch = currentTouch;
                }
            } else { // in the upper part
                action=event.getAction();
                if (action==MotionEvent.ACTION_DOWN) {
                }

                if (action==MotionEvent.ACTION_MOVE) {
                    if (!initVelocity) {
                        targetArrowX=(int) touchX;
                        targetArrowY=(int) touchY;
                    }
                }

                if (action==MotionEvent.ACTION_UP) {
                    if (!initVelocity) {
                        velocityPaddle = 0;
                    }

                }
            }
        }
        return true;
    }



    public void quitGame() {
        ((Activity) getContext()).finish();
    }

    public void openMenu() {
        // Intent intent = new Intent(getContext(), MainActivity.class);
        // getContext().startActivity(intent);
        // since the main activity was not terminated maybe it is better to do it like this
        ((Activity) getContext()).setContentView(R.layout.activity_main);
    }

    public void restartGame() {
        initializeGame();
        invalidate();
    }

    private void launchGameOver() {
        initializeGame(); // in case we go back to the game from game over

        handler.removeCallbacksAndMessages(null);
        Intent intent = new Intent(context, GameOver.class);
        intent.putExtra("points", points);
        context.startActivity(intent);
        ((Activity) context).finish();
    }

    private int getRandomVelocity(){
        int[] values={23,25,32,30,35,38};
        int index=random.nextInt(6);
        return values[index];
    }
/*
    private void drawButtons(Canvas canvas){
        // Define icon positions

        float startY = dHeight - iconSize - iconPadding; // Bottom padding
        float startX = iconPadding; // Starting X position

        // Draw Restart Icon
        canvas.drawBitmap(restartIcon, startX, startY, null);

        // Draw Resume/Pause Icon
        float resumePauseX = startX + iconSize + iconPadding;
        Bitmap currentIcon = gamePaused ? resumeIcon : pauseIcon;
        canvas.drawBitmap(currentIcon, resumePauseX, startY, null);

        // Draw Quit Icon
        float quitX = resumePauseX + iconSize + iconPadding;
        canvas.drawBitmap(quitIcon, quitX, startY, null);

        // Draw Menu Icon
        float menuX = quitX + iconSize +iconPadding;
        canvas.drawBitmap(menuIcon, menuX, startY, null);
    }*/

    private Level loadLevel(String fileName) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    context.getAssets().open(fileName)
            ));

            // Parse JSON using Gson
            Gson gson = new Gson();
            return gson.fromJson(reader, Level.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void calculateBallPos(){
        int deltaMinus=(ballSizeChangeFactor-1)/2*ballWidth;
        int deltaPlus=(ballSizeChangeFactor+1)/2*ballWidth;
        ballLeft=ballX-deltaMinus;
        ballRight=ballX+deltaPlus;
        ballTop=ballY-deltaMinus;
        ballBottom=ballY+deltaPlus;
        ballCenterX=ballX+ballWidth/2;
        ballCenterY=ballY+ballHeight/2;
        ballRadius=ballWidth*ballSizeChangeFactor/2;
    }
    public GameState getGameState(){
        return new GameState(points,levelNum,life);
    }

}







