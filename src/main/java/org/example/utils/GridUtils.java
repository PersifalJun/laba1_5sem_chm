package org.example.utils;

import org.example.points.Point;
import org.example.spline.Spline;

import java.util.ArrayList;
import java.util.List;



public final class GridUtils {
    private GridUtils() {}


    public static double[] uniformGrid(double a, double b, int n) {
        double[] x = new double[n + 1];
        double h = (b - a) / n;
        for (int i = 0; i <= n; i++) x[i] = a + i * h;
        x[n] = b;
        return x;
    }



    public static List<Point> toPoints(double[] xs) {
        List<Point> pts = new ArrayList<>(xs.length);
        for (double x : xs) pts.add(new Point(x));
        return pts;
    }


    private static void writeTwoColsWide(String fileName, double[] xs,
                                         java.util.function.IntFunction<double[]> valueGetter)
            throws java.io.IOException {
        try (java.io.PrintWriter out = new java.io.PrintWriter(fileName)) {
            final String COL = "%20.6f";
            final String GAP = "        ";

            for (int i = 0; i < xs.length; i++) {
                double x = xs[i];
                double v = valueGetter.apply(i)[0];


                if (Math.abs(v - Math.rint(v)) < 1e-12) v = Math.rint(v);

                out.println(String.format(java.util.Locale.US, COL, x) + GAP +
                        String.format(java.util.Locale.US, COL, v));
            }
        }
    }

    //файлы со сплайнами
    public static void writeSplineTripletTxt(String prefix, double[] xs, Spline spline)
            throws java.io.IOException {
        double[][] vals = new double[xs.length][3];
        for (int i = 0; i < xs.length; i++) {
            double[] r = new double[3];
            spline.getValue(new Point(xs[i]), r);
            vals[i] = r;
        }
        writeTwoColsWide(prefix + "_S.txt",  xs, i -> new double[]{vals[i][0]}); // сплайн
        writeTwoColsWide(prefix + "_S1.txt", xs, i -> new double[]{vals[i][1]}); // первая производная сплайна
        writeTwoColsWide(prefix + "_S2.txt", xs, i -> new double[]{vals[i][2]}); // вторая производная сплайна
    }
}
