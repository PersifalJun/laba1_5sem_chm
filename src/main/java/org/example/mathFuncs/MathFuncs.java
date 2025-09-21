package org.example.mathFuncs;

public final class MathFuncs {
    private MathFuncs() {}

    //Функция
    public static double f(double x)  { return Math.log1p(x); }
    //1-ая производная
    public static double df(double x) { return 1.0 / (1.0 + x); }
    //2-ая производная
    public static double d2f(double x){ return -1.0 / ((1.0 + x)*(1.0 + x)); }
}
