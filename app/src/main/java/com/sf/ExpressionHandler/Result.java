package com.sf.ExpressionHandler;

//包装了complex类和错误信息的结果类。
public class Result {
    public Complex val;//默认用0初始化
    private int err;
    public static int precision = 10;//默认精度为10
    public static int base = 10;//默认进制为10进制
    public static int maxPrecision = 15;//最大精度为15

    //err在complex中默认为0
    public Result(Complex v) {
        val = v;
        err = v.err;
    }

    //使用错误信息构造时，默认complex用Double.NaN初始化
    public Result(int err_) {
        val = new Complex(Double.NaN, Double.NaN);
        err = err_;
    }

    //设置反馈信息
    public Result setAnswer(String answer) {
        val.setAnswer(answer);
        //append(answer);
        return this;
    }

/*    public Result append(String name) {
        //Temporarily not used
        return this;
    }*/

    //仅构造complex
    public Result setVal(Complex v_) {
        val = v_;
        return this;
    }

    //
    public static void setBase(int base_) {
        base = base_;
        precision = (int) Math.floor(35 * Math.log(2) / Math.log(base_));//logbase(2)
        maxPrecision = (int) Math.floor(52 * Math.log(2) / Math.log(base_));
    }

    public boolean isFatalError() {
        return this.err > 0;
    }

    public int getError() {
        return this.err;
    }
}
