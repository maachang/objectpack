package objectpack.reflect;

import java.lang.reflect.InvocationTargetException;

/**
 * メソッド要素群定義.
 *
 * @version 2013/12/07
 * @author masahito suzuki
 *
 */
@SuppressWarnings("rawtypes")
abstract class FastMethodElements {
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
    public abstract boolean invoke(Object[] result, Object target, String name,
            ClassLoader cl, Class[] types, Object[] args)
            throws ClassNotFoundException, IllegalAccessException,
            InvocationTargetException;
}
