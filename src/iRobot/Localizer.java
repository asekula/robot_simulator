package iRobot;

public class Localizer {

	/*
	 * Finish.
	 */

	/*
	 * Orientation is the true orientation of the robot. This method uses only
	 * the sensor data to update the robot's location.
	 * 
	 * The number of coordinates we can update (0, 1, or 2) depends on which
	 * values are -1, and which grid line types (horizontal or vertical) the
	 * sensors are detecting.
	 * 
	 * This method is meant to update the small errors that arise from
	 * curveRobot. If there is enough data, then it won't even use the input
	 * location, and return the true location (e.g. when all sensors return
	 * values != -1).
	 * 
	 * Returns nothing, modifies location. Does not modify anything else.
	 * 
	 * Only public interface of Localizer.
	 */
	public static void modifyLocation(Point<Double> location,
			Point<Integer> currentCell, SensorData sensorData, Map map,
			double orientation) {

		double leftDist = -1.0, rightDist = -1.0, frontDist = -1.0;

		if (sensorData.leftIR != -1) {
			leftDist = sensorData.leftIR
					+ (Constants.DISTANCE_BETWEEN_MOTORS / 2);
		}
		if (sensorData.rightIR != -1) {
			rightDist = sensorData.rightIR
					+ (Constants.DISTANCE_BETWEEN_MOTORS / 2);
		}
		if (sensorData.frontIR != -1) {
			frontDist = sensorData.frontIR + Constants.FRONT_IR_TO_CENTER;
		}

		// If we need to save memory, here's a good place to do it.

		Point<Double> p1, p2, p3, avg;

		p1 = pointData(leftDist, (90 + orientation) % 360, currentCell, map);
		p2 = pointData(frontDist, orientation, currentCell, map);
		p3 = pointData(rightDist, (270 + orientation) % 360, currentCell, map);

		avg = avgData(p1, p2, p3);
		if (avg.x != -Constants.CELL_WIDTH) {
			location.x = avg.x;
		}

		if (avg.y != -Constants.CELL_WIDTH) {
			location.y = avg.y;
		}
	}

	/*
	 * True orientation is the direction that dist is facing. cell is the
	 * current cell. Returns a point with as much data as possible, indicating
	 * where the robot is in the current cell. If there is no data in any of the
	 * coordinates it uses -Constants.CELL_WIDTH to fill the data. Since we're
	 * using only one distance value, one of the coordinates will definitely be
	 * -CELL_WIDTH.
	 */
	private static Point<Double> pointData(double dist, double trueOrientation,
			Point<Integer> cell, Map map) {

		// Todo.

		return new Point<Double>(-Constants.CELL_WIDTH, -Constants.CELL_WIDTH);
	}

	/*
	 * Avgs values when they are not -CELL_WIDTH. Returns a point containing
	 * those average values.
	 */
	private static Point<Double> avgData(Point<Double> p1, Point<Double> p2,
			Point<Double> p3) {
		int xCoords = 0, yCoords = 0;
		Point<Double> avgPoint = new Point<Double>(0.0, 0.0);

		// Want tuple here.
		xCoords = addToAvg(avgPoint, p1.x, xCoords, true);
		xCoords = addToAvg(avgPoint, p2.x, xCoords, true);
		xCoords = addToAvg(avgPoint, p3.x, xCoords, true);

		yCoords = addToAvg(avgPoint, p1.y, yCoords, false);
		yCoords = addToAvg(avgPoint, p1.y, yCoords, false);
		yCoords = addToAvg(avgPoint, p1.y, yCoords, false);

		if (xCoords == 0) {
			avgPoint.x = -Constants.CELL_WIDTH;
		} else {
			avgPoint.x /= xCoords;
		}

		if (yCoords == 0) {
			avgPoint.y = -Constants.CELL_WIDTH;
		} else {
			avgPoint.y /= yCoords;
		}

		return avgPoint;
	}

	/*
	 * Returns num if avg wasn't added to, returns num + 1 if it added val to
	 * one of avg's coordinates (as decided by xPos).
	 */
	private static int addToAvg(Point<Double> avg, double val, int num,
			boolean xPos) {

		if (val != -Constants.CELL_WIDTH) {
			if (xPos) {
				avg.x += val;
			} else {
				avg.y += val;
			}
			return num + 1;
		} else {
			return num;
		}
	}
}
