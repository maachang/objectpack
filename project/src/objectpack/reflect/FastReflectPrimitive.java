package objectpack.reflect;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * プリミティブ系処理.
 *
 * @version 2013/12/07
 * @author masahito suzuki
 *
 */
@SuppressWarnings("rawtypes")
abstract class FastReflectPrimitive {
    private FastReflectPrimitive() {
    }

    /** 非パラメータ. **/
    protected static final Object[] NO_PARAM = new Object[0];

    /** 非パラメータクラス **/
    protected static final Class[] NO_PARAM_CLASS = new Class[0];

    /** 文字列. **/
    protected static final Class STRING = String.class;

    /** オブジェクト. **/
    protected static final Class OBJECT = Object.class;

    protected static final Class BOOLEAN_CLASS = Boolean.class;
    protected static final Class BYTE_CLASS = Byte.class;
    protected static final Class CHAR_CLASS = Character.class;
    protected static final Class SHORT_CLASS = Short.class;
    protected static final Class INTEGER_CLASS = Integer.class;
    protected static final Class LONG_CLASS = Long.class;
    protected static final Class FLOAT_CLASS = Float.class;
    protected static final Class DOUBLE_CLASS = Double.class;

    protected static final Class BOOLEAN_TYPE = Boolean.TYPE;
    protected static final Class BYTE_TYPE = Byte.TYPE;
    protected static final Class CHAR_TYPE = Character.TYPE;
    protected static final Class SHORT_TYPE = Short.TYPE;
    protected static final Class INTEGER_TYPE = Integer.TYPE;
    protected static final Class LONG_TYPE = Long.TYPE;
    protected static final Class FLOAT_TYPE = Float.TYPE;
    protected static final Class DOUBLE_TYPE = Double.TYPE;

    protected static final Class BIG_DECIMAL_CLASS = BigDecimal.class;
    protected static final Class BIG_INTEGER_CLASS = BigInteger.class;

    /** 指定クラスがプリミティブ型かチェック. **/
    protected static final boolean _PRIMITIVE(final Class c) {
        return (c == BOOLEAN_CLASS || c == BOOLEAN_TYPE || c == BYTE_CLASS
                || c == BYTE_TYPE || c == CHAR_CLASS || c == CHAR_TYPE
                || c == SHORT_CLASS || c == SHORT_TYPE || c == INTEGER_CLASS
                || c == INTEGER_TYPE || c == LONG_CLASS || c == LONG_TYPE
                || c == FLOAT_CLASS || c == FLOAT_TYPE || c == DOUBLE_CLASS
                || c == DOUBLE_TYPE || c == BIG_DECIMAL_CLASS || c == BIG_INTEGER_CLASS);
    }

    /** プリミティブコンバート. **/
    protected static final Class _CONV_PRIMITIVE(final Class c) {
        if (c == BOOLEAN_CLASS || c == BOOLEAN_TYPE) {
            return BOOLEAN_CLASS;
        } else if (c == BYTE_CLASS || c == BYTE_TYPE) {
            return BYTE_CLASS;
        } else if (c == CHAR_CLASS || c == CHAR_TYPE) {
            return CHAR_CLASS;
        } else if (c == SHORT_CLASS || c == SHORT_TYPE) {
            return SHORT_CLASS;
        } else if (c == INTEGER_CLASS || c == INTEGER_TYPE) {
            return INTEGER_CLASS;
        } else if (c == LONG_CLASS || c == LONG_TYPE) {
            return LONG_CLASS;
        } else if (c == FLOAT_CLASS || c == FLOAT_TYPE) {
            return FLOAT_CLASS;
        } else if (c == DOUBLE_CLASS || c == DOUBLE_TYPE) {
            return DOUBLE_CLASS;
        } else if (c == BIG_DECIMAL_CLASS) {
            return BIG_DECIMAL_CLASS;
        } else if (c == BIG_INTEGER_CLASS) {
            return BIG_INTEGER_CLASS;
        }
        return null;
    }

    /** 指定クラスが数値プリミティブ型かチェック. **/
    protected static final boolean _NUMBER_PRIMITIVE(final Class c) {
        return (c == BYTE_CLASS || c == BYTE_TYPE || c == CHAR_CLASS
                || c == CHAR_TYPE || c == SHORT_CLASS || c == SHORT_TYPE
                || c == INTEGER_CLASS || c == INTEGER_TYPE || c == LONG_CLASS
                || c == LONG_TYPE || c == FLOAT_CLASS || c == FLOAT_TYPE
                || c == DOUBLE_CLASS || c == DOUBLE_TYPE
                || c == BIG_DECIMAL_CLASS || c == BIG_INTEGER_CLASS);
    }

    /** 指定クラスが小数点プリミティブ型かチェック. **/
    protected static final boolean _FLOAT_PRIMITIVE(final Class c) {
        return (c == FLOAT_CLASS || c == FLOAT_TYPE || c == DOUBLE_CLASS
                || c == DOUBLE_TYPE || c == BIG_DECIMAL_CLASS);
    }

    /**
     * プリミティブ条件を、プリミティブラッパークラスに変換.
     * 
     * @param clazz
     *            対象のクラスを設定します.
     * @return Class 変換されたクラス情報が返されます.
     */
    public static final Class convertClass(final Class clazz) {
        return _CONV_PRIMITIVE(clazz);
    }

    /**
     * プリミティブ条件かチェック.
     * 
     * @param clazz
     *            対象のクラスを設定します.
     * @return boolean [true]の場合、プリミティブ型です.
     */
    public static final boolean isPrimitive(final Class clazz) {
        return _PRIMITIVE(clazz);
    }
}
