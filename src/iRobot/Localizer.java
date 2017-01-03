package iRobot;

public class Localizer {

	// Important: Do not change this line.
	// Including this to make the code less cluttered.
	private static final double CELL_WIDTH = Constants.CELL_WIDTH;

	/*
	 * This method uses the robot's data (including sensor data) to update the
	 * location of the robot. It is assumed that the robot already moved itself
	 * via curveRobot with the tacho counts.
	 * 
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
		double thetaL = (90 + orientation) % 360, thetaF = orientation,
				thetaR = (270 + orientation) % 360;

		if (leftDist != -1 && frontDist == -1 && rightDist == -1) {
			closestValidPoint(location, leftDist, thetaL, currentCell, map);
			return;
		}

		if (leftDist == -1 && frontDist != -1 && rightDist == -1) {
			closestValidPoint(location, frontDist, thetaF, currentCell, map);
			return;
		}

		if (leftDist == -1 && frontDist == -1 && rightDist != -1) {
			closestValidPoint(location, rightDist, thetaR, currentCell, map);
			return;
		}

		Point<Double> p1, p2, p3, avg;

		p1 = pointData(leftDist, thetaL, frontDist, thetaF, currentCell, map);
		p2 = pointData(frontDist, thetaF, rightDist, thetaR, currentCell, map);
		p3 = pointData(leftDist, thetaL, rightDist, thetaR, currentCell, map);

		avg = avgData(p1, p2, p3);

		if (avg.x != -CELL_WIDTH) {
			location.x = avg.x;
		}

		if (avg.y != -CELL_WIDTH) {
			location.y = avg.y;
		}
	}

	/*
	 * Modifies location. Assumes that there are no other distance values.
	 * Location will not be modified more after this method.
	 */
	private static void closestValidPoint(Point<Double> location, double dist,
			double theta, Point<Integer> currentCell, Map map) {
		Point<Double> origin = new Point<Double>(0.0, 0.0);
		Point<Double> p = Geometry.getRelativePoint(origin, 0, theta, dist);

		double xVal = locInCell(p.x), yVal = locInCell(p.y);

		if (Math.abs(location.x - xVal) < Math.abs(location.y - yVal)) {
			location.x = xVal;
		} else {
			location.y = yVal;
		}
	}

