package objectpack.reflect;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;

/**
 * 高速リフレクションUtil.
 *
 * @version 2013/12/07
 * @author masahito suzuki
 *
 */
@SuppressWarnings("rawtypes")
abstract class FastReflectUtil {
    private FastReflectUtil() {
    }

    /** BigDecimal. **/
    protected static final Class BIGDECIMAL_CLASS = FastReflectPrimitive.BIG_DECIMAL_CLASS;

    /** BigInteger. **/
    protected static final Class BIGINTEGER_CLASS = FastReflectPrimitive.BIG_INTEGER_CLASS;

    /** プリミティブ系. **/
    private static final Object OBJECT = FastReflectPrimitive.OBJECT;
    private static final Object STRING = FastReflectPrimitive.STRING;

    private static final Class BYTE_CLASS = Byte.class;
    private static final Class CHAR_CLASS = Character.class;
    private static final Class SHORT_CLASS = Short.class;
    private static final Class INTEGER_CLASS = Integer.class;
    private static final Class LONG_CLASS = Long.class;
    private static final Class FLOAT_CLASS = Float.class;
    private static final Class DOUBLE_CLASS = Double.class;

    /** プリミティブ関連のコンバート処理. **/
    private static final int CONV_PRIMITIVE_TABLE(final Class n) {
        if (n == BYTE_CLASS) {
            return 0;
        }
        if (n == CHAR_CLASS) {
            return 1;
        }
        if (n == SHORT_CLASS) {
            return 2;
        }
        if (n == INTEGER_CLASS) {
            return 3;
        }
        if (n == LONG_CLASS) {
            return 4;
        }
        if (n == FLOAT_CLASS) {
            return 5;
        }
        if (n == DOUBLE_CLASS) {
            return 6;
        }
        if (n == BIGDECIMAL_CLASS) {
            return 7;
        }
        if (n == BIGINTEGER_CLASS) {
            return 8;
        }
        return -1;
    }

    /**
     * パラメータに対する型を取得.
     * 
     * @param args
     *            パラメータを設定します.
     * @return Class[] パラメータ型が返されます.
     */
    public static final Class[] getParamsType(Object... args) {
        int argsLen;
        if (args == null || (argsLen = args.length) <= 0) {
            return FastReflectPrimitive.NO_PARAM_CLASS;
        }
        Class[] c = new Class[argsLen];
        Object o;
        for (int i = 0; i < argsLen; i++) {
            if ((o = args[i]) != null) {
                c[i] = o.getClass();
            }
        }
        return c;
    }

    /**
     * パラメータに対する型を取得.
     * 
     * @param args
     *            パラメータを設定します.
     * @return Class[] パラメータ型が返されます.
     */
    public static final Class[] getParamsTypeByClass(Class[] args) {
        int argsLen;
        if (args == null || (argsLen = args.length) <= 0) {
            return FastReflectPrimitive.NO_PARAM_CLASS;
        }
        Class pc;
        for (int i = 0; i < argsLen; i++) {
            if (args[i] != null) {
                pc = FastReflectPrimitive._CONV_PRIMITIVE(args[i]);
                if (pc != null) {
                    args[i] = pc;
                }
            }
        }
        return args;
    }

