package io.shulie.takin.cloud.common.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * @author shulie
 * @date 2019-04-02 20:44 生成唯一短ID
 */
public class Generate {

    private static final String X36 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private static final String[] X36_ARRAY = "0,1,2,3,4,5,6,7,8,9,A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z"
        .split(",");

    private static final Map<Character, Integer> mapX36 = new HashMap<>();

    static {
        for (int i = 0; i < X36.length(); i++) {
            mapX36.put(X36.charAt(i), i);
        }
    }

    public static String generateId36Scale(String prefix) {
        return prefix + generateId36Scale();
    }

    public static String generateId36Scale() {
        Calendar calendar = Calendar.getInstance();
        StringBuilder stringBuilder = new StringBuilder().
            append(calendar.get(Calendar.YEAR)).
            append(calendar.get(Calendar.MONTH) + 1).
            append(calendar.get(Calendar.DAY_OF_MONTH)).
            append(calendar.get(Calendar.HOUR_OF_DAY)).
            append(calendar.get(Calendar.MINUTE)).
            append(calendar.get(Calendar.SECOND)).
            append(calendar.get(Calendar.MILLISECOND)).
            append((int)((Math.random() * 9 + 1) * 1000));
        int scale = 36;
        return scaleToString(new BigInteger(stringBuilder.toString()), scale);
    }

    private static String scaleToString(BigInteger num, int scaleInt) {
        StringBuilder stringBuilder = new StringBuilder();
        BigInteger scale = new BigInteger(String.valueOf(scaleInt));
        if (num.compareTo(BigInteger.ONE) == 0) {
            stringBuilder.append("0");
        }
        while (num.compareTo(BigInteger.ONE) > 0) {
            stringBuilder.append(X36_ARRAY[num.mod(scale).intValue()]);
            num = num.divide(scale);
        }
        return stringBuilder.reverse().toString();
    }

    private static BigInteger scaleConver(String str, int scale) {
        BigInteger num = new BigInteger("0");
        int size = str.length();
        for (int i = 0; i < str.length(); i++) {
            String digits = String.valueOf(str.charAt(i)).toUpperCase();
            double dn = mapX36.get(digits.charAt(0)) * Math.pow(scale, size - i - 1);
            BigDecimal bigDecimal = new BigDecimal(dn);
            num = num.add(bigDecimal.toBigInteger());
        }
        return num;
    }
}
