package com.np.brickbreaker;


import android.graphics.LinearGradient;

import com.np.brickbreaker.enums.BrickType;
public class Brick
{
    private boolean isVisible;
    public int row,column,width,height;
    public float left,right,top,bottom;
    LinearGradient gradient;
    public int color;
    public int hp=5;
    public BrickType brickType=BrickType.COMMON;

    public Brick(int row,int column,int width,int height,int color) {
        this.row=row;
        this.column=column;
        this.width=width;
        this.height=height;
        this.isVisible=true;
        this.color=color;
    }

    public void setInvisible(){
        this.isVisible=false;
    }
    public boolean getVisibility(){
        return isVisible;
    }
    @Override
    public String toString() {
        return "Brick [width=" + width + ", height=" + height +
                ", row=" + row + ", column=" + column +
                ", isVisible=" + isVisible +
                ", left=" + left + ", top=" + top +
                ", right=" + right + ", bottom=" + bottom + "]";
    }
}
