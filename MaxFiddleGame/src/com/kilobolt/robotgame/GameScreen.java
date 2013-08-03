package com.kilobolt.robotgame;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import com.kilobolt.framework.Game;
import com.kilobolt.framework.Graphics;
import com.kilobolt.framework.Input.TouchEvent;
import com.kilobolt.framework.Screen;

public class GameScreen extends Screen {
	enum GameState {
		Ready, Running, Paused, GameOver
	}

	GameState state = GameState.Ready;

	// Variable Setup
	private static Background bg1, bg2;
	
	// For dragging
	int curX = -1;
	int curY = -1;
	int prevX = -1;
	int prevY = -1;
	int totalX = 0;
	int totalY = 0;
	
	//private ArrayList<Tile> originalTilearray = new ArrayList<Tile>();
	private ArrayList<Tile> tilearray = new ArrayList<Tile>();
	private Queue<Integer> changedTiles = new LinkedList<Integer>();

	
	Paint paint, paint2;

	public GameScreen(Game game) {
		super(game);

		// Initialize game objects here
		bg1 = new Background(0, 0);
		bg2 = new Background(2160, 0);
		loadMap();
		// Defining a paint object
		paint = new Paint();
		paint.setTextSize(30);
		paint.setTextAlign(Paint.Align.CENTER);
		paint.setAntiAlias(true);
		paint.setColor(Color.WHITE);

		paint2 = new Paint();
		paint2.setTextSize(100);
		paint2.setTextAlign(Paint.Align.CENTER);
		paint2.setAntiAlias(true);
		paint2.setColor(Color.WHITE);
	}

	private void loadMap() {
		// Treat this map as a 2D array. Easier to manange for right now.
		// Make new Tile for tilearray and originalTilearray to avoid
		// reference issues. Before I copied tileArray into original
		// which caused the tiles to be changed twice due to referencing
		// the same tile
		for (int i = 0; i < 16; i++)
		{
			for (int j = 0; j < 16; j++)
			{
				if (i%2 == 0)
				{
					Tile t = new Tile(i, j, Character.getNumericValue('2'));
					//Tile o = new Tile(i, j, Character.getNumericValue('2'));
					tilearray.add(t);
					//originalTilearray.add(o);
				}
				else
				{
					Tile t = new Tile(i, j, Character.getNumericValue('8'));
					//Tile o = new Tile(i, j, Character.getNumericValue('8'));
					tilearray.add(t);
					//originalTilearray.add(o);
				}	
			}
		}
	}

	@Override
	public void update(float deltaTime) {
		List touchEvents = game.getInput().getTouchEvents();

		// We have four separate update methods in this example.
		// Depending on the state of the game, we call different update methods.
		// Refer to Unit 3's code. We did a similar thing without separating the
		// update methods.

		if (state == GameState.Ready)
			updateReady(touchEvents);
		if (state == GameState.Running)
			updateRunning(touchEvents, deltaTime);
		if (state == GameState.Paused)
			updatePaused(touchEvents);
		if (state == GameState.GameOver)
			updateGameOver(touchEvents);
	}

	private void updateReady(List touchEvents) {

		// This example starts with a "Ready" screen.
		// When the user touches the screen, the game begins.
		// state now becomes GameState.Running.
		// Now the updateRunning() method will be called!

		if (touchEvents.size() > 0)
			state = GameState.Running;
	}