    /**
     * パラメータに対して、一致点数を取得.
     * 
     * @param pms
     *            チェック元のプリミティブ条件を設定します.
     * @param src
     *            チェック元を設定します.
     * @param dest
     *            チェック先を設定します.
     * @param args
     *            実行対象のパラメータを設定します.
     * @param cl
     *            対象のクラスローダーを設定します.
     * @return int 一致点数が返されます.
     * @exception ClassNotFoundException
     *                該当クラスが存在しない場合に発生します.
     */
    public static final int parmasScore(boolean[] pms, Class[] src,
            Class[] dest, Object[] args, ClassLoader cl)
            throws ClassNotFoundException {
        /*
         * 修正:#16004 (オーバーロードのあるメソッドの同定に失敗する). 2009/04/13 masahito suzuki.
         */
        // ●１つの引数が一致した場合は、100点が加算される.
        // ●１つの引数が継承で一致した場合は、継承１つの条件に対して、
        // 100点から１点減算させた値で得点加算する.
        // ●１つのsrc引数がObject型の場合は、60点が加算される.
        // ●１つの引数が数値のプリミティブ型同士の場合は、50点が加算される.
        // ●１つの引数の関係が、プリミティブ - 文字列関係の場合は、40点が加算される.
        // ●ただし、引数のどれか１つが一致しなかった場合は、-1を返す.
        int ret = 0;
        int lenJ;
        Class s, d;
        Object a;
        String ss;
        boolean one = true;
        int len = src.length;
        Class o;
        String[] ifce;
        int wScore;
        int befWScore;
        String sname;
        for (int i = 0; i < len; i++) {
            s = src[i];
            d = dest[i];
            a = args[i];
            // チェック先がNULLの場合は、相互チェックしない.
            if (d != null) {
                // チェック元と、チェック先が一致している.
                if (s == d) {
                    ret += 100;
                }
                // チェック元がObjectの場合は、相互チェックしない.
                else if (s == OBJECT) {
                    ret += 60;
                }
                // ・チェック元・先が、数値プリミティブ値の場合.
                // else if( NUMBER_PRIMITIVE.contains( s ) &&
                // NUMBER_PRIMITIVE.contains( d ) ) {
                else if (FastReflectPrimitive._NUMBER_PRIMITIVE(s)
                        && FastReflectPrimitive._NUMBER_PRIMITIVE(d)) {
                    ret += 50;
                }
                // ・チェック元が文字列で、チェック先が、文字列か、プリミティブ型の場合.
                // else if( s == STRING && ( PRIMITIVE.contains( d ) ) ) {
                else if (s == STRING && (FastReflectPrimitive._PRIMITIVE(d))) {
                    ret += 40;
                }
                // ・チェック元がプリミティブか、文字列で、チェック先が、文字列の場合.
                // else if( d == STRING && ( PRIMITIVE.contains( s ) ) ) {
                else if (d == STRING && (FastReflectPrimitive._PRIMITIVE(s))) {
                    // 呼び出し対象が文字列の場合、呼び出し元がBooleanならば、true/falseチェック
                    // それ以外の場合は、数値かチェックして、それが一致する場合は、得点プラス.
                    ss = (String) a;
                    if ((s == Boolean.class && ("true".equals(ss) || "false"
                            .equals(ss))) || isNumber(ss)) {
                        ret += 40;
                    }
                    // 文字列がプリミティブ型でない場合は、対象外.
                    else {
                        return -1;
                    }
                } else {
                    // チェック元に対して、チェック先の継承クラス／インターフェイスを
                    // 掘り下げてチェックする.
                    FastClassElement em = FastReflectClass
                            .getClass(d.getName());
                    one = false;
                    wScore = 100;
                    // チェック元がインターフェイス属性の場合は、インターフェイス内容と、
                    // スーパークラスのチェックを行う.
                    if (s.isInterface()) {
                        sname = s.getName();
                        while (true) {
                            wScore--;
                            befWScore = wScore;
                            ifce = em.getInterfaseNames();
                            if (ifce != null && (lenJ = ifce.length) > 0) {
                                // インターフェース名群と、チェック元のクラス名が一致.
                                if (Arrays.binarySearch(ifce, sname) != -1) {
                                    one = true;
                                    ret += wScore;
                                }
                                // 継承インターフェイスが１つの場合.
                                else if (lenJ == 1) {
                                    if ((wScore = toInterface(wScore, sname,
                                            ifce[0], cl)) != -1) {
                                        one = true;
                                        ret += wScore;
                                    }
                                }
                                // 継承インターフェイスが複数の場合.
                                else {
                                    for (int j = 0; j < lenJ; j++) {
                                        if ((wScore = toInterface(wScore,
                                                sname, ifce[i], cl)) != -1) {
                                            one = true;
                                            ret += wScore;
                                            break;
                                        }
                                    }
                                }
                                if (one) {
                                    break;
                                }
                            }
                            wScore = befWScore;
                            // スーパークラスを取得.
                            em = FastReflectClass.getClass(em
                                    .getSuperClassName());
                            // スーパークラスがオブジェクトの場合.
                            if ((o = em.getClassObject()) == OBJECT) {
                                return -1;
                            }
                            // スーパークラスと、チェック元が一致する場合.
                            else if (o == s) {
                                one = true;
                                ret += wScore;
                                break;
                            }
                        }
                    }
                    // チェック元がオブジェクトの場合は、スーパークラスのみチェック.
                    else {
                        while (true) {
                            wScore--;
                            // スーパークラスを取得.
                            em = FastReflectClass.getClass(em
                                    .getSuperClassName());
                            // スーパークラスがオブジェクトの場合.
                            if ((o = em.getClassObject()) == OBJECT) {
                                return -1;
                            }
                            // スーパークラスと、チェック元が一致する場合.
                            else if (o == s) {
                                one = true;
                                ret += wScore;
                                break;
                            }
                        }
                    }
                    // 一致条件が存在しない.
                    if (!one) {
                        return -1;
                    }
                }
            }
            // nullに対してチェック元がプリミティブ型の場合.
            else if (pms[i]) {
                return -1;
            }
        }
        return ret;
    }

