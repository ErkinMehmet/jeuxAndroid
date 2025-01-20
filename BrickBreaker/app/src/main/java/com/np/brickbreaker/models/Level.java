package com.np.brickbreaker.models;

public class Level {
    private int level;
    private int[][] brickMap;

    public int getLevel() {
        return level;
    }

    public int[][] getBrickMap() {
        return brickMap;
    }

    public void printBrickMap(){
        for (int[] row : brickMap){
            StringBuilder rowString = new StringBuilder();
            for (int v : row){
                rowString.append(v).append(" ");
            }
            System.out.println(rowString.toString().trim());
        }
    }
}