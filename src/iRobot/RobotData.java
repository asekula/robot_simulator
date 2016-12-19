package iRobot;

import java.awt.Point; // Stores two ints as coordinates. 

public class RobotData {

	/*
	 * The number of units that the front IR is away from the center of the
	 * robot. Note that we are saying that the center is the exact midpoint
	 * between the two motors.
	 */
	private static final int FRONT_IR_OFFSET = 10;

	private static final int ANGLES_PER_ROTATION = 360;

	/*
	 * In degrees, from 0 to 360, the orientation relative to the starting true
	 * orientation, which is 0. It is true because 0, 90, 180, and 270 refer
	 * exactly to North/East/South/West in the maze.
	 */
	private double trueOrientation;

	/*
	 * orientationOffset corresponds to the amount of degrees that our robot is
	 * off by at the start of the run. Will be set once at the start of the run
	 * and then never changed.
	 * 
	 * Important: sensorOrientation + orientationOffset = trueOrientation.
	 * 
	 * If orientationOffset is positive, then our robot was slightly angled to
	 * the right when it started.
	 */
	private double orientationOffset;

	/*
	 * ^Might need a way to calibrate this at the start of the maze. The robot
	 * could do a quick spin around to figure out which orientation value is
	 * exactly aligned with the maze. (If we assume that it starts out correctly
	 * aligned, and it doesn't double check, then some small error at the start
	 * may become a much bigger error later on in the maze.)
	 */

	/*
	 * Current x and y coordinates of the robot. Coordinates correspond to the
	 * tile that the robot is currently in, with the minimum being (0,0) and the
	 * max being (15,15).
	 */
	private Point currentTile;

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
	 * Important: The coordinates will be between 0 and 99 inclusive, where
	 * (0,0) to the center of the tile is the same as (0,0) in the maze to the
	 * center of the maze. In other words, the tile will have the same axes as
	 * the maze. Note that we can change the precision to be a smaller range.
	 * 
	 * Also note: locationInTile is the location of the *center* of the robot,
	 * which we will say is the point on the line that goes through the two
	 * motors, and is equidistant to the two motors.
	 */
	private Point locationInTile; // Coordinates between (0,0) and (99,99).

	private Phase phase; // Either exploring, returning, speed run, or finished.

	private Path path; // Will be a list of references to nodes in the map.

	/*
	 * Maybe include: private int prevLeftIR, prevRightIR so that we can detect
	 * the immediate change in the IR sensor, which will tell us that there is a
	 * wall 5cm from the left or right IR.
	 */

	public RobotData(double orientationOffset, Point location) {

		this.orientationOffset = orientationOffset;
		locationInTile = location;

		trueOrientation = orientationOffset;
		currentTile = new Point(0, 0);

		goalTile = new Point(7, 7); // Todo: Check that this is correct.
		phase = Phase.EXPLORING;
	}

	public Phase getPhase() {
		return phase;
	}

	/*
	 * Updates the orientation, location, phase, and path of the robot. It only
	 * alters the path by removing the head if it reached the head's location.
	 */
	public void updateData(SensorData sensorData) {
		double newTrueOrientation = (sensorData.IMU + orientationOffset)
				% ANGLES_PER_ROTATION;
		double orientationChange = (newTrueOrientation - trueOrientation)
				% ANGLES_PER_ROTATION;
		trueOrientation = newTrueOrientation;

		/*
		 * In calculating the new location in the tile, we need to take into
		 * account the distance the motors traveled (the tachos), the
		 * orientation change (which corresponds to rotation), and the front IR
		 * sensor distance. Note that the left and right IR sensors only give
		 * 0/1's and thus cannot be used in calculating the location of the
		 * motors.
		 * 
		 * If we have the front IR sensor then we can figure out one of the two
		 * coordinates of the motors.
		 * 
		 */

		/*
		 * Uses the change in orientation and the tacho lengths to calculate the
		 * new motor locations by calculating the motor arcs. Will require trig.
		 * 
		 * Note that to account for slippage, we could assume that the larger
		 * tacho count is the accurate one, because in turning the near motor
		 * usually encounters slippage, whereas the far one doesn't.
		 */
		updateLocationInTile(sensorData, orientationChange);

		/*
		 * Using the new motor locations, updates the current tile that the
		 * motor is in. Also removes the tile from the path. Note that if the
		 * two motors are in two different tiles, the current tile doesn't get
		 * updated.
		 */
		updateCurrentTile();
		updatePath();

		/*
		 * Updates the phase of the robot. If the robot reached its goal, then
		 * it should change to the next phase.
		 */
		updatePhase();
	}
}