    /** interface比較. **/
    private static final int toInterface(int wScore, String sname, String name,
            ClassLoader cl) throws ClassNotFoundException {
        FastClassElement em = FastReflectClass.getClass(cl, name);
        String superClassName;
        while (em != null) {
            wScore--;
            superClassName = em.getSuperClassName();
            if (superClassName == null || superClassName.length() <= 0) {
                return -1;
            } else if (superClassName == sname) {
                return wScore;
            }
            em = FastReflectClass.getClass(cl, superClassName);
        }
        return -1;
    }

    /**
     * パラメータ変換.
     * 
     * @param args
     *            変換対象のパラメータを設定します.
     * @param types
     *            変換対象のパラメータタイプを設定します.
     * @return Object[] 変換されたオブジェクトが返されます.
     */
    public static final Object[] convertParams(Object[] args, Class[] types) {
        int len = args.length;
        Class s, d;
        Object[] ret;
        if (len > 0) {
            ret = new Object[len];
            System.arraycopy(args, 0, ret, 0, len);
        } else {
            return FastReflectPrimitive.NO_PARAM;
        }
        for (int i = 0; i < len; i++) {
            if (ret[i] != null) {
                s = types[i];
                d = ret[i].getClass();
                if (s != d) {
                    if (FastReflectPrimitive._NUMBER_PRIMITIVE(s)
                            && FastReflectPrimitive._NUMBER_PRIMITIVE(d)) {
                        ret[i] = convertNumberPrimitive(ret[i],
                                CONV_PRIMITIVE_TABLE(s),
                                CONV_PRIMITIVE_TABLE(d));
                    } else if (s == STRING
                            && (FastReflectPrimitive._PRIMITIVE(d))) {
                        ret[i] = ret[i].toString();
                    } else if (d == STRING
                            && (FastReflectPrimitive._PRIMITIVE(s))) {
                        if (s == Boolean.class) {
                            String str = (String) args[i];
                            if (str.equals("true")) {
                                ret[i] = Boolean.TRUE;
                            } else if (str.equals("false")) {
                                ret[i] = Boolean.FALSE;
                            } else {
                                throw new ClassCastException("第" + (i + 1)
                                        + "引数のキャストに失敗しました");
                            }
                        } else {
                            int o = CONV_PRIMITIVE_TABLE(s);
                            if (o != -1) {
                                if (isNumber((String) ret[i])) {
                                    ret[i] = convertNumber(o, (String) ret[i]);
                                } else {
                                    return null;
                                }
                            }
                        }
                    }
                }
            }
        }
        return ret;
    }

