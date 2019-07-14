package objectpack.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * メソッド要素群.
 * <p>
 * SUNパッケージを利用.
 * </p>
 *
 * @version 2013/12/07
 * @author masahito suzuki
 *
 */
@SuppressWarnings("rawtypes")
class FastMethodElementsSun extends FastMethodElements {
    protected static final String ACQUIRE_METHOD_ACCESSOR = "acquireMethodAccessor";
    protected static final String METHOD_ACCESSOR = "methodAccessor";

    /** メソッド情報格納用 **/
    private static class FastMethodEmtSun {
        /** 次のメソッドポインタ. **/
        protected FastMethodEmtSun next;

        /** メソッドオブジェクト **/
        protected sun.reflect.MethodAccessor method;

        /** メソッド引数. **/
        protected Class[] params;

        /** メソッドプリミティブ判別 **/
        protected boolean[] primitives;

        /** メソッド引数長 **/
        protected int paramsLength;

        /** static条件 **/
        protected boolean isStatic;

        /** コンストラクタ. **/
        protected FastMethodEmtSun(Method method) throws NoSuchMethodException,
                InvocationTargetException, NoSuchFieldException,
                IllegalAccessException {
            boolean[] pms = null;
            Class[] args = method.getParameterTypes();
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
            method.setAccessible(true);
            // メソッドアクセサを取得.
            Method am = method.getClass().getDeclaredMethod(
                    ACQUIRE_METHOD_ACCESSOR);
            am.setAccessible(true);
            am.invoke(method);
            am = null;
            Field ma = method.getClass().getDeclaredField(METHOD_ACCESSOR);
            ma.setAccessible(true);
            // アクセサメソッドを取得.
            sun.reflect.MethodAccessor accessor = (sun.reflect.MethodAccessor) ma
                    .get(method);
            this.method = accessor;
            this.params = args;
            this.primitives = pms;
            this.paramsLength = len;
            this.isStatic = Modifier.isStatic(method.getModifiers());
        }
    }

    /** フィールド格納Map. **/
    private final Map<String, FastMethodEmtSun> map = new HashMap<String, FastMethodEmtSun>(
            FastReflect.INIT_MAP_LENGTH);

    /**
     * コンストラクタ.
     */
    protected FastMethodElementsSun() {
    }

    /**
     * コンストラクタ.
     * 
     * @param clazz
     *            対象のクラスオブジェクトを設定します.
     * @exception Exception
     *                例外.
     */
    public FastMethodElementsSun(Class clazz) throws NoSuchMethodException,
            InvocationTargetException, NoSuchFieldException,
            IllegalAccessException {
        Method[] ms = clazz.getDeclaredMethods();
        if (ms != null) {
            int len = ms.length;
            for (int i = 0; i < len; i++) {
                // publicのみキャッシュ.
                Method m = ms[i];
                if (Modifier.isPublic(m.getModifiers())) {
                    String name = m.getName();
                    if (map.containsKey(name)) {
                        FastMethodEmtSun f = map.get(name);
                        if (f.next == null) {
                            f.next = new FastMethodEmtSun(m);
                        } else {
                            FastMethodEmtSun n = f.next;
                            f.next = new FastMethodEmtSun(m);
                            f.next.next = n;
                        }
                    } else {
                        map.put(name, new FastMethodEmtSun(m));
                    }
                }
            }
        }
    }

    /**
     * デストラクタ.
     */
    protected void finalize() throws Exception {
        map.clear();
    }

    /**
     * 指定名のメソッドを実行.
     * 
     * @param result
     *            処理結果を受け取るオブジェクト配列を設定します.
     * @param target
     *            対象のターゲットオブジェクトを設定します.
     * @param name
     *            対象のフィールド名を設定します.
     * @param cl
     *            対象のクラスローダを設定します.
     * @param types
     *            対象のパラメータタイプを設定します.
     * @param args
     *            引数パラメータ型群を設定します.
     * @return boolean [true]の場合、実行されました.
     * @exception ClassNotFoundException
     *                クラスが存在しない場合の例外.
     * @exception InvocationTargetException
     *                基本となるコンストラクタが例外をスローする場合.
     * @exception IllegalAccessException
     *                指定条件に対してアクセスできない場合.
     */
    public boolean invoke(Object[] result, Object target, String name,
            ClassLoader cl, Class[] types, Object[] args)
            throws ClassNotFoundException, IllegalAccessException,
            InvocationTargetException {
        boolean staticFlag = (target == null);
        int argsLen = (args == null) ? 0 : args.length;
        FastMethodEmtSun emt = map.get(name);
        if (argsLen == 0) {
            while (emt != null) {
                if (staticFlag == emt.isStatic && argsLen == emt.paramsLength) {
                    Object res = emt.method.invoke(target,
                            FastReflectPrimitive.NO_PARAM);
                    if (result != null) {
                        result[0] = res;
                    }
                    return true;
                }
                emt = emt.next;
            }
        } else {
            int pf = 100 * argsLen;
            int score = -1;
            FastMethodEmtSun targetEmt = null;
            int sc;
            while (emt != null) {
                if (staticFlag == emt.isStatic && argsLen == emt.paramsLength) {
                    sc = FastReflectUtil.parmasScore(emt.primitives,
                            emt.params, types, args, cl);
                    if (sc != -1 && score < sc) {
                        if (sc == pf) {
                            Object res = emt.method.invoke(target, args);
                            if (result != null) {
                                result[0] = res;
                            }
                            return true;
                        }
                        score = sc;
                        targetEmt = emt;
                    }
                }
                emt = emt.next;
            }
            if (targetEmt != null) {
                // 変換失敗の場合は実行失敗にする.
                args = FastReflectUtil.convertParams(args, targetEmt.params);
                if (args == null) {
                    return false;
                }
                Object res = targetEmt.method.invoke(target, args);
                if (result != null) {
                    result[0] = res;
                }
                return true;
            }
        }
        return false;
    }
}
