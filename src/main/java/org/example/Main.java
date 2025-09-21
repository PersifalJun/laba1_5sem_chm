package org.example;


import org.example.mathFuncs.MathFuncs;
import org.example.points.MidpointFD;
import org.example.points.Point;
import org.example.spline.CubicInterpolationSpline1D;
import org.example.spline.Spline;
import org.example.errors.ErrorReporter;
import org.example.utils.GridUtils;

import java.util.List;
import java.util.Locale;

public class Main {

    private static final double A  = 0.07;
    private static final double B  = 0.37;
    private static final int    N  = 30;   // h = (B-A)/N = 0.01
    private static final double h = (B - A) / N;
    private static final double eps = 1e-4;

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.US);


        // запись значений сплайна на равномерных сетках: h, h/2, h/4
        for (int f : new int[]{1, 2, 4}) {
            int n = N * f;
            double[] x = GridUtils.uniformGrid(A, B, n);
            double[] y = new double[x.length];
            for (int i = 0; i < x.length; i++) y[i] = MathFuncs.f(x[i]);

            Spline sp = new CubicInterpolationSpline1D();
            List<Point> pts = GridUtils.toPoints(x);
            sp.updateSpline(pts, y);

            GridUtils.writeSplineTripletTxt("uniform_n" + n + "_spline", x, sp);
        }

        ErrorReporter.writeUniformErrors(A, B, N, "errors_uniform.txt");



        MidpointFD.writeMidpointDfForH("midpoint_h.txt", A, B, h, true, eps);


        MidpointFD.writeMidpointDfForEps("midpoint_eps.txt", A, B, eps, true);



    }
}
