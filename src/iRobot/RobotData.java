package iRobot;

public class RobotData {

	/*
	 * Important: All distance values are doubles with centimeters as units. If
	 * need be, this can be optimized to save memory. For accuracy and code
	 * correctness, this code ignores any memory concerns and uses doubles
	 * generously.
	 */

	// Distance from front IR to locationInCell.
	private static final double FRONT_IR_TO_CENTER = 3;

	private static final double DISTANCE_BETWEEN_MOTORS = 5;

	// Pertaining the IMU.
	private static final int ANGLES_PER_ROTATION = 360;

	// The center of the maze is at (CENTER_CELL, CENTER_CELL).
	private static final int CENTER_CELL = 7;

	// The max cell in the maze is at (MAZE_WIDTH - 1, MAZE_WIDTH - 1).
	private static final int MAZE_WIDTH = 16;

	private static final double CELL_WIDTH = 20;

	// Pertaining the motors.
	private static final int TACHOS_PER_ROTATION = 1204;

	private static final double WHEEL_DIAMETER = 2;

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

	// The cell that the robot is currently in.
	private Point<Integer> currentCell;

	// For exploring/speed runs, this will be (CENTER_CELL, CENTER_CELL).
	private Point<Integer> goalCell;

	/*
	 * We will need to keep track of the robot location to some accuracy within
	 * the current cell, in order to figure out when it has moved to a new cell,
	 * and to make the instructions as to how the robot should move to the next
	 * cell.
	 * 
	 * Important: The coordinates will be between 0 and inclusive, where (0,0)
	 * to the center of the cell is the same as (0,0) in the maze to the center
	 * of the maze. In other words, the cell will have the same axes as the
	 * maze. Note that we can change the precision to be a smaller range.
	 * 
	 * Also note: locationInCell is the location of the *center* of the robot,
	 * which we will say is the point on the line that goes through the two
	 * motors, and is equidistant to the two motors.
	 */
	private Point<Double> locationInCell;

	private Phase phase;

	private Path path; // Will be a list of references to nodes in the map.

	// -1 means no data. 0 is nothing, 1 is something.
	// Q: Is the L/R IR data precise enough to be useful?
	private int prevRightIR, prevLeftIR;

	public RobotData(double orientationOffset, Point<Double> location) {

		this.orientationOffset = orientationOffset;
		locationInCell = location;

		trueOrientation = orientationOffset;

		currentCell = new Point<Integer>(0, 0);
		goalCell = new Point<Integer>(CENTER_CELL, CENTER_CELL);
		phase = Phase.EXPLORING;

		prevRightIR = -1;
		prevLeftIR = -1;
	}

	public Phase getPhase() {
		return phase;
	}

	/*
	 * Updates the orientation, location, phase, and path of the robot.
	 */
	public void updateData(SensorData sensorData) {

		// Updates orientation.
		double newTrueOrientation = (sensorData.IMU + orientationOffset)
				% ANGLES_PER_ROTATION;
		double orientationChange = (newTrueOrientation - trueOrientation)
				% ANGLES_PER_ROTATION;
		trueOrientation = newTrueOrientation;

		updateLocationInCell(sensorData, orientationChange);

		prevLeftIR = sensorData.leftIR;
		prevRightIR = sensorData.rightIR;

		updateCurrentCell();
		updatePath();
		updatePhase();
	}

	/*
	 * Uses the change in orientation and the tacho lengths to calculate the new
	 * locations in cell by calculating the motor arcs. Will require trig.
	 * 
	 * Note that to account for slippage, we could assume that the larger tacho
	 * count is the accurate one, because in turning the near motor usually
	 * encounters slippage, whereas the far one doesn't. There might be a better
	 * way of calculating this.
	 * 
	 * Only modifies locationInCell.
	 */
	private void updateLocationInCell(SensorData sensorData,
			double orientationChange) {
		/*
		 * In calculating the new location in the cell, we need to take into
		 * account the distance the motors traveled (the tachos), the
		 * orientation change (which corresponds to rotation), and the front IR
		 * sensor distance. Note that the left and right IR sensors only give
		 * 0/1's and thus cannot be used in calculating the location of the
		 * motors.
		 * 
		 * If we have the front IR sensor then we can figure out one of the two
		 * coordinates of the motors.
		 */
	}

	/*
	 * Using the new locationInCell, updates the current cell that the robot is
	 * in.
	 * 
	 * Only modifies currentCell and locationInCell.
	 */
	private void updateCurrentCell() {
		if (locationInCell.x < 0)
			setCurrentToAdjacentCell(Direction.WEST);
		if (locationInCell.x >= CELL_WIDTH)
			setCurrentToAdjacentCell(Direction.EAST);
		if (locationInCell.y < 0)
			setCurrentToAdjacentCell(Direction.SOUTH);
		if (locationInCell.y >= CELL_WIDTH)
			setCurrentToAdjacentCell(Direction.NORTH);

		locationInCell.x = locationInCell.x % CELL_WIDTH;
		locationInCell.y = locationInCell.y % CELL_WIDTH;
	}

	/*
	 * Moves the current cell to one of its neighboring cells based on the
	 * direction.
	 */
	private void setCurrentToAdjacentCell(Direction dir) {
		switch (dir) {
			case NORTH :
				currentCell.y += 1;
				break;
			case SOUTH :
				currentCell.y -= 1;
				break;
			case EAST :
				currentCell.x += 1;
				break;
			case WEST :
				currentCell.x -= 1;
				break;
		}
		// Q: Include error checking? (if out of bounds)
	}

	/*
	 * Removes the head of the path if it is currently in it.
	 */
	private void updatePath() {
		if (path.getNextCell().equals(currentCell)) {
			path.removeHead(); // Removes head from path.
		}
	}

	/*
	 * Updates the phase of the robot. If the robot reached its goal, then it
	 * should change to the next phase. Also updates the goal cell. Does not
	 * change the path, that's the solver/mapper's job.
	 */
	private void updatePhase() {
		if (currentCell.equals(goalCell)) {
			setNextPhase();
			updateGoalCell();
		}
	}

	/*
	 * Modifies phase. Sets it to its next phase.
	 */
	private void setNextPhase() {
		// Including redundant breaks because C++ needs them.
		switch (phase) {
			case EXPLORING :
				phase = Phase.RETURNING;
				break;
			case RETURNING :
				phase = Phase.SPEED_RUN;
				break;
			case SPEED_RUN :
				phase = Phase.RETURNING;
				break;
		}
	}

	private void updateGoalCell() {
		switch (phase) {
			case EXPLORING :
				goalCell = new Point<Integer>(CENTER_CELL, CENTER_CELL);
				break;
			case RETURNING :
				goalCell = new Point<Integer>(0, 0);
				break;
			case SPEED_RUN :
				goalCell = new Point<Integer>(CENTER_CELL, CENTER_CELL);
				break;
		}
	}
}
