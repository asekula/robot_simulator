package iRobot;

/*
 * Collects the primary geometry-heavy methods that both robotData and the emulator use.
 */
public class Geometry {
	/*
	 * OrientationChange is relative to current orientation, and is positive
	 * between 0 and 359.
	 */
	public static Point<Double> curveRobot(Point<Double> location,
			double orientationBefore, double orientationChange, double leftArc,
			double rightArc) {

		if (orientationChange == 0) {
			return getRelativePoint(location, orientationBefore, 0, leftArc);
		}

		double theta = orientationChange;
		double phi; // In degrees.
		if (orientationChange > 180) {
			theta = 360 - orientationChange;
		}

		phi = (180 - theta) / 2;

		// Theta guaranteed to be <= 180.
		// Phi guaranteed to be <= 90.

		double longRadius = Math.max(leftArc, rightArc) / Math.toRadians(theta);
		double radiusToCenter;

		longRadius = Math.abs(longRadius);
		radiusToCenter = longRadius - (Constants.DISTANCE_BETWEEN_MOTORS / 2);

		double halfTheta = Math.toRadians(theta / 2); // In radians.
		double hypotenuse = Math.sin(halfTheta) * radiusToCenter * 2;
		double relativeAngle = 90 - phi;

		if (orientationChange <= 180) {
			return getRelativePoint(location, orientationBefore, relativeAngle,
					hypotenuse);
		} else {
			return getRelativePoint(location, orientationBefore,
					360 - relativeAngle, hypotenuse);
		}
	}

	/*
	 * Theta is in degrees. From the point it calculates a point relative to it
	 * that is length distance away, in direction theta relative to it. (If the
	 * robot is facing north, theta = 90 would return a point exactly west of
	 * the robot.)
	 */
	public static Point<Double> getRelativePoint(Point<Double> p,
			double orientation, double theta, double length) {
		double absRadians = Math.toRadians((orientation + theta + 360) % 360);
		double locX = p.x + (Math.cos(absRadians) * length);
		double locY = p.y + (Math.sin(absRadians) * length);

		return new Point<Double>(locX, locY);
	}

	public static double distanceBetween(Point<Double> p1, Point<Double> p2) {
		return Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
	}

	/*
	 * From the location looking at the direction orientation, find the distance
	 * to the nearest wall. Uses map. Doesn't alter any variables. Note that the
	 * orientation is not relative.
	 * 
	 * Returns -1 if there is no wall within the maxDist, otherwise returns the
	 * distance in cm.
	 * 
	 * Important: Assuming that maxDist <= 15. This ensures that no matter where
	 * the sensor is located, there will be a maximum of two walls that will be
	 * in its line of sight. In other words, we only need to check for the
	 * existence of two walls, and if neither exist then we know that the sensor
	 * did not detect a wall.
	 */
	public static double getDistanceToNearestWall(Point<Double> location,
			double orientation, double maxDist, Map map) {

		assert (maxDist < Constants.CELL_WIDTH); // See important comment above.

		// Simpler if we handle these cases seperately.
		if (orientation == 0 || orientation == 90 || orientation == 180
				|| orientation == 270) {

			Direction dir = Direction.getDirection(orientation);
			return getNearestWallInDirection(location, dir, maxDist, map);
		} else {
			// Line along which we're looking: y = mx + b
			double m = Math.tan(Math.toRadians(orientation));
			double b = location.y - (location.x * m);

			assert (m != 0); // Handled these cases above.

			// Want to find intersection of the line with two grid lines.
			// (grid lines are lines where walls can be located)
			double xGridLine = getXGridLine(location.x, orientation);
			double yGridLine = getYGridLine(location.y, orientation);

			Point<Double> xGridLinePoint = new Point<Double>(xGridLine,
					xGridLine * m + b);
			double xGridLineDistance = Geometry.distanceBetween(location,
					xGridLinePoint);

			// x = (y - b) / m. Assuming m != 0.
			Point<Double> yGridLinePoint = new Point<Double>(
					(yGridLine - b) / m, yGridLine);
			double yGridLineDistance = distanceBetween(location,
					yGridLinePoint);

			boolean wallAtXGridLine = map.wallAt(xGridLinePoint);
			boolean wallAtYGridLine = map.wallAt(yGridLinePoint);

			if (wallAtXGridLine && wallAtYGridLine) {
				double nearest = Math.min(xGridLineDistance, yGridLineDistance);
				if (nearest <= maxDist) {
					return nearest;
				}
			} else if (wallAtXGridLine && xGridLineDistance <= maxDist) {
				return xGridLineDistance;
			} else if (wallAtYGridLine && yGridLineDistance <= maxDist) {
				return yGridLineDistance;
			}

			return -1;
		}
	}

