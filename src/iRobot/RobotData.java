package iRobot;

/*
 * Important method in here is updateData.
 */
public class RobotData {

	/*
	 * In degrees, from 0 to 360, the orientation relative to the starting true
	 * orientation, which is 0. It is true because 0, 90, 180, and 270 refer
	 * exactly to East, North, West, and South respectively.
	 * 
	 * Important: The robot starts out at cell (0,0) looking towards (+inf, 0),
	 * i.e. EAST. 0 degrees is EAST.
	 * 
	 * Also important: Angles increase counter-clockwise, like in trig.
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

	private Point<Double> currentGoalLocation;

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
	protected Point<Double> locationInCell;

	private Phase phase;

	private Path path; // Will be a list of references to nodes in the map.

	public RobotData(double orientationOffset, Point<Double> location) {

		this.orientationOffset = orientationOffset;
		locationInCell = location;

		trueOrientation = orientationOffset;

		currentCell = new Point<Integer>(0, 0);
		goalCell = new Point<Integer>(Constants.CENTER_CELL,
				Constants.CENTER_CELL);
		currentGoalLocation = centerOf(currentCell); // Won't move.
		phase = Phase.EXPLORING;
		path = new Path();
	}

	public Phase getPhase() {
		return phase;
	}

	/*
	 * Updates the orientation, location, phase, and path of the robot.
	 */
	public void updateData(SensorData sensorData) {

		// Updates orientation. Values will be positive.
		double newTrueOrientation = ((sensorData.IMU + orientationOffset) + 360)
				% 360;
		double orientationChange = ((newTrueOrientation - trueOrientation)
				+ 360) % 360;

		trueOrientation = newTrueOrientation;
		locationInCell = getNewLocationInCell(sensorData, locationInCell,
				trueOrientation, orientationChange);
		currentCell = getCurrentCell(currentCell, locationInCell);
		locationInCell = fixLocationBounds(locationInCell);
		updatePath();
		updatePhase();
	}

	/*
	 * In calculating the new location in the cell, we need to take into account
	 * the distance the motors traveled (the tachos), the orientation change
	 * (which corresponds to rotation), and the front IR sensor distance. Note
	 * that the left and right IR sensors only give 0/1's.
	 * 
	 * If the front IR sensor value isn't -1 then we can figure out one of the
	 * two coordinates of the motors.
	 * 
	 * Tries to rely as little as possible on the previous locationInCell. This
	 * ensures that if we have to make approximations then small errors won't
	 * snowball into bigger ones.
	 * 
	 * No side effects.
	 */
	private Point<Double> getNewLocationInCell(SensorData sensorData,
			Point<Double> currentLocation, double trueOrientation,
			double orientationChange) {
		/*
		 * Important: For now, using a very simple method. This is just so that
		 * we can start testing sooner, but eventually we will scrap this code
		 * and replace it with more feedback-oriented code that will hopefully
		 * have distance sensors on the sides.
		 * 
		 * The simple method is as such: Either the robot is moving in a
		 * straight line or it is rotating in place.
		 * 
		 * Only updates the location in the cell if it moved straight. Doesn't
		 * if it rotated.
		 */

		if (within(orientationChange, 0, 5) && sensorData.leftTachoCount > 0
				&& sensorData.rightTachoCount > 0) {
			int tachoAvg = (sensorData.leftTachoCount
					+ sensorData.rightTachoCount) / 2;
			double distanceMoved = tachoToCM(tachoAvg);

			Point<Double> newLocation = new Point<Double>(0.0, 0.0);

			// Assuming the robot moved in a straight line.
			newLocation.x = currentLocation.x + (distanceMoved
					* Math.cos(trueOrientation * Math.PI / 180));
			newLocation.y = currentLocation.y + (distanceMoved
					* Math.sin(trueOrientation * Math.PI / 180));

			// Not worrying about bounds here, handled in fixLocationBounds.
			return newLocation;
		} else {
			// Assuming that if the orientation changed, it did not move (part
			// of the simple method).
			return currentLocation;
		}
	}

	/*
	 * Assuming that it is aligned with one of the four directions.
	 */
	public Direction getDirectionFacing() {
		int error = 1;

		if (within(trueOrientation, 0, error)
				|| within(trueOrientation, 360, error))
			return Direction.EAST;
		if (within(trueOrientation, 90, error))
			return Direction.NORTH;
		if (within(trueOrientation, 180, error))
			return Direction.WEST;
		if (within(trueOrientation, 270, error))
			return Direction.SOUTH;

		return Direction.NORTH; // Q: Throw error?
	}

	public boolean alignedWithMainDirection() {
		double error = 1;
		return (within(trueOrientation, 0, error)
				|| within(trueOrientation, 90, error)
				|| within(trueOrientation, 180, error)
				|| within(trueOrientation, 270, error)
				|| within(trueOrientation, 360, error));
	}

