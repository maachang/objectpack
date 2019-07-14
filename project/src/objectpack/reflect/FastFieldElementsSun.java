package objectpack.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * フィールド要素群.
 * <p>
 * SUNパッケージを利用.
 * </p>
 *
 * @version 2013/12/07
 * @author masahito suzuki
 *
 */
@SuppressWarnings("rawtypes")
class FastFieldElementsSun extends FastFieldElements {
    protected static final String OVERRIDE_FIELD = "override";
    protected static final String ACQUIRE_FIELD_ACCESSOR = "acquireFieldAccessor";

    class FastFieldChildSun extends FastFieldChild {
        protected sun.reflect.FieldAccessor field;
        protected boolean staticFlag;

        public void set(Object target, Object value)
                throws IllegalAccessException {
            field.set(target, value);
        }

        public Object get(Object target) throws IllegalAccessException {
            return field.get(target);
        }
    }

    /** フィールド格納Map. **/
    private final Map<String, FastFieldChildSun> map = new HashMap<String, FastFieldChildSun>(
            FastReflect.INIT_MAP_LENGTH);

    /**
     * コンストラクタ.
     */
    protected FastFieldElementsSun() {
    }

    /**
     * コンストラクタ.
     * 
     * @param clazz
     *            対象のクラスオブジェクトを設定します.
     * @exception Exception
     *                例外.
     */
    public FastFieldElementsSun(Class clazz) throws NoSuchFieldException,
            IllegalAccessException, NoSuchMethodException,
            InvocationTargetException {
        Field[] fs = clazz.getDeclaredFields();
        if (fs != null) {
            int len = fs.length;
            for (int i = 0; i < len; i++) {
                // publicのみキャッシュ.
                Field f = fs[i];
                if (Modifier.isPublic(f.getModifiers())) {
                    f.setAccessible(true);
                    FastFieldChildSun n = new FastFieldChildSun();
                    n.staticFlag = Modifier.isStatic(f.getModifiers());
                    // オーバーライド定義を取得.
                    Field mf = f.getClass().getSuperclass()
                            .getDeclaredField(OVERRIDE_FIELD);
                    mf.setAccessible(true);
                    Boolean ov = (Boolean) mf.get(f);
                    mf = null;
                    // フィールドアクセサを取得.
                    Method am = f.getClass().getDeclaredMethod(
                            ACQUIRE_FIELD_ACCESSOR, boolean.class);
                    am.setAccessible(true);
                    sun.reflect.FieldAccessor ff = (sun.reflect.FieldAccessor) am
                            .invoke(f, ov);
                    n.field = ff;
                    map.put(f.getName(), n);
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
     * 指定名のフィールド情報を取得.
     * 
     * @param staticFlag
     *            [true]の場合、staticアクセス用として取得します.
     * @param name
     *            対象のフィールド名を設定します.
     * @return FastFieldChild 対象のフィールド格納情報が返されます.
     */
    public FastFieldChild get(boolean staticFlag, String name) {
        FastFieldChildSun n = map.get(name);
        if (n != null && n.staticFlag == staticFlag) {
            return n;
        }
        return null;
    }
}
