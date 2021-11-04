package com.sf.ExpressionHandler;

import android.text.TextUtils;

public class Complex {
    public static Complex E = new Complex(Math.E);
    public static Complex PI = new Complex(Math.PI);
    public static Complex I = new Complex(0, 1);
    public static Complex Inf = new Complex(Double.POSITIVE_INFINITY);

    public int err = 0;
    public double re;
    public double im;

    private String answer = "";


    public Complex(double re_, double im_) {
        re = re_;
        im = im_;
    }

    public Complex(double re_) {
        re = re_;
        im = 0;
    }

    public Complex(String answer_) {
        answer = answer_;
        try {
            re = Double.parseDouble(answer);
        } catch (Exception e) {
            re = Double.NaN;
        }
        im = 0;
    }

    public Complex() {
        re = Double.NaN;
        im = Double.NaN;
    }

    public Complex error(int err) {
        this.err = err;
        return this;
    }

    public void setAnswer(String str) {
        this.answer = str;
    }

    //加
    public static Complex add(Complex a, Complex b) {
        return new Complex(a.re + b.re, a.im + b.im);
    }

    //减
    public static Complex sub(Complex a, Complex b) {
        return new Complex(a.re - b.re, a.im - b.im);
    }

    //取反
    public static Complex inv(Complex a) {
        return new Complex(-a.re, -a.im);
    }

    //乘
    public static Complex mul(Complex a, Complex b) {
        return new Complex(
                a.re * b.re - a.im * b.im,
                a.re * b.im + a.im * b.re
        );
    }

    //绝对值
    public Complex abs() {
        if (im != 0)
            return new Complex().error(3);
        return new Complex(Math.abs(re));
    }

    //返回re^2+im^2
    public Complex norm() {
        return new Complex(Math.hypot(re, im));
    }

    public Complex arg() {

        if (im == 0) {
            im = 0;
            if (re == 0)
                return new Complex(Double.NaN);
        }
        return new Complex(Math.atan2(im, re));
    }

    //不管虚部
    public boolean isNaN() {
        return Double.isNaN(re);
    }

    public boolean isValid() { // finite complex or Complex Infinity
        return !(isDoubleFinite(re) && Double.isNaN(im));
    }

    //当d不为无穷小也不为无穷大时返回true
    public static boolean isDoubleFinite(double d) {
        return !(Double.isNaN(d) || Double.isInfinite(d));
    }

    //除
    public static Complex div(Complex a, Complex b) {
        double aNorm = a.norm().re;
        double bNorm = b.norm().re;
        if (aNorm > 0 && bNorm == 0) return Inf; //分母实数化后分母bNorm为0
        if (Double.isInfinite(bNorm) && Complex.isDoubleFinite(aNorm)) return new Complex(0);
        double ure = b.re / bNorm;
        double uim = b.im / bNorm;
        double re = (a.re * ure + a.im * uim) / bNorm;
        double im = (a.im * ure - a.re * uim) / bNorm;
        return new Complex(re, im);
    }

    //指数
    public static Complex pow(Complex a, Complex b) {
        if (a.re == 0 && a.im == 0) { //先把0的情况处理了
            if (b.re > 0) return new Complex(0);
            else if (b.re < 0 && b.im == 0) return Complex.Inf;
            else return new Complex();
        }
        if (a.norm().re < 1 && b.re == Double.POSITIVE_INFINITY) { //处理正无穷小
            return new Complex(0);
        }
        if (a.norm().re > 1 && b.re == Double.NEGATIVE_INFINITY) { //处理负无穷小
            return new Complex(0);
        }

        return Complex.exp(Complex.mul(b, Complex.ln(a)));
    }

    //转换字符串
    private static String doubleToString(double d) {
        if (Double.isNaN(d)) {
            return "nan";
        }
        if (Double.isInfinite(d)) {
            return d > 0 ? "∞" : "-∞";
        }

        if (Result.base == 10 && Result.precision == Result.maxPrecision) {
            return Double.toString(d);
        }

        return ParseNumber.toBaseString(d, Result.base, Result.precision);
    }

