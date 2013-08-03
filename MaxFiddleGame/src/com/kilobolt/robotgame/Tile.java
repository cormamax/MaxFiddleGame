package com.kilobolt.robotgame;

import android.graphics.Rect;
import android.util.Log;

import com.kilobolt.framework.Image;

public class Tile {

    private int tileX, tileY, speedX;
    private int origX, origY;
    public int type;
    private int orig_type;
    public Image tileImage;
    boolean traveled;

    //private Robot robot = GameScreen.getRobot();
    //private Background bg = GameScreen.getBg1();

    private Rect r;

    public Tile(int x, int y, int typeInt) {
        tileX = x * 40;	// 40 is width of the tile. Is needed so no overlapping
        tileY = y * 40; // of tiles
        origX = x * 40;
        origY = y * 40;
        
        type = typeInt;
        orig_type = typeInt;
        
        traveled = false;
        
        r = new Rect();

        if (type == 5) {
            tileImage = Assets.tiledirt;
        } else if (type == 8) {
            tileImage = Assets.tilegrassTop;
        } else if (type == 4) {
            tileImage = Assets.tilegrassLeft;

        } else if (type == 6) {
            tileImage = Assets.tilegrassRight;

        } else if (type == 2) {
            tileImage = Assets.tilegrassBot;
        } else {
            type = 0;
        }

    }

    public void update() {
        
        r.set(tileX, tileY, tileX+40, tileY+40);
    }

    public void update(int changeX, int changeY)
    {

        tileX = origX + changeX;
        tileY = origY + changeY;
        //tileX += changeX;
        //tileY += changeY;
        //Log.e("Tile", "tileX at " + tileX);
        //Log.e("Tile", "tileY at " + tileY);
        r.set(tileX, tileY, tileX+40, tileY+40);
    }
    
    public int getTileX() {
        return tileX;
    }

    public void setTileX(int tileX) {
        this.tileX = tileX;
    }

    public int getTileY() {
        return tileY;
    }

    public void setTileY(int tileY) {
        this.tileY = tileY;
    }

    public Image getTileImage() {
        return tileImage;
    }

    public void setTileImage(Image tileImage) {
        this.tileImage = tileImage;
    }
    
    public void resetTileImage()
    {
    	if (orig_type == 5) {
            tileImage = Assets.tiledirt;
        } else if (type == 8) {
            tileImage = Assets.tilegrassTop;
        } else if (type == 4) {
            tileImage = Assets.tilegrassLeft;

        } else if (type == 6) {
            tileImage = Assets.tilegrassRight;

        } else if (type == 2) {
            tileImage = Assets.tilegrassBot;
        } else {
            orig_type = 0;
        }
    }
    
    public void traveledOver()
    {
    	this.traveled = true;
    }
    
    public boolean isTraveled()
    {
    	return this.traveled;
    }
    
    public void resetTraveled()
    {
    	this.traveled = false;
    }
}