	/*
	 * Thetax is the direction that distx is facing. cell is the current cell.
	 * Returns a point with as much data as possible, indicating where the robot
	 * is in the current cell. If there is no data in any of the coordinates it
	 * uses -Constants.CELL_WIDTH to fill the data. Since we're using only one
	 * distance value, one of the coordinates will definitely be -CELL_WIDTH.
	 * 
	 * Using -Constants.CELL_WIDTH because the values will never be that low,
	 * since localization is done quite often, and once the values become
	 * negative, the cell changes and they become positive again.
	 * 
	 * Returns unknown if either distance is -1.
	 */
	private static Point<Double> pointData(double dist1, double theta1,
			double dist2, double theta2, Point<Integer> cell, Map map) {

		// I've accepted the fact that I'll need to plug through cases here.

		if (dist1 == -1 || dist2 == -1) {
			return new Point<Double>(-CELL_WIDTH, -CELL_WIDTH);
		}

		Point<Double> origin = new Point<Double>(0.0, 0.0);

		Point<Double> p1 = Geometry.getRelativePoint(origin, 0, theta1, dist1);
		Point<Double> p2 = Geometry.getRelativePoint(origin, 0, theta2, dist2);

		double precision = 0.0001;
		p1.x = round(p1.x, precision);
		p1.y = round(p1.y, precision);
		p2.x = round(p2.x, precision);
		p2.y = round(p2.y, precision);

		double error = 0.03; // Important: Change this if localization isn't
								// precise.

		// If a coordinate of a point is 0, then we know that there is only one
		// kind of wall that the sensor is detecting.

		if (Math.abs(theta1 - theta2) != 180) {
			if ((p1.x == 0) || (p2.y == 0)) {
				return new Point<Double>(locInCell(p2.x), locInCell(p1.y));
			}

			if ((p1.y == 0) || (p2.x == 0)) {
				return new Point<Double>(locInCell(p1.x), locInCell(p2.y));
			}
		} else {

			if (p1.x == 0 || p2.x == 0) {
				// p2.x == 0.
				return new Point<Double>(-CELL_WIDTH, avgLocs(p1.y, p2.y));
			}

			if (p1.y == 0 || p2.x == 0) {
				// p2.y == 0.
				return new Point<Double>(avgLocs(p1.x, p2.x), -CELL_WIDTH);
			}
		}

		// If the coordinates are close enough, they are detecting the same
		// wall.
		if (Geometry.within(p1.x, p2.x, error)) {
			return new Point<Double>(avgLocs(p1.x, p2.x), -CELL_WIDTH);
		}

		if (Geometry.within(p1.y, p2.y, error)) {
			return new Point<Double>(-CELL_WIDTH, avgLocs(p1.y, p2.y));
		}

		if (p1.x < 0 && p2.x < 0) {

			// If the y's are the same sign, the vectors are in the same
			// quadrant, which won't happen with our L/F/R vectors.

			assert (p1.y * p2.y < 0); // True if opposite signs.
			// (already covered case where one of the coordinates is 0.

			if (Geometry.within(Math.abs(p1.y - p2.y), CELL_WIDTH, error)) {
				return new Point<Double>(-CELL_WIDTH, avgLocs(p1.y, p2.y));
			} else {
				if (p1.x < p2.x) { // p1 hits the left wall.
					return new Point<Double>(-p1.x, locInCell(p2.y));
				} else {
					return new Point<Double>(-p2.x, locInCell(p1.y));
				}
			}
		}

		if (p1.x > 0 && p2.x > 0) {
			assert (p1.y * p2.y < 0);

			if (Geometry.within(Math.abs(p1.y - p2.y), CELL_WIDTH, error)) {
				return new Point<Double>(-CELL_WIDTH, avgLocs(p1.y, p2.y));
			} else {
				if (p1.x > p2.x) { // p1 hits the right wall.
					return new Point<Double>(locInCell(p1.x), locInCell(p2.y));
				} else {
					return new Point<Double>(locInCell(p2.x), locInCell(p1.y));
				}
			}
		}

		if (p1.y > 0 && p2.y > 0) {

			if (Geometry.within(Math.abs(p1.x - p2.x), CELL_WIDTH, error)) {
				return new Point<Double>(avgLocs(p1.x, p2.x), -CELL_WIDTH);
			} else {
				if (p1.y > p2.y) { // p1 hits the top wall.
					return new Point<Double>(locInCell(p2.x), locInCell(p1.y));
				} else {
					return new Point<Double>(locInCell(p1.x), locInCell(p2.y));
				}
			}
		}

		if (p1.y < 0 && p2.y < 0) {
			if (Geometry.within(Math.abs(p1.x - p2.x), CELL_WIDTH, error)) {

				return new Point<Double>(avgLocs(p1.x, p2.x), -CELL_WIDTH);
			} else {
				if (p1.y < p2.y) { // p1 hits the bottom wall.
					return new Point<Double>(locInCell(p2.x), locInCell(p1.y));
				} else {
					return new Point<Double>(locInCell(p1.x), locInCell(p2.y));
				}
			}
		}

		// Last case: across from each other (opposite quadrants).
		// Only case that uses the map.
		if ((p1.y * p2.y < 0) && (p1.x * p2.x < 0)) {
			assert (Math.abs(theta1 - theta2) == 180); // No other case.

			// Subtracting p1.x and p2.x because they have different signs.
			if (Geometry.within(Math.abs(p1.x - p2.x), CELL_WIDTH, error)) {
				return new Point<Double>(avgLocs(p1.x, p2.x), -CELL_WIDTH);
			}

			if (Geometry.within(Math.abs(p1.y - p2.y), CELL_WIDTH, error)) {
				return new Point<Double>(-CELL_WIDTH, avgLocs(p1.y, p2.y));
			}

			boolean westWall = map.wallBetween(cell,
					Point.getAdjacentCell(cell, Direction.WEST));
			boolean eastWall = map.wallBetween(cell,
					Point.getAdjacentCell(cell, Direction.EAST));
			boolean northWall = map.wallBetween(cell,
					Point.getAdjacentCell(cell, Direction.NORTH));
			boolean southWall = map.wallBetween(cell,
					Point.getAdjacentCell(cell, Direction.SOUTH));

			boolean bottomRight = eastWall && southWall;
			boolean topLeft = northWall && westWall;
			boolean topRight = northWall && eastWall;
			boolean bottomLeft = southWall && westWall;

			/*
			 * Even if it's not detecting the actual wall (maybe it's detecting
			 * the wall next to it), we can still use the coordinates to get its
			 * location relative to that grid line, which is all that we care
			 * about.
			 */

			// Todo later: clean this up.

			if (p1.x > 0 && p1.y > 0) {
				if (topLeft && (!bottomRight)) {
					return new Point<Double>(locInCell(p2.x), locInCell(p1.y));
				}
				if ((!topLeft) && bottomRight) {
					return new Point<Double>(locInCell(p1.x), locInCell(p2.y));
				}
			}

			if (p1.x < 0 && p1.y < 0) {
				if (topLeft && (!bottomRight)) {
					return new Point<Double>(locInCell(p1.x), locInCell(p2.y));
				}
				if ((!topLeft) && bottomRight) {
					return new Point<Double>(locInCell(p2.x), locInCell(p1.y));
				}
			}

			if (p1.x < 0 && p1.y > 0) {
				if (topRight && (!bottomLeft)) {
					return new Point<Double>(locInCell(p2.x), locInCell(p1.y));
				}
				if ((!topRight) && bottomLeft) {
					return new Point<Double>(locInCell(p1.x), locInCell(p2.y));
				}
			}

			if (p1.x > 0 && p1.y < 0) {
				if (topRight && (!bottomLeft)) {
					return new Point<Double>(locInCell(p1.x), locInCell(p2.y));
				}
				if ((!topRight) && bottomLeft) {
					return new Point<Double>(locInCell(p2.x), locInCell(p1.y));
				}
			}
		}

		return new Point<Double>(-CELL_WIDTH, -CELL_WIDTH);
	}

	private static double avgLocs(double val1, double val2) {
		return (locInCell(val1) + locInCell(val2)) / 2;
	}

	private static double locInCell(double val) {
		if (val > 0) {
			return Constants.CELL_WIDTH - val;
		} else {
			return -val;
		}
	}

	/*
	 * Averages values when they are not -CELL_WIDTH. Returns a point containing
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
		yCoords = addToAvg(avgPoint, p2.y, yCoords, false);
		yCoords = addToAvg(avgPoint, p3.y, yCoords, false);

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

	private static double round(double val, double precision) {
		return Math.round(val / precision) * precision;
	}
}
