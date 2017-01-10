package iRobot;

import java.util.LinkedList;

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
	private Point<Double> locationInMaze;

	private Phase phase;

	private LinkedList<Point<Integer>> path;

	// Stores the path was cells walked through
	private LinkedList<Point<Integer>> traveresedPath;

	public RobotData(double orientationOffset, Point<Double> location) {

		this.orientationOffset = orientationOffset;
		locationInMaze = location;

		trueOrientation = orientationOffset;

		currentCell = new Point<Integer>(0, 0);
		goalCell = new Point<Integer>(Constants.CENTER_CELL,
				Constants.CENTER_CELL);
		currentGoalLocation = centerOf(currentCell); // Won't move.
		phase = Phase.EXPLORING;
		path = new LinkedList<Point<Integer>>();
		traveresedPath = new LinkedList<Point<Integer>>();
	}

	public Phase getPhase() {
		return phase;
	}

	/*
	 * Alters the location and orientation of the robot. Also changes it's phase
	 * value (exploring vs. speed run) if it reached its goal. Also removes the
	 * head of the path if the robot reached the head's location.
	 */
	public void updateData(SensorData sensorData, Map map) {

		double newTrueOrientation = ((sensorData.IMU + orientationOffset) + 360)
				% 360;
		double orientationChange = ((newTrueOrientation - trueOrientation)
				+ 360) % 360;

		trueOrientation = newTrueOrientation;

		locationInMaze = Geometry.curveRobot(locationInMaze, sensorData.IMU,
				orientationChange,
				Geometry.tachoToCM(sensorData.leftTachoCount),
				Geometry.tachoToCM(sensorData.rightTachoCount));

		currentCell = getCurrentCell(locationInMaze);
		updatePath();
		updatePhase(map); // Uncomment this later.
	}

	/*
	 * Modifies as many coordinates as possible. If we need to optimize code,
	 * here's where to do it. (i.e. put this else where to avoid recomputing the
	 * same values).
	 */
	public void fixLocation(SensorData sensorData, Map map) {
		locationInMaze = Localizer.fixedLocation(locationInMaze, sensorData,
				map, trueOrientation);
	}

	/*
	 * Using the new locationInCell, updates the current cell that the robot is
	 * in.
	 * 
	 * Only modifies currentCell and locationInCell.
	 */
	private Point<Integer> getCurrentCell(Point<Double> location) {
		return new Point<Integer>(
				(int) Math.floor(location.x / Constants.CELL_WIDTH),
				(int) Math.floor(location.y / Constants.CELL_WIDTH));
	}

	/*
	 * Removes the head of the path if it is currently in it.
	 */
	private void updatePath() {
		// When path is implemented, remove currentCell.
		if (!path.isEmpty()) {
			if (path.getFirst().equals(currentCell)) {
				path.removeFirst();
			}
		}
	}

	/*
	 * Updates the phase of the robot. If the robot reached its goal, then it
	 * should change to the next phase. Also updates the goal cell. Does not
	 * change the path, that's the solver/mapper's job.
	 */
	private void updatePhase(Map map) {
		if (phase == Phase.EXPLORING) {
			if (!map.needsWallData()) {
				setNextPhase();
				updateGoalCell();
			}
		} else {
			if (currentCell.equals(goalCell)) {
				setNextPhase();
				updateGoalCell();
			}
		}
	}

	/*
	 * Modifies phase. Sets it to its next phase.
	 */
	private void setNextPhase() {
		// Including redundant breaks because C++ needs them.
		path.clear();
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

	public LinkedList<Point<Integer>> getPath() {
		return path;
	}

	public LinkedList<Point<Integer>> getTraversedPath() {
		return traveresedPath;
	}

	// Will be -1, -1 if there is no next cell.
	public Point<Integer> nextCell() {
		if (path.isEmpty()) {
			return new Point<Integer>(-1, -1);
		} else {
			return path.getFirst();
		}
	}

	public Point<Integer> secondNextCell() {
		if (path.size() >= 2) {
			return path.get(1);
		} else {
			return new Point<Integer>(-1, -1);
		}
	}

	public double getTrueOrientation() {
		return trueOrientation;
	}

	public Point<Double> getLocationInMaze() {
		return locationInMaze;
	}

	public Point<Integer> getGoalCell() {
		return goalCell;
	}

	/*
	 * Returns a location in the maze (not necessarily a location in cell).
	 * 
	 * Called by the instructionGenerator.
	 */
	public Point<Double> nextGoalLocation() {
		if (closeEnough(currentGoalLocation, getLocationInMaze())
				|| currentGoalLocation.x == -1) {
			Point<Integer> next = nextCell();

			if (next.x == -1) {
				currentGoalLocation = new Point<Double>(-1.0, -1.0);
			} else {
				currentGoalLocation = centerOf(next);
			}
		}

		return currentGoalLocation;
	}

	public boolean closeEnough(Point<Double> p1, Point<Double> p2) {
		return (Geometry.distanceBetween(p1,
				p2) <= Constants.MAX_DISTANCE_TO_GOAL);
	}

	public Point<Double> centerOf(Point<Integer> cell) {
		return new Point<Double>((cell.x + 0.5) * Constants.CELL_WIDTH,
				(cell.y + 0.5) * Constants.CELL_WIDTH);
	}
}