	private void updateRunning(List<TouchEvent> touchEvents, float deltaTime) {

		// This is identical to the update() method from our Unit 2/3 game.

		// 1. All touch input is handled here:
		int len = touchEvents.size();
		//Log.e("Game", "number of touch events = " + len);
		for (int i = 0; i < len; i++) {
			TouchEvent event = touchEvents.get(i);
						
			// Map is kept in an array. The 2D map is kept in a 1D array
			// Use math to figure out location. Map goes i = column, j = row
			// So x = row/i, y = column/i
			// Width/Height of sample map is 16x16
			
			if (event.type == TouchEvent.TOUCH_DOWN) {

				Log.e("Game", "tile 0 is at " + tilearray.get(0).getTileX() + ", " + tilearray.get(0).getTileY());
				int locX = event.x - totalX;
				int locY = event.y - totalY;
				//Log.e("Game", "x = " + event.x);
				//Log.e("Game", "y = " + event.y);
				//Log.e("Game", "totalX = " + totalX);
				//Log.e("Game", "totalY = " + totalY);
				//Log.e("Game", "locX = " + locX);
				//Log.e("Game", "locY = " + locY);
				
				int column = locX/40;	// tells the column
				int row = locY/40;	// tells me the row
				//Log.e("Game", "column at " + column);
				//Log.e("Game", "row at " + row);
				// This is the location of the Tile in the array that is changed
				int loc = 16*column + row;
				
				
				
				//Tile t = new Tile(column, row, Character.getNumericValue('4'));
				tilearray.get(loc).setTileImage(Assets.tilegrassLeft);
				//tilearray.set(loc, t);
				
				// add changed tile to queue
				changedTiles.add(loc);
				
				// change surrounding tiles
				BFSTiles(column, row);
				
			}
			
			if (event.type == TouchEvent.TOUCH_UP) {
				// change tile type back
				// Can't do this due to referencing issues. object references and whatnot
				//tilearray.clear();
				//tilearray.addAll(originalTilearray);
				for (int j = 0; j < tilearray.size(); j++)
				{
					tilearray.get(j).resetTileImage();
					tilearray.get(j).resetTraveled();
				}
				
				Log.e("Game", "up event.x at " + event.x);
				Log.e("Game", "up event.y at " + event.y);
				
				Log.e("Game", "reseting dragging variables to -1");
				Log.e("Game", "tile 0 is at " + tilearray.get(0).getTileX() + ", " + tilearray.get(0).getTileY());
				
				curX = -1;
				curY = -1;
				prevX = -1;
				prevY = -1;
			}
			
			if (event.type == TouchEvent.TOUCH_DRAGGED)
			{
				if (prevX < 0 || prevY < 0)
				{
					curX = event.x;
					curY = event.y;
					prevX = event.x;
					prevY = event.y;
				}
				else
				{
					prevX = curX;
					prevY = curY;
					curX = event.x;
					curY = event.y;
				}
				
				// add end - begin basically
				totalX += (curX - prevX);
				totalY += (curY - prevY);
				
				/*Log.e("Game", "curX is now " + curX);
				Log.e("Game", "curY is now " + curY);
				Log.e("Game", "prevX is now " + prevX);
				Log.e("Game", "prevY is now " + prevY);
				Log.e("Game", "diffX added " + (curX - prevX));
				Log.e("Game", "diffY added " + (curY - prevY));
				Log.e("Game", "totalX is now " + totalX);
				Log.e("Game", "totalY is now " + totalY);
				*/
				
			}
		}
		
		updateTiles();
		
	}

	// You also need to update the original tile array. I think
	private void updateTiles() {

		for (int i = 0; i < tilearray.size(); i++) {
			Tile t = (Tile) tilearray.get(i);
			//Tile o = (Tile) originalTilearray.get(i);
			
			// update tiles with drag distances factored in
			
			// These cannot be negative because they refer to screen coordinates
			if (curX < 0 || curY < 0 || prevX < 0 || prevY < 0)
			{
				t.update();
			}
			else
			{
				//t.update(curX - prevX, curY - prevY);
				t.update(totalX, totalY);
			}
		}

		//Log.e("Game", "tile 0 at " + tilearray.get(0).getTileX() + ", " + tilearray.get(0).getTileY());
	}


