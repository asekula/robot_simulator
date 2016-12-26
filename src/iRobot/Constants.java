package iRobot;

public class Constants {

	/*
	 * Important: All distance values are doubles with centimeters as units. If
	 * need be, this can be optimized to save memory. For accuracy and code
	 * correctness, this code ignores any memory concerns and uses doubles
	 * generously.
	 */

	// Distance from front IR to locationInCell.
	public static final double FRONT_IR_TO_CENTER = 3;

	public static final double DISTANCE_BETWEEN_MOTORS = 5;

	public static final double CELL_WIDTH = 20;

	// Pertaining the motors.
	public static final int TACHOS_PER_ROTATION = 1204;

	public static final double WHEEL_DIAMETER = 2;

	// The center of the maze is at (CENTER_CELL, CENTER_CELL).
	public static final int CENTER_CELL = 7;

	// The max cell in the maze is at (MAZE_WIDTH - 1, MAZE_WIDTH - 1).
	public static final int MAZE_WIDTH = 16;

	// Todo: Figure these out.
	public static final double FRONT_MAX_DISTANCE = 15;
	public static final double LR_MAX_DISTANCE = 15;

}