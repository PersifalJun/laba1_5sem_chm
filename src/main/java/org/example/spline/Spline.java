package org.example.spline;

import org.example.points.Point;

public interface Spline {
    /** Обновить сплайн по узлам и значениям табличной функции в них. */
    void updateSpline(java.util.List<Point> points, double[] fValues);

    /** Получить значение сплайна и двух производных в точке p.*/
    void getValue(Point p, double[] Res);
}