	private void BFSTiles(int column, int row)
	{	// Also, should think about weight of tiles for different terrains.
		
		// movement = 3 for now.
		ArrayList<Integer> spots = new ArrayList<Integer>();
		Queue<Integer> queue = new LinkedList<Integer>();
		Queue<Integer> move_queue = new LinkedList<Integer>();
		queue.add(16*column+row);
		move_queue.add(0);
		
		// reverse is column = num/4, row = num%4
		// find which tiles to change
		int spot = 0;
		int move = 0;
		while (!queue.isEmpty())
		{
			spot = queue.remove();
			//Log.e("Game", "spot = " + spot);
			move = move_queue.remove();
			
			//Log.e("Game", "here " + spot);
			if ((move < 4) && (!tilearray.get(spot).isTraveled()))
			{
				//Log.e("Game", "spot added = " + spot);
				spots.add(spot);
			}
			else
			{
				if (move >= 4)
				{
					//Log.e("Game", "spot = " + spot + " move = " + move);
				}
				else if (!tilearray.get(spot).isTraveled())
				{
					//Log.e("Game", "spot = " + spot + " is already traveled");
				}
				continue;
			}
				
			//Log.e("Game", "Checking adjacent spots");
			int c = spot/16;
			int r = spot%16;
			//Log.e("Game", "c = " + c);
			//Log.e("Game", "r = " + r);
			if (c+1 < 16)
			{
				if (!tilearray.get(16*(c+1) + r).isTraveled())
				{
					tilearray.get(spot).traveledOver();
					queue.add(16*(c+1) + r);
					//Log.e("Game", "adding " + (16*(c+1) + r));
					move_queue.add(move+1);
				}
				//else
					//Log.e("Game", "c+1 already traveled");
			}
			//else
				//Log.e("Game", "column < 16");
			if (c-1 >= 0)
			{
				if (!tilearray.get(16*(c-1) + r).isTraveled())
				{
					tilearray.get(spot).traveledOver();
					queue.add(16*(c-1) + r);
					//Log.e("Game", "adding " + (16*(c-1) + r));
					move_queue.add(move+1);
				}
				//else
					//Log.e("Game", "c-1 already traveled");
			}
			//else
				//Log.e("Game", "column <= 0");
			if (r+1 < 16)
			{
				if (!tilearray.get(16*c + (r+1)).isTraveled())
				{
					tilearray.get(spot).traveledOver();
					queue.add(16*c + (r+1));
					//Log.e("Game", "adding " + (16*c + (r+1)));
					move_queue.add(move+1);
				}
				//else
					//Log.e("Game", "r+1 already traveled");
				
			}
			//else
				//Log.e("Game", "row < 16");
			if (r-1 >= 0)
			{
				if (!tilearray.get(16*c + (r-1)).isTraveled())
				{
					tilearray.get(spot).traveledOver();
					queue.add(16*c + (r-1));
					//Log.e("Game", "adding " + (16*c + (r-1)));
					move_queue.add(move+1);
				}
				//else
					//Log.e("Game", "r-1 already traveled");
			}
			//else
				//Log.e("Game", "row <= 16");
		}
		
		// Now change the tiles
		//Log.e("Game", "spots.size() = " + spots.size());
		for(int i = 0; i < spots.size(); i++)
		{
			//Log.e("Game", "changing tiles");
			//Log.e("Game", "i = " + i);
			int s = spots.get(i);
			int c = s/16;
			int r = s%16;
			//Log.e("Game", "s = " + s);
			//Log.e("Game", "c = " + c);
			//Log.e("Game", "r = " + r);
			//tilearray.set(s, new Tile(c, r, Character.getNumericValue('4')));
			tilearray.get(s).setTileImage(Assets.tilegrassLeft);
			changedTiles.add(s);
		}
		
		// set movementarray
		//movementarray.clear();
		//movementarray.addAll(spots);
		
	}
	
	private boolean inBounds(TouchEvent event, int x, int y, int width,
			int height) {
		if (event.x > x && event.x < x + width - 1 && event.y > y
				&& event.y < y + height - 1)
			return true;
		else
			return false;
	}

	private void updatePaused(List<TouchEvent> touchEvents) {
		
	}

	private void updateGameOver(List<TouchEvent> touchEvents) {
		
	}
	
	@Override
	public void paint(float deltaTime) {
		Graphics g = game.getGraphics();

		g.drawImage(Assets.background, bg1.getBgX(), bg1.getBgY());
		g.drawImage(Assets.background, bg2.getBgX(), bg2.getBgY());
		paintTiles(g);
		

	}

	private void paintTiles(Graphics g) {
		for (int i = 0; i < tilearray.size(); i++) {
			Tile t = (Tile) tilearray.get(i);
			if (t.type != 0) {
				g.drawImage(t.getTileImage(), t.getTileX(), t.getTileY());
			}
		}
	}

	public void animate() {
		
	}

	private void nullify() {

		

		// Call garbage collector to clean up memory.
		System.gc();

	}

	private void drawReadyUI() {
		Graphics g = game.getGraphics();

		

	}

	private void drawRunningUI() {
		Graphics g = game.getGraphics();
		
	}

	private void drawPausedUI() {
		Graphics g = game.getGraphics();
		// Darken the entire screen so you can display the Paused screen.
		

	}

	private void drawGameOverUI() {
		Graphics g = game.getGraphics();
		
	}

	@Override
	public void pause() {
		if (state == GameState.Running)
			state = GameState.Paused;

	}

	@Override
	public void resume() {
		if (state == GameState.Paused)
			state = GameState.Running;
	}

	@Override
	public void dispose() {

	}

	@Override
	public void backButton() {
		pause();
	}

	private void goToMenu() {
		// TODO Auto-generated method stub
		game.setScreen(new MainMenuScreen(game));

	}

	
}