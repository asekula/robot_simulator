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
		return false;
	}

	public void drawMaze(Graphics g) {
		// Can be recursive.
	}
}
