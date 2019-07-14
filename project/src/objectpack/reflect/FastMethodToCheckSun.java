package objectpack.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * メソッドSUNパッケージ利用可能チェック.
 *
 * @version 2013/12/07
 * @author masahito suzuki
 *
 */
abstract class FastMethodToCheckSun {
    /**
     * チェック用処理.
     * 
     * @return boolean [true]の場合は、利用可能.
     */
    public static final boolean isSun() {
        boolean ret = true;
        try {
            Class<?> c = Class.forName("java.lang.Object");
            Method method = c.getMethod("getClass");
            method.setAccessible(true);
            Method am = method.getClass().getDeclaredMethod(
                    FastMethodElementsSun.ACQUIRE_METHOD_ACCESSOR);
            am.setAccessible(true);
            am.invoke(method);
            am = null;
            Field ma = method.getClass().getDeclaredField(
                    FastMethodElementsSun.METHOD_ACCESSOR);
            ma.setAccessible(true);
            ma.get(method);
        } catch (Throwable t) {
            ret = false;
        }
        return ret;
    }
}