	private double tachoToCM(int tacho) {
		double circumference = Constants.WHEEL_DIAMETER * Math.PI;
		return circumference
				* (((double) tacho) / Constants.TACHOS_PER_ROTATION);
	}

	private boolean within(double a, double b, double error) {
		return (Math.abs(a - b) <= error);
	}

	/*
	 * Using the new locationInCell, updates the current cell that the robot is
	 * in.
	 * 
	 * Only modifies currentCell and locationInCell.
	 */
	private Point<Integer> getCurrentCell(Point<Integer> cell,
			Point<Double> location) {

		Point<Integer> nextCell = new Point<Integer>(cell.x, cell.y);

		if (location.x < 0)
			nextCell = getAdjacentCell(nextCell, Direction.WEST);
		if (location.x >= Constants.CELL_WIDTH)
			nextCell = getAdjacentCell(nextCell, Direction.EAST);
		if (location.y < 0)
			nextCell = getAdjacentCell(nextCell, Direction.SOUTH);
		if (location.y >= Constants.CELL_WIDTH)
			nextCell = getAdjacentCell(nextCell, Direction.NORTH);

		return nextCell;
	}

	private Point<Double> fixLocationBounds(Point<Double> location) {
		Point<Double> fixed = new Point<Double>(0.0, 0.0);
		fixed.x = (location.x + Constants.CELL_WIDTH) % Constants.CELL_WIDTH;
		fixed.y = (location.y + Constants.CELL_WIDTH) % Constants.CELL_WIDTH;
		return fixed;
	}

	/*
	 * Moves the current cell to one of its neighboring cells based on the
	 * direction.
	 */
	private Point<Integer> getAdjacentCell(Point<Integer> cell, Direction dir) {
		Point<Integer> adjacent = new Point<Integer>(cell.x, cell.y);
		switch (dir) {
			case NORTH :
				adjacent.y += 1;
				break;
			case SOUTH :
				adjacent.y -= 1;
				break;
			case EAST :
				adjacent.x += 1;
				break;
			case WEST :
				adjacent.x -= 1;
				break;
		}

		return adjacent;
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
				goalCell = new Point<Integer>(Constants.CENTER_CELL,
						Constants.CENTER_CELL);
				break;
			case RETURNING :
				goalCell = new Point<Integer>(0, 0);
				break;
			case SPEED_RUN :
				goalCell = new Point<Integer>(Constants.CENTER_CELL,
						Constants.CENTER_CELL);
				break;
		}
	}

	public Point<Integer> getCurrentCell() {
		return currentCell;
	}

	// Will be -1, -1 if there is no next cell.
	public Point<Integer> nextCell() {
		// Temporary, for testing purposes:
		if (currentCell.x == 0 && currentCell.y == 0) {
			return new Point<Integer>(1, 0);
		}

		if (currentCell.x == 1 && currentCell.y == 0) {
			return new Point<Integer>(1, 1);
		}

		if (currentCell.x == 1 && currentCell.y == 1) {
			return new Point<Integer>(2, 1);
		}

		if (currentCell.x == 2 && currentCell.y == 1) {
			return new Point<Integer>(2, 2);
		}

		if (currentCell.x == 2 && currentCell.y == 2) {
			return new Point<Integer>(1, 2);
		}

		if (currentCell.x == 1 && currentCell.y == 2) {
			return new Point<Integer>(0, 2);
		}

		if (currentCell.x == 0 && currentCell.y == 2) {
			return new Point<Integer>(0, 1);
		}

		if (currentCell.x == 0 && currentCell.y == 1) {
			return new Point<Integer>(0, 0);
		}

		return path.getNextCell();
	}

	public double getTrueOrientation() {
		return trueOrientation;
	}

	public Point<Double> getLocationInMaze() {
		return new Point<Double>(
				currentCell.x * Constants.CELL_WIDTH + locationInCell.x,
				currentCell.y * Constants.CELL_WIDTH + locationInCell.y);
	}

	/*
	 * Returns a location in the maze (not necessarily a location in cell).
	 */
	public Point<Double> nextGoalLocation() {
		if (closeEnough(currentGoalLocation, getLocationInMaze())) {
			currentGoalLocation = centerOf(nextCell());
		}

		return currentGoalLocation;
	}

	public boolean closeEnough(Point<Double> p1, Point<Double> p2) {
		double error = 0.5; // In cms.
		return (Math.sqrt(
				Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2)) <= error);
	}

	public Point<Double> centerOf(Point<Integer> cell) {
		return new Point<Double>((cell.x + 0.5) * Constants.CELL_WIDTH,
				(cell.y + 0.5) * Constants.CELL_WIDTH);
	}
}
