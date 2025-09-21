package org.example.errors;

import org.example.points.Point;
import org.example.mathFuncs.MathFuncs;
import org.example.spline.CubicInterpolationSpline1D;
import org.example.spline.Spline;
import org.example.utils.GridUtils;

import java.io.PrintWriter;
import java.util.Locale;


public final class ErrorReporter {
    private ErrorReporter() {}


    public static void writeUniformErrors(double a, double b, int nBase, String fileName) throws Exception {
        try (PrintWriter out = new PrintWriter(fileName)) {
            out.println(String.format(Locale.US, "%10s%20s%20s%20s",
                    "h", "maxDelta0 |f-S|", "maxDelta1 |f'-S'|", "maxDelta2 |f''-S''|"));
            writeErrorsForN(a, b, nBase, out);        // h
            writeErrorsForN(a, b, 2 * nBase, out);    // h/2
            writeErrorsForN(a, b, 4 * nBase, out);    // h/4
        }
    }


    private static void writeErrorsForN(double a, double b, int n, PrintWriter out) {

        double[] xs = GridUtils.uniformGrid(a, b, n);
        double[] y  = new double[xs.length];
        for (int i = 0; i < xs.length; i++) y[i] = MathFuncs.f(xs[i]);


        Spline sp = new CubicInterpolationSpline1D();
        sp.updateSpline(GridUtils.toPoints(xs), y);


        double h = (b - a) / n;
        int samples = Math.max(10, 3 * n);
        double d0 = 0.0, d1 = 0.0, d2 = 0.0;
        double[] buf = new double[3];

        for (int j = 0; j < samples; j++) {
            double x = a + (j + 0.5) * (b - a) / samples;
            if (x <= a) x = Math.nextUp(a);
            if (x >= b) x = Math.nextDown(b);


            double f0 = MathFuncs.f(x);
            double f1 = MathFuncs.df(x);
            double f2 = MathFuncs.d2f(x);


            sp.getValue(new Point(x), buf);
            double s0 = buf[0], s1 = buf[1], s2 = buf[2];

            d0 = Math.max(d0, Math.abs(f0 - s0));
            d1 = Math.max(d1, Math.abs(f1 - s1));
            d2 = Math.max(d2, Math.abs(f2 - s2));
        }

        out.println(String.format(Locale.US, "%10.6f%20.12f%20.12f%20.12f", h, d0, d1, d2));
    }
}