	/*
	 * Edge case of the method above. If a wall exists in the current direction,
	 * it will be one of the four possible walls of the current cell.
	 * 
	 * Returns -1 if there is no wall in the direction.
	 */
	public static double getNearestWallInDirection(Point<Double> p,
			Direction dir, double maxDist, Map map) {
		Point<Double> gridLinePoint = new Point<Double>(0.0, 0.0);
		if (dir == Direction.EAST || dir == Direction.WEST) {
			double theta = 0;
			if (dir == Direction.WEST)
				theta = 180;

			gridLinePoint.x = getXGridLine(p.x, theta);
			gridLinePoint.y = p.y;
		} else {
			double theta = 90;
			if (dir == Direction.SOUTH)
				theta = 270;

			gridLinePoint.x = p.x;
			gridLinePoint.y = getYGridLine(p.y, theta);
		}

		if (map.wallAt(gridLinePoint)) {
			double dist = Geometry.distanceBetween(p, gridLinePoint);
			if (dist <= maxDist)
				return dist;
		}

		return -1;
	}

	/*
	 * Theta is orientation (not relative), y is the location's y coordinate.
	 */
	public static double getYGridLine(double y, double theta) {
		if (theta > 0 && theta < 180) {
			return Math.ceil(y / Constants.CELL_WIDTH) * Constants.CELL_WIDTH;
		} else {
			return Math.floor(y / Constants.CELL_WIDTH) * Constants.CELL_WIDTH;
		}
	}

	public static double getXGridLine(double x, double theta) {
		if (theta < 90 || theta > 270) {
			return Math.ceil(x / Constants.CELL_WIDTH) * Constants.CELL_WIDTH;
		} else {
			return Math.floor(x / Constants.CELL_WIDTH) * Constants.CELL_WIDTH;
		}
	}

	/*
	 * Converts the arc length in cm to the number of tachos that the motor did
	 * to travel that arc.
	 */
	public static int cmToTacho(double cm) {
		double circumference = Constants.WHEEL_DIAMETER * Math.PI; // In cm.
		return (int) (cm * Constants.TACHOS_PER_ROTATION / circumference);
	}

	public static double tachoToCM(int tacho) {
		double circumference = Constants.WHEEL_DIAMETER * Math.PI;
		return circumference
				* (((double) tacho) / Constants.TACHOS_PER_ROTATION);
	}

	/*
	 * Returns angle in degrees between 0 and 360.
	 */
	public static double fullTanInverse(double x, double y) {
		if (x == 0 && y >= 0)
			return 90;
		if (x == 0 && y < 0)
			return 270;

		double tanInvDegrees = (Math.toDegrees(Math.atan(y / x)) + 360) % 360;

		if (x < 0 && y < 0)
			tanInvDegrees += 180;
		if (x < 0 && y > 0)
			tanInvDegrees -= 180;

		assert (tanInvDegrees < 360 && tanInvDegrees >= 0);
		return tanInvDegrees;
	}

	public static boolean within(double a, double b, double error) {
		return (Math.abs(a - b) <= error);
	}

}
