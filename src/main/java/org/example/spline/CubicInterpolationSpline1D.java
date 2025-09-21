package org.example.spline;

import org.example.points.Point;

import java.util.ArrayList;
import java.util.List;


public class CubicInterpolationSpline1D implements Spline {
    private final List<Point> points = new ArrayList<>();
    private double[] a, b, c, d;

    @Override
    public void updateSpline(List<Point> Points, double[] F_Value) {
        points.clear();
        points.addAll(Points);

        int nSeg = Points.size() - 1;
        a = new double[nSeg];
        b = new double[nSeg];
        c = new double[nSeg];
        d = new double[nSeg];

        // система на внутренние коэффициенты c_i (вторая производная пополам), i=1..n-1
        double[] rhs = new double[nSeg - 1];
        double hcur, hnext;

        for (int i = 0; i < nSeg - 1; i++) {
            hcur  = Points.get(i + 1).x() - Points.get(i).x();
            hnext = Points.get(i + 2).x() - Points.get(i + 1).x();

            // b, a, d как диагонали матрицы трёхдиагональной системы
            b[i]     = 2.0 * (hcur + hnext); // главная диагональ
            a[i + 1] = hcur;                 // нижняя диагональ
            d[i]     = hnext;                // верхняя диагональ
            rhs[i] = 3.0 * ((F_Value[i + 2] - F_Value[i + 1]) / hnext
                    - (F_Value[i + 1] - F_Value[i]) / hcur);
        }

        // прогонка (прямой ход)
        for (int j = 1; j < nSeg - 1; j++) {
            double coef = a[j] / b[j - 1];
            b[j]   -= coef * d[j - 1];
            rhs[j] -= coef * rhs[j - 1];
        }

        // обратный ход
        c[nSeg - 1] = rhs[nSeg - 2] / b[nSeg - 2];
        for (int j = nSeg - 2; j > 0; j--) {
            c[j] = (rhs[j - 1] - c[j + 1] * d[j - 1]) / b[j - 1];
        }
        c[0] = 0.0; // S''(a)=0

        // восстановление коэффициентов кубиков на каждом сегменте
        for (int i = 0; i < nSeg - 1; i++) {
            hcur = Points.get(i + 1).x() - Points.get(i).x();
            a[i] = F_Value[i];
            b[i] = (F_Value[i + 1] - F_Value[i]) / hcur - (c[i + 1] + 2.0 * c[i]) * hcur / 3.0;
            d[i] = (c[i + 1] - c[i]) / (3.0 * hcur);
        }

        // последний сегмент
        hcur = Points.get(nSeg).x() - Points.get(nSeg - 1).x();
        a[nSeg - 1] = F_Value[nSeg - 1];
        b[nSeg - 1] = (F_Value[nSeg] - F_Value[nSeg - 1]) / hcur - 2.0 * c[nSeg - 1] * hcur / 3.0;
        d[nSeg - 1] = -c[nSeg - 1] / (3.0 * hcur);
    }

    @Override
    public void getValue(Point P, double[] Res) {
        double eps = 1e-12;
        int nseg = points.size() - 1;
        double x = P.x();
        for (int i = 0; i < nseg; i++) {
            double xi = points.get(i).x(), xip1 = points.get(i + 1).x();
            if ((x > xi && x < xip1) || Math.abs(x - xi) < eps || Math.abs(x - xip1) < eps) {
                double dx = x - xi;
                Res[0] = a[i] + b[i] * dx + c[i] * dx * dx + d[i] * dx * dx * dx; // S
                Res[1] = b[i] + 2.0 * c[i] * dx + 3.0 * d[i] * dx * dx;            // S'
                Res[2] = 2.0 * c[i] + 6.0 * d[i] * dx;                              // S''
                return;
            }
        }
        throw new IllegalArgumentException("The point is not found in the segments.");
    }
}
