package com.np.brickbreaker.models;

public class GameState {
    public int points;
    public int levelNum;
    public int life;

    // Constructor
    public GameState(int points, int levelNum, int life) {
        this.points = points;
        this.levelNum = levelNum;
        this.life = life;
    }


}