    /** 指定タイプに対して文字列から、数値変換. **/
    private static final Object convertNumber(int type, String s) {
        s = s.trim().toLowerCase();
        if (s.endsWith("f") || s.endsWith("l")) {
            s = s.substring(0, s.length() - 1);
        }
        if (type == 5 || type == 6) {
            return convertType(type, s);
        }
        int p = s.indexOf(".");
        if (p == -1) {
            if (s.startsWith("0x")) {
                s = s.substring(2);
                int len = s.length();
                if (len > 8) {
                    if (len > 16) {
                        throw new NumberFormatException("数値変換に失敗しました");
                    }
                    long ret = 0L;
                    for (int i = 0; i < len; i++) {
                        char c = s.charAt(i);
                        if (c >= '1' && c <= '9') {
                            ret |= ((int) (c - '0') << i);
                        } else if (c >= 'a' && c <= 'f') {
                            ret |= ((int) ((c - 'a') + 10) << i);
                        }
                    }
                    switch (type) {
                    case 0:
                        return (byte) (ret & 0x00000000000000ffL);
                    case 1:
                        return (char) (ret & 0x000000000000ffffL);
                    case 2:
                        return (short) (ret & 0x000000000000ffffL);
                    case 3:
                        return (int) (ret & 0x00000000ffffffffL);
                    case 4:
                        return ret;
                    }
                    return null;
                } else {
                    int ret = 0;
                    for (int i = 0; i < len; i++) {
                        char c = s.charAt(i);
                        if (c >= '1' && c <= '9') {
                            ret |= ((int) (c - '0') << i);
                        } else if (c >= 'a' && c <= 'f') {
                            ret |= ((int) ((c - 'a') + 10) << i);
                        }
                    }
                    switch (type) {
                    case 0:
                        return (byte) (ret & 0x000000ff);
                    case 1:
                        return (char) (ret & 0x0000ffff);
                    case 2:
                        return (short) (ret & 0x0000ffff);
                    case 3:
                        return ret;
                    case 4:
                        return (long) ret;
                    }
                    return null;
                }
            }
            return convertType(type, s);
        }
        return convertType(type, s.substring(0, p));
    }

    /** 文字列に対して、プリミティブタイプ変換. **/
    private static final Object convertType(int type, String s) {
        switch (type) {
        case 0:
            return Byte.parseByte(s);
        case 1:
            return (s.length() == 1) ? s.charAt(0) : (char) (Integer
                    .parseInt(s) & 0x0000ffff);
        case 2:
            return Short.parseShort(s);
        case 3:
            return Integer.parseInt(s);
        case 4:
            return Long.parseLong(s);
        case 5:
            return Float.parseFloat(s);
        case 6:
            return Double.parseDouble(s);
        case 7:
            return new BigDecimal(s);
        case 8:
            return new BigInteger(s);
        }
        return s;
    }