    //拼答案字符串
    public String toString() {
        if (!TextUtils.isEmpty(answer))
            return answer;
        double threshold = (Result.precision < Result.maxPrecision ? Math.pow(Result.base, -Result.precision) : 0);
        if (Double.isNaN(im) && Double.isInfinite(re)) {
            answer = (re > 0 ? "∞" : "-∞");
        } else if (Math.abs(re) > threshold || Double.isNaN(re)) {
            answer += doubleToString(re);

            if (isDoubleFinite(im)) {
                if (Math.abs(im) > threshold) {
                    answer += (im > 0 ? "+" : "-");
                    if (Math.abs(Math.abs(im) - 1) > threshold) {
                        answer += doubleToString(Math.abs(im));
                    }
                    answer += "i";
                }
            } else { // 无穷大或者无穷小
                answer += (im < 0 ? "" : "+");
                answer += doubleToString(im) + "*i";
            }
        } else {
            if (isDoubleFinite(im)) {
                if (Math.abs(im) > threshold) {
                    answer += (im > 0 ? "" : "-");
                    if (Math.abs(Math.abs(im) - 1) > threshold) {
                        answer += doubleToString(Math.abs(im));
                    }
                    answer += "i";
                } else {//补精度
                    answer += "0";
                }
            } else {//无穷大或者无穷小情况
                answer += doubleToString(im) + "*i";
            }
        }
        return answer;
    }

    //======================= 函数方法 ============================

    public static Complex ln(Complex c) {
        return new Complex(Math.log(c.norm().re), c.arg().re);
    }

    public static Complex exp(Complex c) {
        if (c.re == Double.NEGATIVE_INFINITY)
            return new Complex(0);
        double norm = Math.exp(c.re);
        return new Complex(norm * Math.cos(c.im), norm * Math.sin(c.im));
    }

    public static Complex sqrt(Complex c) {
        double norm = c.norm().re;
        if (norm == 0) return new Complex(0);
        double cosArg = c.re / norm;
        double sind2 = Math.sqrt((1 - cosArg) / 2);
        double cosd2 = Math.sqrt((1 + cosArg) / 2);
        if (c.im < 0) sind2 = -sind2;
        norm = Math.sqrt(norm);
        return new Complex(norm * cosd2, norm * sind2);
    }

    public static Complex cbrt(Complex c) {
        return pow(c, div(new Complex(1), new Complex(3)));
    }

    public static Complex sin(Complex c) {
        double eip = Math.exp(c.im);
        double ein = Math.exp(-c.im);
        return new Complex((eip + ein) * Math.sin(c.re) / 2, (eip - ein) * Math.cos(c.re) / 2);
    }

    public static Complex cos(Complex c) {
        double eip = Math.exp(c.im);
        double ein = Math.exp(-c.im);
        return new Complex((eip + ein) * Math.cos(c.re) / 2, (ein - eip) * Math.sin(c.re) / 2);
    }

    public static Complex tan(Complex c) {

        double re2 = c.re * 2;
        double im2 = c.im * 2;

        double eip2 = Math.exp(im2);
        double ein2 = Math.exp(-im2);
        double sinhi2 = (eip2 - ein2) / 2;
        double coshi2 = (eip2 + ein2) / 2;

        if (Double.isInfinite(coshi2)) { //特殊情况
            return new Complex(0, c.im > 0 ? 1 : -1);
        }

        double ratio = Math.cos(re2) + coshi2;
        double resRe = Math.sin(re2) / ratio;
        double resIm = sinhi2 / ratio;
        return new Complex(resRe, resIm);
    }

    public static Complex arcsin(Complex c) {
        Complex v = Complex.add(Complex.mul(c, I), Complex.sqrt(Complex.sub(new Complex(1), Complex.mul(c, c))));
        return Complex.mul(new Complex(0, -1), Complex.ln(v));
    }

    public static Complex arccos(Complex c) {
        Complex v = Complex.add(c, Complex.sqrt(Complex.sub(Complex.mul(c, c), new Complex(1))));
        return Complex.mul(new Complex(0, -1), Complex.ln(v));
    }

    public static Complex arctan(Complex c) {
        if (c.re == Double.POSITIVE_INFINITY) return new Complex(Math.PI / 2);
        if (c.re == Double.NEGATIVE_INFINITY) return new Complex(Math.PI / 2);

        Complex c1 = new Complex(1 - c.im, c.re);
        Complex c2 = new Complex(1 + c.im, -c.re);
        double re_ = (c1.arg().re - c2.arg().re) / 2;
        double im_ = (Math.log(c2.norm().re) - Math.log(c1.norm().re)) / 2;
        return new Complex(re_, im_);
    }

}
