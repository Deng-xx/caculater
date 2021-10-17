package com.sf.ExpressionHandler;

public class ParseNumber {

    private static final String baseSymbol = "  ⑵⑶⑷⑸⑹⑺⑻⑼⑽⑾⑿⒀⒁⒂⒃";

    // is this character a base notation?这个字符是基本符号吗？
    public static boolean isBaseSymbol(char c) {
        return baseSymbol.indexOf(c) != -1;
    }

    //在10和16进制下将数字字符数字化
    private static int getDigit(char c, int base) throws NumberFormatException {
        int digit;
        if (c >= '0' && c <= '9') { //10进制
            digit = c - '0';
        } else if (c >= 'A' && c <= 'F') { //16进制
            digit = c - 'A' + 10;
        } else { //不是一个有效的数字
            throw new NumberFormatException();
        }
        if (digit >= base) throw new NumberFormatException();
        return digit;
    }

    //解析原始数据
    private static double parseRaw(String s, int base) throws NumberFormatException {
        int dotPos;
        for (dotPos = 0; dotPos < s.length(); dotPos++) {
            if (s.charAt(dotPos) == '.')
                break;
        }

        double frac = 0;

        //先解析小数点左边的整数
        double digitBase = 1;//数字的每位基数大小，如十进制下的个十百千
        for (int i = dotPos - 1; i >= 0; i--) {
            frac += getDigit(s.charAt(i), base) * digitBase;
            digitBase *= base;
        }
        //解析小数点右边的小数
        digitBase = 1. / base;
        for (int i = dotPos + 1; i < s.length(); i++) {
            frac += getDigit(s.charAt(i), base) * digitBase;
            digitBase /= base;
        }

        return frac;
    }

    //解析科学计数法形式将其适配，如10~10^2转换为100
    public static double parseCompat(String s) throws NumberFormatException {
        int baseDivSymbolPos = s.indexOf('~');
        if (baseDivSymbolPos <= 0 || baseDivSymbolPos >= s.length() - 1)
            throw new NumberFormatException();

        //将~号右边数字解析
        int base = Integer.parseInt(s.substring(baseDivSymbolPos + 1));
        if (!(base >= 2 && base <= 10 || base == 12 || base == 16)) // base not supported有些进制不支持
            throw new NumberFormatException();

        return parseRaw(s.substring(0, baseDivSymbolPos), base);
    }

    //解析某进制下的浮点数，只有在有进制符或以科学记数法形式才能解析
    public static double parse(String s) throws NumberFormatException {
        int base = 0;
        int baseSymbolPos = -1;
        for (int i = 0; i < s.length(); i++) {
            base = baseSymbol.indexOf(s.charAt(i));
            if (base > 0) { //进制基数不为0
                baseSymbolPos = i;
                break;
            }
        }
        if (baseSymbolPos == 0) throw new NumberFormatException();
        if (baseSymbolPos < 0) return parseCompat(s); //将字符串解析为适配格式

        //TODO:有进制符就找下一位指数，计算100(2)1?
        int exp;//存放指数
        if (baseSymbolPos == s.length() - 1) {
            exp = 0;//没有指数
        } else {
            exp = Integer.parseInt(s.substring(baseSymbolPos + 1));
        }
        double frac = parseRaw(s.substring(0, baseSymbolPos), base);

        return frac * Math.pow(base, exp);
    }

    //没有使用科学记数法的形式
    private static final String numSymbol = "0123456789ABCDEF";

    //将正数按精度解析为对应进制的字符串
    private static String toPositiveRawBaseString(double d, long base, int prec) { // d_>0
        int[] digits = new int[100];
        //整数位数
        int intDigitNum = (int) Math.floor(Math.log(d) / Math.log(base)) + 1;
        if (intDigitNum < 0) intDigitNum = 0;

        long intPart = (long) Math.floor(d);//整数部分
        double fracPart = d - intPart;//小数部分

        //将整数部分按照除k取余法获得k进制下每i位数字存放进digits[i],digits[0]一定为0
        for (int i = intDigitNum; i >= 0; i--) {
            digits[i] = (int) (intPart % base);
            intPart /= base;
        }

        //小数部分按照乘k取整获取对应位数,precision为精度。intDigitNum+prec+1
        for (int i = intDigitNum + 1; i <= prec + 1; i++) {
            fracPart *= base;
            digits[i] = (int) Math.floor(fracPart);
            fracPart -= digits[i];
        }

        //四舍五入
        if (digits[prec + 1] * 2 >= base) {
            digits[prec]++;
            for (int i = prec; i > 0; i--) {
                if (digits[i] == base) {
                    digits[i] = 0;
                    digits[i - 1]++;
                } else {
                    break;
                }
            }
        }

        //确定在精度内的最后一个有效数字位
        int maxNonZeroPos;
        for (maxNonZeroPos = prec; maxNonZeroPos >= 0; maxNonZeroPos--) {
            if (maxNonZeroPos <= intDigitNum || digits[maxNonZeroPos] > 0)
                break;
        }

        //转换为字符串存进result
        String result = "";
        for (int i = 0; i <= maxNonZeroPos; i++) {
            if (!(intDigitNum > 0 && i == 0 && digits[0] == 0))//同时满足不+，其实就是因为digits[0]=0在正数时无意义
                result += numSymbol.charAt(digits[i]);
            if (i == intDigitNum && i < maxNonZeroPos)
                result += '.';
        }

        //补0
        for (int i = maxNonZeroPos + 1; i <= intDigitNum; i++)
            result += '0';

        return result;
    }

    //将无科学记数法的base进制下的数据d转换为精度为prec位的对应字符串
    public static String toBaseString(double d_, int base, int prec) {
        if (Double.isNaN(d_)) return "nan";//TODO:可以改为无穷小
        if (d_ == Double.POSITIVE_INFINITY) return "∞";
        if (d_ == Double.NEGATIVE_INFINITY) return "-∞";

        String negativeSymbol = (d_ >= 0 ? "" : "-");
        double d = Math.abs(d_);
        double maxPreciseValue = Math.pow(base, prec);
        double minPreciseValue = Math.pow(base, -prec);

        if (d < maxPreciseValue && d > minPreciseValue) { //能在当前精度下转换
            return negativeSymbol + toPositiveRawBaseString(d, base, prec) + (base == 10 ? "" : baseSymbol.charAt(base));
        } else { //用科学记数法转换为在当前精度下能转换的数据
            double fracPart = d;
            int digitExp = 0;
            while (fracPart >= base) {
                digitExp++;
                fracPart /= base;
            }
            while (fracPart < 1) {
                digitExp--;
                fracPart *= base;
            }

            String res = toPositiveRawBaseString(fracPart, base, prec) + (base == 10 ? "E" : baseSymbol.charAt(base)) + digitExp;
            return negativeSymbol + res;
        }
    }


}