    /** 数値系プリミティブから、数値系プリミティブに対して、キャスト処理を行う. **/
    private static final Object convertNumberPrimitive(Object o, int srcType,
            int destType) {
        switch (destType) {
        case 0: {
            byte x = (Byte) o;
            switch (srcType) {
            case 0:
                return x;
            case 1:
                return (char) x;
            case 2:
                return (short) x;
            case 3:
                return (int) x;
            case 4:
                return (long) x;
            case 5:
                return (float) x;
            case 6:
                return (double) x;
            case 7:
                return new BigDecimal(String.valueOf(x));
            case 8:
                return new BigInteger(String.valueOf(x));
            }
        }
        case 1: {
            char x = (Character) o;
            switch (srcType) {
            case 0:
                return (byte) x;
            case 1:
                return x;
            case 2:
                return (short) x;
            case 3:
                return (int) x;
            case 4:
                return (long) x;
            case 5:
                return (float) x;
            case 6:
                return (double) x;
            case 7:
                return new BigDecimal(String.valueOf(x & 0x0000ffff));
            case 8:
                return new BigInteger(String.valueOf(x & 0x0000ffff));
            }
        }
        case 2: {
            short x = (Short) o;
            switch (srcType) {
            case 0:
                return (byte) x;
            case 1:
                return (char) x;
            case 2:
                return x;
            case 3:
                return (int) x;
            case 4:
                return (long) x;
            case 5:
                return (float) x;
            case 6:
                return (double) x;
            case 7:
                return new BigDecimal(String.valueOf(x));
            case 8:
                return new BigInteger(String.valueOf(x));
            }
        }
        case 3: {
            int x = (Integer) o;
            switch (srcType) {
            case 0:
                return (byte) x;
            case 1:
                return (char) x;
            case 2:
                return (short) x;
            case 3:
                return x;
            case 4:
                return (long) x;
            case 5:
                return (float) x;
            case 6:
                return (double) x;
            case 7:
                return new BigDecimal(String.valueOf(x));
            case 8:
                return new BigInteger(String.valueOf(x));
            }
        }
        case 4: {
            long x = (Long) o;
            switch (srcType) {
            case 0:
                return (byte) x;
            case 1:
                return (char) x;
            case 2:
                return (short) x;
            case 3:
                return (int) x;
            case 4:
                return x;
            case 5:
                return (float) x;
            case 6:
                return (double) x;
            case 7:
                return new BigDecimal(String.valueOf(x));
            case 8:
                return new BigInteger(String.valueOf(x));
            }
        }
        case 5: {
            float x = (Float) o;
            switch (srcType) {
            case 0:
                return (byte) x;
            case 1:
                return (char) x;
            case 2:
                return (short) x;
            case 3:
                return (int) x;
            case 4:
                return (long) x;
            case 5:
                return x;
            case 6:
                return (double) x;
            case 7:
                return new BigDecimal(String.valueOf(x));
            case 8:
                return new BigInteger(String.valueOf(x));
            }
        }
        case 6: {
            double x = (Double) o;
            switch (srcType) {
            case 0:
                return (byte) x;
            case 1:
                return (char) x;
            case 2:
                return (short) x;
            case 3:
                return (int) x;
            case 4:
                return (long) x;
            case 5:
                return (float) x;
            case 6:
                return x;
            case 7:
                return new BigDecimal(String.valueOf(x));
            case 8:
                return new BigInteger(String.valueOf(x));
            }
        }
        case 7: {
            BigDecimal x = (BigDecimal) o;
            switch (srcType) {
            case 0:
                return x.byteValue();
            case 1:
                return (char) (x.intValue() & 0x0000ffff);
            case 2:
                return x.shortValue();
            case 3:
                return x.intValue();
            case 4:
                return x.longValue();
            case 5:
                return x.floatValue();
            case 6:
                return x.doubleValue();
            case 7:
                return x;
            case 8:
                return new BigInteger(x.toString());
            }
        }
        case 8: {
            BigInteger x = (BigInteger) o;
            switch (srcType) {
            case 0:
                return x.byteValue();
            case 1:
                return (char) (x.intValue() & 0x0000ffff);
            case 2:
                return x.shortValue();
            case 3:
                return x.intValue();
            case 4:
                return x.longValue();
            case 5:
                return x.floatValue();
            case 6:
                return x.doubleValue();
            case 7:
                return new BigDecimal(x.toString());
            case 8:
                return x;
            }
        }
        }
        return o;
    }

    /** 対象文字列内が数値かチェック. **/
    private static final boolean isNumber(String num) {
        if (num == null || num.length() <= 0) {
            return false;
        }
        int start = 0;
        if (num.startsWith("-")) {
            start = 1;
        }
        boolean dt = false;
        int len = num.length();
        char c;
        if (start < len) {
            for (int i = start; i < len; i++) {
                c = num.charAt(i);
                if (c == '.') {
                    if (dt) {
                        return false;
                    }
                    dt = true;
                } else if ((c >= '0' && c <= '9') == false) {
                    return false;
                }
            }
        } else {
            return false;
        }
        return true;
    }
}
