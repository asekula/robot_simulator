package iRobot;

// Basically a tuple. Thanks java.
public class Point<T> {
	public T x;
	public T y;

	public Point(T x, T y) {
		this.x = x;
		this.y = y;
	}

	public boolean equals(Point<T> p) {
		return (p.x.equals(x) && p.y.equals(y));
	}
}
