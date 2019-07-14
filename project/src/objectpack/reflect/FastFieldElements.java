package objectpack.reflect;

/**
 * フィールド要素群定義.
 *
 * @version 2013/12/07
 * @author masahito suzuki
 *
 */
abstract class FastFieldElements {
    /**
     * 指定名のフィールド情報を取得.
     * 
     * @param staticFlag
     *            [true]の場合、staticアクセス用として取得します.
     * @param name
     *            対象のフィールド名を設定します.
     * @return FastFieldChildNormal 対象のフィールド格納情報が返されます.
     */
    public abstract FastFieldChild get(boolean staticFlag, String name);
}
