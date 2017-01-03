package iRobot;

import java.awt.*;

public class Map {

	// Todo: Fill this with necessary variables and methods.

	public Map() {

	}

	/*
	 * Sets a wall between the cell and it's neighbor in the direction of dir.
	 */
	public void setWall(Point<Integer> cell, Direction dir) {

	}

	/*
	 * Sets an empty passage between the cell and it's neighbor in the input
	 * direction.
	 */
	public void setNoWall(Point<Integer> cell, Direction dir) {

	}

	/*
	 * Returns true if there is unknown wall data pertaining the input cell.
	 */
	public boolean needsWallData(Point<Integer> cell) {
		return false;
	}

	/*
	 * Generates random maze for the emulator.
	 */
	public void generateRandomMaze() {
	}

	public boolean wallAt(Point<Double> p) {
		return true;
	}

	/*
	 * Returns true if wall or unknown. (true if unknown because the
	 * localization code calculates all possibilities).
	 */
	public boolean wallBetween(Point<Integer> cell1, Point<Integer> cell2) {
		return true;
	}

	public void drawMaze(Graphics g) {
		// Can be recursive.
		g.setColor(Color.LIGHT_GRAY);
		int scaleFactor = Constants.SCALE_FACTOR;
		for (int i = 1; i < 5; i++) {
			g.drawLine(0, (int) Constants.CELL_WIDTH * i * scaleFactor,
					(int) Constants.CELL_WIDTH * 5 * scaleFactor,
					(int) Constants.CELL_WIDTH * i * scaleFactor);
			g.drawLine((int) Constants.CELL_WIDTH * i * scaleFactor, 0,
					(int) Constants.CELL_WIDTH * i * scaleFactor,
					(int) Constants.CELL_WIDTH * 5 * scaleFactor);
		}
		g.setColor(Color.BLACK);
	}
}
