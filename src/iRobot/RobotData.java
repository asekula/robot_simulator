package iRobot;

import java.awt.Point; // Stores two ints as coordinates. 
// ^Will be quick to implement in C++.

public class RobotData {
	/*
	 * In degrees, from 0 to 360, the orientation relative to the starting
	 * orientation.
	 */
	private double relativeOrientation;

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
	private double orientationOffset;

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

	public RobotData(double orientationOffset, Point leftLocal,
			Point rightLocal) {

		this.orientationOffset = orientationOffset;
		leftMotorLocal = leftLocal;
		rightMotorLocal = rightLocal;

		relativeOrientation = 0;
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
		double orientationChange = sensorData.IMU - relativeOrientation;
		relativeOrientation = sensorData.IMU;

		/*
		 * In calculating the new local motor points, we need to take into
		 * account the distance the motors travelled (the tachos), the
		 * orientation change (which corresponds to rotation), and the front IR
		 * sensor distance. Note that the left and right IR sensors only give
		 * 0/1's and thus cannot be used in calculating the location of the
		 * motors.
		 * 
		 * If we have the front IR sensor then using that and the true
		 * orientation is all that is needed.
		 */

		if (sensorData.frontIR != -1) {
			Point frontIRLocation;

			/*
			 * Todo: Calculate local location of the front IR, then use that to
			 * find the location of the motors (which should be a quick
			 * calculation given that they are physically attached).
			 */
		} else {
			/*
			 * Todo: Use the change in orientation and the tacho lengths to
			 * calculate the new motor locations by calculating the motor arcs.
			 * Will require trig.
			 * 
			 * Note that to account for slippage, we could assume that the
			 * larger tacho count is the accurate one, because in turning the
			 * near motor usually encounters slippage, whereas the far one
			 * doesn't.
			 */
		}

		/*
		 * Todo: Using the new motor locations, update the current tile that the
		 * motor is in. Also remove the tile from the path. Note that if the two
		 * motors are in two different tiles, the current tile doesn't get
		 * updated.
		 */

		/*
		 * Todo: Update the phase of the robot. If the robot reached its goal,
		 * then it should change to the next phase.
		 */
	}
}
