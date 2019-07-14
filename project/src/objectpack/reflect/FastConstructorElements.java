package objectpack.reflect;

import java.lang.reflect.InvocationTargetException;

/**
 * コンストラクタ要素群定義.
 *
 * @version 2013/12/07
 * @author masahito suzuki
 *
 */
@SuppressWarnings("rawtypes")
abstract class FastConstructorElements {

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
    public abstract Object newInstance(ClassLoader cl, Class[] types,
            Object[] args) throws ClassNotFoundException,
            InstantiationException, InvocationTargetException,
            IllegalAccessException;
}
