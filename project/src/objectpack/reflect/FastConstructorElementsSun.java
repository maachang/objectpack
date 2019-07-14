package objectpack.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * コンストラクタ要素群.
 * <p>
 * SUNパッケージを利用.
 * </p>
 *
 * @version 2013/12/07
 * @author masahito suzuki
 *
 */
@SuppressWarnings("rawtypes")
class FastConstructorElementsSun extends FastConstructorElements {
    protected static final String ACQUIRE_CONST_ACCESSOR = "acquireConstructorAccessor";
    protected static final String CONST_ACCESSOR = "constructorAccessor";

    /** コンストラクタ情報格納用 **/
    private static class FastConstructorEmtSun {
        /** コンストラクタオブジェクト **/
        protected sun.reflect.ConstructorAccessor constructor;

        /** コンストラクタ引数. **/
        protected Class[] params;

        /** メソッドプリミティブ判別 **/
        protected boolean[] primitives;

        /** コンストラクタ引数長 **/
        protected int paramsLength;

        /** コンストラクタ. **/
        protected FastConstructorEmtSun(Constructor constructor)
                throws NoSuchMethodException, IllegalAccessException,
                InvocationTargetException, NoSuchFieldException {
            boolean[] pms = null;
            Class[] args = constructor.getParameterTypes();
            int len = (args == null) ? 0 : args.length;
            if (len == 0) {
                args = FastReflectPrimitive.NO_PARAM_CLASS;
            } else {
                Class pc;
                pms = new boolean[len];
                for (int i = 0; i < len; i++) {
                    pms[i] = args[i].isPrimitive();
                    // pc = FastReflectPrimitive.CONV_PRIMITIVE.get( args[ i ] )
                    // ;
                    pc = FastReflectPrimitive._CONV_PRIMITIVE(args[i]);
                    if (pc != null) {
                        args[i] = pc;
                    }
                }
            }
            constructor.setAccessible(true);
            // メソッドコンストラクタを取得.
            Method am = constructor.getClass().getDeclaredMethod(
                    ACQUIRE_CONST_ACCESSOR);
            am.setAccessible(true);
            am.invoke(constructor);
            am = null;
            Field ma = constructor.getClass().getDeclaredField(CONST_ACCESSOR);
            ma.setAccessible(true);
            // アクセサコンストラクタを取得.
            sun.reflect.ConstructorAccessor accessor = (sun.reflect.ConstructorAccessor) ma
                    .get(constructor);
            this.constructor = accessor;
            this.params = args;
            this.primitives = pms;
            this.paramsLength = len;
        }
    }

    /** フィールド格納Map. **/
    private final List<FastConstructorEmtSun> list = new ArrayList<FastConstructorEmtSun>();

    /** BigDecimalフラグ. **/
    private boolean bigDecimalFlag;

    /**
     * コンストラクタ.
     */
    protected FastConstructorElementsSun() {
    }

    /**
     * コンストラクタ.
     * 
     * @param clazz
     *            対象のクラスオブジェクトを設定します.
     * @exception Exception
     *                例外.
     */
    public FastConstructorElementsSun(Class clazz)
            throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, NoSuchFieldException {
        Constructor[] cs = clazz.getDeclaredConstructors();
        if (cs != null) {
            int len = cs.length;
            for (int i = 0; i < len; i++) {
                // privateもキャッシュ.
                Constructor c = cs[i];
                // if( Modifier.isPublic( c.getModifiers() ) ) {
                // list.add( new FastConstructorEmtSun( c ) ) ;
                // }
                list.add(new FastConstructorEmtSun(c));
            }
        }
        bigDecimalFlag = (clazz == FastReflectUtil.BIGDECIMAL_CLASS);
    }

    /**
     * デストラクタ.
     */
    protected void finalize() throws Exception {
        list.clear();
    }

    /**
     * 指定名のコンストラクタを実行.
     * 
     * @param cl
     *            対象のクラスローダを設定します.
     * @param types
     *            対象のパラメータタイプを設定します.
     * @param args
     *            引数パラメータ型群を設定します.
     * @return Object [null]の場合、存在しません.
     * @exception ClassNotFoundException
     *                クラスが存在しない場合の例外.
     * @exception InstantiationException
     *                基本となるコンストラクタを宣言するクラスが abstract クラスを表す場合.
     * @exception InvocationTargetException
     *                基本となるコンストラクタが例外をスローする場合.
     * @exception IllegalAccessException
     *                指定条件に対してアクセスできない場合.
     */
    public Object newInstance(ClassLoader cl, Class[] types, Object[] args)
            throws ClassNotFoundException, InstantiationException,
            InvocationTargetException, IllegalAccessException {
        int len = list.size();
        int argsLen = (args == null) ? 0 : args.length;
        if (argsLen == 0) {
            for (int i = 0; i < len; i++) {
                FastConstructorEmtSun emt = list.get(i);
                if (emt.paramsLength == 0) {
                    return emt.constructor
                            .newInstance(FastReflectPrimitive.NO_PARAM);
                }
            }
        } else {
            if (bigDecimalFlag && argsLen >= 1
                    && args[0] instanceof java.math.BigDecimal) {
                if (argsLen == 1) {
                    return new java.math.BigDecimal(args[0].toString());
                } else if (argsLen == 2) {
                    if (args[1] instanceof java.math.MathContext) {
                        return new java.math.BigDecimal(args[0].toString(),
                                (java.math.MathContext) args[1]);
                    } else {
                        return new java.math.BigDecimal(args[0].toString());
                    }
                }
            }
            int pf = 100 * argsLen;
            int score = -1;
            FastConstructorEmtSun targetEmt = null;
            int sc;
            for (int i = 0; i < len; i++) {
                FastConstructorEmtSun emt = list.get(i);
                if (emt.paramsLength == argsLen) {
                    sc = FastReflectUtil.parmasScore(emt.primitives,
                            emt.params, types, args, cl);
                    if (sc != -1 && score < sc) {
                        if (sc == pf) {
                            return emt.constructor.newInstance(args);
                        }
                        score = sc;
                        targetEmt = emt;
                    }
                }
            }
            if (targetEmt != null) {
                if ((args = FastReflectUtil.convertParams(args,
                        targetEmt.params)) == null) {
                    throw new IllegalAccessException("パラメータ解析に失敗しました");
                }
                return targetEmt.constructor.newInstance(args);
            }
        }
        return null;
    }
}
