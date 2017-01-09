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
	public static final int MAZE_WIDTH = 7;

	public static final int NUM_REMOVED_WALLS = 10;

	// Todo: Figure these out.
	public static final double IR_MAX = 15; // In cms.

	// For the emulator.
	public static double TIME_STEP = 0.05; // No idea what to put here.

	// Pertaining the applet:
	public static final int SCALE_FACTOR = 6;

	public static int APPLET_DELAY = 2;

	public static int EXTRA_ROBOT_DELAY = 0;

	// *********Errors Variables Below*********
	// (nothing tweakable outside of these)
	// (set these to private to figure out where they're used)

	public static double MAX_SHIFT_DISTANCE = 3;

	// The maximum number of cms that the current location needs to be away from
	// the goal location to say that the robot reached the goal.
	public static double MAX_DISTANCE_TO_GOAL = 1;

	// Mapper uses the following:

	// Theta is within x degrees of a main direction to be used in mapper.
	public static double CLOSE_TO_MAIN_DIRECTION = 10;

	// x % width needs to be 0 with this error in order to treat x as a multiple
	// of width.
	public static double CHECK_MULTIPLE_OF_CELL_WIDTH = 0.01;

	// If the location is farther than this value from the walls, it is
	// considered inside the cell. (where the walls are the width multiples).
	public static double INSIDE_CELL = 4;

	// If a point is within this value distance of a wall, then it is dubbed
	// "too close" by the map, and if the point is at a corner then it doesn't
	// set a wall there.
	public static double TOO_CLOSE_TO_WALL = 1;

	// If a sensor distance is close enough to the grid line point, then there
	// is a wall at that grid line point. Error described here.
	public static double GRID_LINE_IS_WALL = 5;

	// If the grid line points are too close (i.e. corner) don't map.
	public static double GRID_LINES_TOO_CLOSE = 7;

	// The number of times a wall needs to be set to a wall before it's actually
	// set in the graph as a wall (and same for opening)
	public static final int MAPPING_CONFIRMATIONS = 3;
}
