package iRobot;

public class RobotData {
	/*
	 * In degrees, from 0 to 360, the orientation relative to the starting
	 * orientation.
	 */
	private int relativeOrientation;

	/*
	 * orientationOffset corresponds to the amount of degrees that our robot is
	 * off by at the start of the run. Will be set once at the start of the run
	 * and then never changed.
	 * 
	 * Important: relativeOrientation - orientationOffset = trueOrientation,
	 * where trueOrientation is 0 when the robot is facing forward at the start
	 * of the run
	 */
	private int orientationOffset;

	/*
	 * ^Might need a way to calibrate this at the start of the maze. The robot
	 * could do a quick spin around to figure out which orientation value is
	 * exactly aligned with the maze. (If we assume that it starts out correctly
	 * aligned, and it doesn't double check, then some small error at the start
	 * may become a much bigger error later on in the maze.)
	 */

	private int currentX, currentY; // Current x and y coordinates of the robot.

	/*
	 * Where the robot wants to go. (for exploring it will be (7,7)), for
	 * returning, (0,0).
	 */
	private int goalX, goalY;

	private Phase phase; // Either exploring, returning, speed run, or finished.

	private Node path; // Will be a list of references to nodes in the map.

	public RobotData() {
		relativeOrientation = 0;
		currentX = 0;
		currentY = 0;
		goalX = 7;
		goalY = 7; // Todo: Check that these are the correct initial goals.
		phase = Phase.EXPLORING;
	}

	public Phase getPhase() {
		return phase;
	}

	// Set after calibration.
	public void setOrientationOffset(int offset) {
		assert (phase == Phase.EXPLORING); // To make sure we don't change this
											// in a later phase.
		// ^(not a complete safety check)

		orientationOffset = offset;
	}

	/*
	 * Updates the orientation, location, phase, and path of the robot. It only
	 * alters the path by removing the head if it reached the head's location.
	 */
	public void updateData(SensorData sensorData) {
		// Todo
	}
}
