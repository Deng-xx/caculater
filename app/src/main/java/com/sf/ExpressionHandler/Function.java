package com.sf.ExpressionHandler;


//基本函数相关
class Function {
    static final int EXP = 10;
    static final int LN = 20;
    static final int RE = 30;
    static final int IM = 40;
    static final int SQRT = 50;
    static final int ABS = 60;
    static final int SIN = 90;
    static final int COS = 100;
    static final int TAN = 110;
    static final int ASIN = 120;
    static final int ACOS = 130;
    static final int ATAN = 140;
    static final int REG = 180;
    static final int PREC = 300;
    static final int BASE = 310;
    static final int CBRT = 320;
    static final int FACT = 360;

    static class Serial { //函数名称和序列化
        String funcName;
        int funcSerial; //函数序列化
        int exprParamNum; //有多少参数作为输入

        Serial(String name_, int serial_) {
            funcName = name_;
            funcSerial = serial_;
            exprParamNum = 0;
        }
    }

    // 注册名称和序列号对，funcList会在Expression里匹配基本函数使用
    static final Serial[] funcList = {
            new Serial("exp", EXP),
            new Serial("ln", LN),
            new Serial("re", RE),
            new Serial("im", IM),
            new Serial("sqrt", SQRT),
            new Serial("abs", ABS),
            new Serial("sin", SIN),
            new Serial("cos", COS),
            new Serial("tan", TAN),
            new Serial("asin", ASIN),
            new Serial("acos", ACOS),
            new Serial("atan", ATAN),
            new Serial("reg", REG),
            new Serial("prec", PREC),
            new Serial("base", BASE),
            new Serial("cbrt", CBRT),
            new Serial("fact", FACT),
    };
}
