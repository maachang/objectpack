package objectpack.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * フィールド要素群.
 * <p>
 * リフレクションMethod利用版.
 * </p>
 *
 * @version 2013/12/07
 * @author masahito suzuki
 *
 */
@SuppressWarnings("rawtypes")
class FastFieldElementsNormal extends FastFieldElements {
    class FastFieldChildNormal extends FastFieldChild {
        protected Field field;
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
    private final Map<String, FastFieldChildNormal> map = new HashMap<String, FastFieldChildNormal>(
            FastReflect.INIT_MAP_LENGTH);

    /**
     * コンストラクタ.
     */
    protected FastFieldElementsNormal() {
    }

    /**
     * コンストラクタ.
     * 
     * @param clazz
     *            対象のクラスオブジェクトを設定します.
     * @exception Exception
     *                例外.
     */
    public FastFieldElementsNormal(Class clazz) {
        Field[] fs = clazz.getDeclaredFields();
        if (fs != null) {
            int len = fs.length;
            for (int i = 0; i < len; i++) {
                // publicのみキャッシュ.
                Field f = fs[i];
                if (Modifier.isPublic(f.getModifiers())) {
                    f.setAccessible(true);
                    FastFieldChildNormal n = new FastFieldChildNormal();
                    n.field = f;
                    n.staticFlag = Modifier.isStatic(f.getModifiers());
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
        FastFieldChildNormal n = map.get(name);
        if (n != null && n.staticFlag == staticFlag) {
            return n;
        }
        return null;
    }
}
