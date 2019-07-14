package objectpack.reflect;

/**
 * フィールド子要素.
 *
 * @version 2013/12/07
 * @author masahito suzuki
 *
 */
public abstract class FastFieldChild {

    /**
     * フィールドセット.
     * 
     * @param target
     *            対象のターゲットオブジェクトを設定します.
     * @param value
     *            対象の要素を設定します.
     */
    public abstract void set(Object target, Object value)
            throws IllegalAccessException;

    /**
     * フィールド取得.
     * 
     * @param target
     *            対象のターゲットオブジェクトを設定します.
     * @return Object 対象の要素が返されます.
     */
    public abstract Object get(Object target) throws IllegalAccessException;

}
