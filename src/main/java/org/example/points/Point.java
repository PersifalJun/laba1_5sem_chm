package org.example.points;

public class Point {
    private final double X, Y, Z;
    public Point(double x) { this(x, 0.0, 0.0); }
    public Point(double x, double y, double z) { this.X = x; this.Y = y; this.Z = z; }
    public double x() { return X; }
    public double y() { return Y; }
    public double z() { return Z; }
}