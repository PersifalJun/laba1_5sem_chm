package org.example.points;

import org.example.mathFuncs.MathFuncs;

import java.io.PrintWriter;
import java.util.Locale;


public final class MidpointFD {
    private MidpointFD() {}


    public static void writeMidpointDfForH(String fileName, double a, double b,
                                           double hGuess, boolean use5pt) throws Exception {
        final double xc = 0.5 * (a + b);
        double h = clampH(a, b, xc, hGuess, use5pt);
        double df = use5pt ? df5pt(xc, h) : df3pt(xc, h);

        try (PrintWriter out = new PrintWriter(fileName)) {
            out.println(String.format(Locale.US, "# center x = %.6f", xc));
            out.println(String.format(Locale.US, "%12s  %12s  %20s", "h", "x", "df(x)"));
            out.println(String.format(Locale.US, "%12.6f  %12.6f  %20.12f", h, xc, df));
        }
    }



    public static void writeMidpointDfForEps(String fileName, double a, double b,
                                             double eps, boolean use5pt) throws Exception {
        final double xc = 0.5 * (a + b);


        double hMax = Math.min(xc - a, b - xc) / (use5pt ? 2.0 : 1.0);
        if (hMax <= 0) throw new IllegalArgumentException("Invalid interval (too small)");
        double h = Math.min(hMax, (b - a) / 10.0);

        double prev = Double.NaN, cur;
        int it = 0, itMax = 25;
        do {
            h = clampH(a, b, xc, h, use5pt);
            cur = use5pt ? df5pt(xc, h) : df3pt(xc, h);

            if (!Double.isNaN(prev) && Math.abs(cur - prev) < eps) break;

            prev = cur;
            h *= 0.5;
            it++;
        } while (it < itMax);

        try (PrintWriter out = new PrintWriter(fileName)) {
            out.println(String.format(Locale.US, "# center x = %.6f, eps = %.4e",
                    xc, eps, use5pt ? "5-point" : "3-point"));
            out.println(String.format(Locale.US, "%12s  %12s  %20s  %10s",
                    "h", "x", "df(x)", "iters"));
            out.println(String.format(Locale.US, "%12.6f  %12.6f  %20.12f  %10d",
                    h, xc, cur, it));
        }
    }


    public static double df3pt(double x, double h) {
        return (MathFuncs.f(x + h) - MathFuncs.f(x - h)) / (2.0 * h);
    }


    public static double df5pt(double x, double h) {
        return (-MathFuncs.f(x + 2*h) + 8*MathFuncs.f(x + h)
                - 8*MathFuncs.f(x - h) + MathFuncs.f(x - 2*h)) / (12.0 * h);
    }


    private static double clampH(double a, double b, double x, double h, boolean fivePoint) {
        double k = fivePoint ? 2.0 : 1.0;
        double hMax = Math.min(x - a, b - x) / k;
        if (hMax <= 0) throw new IllegalArgumentException("Invalid interval (too small)");
        return Math.min(Math.abs(h), hMax);
    }
}