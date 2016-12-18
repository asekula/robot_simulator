package iRobot;

import java.awt.Point; // Stores ints as coordinates. 
// ^Will be quick to implement in C++.

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
	 * where trueOrientation is 0 when the robot completely aligned with a
	 * direction in the maze (north/east/south/west) (where the other directions
	 * are 90, 180, and 270).
	 */
	private int orientationOffset;

	/*
	 * ^Might need a way to calibrate this at the start of the maze. The robot
	 * could do a quick spin around to figure out which orientation value is
	 * exactly aligned with the maze. (If we assume that it starts out correctly
	 * aligned, and it doesn't double check, then some small error at the start
	 * may become a much bigger error later on in the maze.)
	 */

	private Point currentTile; // Current x and y coordinates of the robot.

	/*
	 * Where the robot wants to go. (for speed runs it will be (7,7)), for
	 * returning, (0,0).
	 */
	private Point goalTile;

	/*
	 * We will need to keep track of the motor locations to some accuracy within
	 * the current tile that the robot is in, in order to figure out when it has
	 * moved to a new tile, and to make the instructions as to how the robot
	 * should move to the next tile.
	 * 
	 * Important: The values will be between 0 and 99 inclusive, where (0,0) to
	 * the center of the tile is the same as (0,0) in the maze to the center of
	 * the maze. In other words, the tile will have the same axes as the maze.
	 * 
	 * Also important: If the motors are in two different tiles, then it will
	 * treat the current tile as the tile the robot was just previously in. The
	 * motor that is not in the current tile will have local x and y coordinates
	 * outside of the 0-99 range. Once the other motor leaves the tile it then
	 * sets the outside motor values to their value % 100.
	 */
	private Point leftMotorLocal, rightMotorLocal;

	private Phase phase; // Either exploring, returning, speed run, or finished.

	private Node path; // Will be a list of references to nodes in the map.

	public RobotData() {
		relativeOrientation = 0;
		currentTile = new Point(0, 0);
		leftMotorLocal = new Point(0, 0);
		rightMotorLocal = new Point(0, 0); // Will be changed after calibration.

		goalTile = new Point(7, 7); // Todo: Check that this is correct.
		phase = Phase.EXPLORING;
	}

	public Phase getPhase() {
		return phase;
	}

	/*
	 * Sets the orientation offset, and the motors' local values in the tile.
	 */
	public void calibrate(int offset, Point leftLocal, Point rightLocal) {
		assert (phase == Phase.EXPLORING); // To make sure we don't change this
											// in a later phase.
		// ^(not a complete safety check)

		orientationOffset = offset;
		leftMotorLocal = leftLocal;
		rightMotorLocal = rightLocal;
	}

	/*
	 * Updates the orientation, location, phase, and path of the robot. It only
	 * alters the path by removing the head if it reached the head's location.
	 */
	public void updateData(SensorData sensorData) {
		// Todo
	}
}
