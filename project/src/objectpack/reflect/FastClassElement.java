package objectpack.reflect;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;

/**
 * クラス要素.
 *
 * @version 2013/12/07
 * @author masahito suzuki
 *
 */
@SuppressWarnings("rawtypes")
class FastClassElement {

    /** クラス名. **/
    private String name = null;

    /** クラスオブジェクト **/
    private Class clazz = null;

    /** スーパークラス名. **/
    private String superClazz = null;

    /** 継承インターフェイス群 **/
    private String[] interfaze = null;

    /** クラス内コンストラクタ. **/
    private FastConstructorElements constructors = null;

    /** クラス内フィールド群. **/
    private FastFieldElements fileds = null;

    /** クラス内メソッド群 **/
    private FastMethodElements methods = null;

    /** 最終アクセス時間. **/
    private final AtomicLong lastAccessTime = new AtomicLong(
            System.currentTimeMillis());

    /**
     * Method-Sunパッケージ利用.
     */
    protected static final boolean _IS_SUN_PACKAGE;
    static {
        _IS_SUN_PACKAGE = FastMethodToCheckSun.isSun();
        // _IS_SUN_PACKAGE = false ;
    }

    /**
     * コンストラクタ.
     */
    protected FastClassElement() {
    }

    /**
     * コンストラクタ.
     * 
     * @param clazz
     *            対象のクラスオブジェクトを設定します.
     * @exception ClassNotFoundException
     *                ロードクラス例外.
     */
    protected FastClassElement(Class clazz) throws ClassNotFoundException {
        try {
            this.name = clazz.getName();
            this.clazz = clazz;
            this.superClazz = getSuperClazz(clazz);
            this.interfaze = getInterface(clazz);
            if (_IS_SUN_PACKAGE) {
                this.constructors = new FastConstructorElementsSun(clazz);
                this.fileds = new FastFieldElementsSun(clazz);
                this.methods = new FastMethodElementsSun(clazz);
            } else {
                this.constructors = new FastConstructorElementsNormal(clazz);
                this.fileds = new FastFieldElementsNormal(clazz);
                this.methods = new FastMethodElementsNormal(clazz);
            }
        } catch (Exception e) {
            if (clazz == null) {
                throw new ClassNotFoundException("クラスの読み込みに失敗しました:null", e);
            } else {
                throw new ClassNotFoundException("クラスの読み込みに失敗しました:"
                        + clazz.getName(), e);
            }
        }
    }

    /**
     * コンストラクタ.
     * 
     * @param name
     *            対象のクラス名を設定します.
     * @param cl
     *            対象のクラスローダーを設定します.
     * @exception Exception
     *                例外.
     */
    protected FastClassElement(String name, ClassLoader cl)
            throws NoSuchMethodException, InvocationTargetException,
            ClassNotFoundException, NoSuchFieldException,
            IllegalAccessException {
        Class clazz;
        if (cl == null) {
            clazz = Class.forName(name);
        } else {
            clazz = cl.loadClass(name);
        }
        this.name = name;
        this.clazz = clazz;
        this.superClazz = getSuperClazz(clazz);
        this.interfaze = getInterface(clazz);
        if (_IS_SUN_PACKAGE) {
            this.constructors = new FastConstructorElementsSun(clazz);
            this.fileds = new FastFieldElementsSun(clazz);
            this.methods = new FastMethodElementsSun(clazz);
        } else {
            this.constructors = new FastConstructorElementsNormal(clazz);
            this.fileds = new FastFieldElementsNormal(clazz);
            this.methods = new FastMethodElementsNormal(clazz);
        }
    }

    /**
     * デストラクタ.
     */
    protected void finalize() throws Exception {
        this.name = null;
        this.clazz = null;
        this.superClazz = null;
        this.interfaze = null;
        this.constructors = null;
        this.fileds = null;
        this.methods = null;
    }

    /**
     * クラス名を取得.
     * 
     * @return String クラス名が返されます.
     */
    public String getName() {
        return name;
    }

    /**
     * クラスオブジェクトを取得.
     * 
     * @return Class クラスオブジェクトが返されます.
     */
    public Class getClassObject() {
        return clazz;
    }

    /**
     * スーパークラス名を取得.
     * 
     * @return String スーパークラス名が返されます.
     */
    public String getSuperClassName() {
        return superClazz;
    }

    /**
     * インターフェイス名群を取得.
     * 
     * @return String[] インターフェイス名群が返されます.
     */
    public String[] getInterfaseNames() {
        return interfaze;
    }

    /**
     * 指定クラスフィールド名に対するフィールド要素を取得.
     * 
     * @param staticFlag
     *            [true]の場合、staticアクセス用として取得します.
     * @param name
     *            指定クラスフィールド名を設定します.
     * @return FastFieldChild フィールド要素が返されます.
     */
    public FastFieldChild getField(boolean staticFlag, String name) {
        return fileds.get(staticFlag, name);
    }

    /**
     * 対象のコンストラクタを取得します.
     * 
     * @param types
     *            対象のパラメータタイプを設定します.
     * @param args
     *            引数パラメータ型群を設定します.
     * @param cl
     *            対象のクラスローダを設定します.
     * @return Constructor 対象のコンストラクタが返されます.
     * @exception ClassNotFoundException
     *                クラスが存在しない場合の例外.
     * @exception InstantiationException
     *                基本となるコンストラクタを宣言するクラスが abstract クラスを表す場合.
     * @exception InvocationTargetException
     *                基本となるコンストラクタが例外をスローする場合.
     * @exception IllegalAccessException
     *                この Constructor
     *                オブジェクトが言語アクセス制御を実施し、基本となるコンストラクタにアクセスできない場合.
     */
    public Object newInstance(Class[] types, Object[] args, ClassLoader cl)
            throws ClassNotFoundException, InstantiationException,
            InvocationTargetException, IllegalAccessException {
        return constructors.newInstance(cl, types, args);
    }

    /**
     * 指定クラスメソッド名に対するメソッド要素を取得.
     * 
     * @param result
     *            実行結果のオブジェクトを受け取るオブジェクト配列を設定します.
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
     *                基本となるメソッドが例外をスローする場合.
     * @exception IllegalAccessException
     *                この Constructor
     *                オブジェクトが言語アクセス制御を実施し、基本となるコンストラクタにアクセスできない場合.
     */
    public boolean invokeMethod(Object[] result, Object target, String name,
            ClassLoader cl, Class[] types, Object[] args)
            throws ClassNotFoundException, IllegalAccessException,
            InvocationTargetException {
        return methods.invoke(result, target, name, cl, types, args);
    }

    /**
     * 最終更新日を更新.
     */
    public void update() {
        long t = System.currentTimeMillis();
        AtomicLong ato = lastAccessTime;
        while (!ato.compareAndSet(ato.get(), t)) {
        }
    }

    /**
     * 最終更新日を取得.
     * 
     * @return long 最終更新日が返されます.
     */
    public long getLastAccessTime() {
        return lastAccessTime.get();
    }

    /** superClassを取得. **/
    private static final String getSuperClazz(Class clazz) {
        Class c = clazz.getSuperclass();
        if (c == null) {
            return null;
        }
        return c.getName();
    }

    /** interface群を取得. **/
    private static final String[] getInterface(Class clazz) {
        Class[] cz = clazz.getInterfaces();
        if (cz == null) {
            return null;
        }
        int len = cz.length;
        if (len > 0) {
            String[] ret = new String[len];
            for (int i = 0; i < len; i++) {
                ret[i] = cz[i].getName();
            }
            Arrays.sort(ret);
            return ret;
        }
        return null;
    }

}
