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

	public static final double DISTANCE_BETWEEN_MOTORS = 7;

	public static final double CELL_WIDTH = 20;

	// Pertaining the motors.
	public static final int TACHOS_PER_ROTATION = 1204;

	public static final double WHEEL_DIAMETER = 2;

	// The center of the maze is at (CENTER_CELL, CENTER_CELL).
	public static final int CENTER_CELL = 7;

	// The max cell in the maze is at (MAZE_WIDTH - 1, MAZE_WIDTH - 1).
	public static final int MAZE_WIDTH = 16;

	// Todo: Figure these out.
	public static final double IR_MAX = 15; // In cms.

	// For the emulator.
	public static double TIME_STEP = 0.05; // No idea what to put here.

	// Pertaining the applet:
	public static final int SCALE_FACTOR = 10;

	public static int APPLET_DELAY = 20;

	public static int EXTRA_ROBOT_DELAY = 2;

}
