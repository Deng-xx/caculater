package com.sf.ExpressionHandler;

import com.sf.DarkCalculator.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class Expression {
    private String text;//存放输入框文本
    private int[] br; //括号深度
    private int[] lastLB; //同优先级别的最近的一个左括号
    private int[] nextFS; //同级别的下一个运算符或者函数标志
    private int[] commaCnt; //每个左括号的逗号数，用于提取函数参数
    private int[] funcSer; //解释出来的函数参数
    private int brDiff; // 左右括号的差值，少一个

    //缓存解析结果的标志
    private class SymbolCachePair {
        static final int SYMBOL_NUM = 0; //可能缓存值
        static final int SYMBOL_ADD = 1;
        static final int SYMBOL_POS = 2;
        static final int SYMBOL_SUB = 3;
        static final int SYMBOL_NEG = 4;
        static final int SYMBOL_MUL = 5;
        static final int SYMBOL_DIV = 6;
        static final int SYMBOL_MUL_OMIT = 7;
        static final int SYMBOL_POW = 8;
        static final int SYMBOL_SQRT = 9;
        static final int SYMBOL_CONST = 10; //可能缓存值
        static final int SYMBOL_FUNC = 11;
        static final int SYMBOL_BRACKET = 13;
        static final int SYMBOL_FACT = 14;

        int end_pos;//缓存解释到的最后位置
        int symbol;//缓存符号或运算符
        int symbol_pos;//该缓存符号在字符串中的位置
        Complex cachedValue;//缓存结果

        //缓存符号结果对，是保存缓存结果的数据模型
        SymbolCachePair(int end_pos_, int symbol_, int symbol_pos_, Complex cachedValue_) {
            end_pos = end_pos_;
            symbol = symbol_;
            symbol_pos = symbol_pos_;
            cachedValue = cachedValue_;
        }
    }

    //符号缓存，封装了缓存结果对的模型
    private class SymbolCache {
        List<SymbolCachePair> list;

        SymbolCache() {
            list = new ArrayList<>();
        }

        void submit(int end_pos_, int symbol_, int symbol_pos_) {
            list.add(new SymbolCachePair(end_pos_, symbol_, symbol_pos_, new Complex()));
            //Log.i("expression","Submit pos="+symbol_pos_+" type="+symbol_);
        }

        void submit(int end_pos_, int symbol_, Complex cachedValue_) {
            list.add(new SymbolCachePair(end_pos_, symbol_, -1, cachedValue_));
            //Log.i("expression","Submit val="+cachedValue_+" type="+symbol_);
        }

        SymbolCachePair checkCache(int end_pos) {
            for (int i = 0; i < list.size(); i++) {
                SymbolCachePair pair = list.get(i);
                if (pair.end_pos == end_pos) {
                    return pair;
                }
            }
            return null;
        }
    }

    private SymbolCache[] interpretResult;

    private volatile boolean isWorking;

    private static final String mathOperator = "+-*•/^√";
    private static Complex memValue = new Complex(); //用于保存值来完成储存ANS功能

    public Expression(String s) {
        text = s;//输入框的文本
        br = new int[s.length() + 1];
        lastLB = new int[s.length() + 1];
        nextFS = new int[s.length() + 1];
        commaCnt = new int[s.length() + 1];
        brDiff = 0;

        int[] symbolStack = new int[s.length() + 1]; //所有左括号位置的栈
        int[] lastSymbol = new int[s.length() + 1]; //最后一个符号标志枚举值

        int top = -1;

        br[0] = 0;
        for (int i = 0; i < s.length(); i++) {
            lastLB[i] = -1;
            nextFS[i] = -1;
            commaCnt[i] = 0;
            char c = s.charAt(i);
            if (i > 0) {
                br[i] = br[i - 1];
                if (s.charAt(i - 1) == '(') br[i]++;
                if (c == ')') br[i]--;
            }

            if (c == '(') { //是左括号压栈
                top++;
                symbolStack[top] = i;
                lastLB[i] = i;
                lastSymbol[top] = i;
                brDiff++;
            }
            if (c == ',' && top >= 0) { //记录
                lastLB[i] = symbolStack[top];
                commaCnt[symbolStack[top]]++;
                nextFS[lastSymbol[top]] = i;
                lastSymbol[top] = i;
            }
            if (c == ')') {
                if (top >= 0) { //右括号出栈
                    lastLB[i] = symbolStack[top];
                    nextFS[lastSymbol[top]] = i;
                    top--;
                }
                brDiff--;
            }
        }
    }

    private void initCache() { //初始化缓存
        funcSer = new int[text.length()];
        interpretResult = new SymbolCache[text.length()];
        for (int i = 0; i < interpretResult.length; i++) {
            interpretResult[i] = new SymbolCache();
            funcSer[i] = -1;
        }
    }

    //判断c字符是否是mathOperator，是返回true
    private boolean isOperator(char c) {
        return mathOperator.indexOf(c) != -1;
    }

    //判断是否text[p]=='+'/'-' && p>0 && 逻辑合理,p是position
    private boolean isAddSubSymbol(int p) {
        if (p == 0) return false;

        char cj = text.charAt(p);
        if (!(cj == '+' || cj == '-')) {//不是加减返回false
            return false;
        }

        cj = text.charAt(p - 1);//p-1位置还是操作符或者是科学记数法返回false
        if (isOperator(cj) || cj == 'E') {
            return false;
        }
        if (ParseNumber.isBaseSymbol(cj)) { //特定基数下科学记数法中的+-符号
            int pos;
            for (pos = p + 1; pos < text.length(); pos++) {
                cj = text.charAt(pos);
                if (!(cj >= '0' && cj <= '9')) { //不是十进制数跳出循环
                    break;
                }
            }
            if (pos == text.length()) { //解析到最后了
                return false;
            }
            if (pos == p + 1) { //+/-直接后跟非整数符号
                return true;
            }
            if (ParseNumber.isBaseSymbol(cj) || (cj >= 'A' && cj <= 'F') || cj == '.') { //进制表示符、A-F内的数字、小数点返回true
                return true;
            }
            return false;
        }
        return true;
    }

    private boolean isOmitMult(int p) {
        if (p == 0) return false;

        char ci = text.charAt(p);
        char cj = text.charAt(p - 1);

        boolean iscjPreSymbol = (cj == ')' || cj == '∞' || cj == 'π' || cj == '°' || cj == '!' || cj == '%');
        boolean iscjNumber = (cj >= '0' && cj <= '9' || cj == '.');
        boolean iscjBase = ParseNumber.isBaseSymbol(cj);
        boolean iscjFunc = (cj >= 'a' && cj <= 'z');
        boolean isciNumber = (ci >= '0' && ci <= '9' || ci == '.');

        if ((ci >= 'a' && ci <= 'z' || ci == '(') && (iscjNumber || iscjPreSymbol || iscjBase))
            return true;
        else if ((isciNumber) && (iscjPreSymbol || iscjFunc))
            return true;
        else if ((ci == '∞' || ci == 'π' || ci == '°' || ci == '%' || ci == 'Γ' || ci == '√' || ci == '!') && (iscjNumber || iscjPreSymbol || iscjBase || iscjFunc))
            return true;
        else
            return false;
    }

    //递归计算解析字符串的值
    public Result value(int l, int r, Complex vX) {
        if (!isWorking) return new Result(2);

        if (l > r) {
            return new Result(1).setAnswer("表达式语法错误");
        }

        //检测是否结果已经缓存
        SymbolCachePair pair = interpretResult[l].checkCache(r);
        if (pair != null) { //开始缓存结果
            Result r1, r2;
            switch (pair.symbol) { //到目前为止没有错误
                case SymbolCachePair.SYMBOL_CONST:
                case SymbolCachePair.SYMBOL_NUM:
                    return new Result(pair.cachedValue);
                case SymbolCachePair.SYMBOL_ADD:
                    r1 = value(l, pair.symbol_pos - 1, vX);
                    r2 = value(pair.symbol_pos + 1, r, vX);
                    return new Result(Complex.add(r1.val, r2.val));
                case SymbolCachePair.SYMBOL_SUB:
                    r1 = value(l, pair.symbol_pos - 1, vX);
                    r2 = value(pair.symbol_pos + 1, r, vX);
                    return new Result(Complex.sub(r1.val, r2.val));
                case SymbolCachePair.SYMBOL_POS:
                    return value(l + 1, r, vX);
                case SymbolCachePair.SYMBOL_NEG:
                    r1 = value(l + 1, r, vX);
                    return new Result(Complex.inv(r1.val));
                case SymbolCachePair.SYMBOL_MUL:
                    r1 = value(l, pair.symbol_pos - 1, vX);
                    r2 = value(pair.symbol_pos + 1, r, vX);
                    return new Result(Complex.mul(r1.val, r2.val));
                case SymbolCachePair.SYMBOL_DIV:
                    r1 = value(l, pair.symbol_pos - 1, vX);
                    r2 = value(pair.symbol_pos + 1, r, vX);
                    return new Result(Complex.div(r1.val, r2.val));
                case SymbolCachePair.SYMBOL_MUL_OMIT:
                    r1 = value(l, pair.symbol_pos - 1, vX);
                    r2 = value(pair.symbol_pos, r, vX); //注意position
                    return new Result(Complex.mul(r1.val, r2.val));
                case SymbolCachePair.SYMBOL_POW:
                    r1 = value(l, pair.symbol_pos - 1, vX);
                    r2 = value(pair.symbol_pos + 1, r, vX);
                    return new Result(Complex.pow(r1.val, r2.val));
                case SymbolCachePair.SYMBOL_SQRT:
                    r1 = value(l + 1, r, vX);
                    return new Result(Complex.sqrt(r1.val));
                case SymbolCachePair.SYMBOL_FUNC:
                    return funcValue(l, r, vX);
                case SymbolCachePair.SYMBOL_BRACKET:
                    return value(l + 1, r - 1, vX);
                case SymbolCachePair.SYMBOL_FACT:
                    r1 = value(l, pair.symbol_pos - 1, vX);
                    return fact(r1.val);
            }
        }

        //解析表达式
        String s = text.substring(l, r + 1);

        //变量
        if (s.equals("x") && (vX.isValid() || vX.isNaN())) return new Result(vX); //变量x
        if (s.equals("reg")) return new Result(memValue); //正则量

        //忽略空格换行并且解析
        if (text.charAt(l) == ' ' || text.charAt(l) == '\n' || text.charAt(l) == '\r')
            return value(l + 1, r, vX);
        if (text.charAt(r) == ' ' || text.charAt(r) == '\n' || text.charAt(r) == '\r')
            return value(l, r - 1, vX);

        /*======================= 此行下方，字符串只会被解析一次 ========================*/

        { //常数
            Complex complexConst = null;
            if (s.equals("e")) complexConst = Complex.E; //常数e
            else if (s.equals("π")) complexConst = Complex.PI; //常数pi
            else if (s.equals("i")) complexConst = Complex.I; //常数i
            else if (s.equals("∞")) complexConst = Complex.Inf; //常数无穷大
            else if (s.equals("°")) complexConst = new Complex(Math.PI / 180); //度数值
            else if (s.equals("%")) complexConst = new Complex(0.01); //百分号值
            else {
                String constValue = Constants.load().get(s);
                if (constValue != null) {
                    complexConst = new Complex(constValue);
                }
            }

            if (complexConst != null) {
                interpretResult[l].submit(r, SymbolCachePair.SYMBOL_CONST, complexConst);
                return new Result(complexConst);
            }
        }

        //解析数据
        try {
            //禁止默认实数值和字符e作为运算符
            //禁止默认符号
            if (s.indexOf('e') >= 0 || s.indexOf('I') >= 0 || s.indexOf('N') >= 0 ||
                    s.indexOf('X') >= 0 || s.indexOf('P') >= 0 || s.indexOf('x') >= 0 || s.indexOf('p') >= 0) {
                throw new NumberFormatException();
            }

            try { //try解析十进制double
                if (s.indexOf('D') >= 0 || s.indexOf('F') >= 0) { //禁止double和float标志
                    throw new NumberFormatException();
                }

                double v = Double.parseDouble(s);
                interpretResult[l].submit(r, SymbolCachePair.SYMBOL_NUM, new Complex(v));
                return new Result(new Complex(v));
            } catch (NumberFormatException e) { //在某进制下解析double
                //不是合法的十进制double
                double v = ParseNumber.parse(s);
                interpretResult[l].submit(r, SymbolCachePair.SYMBOL_NUM, new Complex(v));
                return new Result(new Complex(v));
            }
        } catch (NumberFormatException e) {
            //不是合法的值
        }

        char ci;
        //加减
        for (int i = r; i > l; i--) {
            ci = text.charAt(i);
            //仅计算以下长布尔表达式之一
            if (br[i] == br[l] && isAddSubSymbol(i)) {
                if (ci == '+') {
                    interpretResult[l].submit(r, SymbolCachePair.SYMBOL_ADD, i);
                    Result r1 = value(l, i - 1, vX);
                    if (r1.isFatalError()) return r1;
                    Result r2 = value(i + 1, r, vX);
                    if (r2.isFatalError()) return r2;
                    return new Result(Complex.add(r1.val, r2.val));
                } else if (ci == '-') {
                    interpretResult[l].submit(r, SymbolCachePair.SYMBOL_SUB, i);
                    Result r1 = value(l, i - 1, vX);
                    if (r1.isFatalError()) return r1;
                    Result r2 = value(i + 1, r, vX);
                    if (r2.isFatalError()) return r2;
                    return new Result(Complex.sub(r1.val, r2.val));
                }
            }
        }

        //一元运算，正负
        if (text.charAt(l) == '+') {
            interpretResult[l].submit(r, SymbolCachePair.SYMBOL_POS, -1);
            return value(l + 1, r, vX);
        } else if (text.charAt(l) == '-') {
            interpretResult[l].submit(r, SymbolCachePair.SYMBOL_NEG, -1);
            Result r1 = value(l + 1, r, vX);
            if (r1.isFatalError()) return r1;
            return new Result(Complex.inv(r1.val));
        }

        //乘除
        for (int i = r; i > l; i--) {
            if (br[i] == br[l]) {
                ci = text.charAt(i);
                Result r1, r2;
                switch (ci) {
                    case '*':
                    case '•':
                        interpretResult[l].submit(r, SymbolCachePair.SYMBOL_MUL, i);
                        r1 = value(l, i - 1, vX);
                        if (r1.isFatalError()) return r1;
                        r2 = value(i + 1, r, vX);
                        if (r2.isFatalError()) return r2;
                        return new Result(Complex.mul(r1.val, r2.val));
                    case '/':
                        interpretResult[l].submit(r, SymbolCachePair.SYMBOL_DIV, i);
                        r1 = value(l, i - 1, vX);
                        if (r1.isFatalError()) return r1;
                        r2 = value(i + 1, r, vX);
                        if (r2.isFatalError()) return r2;
                        return new Result(Complex.div(r1.val, r2.val));
                    case '!':
                        interpretResult[l].submit(r, SymbolCachePair.SYMBOL_FACT, i);
                        r1 = value(l, i - 1, vX);
                        if (r1.isFatalError()) return r1;
                        if (i != r)
                            return new Result(1).setAnswer("无法计算 “" + s + "”");
                        if (r1.val.re % 1 != 0 || r1.val.re < 0)
                            return new Result(1).setAnswer("阶乘只能作用于自然数");
                        return fact(r1.val);
                    default:
                        if (isOmitMult(i)) { //*符号省略了
                            interpretResult[l].submit(r, SymbolCachePair.SYMBOL_MUL_OMIT, i);
                            r1 = value(l, i - 1, vX);
                            if (r1.isFatalError()) return r1;
                            r2 = value(i, r, vX);
                            if (r2.isFatalError()) return r2;
                            return new Result(Complex.mul(r1.val, r2.val));
                        }
                }
            }
        }

        //求方标志
        for (int i = l; i <= r; i++)
            if (br[i] == br[l] && text.charAt(i) == '^') {
                interpretResult[l].submit(r, SymbolCachePair.SYMBOL_POW, i);
                Result r1 = value(l, i - 1, vX);
                if (r1.isFatalError()) return r1;
                Result r2 = value(i + 1, r, vX);
                if (r2.isFatalError()) return r2;
                return new Result(Complex.pow(r1.val, r2.val));
            }

        //平方根标志
        if (text.charAt(l) == '√') {
            interpretResult[l].submit(r, SymbolCachePair.SYMBOL_SQRT, -1);
            Result r1 = value(l + 1, r, vX);
            if (r1.isFatalError()) return r1;
            return new Result(Complex.sqrt(r1.val));
        }

        //检查括号
        if (text.charAt(r) != ')')
            return new Result(1).setAnswer("无法计算 “" + s + "”");
        if (text.charAt(l) == '(') {
            interpretResult[l].submit(r, SymbolCachePair.SYMBOL_BRACKET, -1);
            return value(l + 1, r - 1, vX);
        }

        //解析函数
        interpretResult[l].submit(r, SymbolCachePair.SYMBOL_FUNC, -1);
        return funcValue(l, r, vX);
    }

    private Result funcValue(int l, int r, Complex vX) {
        String s = text.substring(l, r + 1);

        // 以下是基本函数的处理，使用Function枚举
        int listPos; //Function类里函数表位置
        int funcID; //序列化的函数ID，Function类里
        int paramNum; //函数里的参数数量
        int leftBr; //函数左括号的位置
        int exprParamNum; //需要函数输入的参数数量

        if (funcSer[l] < 0) { // not searched in list yet
            for (int i = 0; i < Function.funcList.length; i++) {
                if (s.startsWith(Function.funcList[i].funcName + "(")) {
                    //Log.i("expression","parse "+s);
                    funcSer[l] = i;
                    break;
                }
            }
        }

        listPos = funcSer[l];

        // 没有找到
        if (listPos < 0)
            return new Result(1).setAnswer("没有函数 “" + s.substring(0, s.length()) + "”");

        funcID = Function.funcList[listPos].funcSerial;
        leftBr = l + Function.funcList[listPos].funcName.length();
        exprParamNum = Function.funcList[listPos].exprParamNum;
        if (text.charAt(leftBr + 1) == ')') {
            paramNum = 0;
        } else {
            paramNum = commaCnt[leftBr] + 1;
        }

        // 参数太多
        if (paramNum > 9)
            return new Result(1).setAnswer("函数 “" + Function.funcList[listPos].funcName + "” 参数错误");

        // 计算每一个参数.value
        Complex[] val = new Complex[10];
        if (paramNum > 0) {
            for (int p = leftBr, i = 0; nextFS[p] >= 0; p = nextFS[p], i++) {
                if (i >= exprParamNum) {
                    int resl = p + 1;
                    int resr = nextFS[p] - 1;
                    Result res = value(resl, resr, vX);
                    if (res.isFatalError())
                        return res.setAnswer("函数 “" + Function.funcList[listPos].funcName + "” 参数无效");
                    val[i] = res.val;
                }
            }
        }

        int funcJump = funcID + paramNum;
        switch (funcJump) {
            case Function.FACT + 1:
                if (val[0].re % 1 != 0 || val[0].re < 0)
                    return new Result(1).setAnswer("阶乘函数的参数必须是自然数");
                return fact(val[0]);
            case Function.CBRT + 1:
                return new Result(Complex.cbrt(val[0]));
            case Function.EXP + 1:
                return new Result(Complex.exp(val[0]));
            case Function.LN + 1:
                return new Result(Complex.ln(val[0]));
            case Function.RE + 1:
                return new Result(new Complex(val[0].re));
            case Function.IM + 1:
                return new Result(new Complex(val[0].im));
            case Function.SQRT + 1:
                return new Result(Complex.sqrt(val[0]));
            case Function.ABS + 1:
                return new Result(val[0].abs());
            case Function.SIN + 1:
                return new Result(Complex.sin(val[0]));
            case Function.COS + 1:
                return new Result(Complex.cos(val[0]));
            case Function.TAN + 1:
                return new Result(Complex.tan(val[0]));
            case Function.ASIN + 1:
                return new Result(Complex.arcsin(val[0]));
            case Function.ACOS + 1:
                return new Result(Complex.arccos(val[0]));
            case Function.ATAN + 1:
                return new Result(Complex.arctan(val[0]));
            case Function.REG:
                return new Result(memValue);
            case Function.REG + 1:
                memValue = val[0];
                return new Result(val[0]);
            case Function.PREC:
                Result.setBase(Result.base);
                return new Result(0).setAnswer("精度设置为 " + Result.precision + " 位小数");
            case Function.PREC + 1:
                if (val[0].im != 0)
                    return new Result(3);
                int prec = (int) Math.round(val[0].re);
                if (prec < 0)
                    return new Result(1).setVal(new Complex(1)).setAnswer("精度过低");
                if (prec > Result.maxPrecision)
                    return new Result(1).setVal(new Complex(1))
                            .setAnswer("设置的精度过高，最大精度是 " + Result.maxPrecision + " 位小数");
                Result.precision = prec;
                return new Result(0).setAnswer("精度设置为 " + prec + " 位小数");
            case Function.BASE:
                Result.setBase(10);
                return new Result(0).setAnswer("输出进制被设置为 " + 10 + " 进制，" + "精度为 " + Result.precision + " 位小数");
            case Function.BASE + 1:
                if (val[0].im != 0)
                    return new Result(3);
                int base = (int) Math.round(val[0].re);
                if (!(base >= 2 && base <= 10 || base == 12 || base == 16))
                    return new Result(1).setVal(new Complex(1)).setAnswer("函数的参数无效");
                Result.setBase(base);
                return new Result(0).setAnswer("输出进制被设置为 " + base + " 进制，" + "精度为 " + Result.precision + " 位小数");
        }
        return new Result(1).setAnswer("函数 “" + Function.funcList[listPos].funcName + "” 参数错误");
    }

    //入口
    public Result value() {
        isWorking = true;

        if (brDiff != 0) {
            return new Result(1).setAnswer("括号不匹配");
        }

        // 开始计算
        initCache();
        Result res = value(0, text.length() - 1, new Complex(0, Double.NaN));
        return res;
    }

    private void carry(int[] bit, int pos) {
        int i, carray = 0;
        for (i = 0; i <= pos; i++) {
            bit[i] += carray;
            if (bit[i] <= 9) {
                carray = 0;
            } else if (bit[i] > 9 && i < pos) {
                carray = bit[i] / 10;
                bit[i] = bit[i] % 10;
            } else if (bit[i] > 9 && i >= pos) {
                while (bit[i] > 9) {
                    carray = bit[i] / 10;
                    bit[i] = bit[i] % 10;
                    i++;
                    bit[i] = carray;
                }
            }
        }
    }

    private Result fact(Complex c) {
        if (c.im != 0)
            return new Result(3);
        int bigInteger = (int) c.re;
        int pos = 0;
        int digit;
        int a, b;
        double sum = 0;
        for (a = 1; a <= bigInteger; a++) {
            if (!isWorking) return new Result(2);
            sum += Math.log10(a);
        }
        digit = (int) sum + 1;

        int[] fact = new int[digit];
        fact[0] = 1;

        for (a = 2; a <= bigInteger; a++) {
            for (b = digit - 1; b >= 0; b--) {
                if (fact[b] != 0) {
                    pos = b;
                    break;
                }
            }

            for (b = 0; b <= pos; b++) {
                if (!isWorking) return new Result(2);
                fact[b] *= a;
            }
            carry(fact, pos);
        }

        for (b = digit - 1; b >= 0; b--) {
            if (fact[b] != 0) {
                pos = b;
                break;
            }
        }

        StringBuffer sb = new StringBuffer();
        for (a = pos; a >= 0; a--) {
            if (!isWorking) return new Result(2);
            sb.append(fact[a]);
        }
        return new Result(new Complex(sb.toString()));
    }

    public void stopEvaluation() {
        isWorking = false;
    }
}
