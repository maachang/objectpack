package objectpack.reflect;

import java.lang.reflect.InvocationTargetException;

/**
 * 高速リフレクション.
 *
 * @version 2013/12/07
 * @author masahito suzuki
 *
 */
@SuppressWarnings("rawtypes")
public final class FastReflect {
    private FastReflect() {
    }

    private static final String OBJECT_NAME = Object.class.getName();

    protected static final int INIT_MAP_LENGTH = 128;

    /**
     * SUNパッケージ利用可能かチェック.
     * 
     * @return boolean [true]の場合は利用可能.
     */
    public static final boolean isSunPackage() {
        return FastClassElement._IS_SUN_PACKAGE;
    }

    /**
     * キャッシュクリア.
     */
    public static final void clearCacheAll() {
        FastReflectClass.cacheClass.clear();
    }

    /**
     * 指定クラスキャッシュクリア.
     * 
     * @param name
     *            対象のクラス名を設定します.
     */
    public static final void clearCache(String name) {
        FastReflectClass.cacheClass.remove(name);
    }

    /**
     * クラス情報を取得.
     * 
     * @param name
     *            対象のクラス名を設定します.
     * @return Class 対象のクラス情報が返されます.
     * @exception ClassNotFoundException
     *                クラスが存在しない場合.
     */
    public static final Class getClass(String name)
            throws ClassNotFoundException {
        return getClass(null, name);
    }

    /**
     * クラス情報を取得.
     * 
     * @param loader
     *            対象のクラスローダーを設定します.
     * @param name
     *            対象のクラス名を設定します.
     * @return Class 対象のクラス情報が返されます.
     * @exception ClassNotFoundException
     *                クラスが存在しない場合.
     */
    public static final Class getClass(ClassLoader loader, String name)
            throws ClassNotFoundException {
        if (name == null) {
            throw new IllegalArgumentException("クラス名は指定されていません");
        }
        FastClassElement em = FastReflectClass.getClass(loader, name);
        if (em == null) {
            throw new ClassNotFoundException("指定クラス " + name + " は存在しません");
        }
        return em.getClassObject();
    }

    /**
     * コンストラクタ実行.
     * 
     * @param name
     *            対象のクラス名を設定します.
     * @return Object 生成されたオブジェクトが返されます.
     * @exception NoSuchMethodException
     *                一致するメソッドが見つからない場合 .
     * @exception ClassNotFoundException
     *                クラスが存在しない場合.
     * @exception InstantiationException
     *                基本となるコンストラクタを宣言するクラスが abstract クラスを表す場合.
     * @exception InvocationTargetException
     *                基本となるコンストラクタが例外をスローする場合.
     * @exception IllegalAccessException
     *                指定条件に対してアクセスできない場合.
     */
    public static final Object newInstance(String name)
            throws NoSuchMethodException, ClassNotFoundException,
            InstantiationException, InvocationTargetException,
            IllegalAccessException {
        return newInstance(null, name, (Object[]) null);
    }

    /**
     * コンストラクタ実行.
     * 
     * @param loader
     *            対象のクラスローダーを設定します.
     * @param name
     *            対象のクラス名を設定します.
     * @return Object 生成されたオブジェクトが返されます.
     * @exception NoSuchMethodException
     *                一致するメソッドが見つからない場合 .
     * @exception ClassNotFoundException
     *                クラスが存在しない場合.
     * @exception InstantiationException
     *                基本となるコンストラクタを宣言するクラスが abstract クラスを表す場合.
     * @exception InvocationTargetException
     *                基本となるコンストラクタが例外をスローする場合.
     * @exception IllegalAccessException
     *                指定条件に対してアクセスできない場合.
     */
    public static final Object newInstance(ClassLoader loader, String name)
            throws NoSuchMethodException, ClassNotFoundException,
            InstantiationException, InvocationTargetException,
            IllegalAccessException {
        return newInstance(loader, name, (Object[]) null);
    }

    /**
     * コンストラクタ実行.
     * 
     * @param name
     *            対象のクラス名を設定します.
     * @param args
     *            対象のコンストラクタ引数を設定します.
     * @return Object 生成されたオブジェクトが返されます.
     * @exception NoSuchMethodException
     *                一致するメソッドが見つからない場合 .
     * @exception ClassNotFoundException
     *                クラスが存在しない場合.
     * @exception InstantiationException
     *                基本となるコンストラクタを宣言するクラスが abstract クラスを表す場合.
     * @exception InvocationTargetException
     *                基本となるコンストラクタが例外をスローする場合.
     * @exception IllegalAccessException
     *                指定条件に対してアクセスできない場合.
     */
    public static final Object newInstance(String name, Object... args)
            throws NoSuchMethodException, ClassNotFoundException,
            InstantiationException, InvocationTargetException,
            IllegalAccessException {
        return newInstance(null, name, args);
    }

    /**
     * コンストラクタ実行.
     * 
     * @param loader
     *            対象のクラスローダーを設定します.
     * @param name
     *            対象のクラス名を設定します.
     * @param args
     *            対象のコンストラクタ引数を設定します.
     * @return Object 生成されたオブジェクトが返されます.
     * @exception NoSuchMethodException
     *                一致するメソッドが見つからない場合 .
     * @exception ClassNotFoundException
     *                クラスが存在しない場合.
     * @exception InstantiationException
     *                基本となるコンストラクタを宣言するクラスが abstract クラスを表す場合.
     * @exception InvocationTargetException
     *                基本となるコンストラクタが例外をスローする場合.
     * @exception IllegalAccessException
     *                指定条件に対してアクセスできない場合.
     */
    public static final Object newInstance(ClassLoader loader, String name,
            Object... args) throws NoSuchMethodException,
            ClassNotFoundException, InstantiationException,
            InvocationTargetException, IllegalAccessException {
        /*
         * 修正:#16006(内部クラスのインスタンス生成でObject型が返る) 2009/04/13 masahito suzuki.
         */
        FastClassElement em = FastReflectClass.getClass(loader, name);
        if (em == null) {
            throw new ClassNotFoundException("指定クラス " + name + " は存在しません");
        }
        if (name == OBJECT_NAME) {
            return new Object();
        }
        Object ret;
        Class[] types = FastReflectUtil.getParamsType(args);
        String superName;
        while (true) {
            if ((ret = em.newInstance(types, args, loader)) != null) {
                return ret;
            }
            superName = em.getSuperClassName();
            em = null;
            if (superName != OBJECT_NAME && superName != null) {
                em = FastReflectClass.getClass(loader, superName);
            }
            if (em == null) {
                throw new NoSuchMethodException("指定クラス " + name
                        + " に対して、対象引数のコンストラクタは存在しません");
            }
        }
    }

    /**
     * コンストラクタ実行.
     * 
     * @param loader
     *            対象のクラスローダーを設定します.
     * @param name
     *            対象のクラス名を設定します.
     * @param args
     *            対象のコンストラクタ引数を設定します.
     * @param types
     *            対象のコンストラクタ引数タイプを設定します.
     * @return Object 生成されたオブジェクトが返されます.
     * @exception NoSuchMethodException
     *                一致するメソッドが見つからない場合 .
     * @exception ClassNotFoundException
     *                クラスが存在しない場合.
     * @exception InstantiationException
     *                基本となるコンストラクタを宣言するクラスが abstract クラスを表す場合.
     * @exception InvocationTargetException
     *                基本となるコンストラクタが例外をスローする場合.
     * @exception IllegalAccessException
     *                指定条件に対してアクセスできない場合.
     */
    public static final Object newInstanceTo(ClassLoader loader, String name,
            Object[] args, Class[] types) throws NoSuchMethodException,
            ClassNotFoundException, InstantiationException,
            InvocationTargetException, IllegalAccessException {
        /*
         * 修正:#16006(内部クラスのインスタンス生成でObject型が返る) 2009/04/13 masahito suzuki.
         */
        FastClassElement em = FastReflectClass.getClass(loader, name);
        if (em == null) {
            throw new ClassNotFoundException("指定クラス " + name + " は存在しません");
        }
        if (name == OBJECT_NAME) {
            return new Object();
        }
        Object ret;
        String superName;
        if (args != null && args.length > 0 && types == null) {
            types = FastReflectUtil.getParamsType(args);
        } else if (types != null) {
            types = FastReflectUtil.getParamsTypeByClass(types);
        }
        while (true) {
            if ((ret = em.newInstance(types, args, loader)) != null) {
                return ret;
            }
            superName = em.getSuperClassName();
            em = null;
            if (superName != OBJECT_NAME && superName != null) {
                em = FastReflectClass.getClass(loader, superName);
            }
            if (em == null) {
                throw new NoSuchMethodException("指定クラス " + name
                        + " に対して、対象引数のコンストラクタは存在しません");
            }
        }
    }

    /**
     * フィールド設定.
     * 
     * @param target
     *            設定対象のオブジェクトを設定します.<BR>
     *            [null]は設定できません.
     * @param name
     *            対象のフィールド名を設定します.
     * @param value
     *            対象のパラメータ要素を設定します.
     * @exception NoSuchFieldException
     *                一致するフィールドが見つからない場合.
     * @exception ClassNotFoundException
     *                クラスが存在しない場合.
     * @exception IllegalAccessException
     *                指定条件に対してアクセスできない場合.
     */
    public static final void setField(Object target, String name, Object value)
            throws NoSuchFieldException, ClassNotFoundException,
            IllegalAccessException {
        if (target == null) {
            throw new IllegalAccessException("targetにNULLが設定されています");
        }
        setField(null, target.getClass(), target, name, value);
    }

    /**
     * フィールド設定.
     * 
     * @param loader
     *            対象のクラスローダーを設定します.
     * @param target
     *            設定対象のオブジェクトを設定します.<BR>
     *            [null]は設定できません.
     * @param name
     *            対象のフィールド名を設定します.
     * @param value
     *            対象のパラメータ要素を設定します.
     * @exception NoSuchFieldException
     *                一致するフィールドが見つからない場合.
     * @exception ClassNotFoundException
     *                クラスが存在しない場合.
     * @exception IllegalAccessException
     *                指定条件に対してアクセスできない場合.
     */
    public static final void setField(ClassLoader loader, Object target,
            String name, Object value) throws NoSuchFieldException,
            ClassNotFoundException, IllegalAccessException {
        if (target == null) {
            throw new IllegalAccessException("targetにNULLが設定されています");
        }
        setField(loader, target.getClass(), target, name, value);
    }

    /**
     * フィールド設定.
     * 
     * @param clazzName
     *            対象のクラス名を設定します.
     * @param target
     *            設定対象のオブジェクトを設定します.<BR>
     *            [null]の場合、staticアクセスで処理します.
     * @param name
     *            対象のフィールド名を設定します.
     * @param value
     *            対象のパラメータ要素を設定します.
     * @exception NoSuchFieldException
     *                一致するフィールドが見つからない場合.
     * @exception ClassNotFoundException
     *                クラスが存在しない場合.
     * @exception IllegalAccessException
     *                指定条件に対してアクセスできない場合.
     */
    public static final void setField(String clazzName, Object target,
            String name, Object value) throws NoSuchFieldException,
            ClassNotFoundException, IllegalAccessException {
        setField(null, clazzName, target, name, value);
    }

    /**
     * フィールド設定.
     * 
     * @param loader
     *            対象のクラスローダーを設定します.
     * @param clazzName
     *            対象のクラス名を設定します.
     * @param target
     *            設定対象のオブジェクトを設定します.<BR>
     *            [null]の場合、staticアクセスで処理します.
     * @param name
     *            対象のフィールド名を設定します.
     * @param value
     *            対象のパラメータ要素を設定します.
     * @exception NoSuchFieldException
     *                一致するフィールドが見つからない場合.
     * @exception ClassNotFoundException
     *                クラスが存在しない場合.
     * @exception IllegalAccessException
     *                指定条件に対してアクセスできない場合.
     */
    public static final void setField(ClassLoader loader, String clazzName,
            Object target, String name, Object value)
            throws NoSuchFieldException, ClassNotFoundException,
            IllegalAccessException {
        FastFieldChild f = getFiledChild(loader, clazzName, target, name);
        if (f == null) {
            throw new NoSuchFieldException("指定フィールド " + name + " はクラス "
                    + clazzName + " に存在しません");
        }
        f.set(target, value);
    }

    /**
     * フィールド設定.
     * 
     * @param clazz
     *            対象のクラスを設定します.
     * @param target
     *            設定対象のオブジェクトを設定します.<BR>
     *            [null]の場合、staticアクセスで処理します.
     * @param name
     *            対象のフィールド名を設定します.
     * @param value
     *            対象のパラメータ要素を設定します.
     * @exception NoSuchFieldException
     *                一致するフィールドが見つからない場合.
     * @exception ClassNotFoundException
     *                クラスが存在しない場合.
     * @exception IllegalAccessException
     *                指定条件に対してアクセスできない場合.
     */
    public static final void setField(Class clazz, Object target, String name,
            Object value) throws NoSuchFieldException, ClassNotFoundException,
            IllegalAccessException {
        setField(null, clazz, target, name, value);
    }

    /**
     * フィールド設定.
     * 
     * @param loader
     *            対象のクラスローダーを設定します.
     * @param clazz
     *            対象のクラスを設定します.
     * @param target
     *            設定対象のオブジェクトを設定します.<BR>
     *            [null]の場合、staticアクセスで処理します.
     * @param name
     *            対象のフィールド名を設定します.
     * @param value
     *            対象のパラメータ要素を設定します.
     * @exception NoSuchFieldException
     *                一致するフィールドが見つからない場合.
     * @exception ClassNotFoundException
     *                クラスが存在しない場合.
     * @exception IllegalAccessException
     *                指定条件に対してアクセスできない場合.
     */
    public static final void setField(ClassLoader loader, Class clazz,
            Object target, String name, Object value)
            throws NoSuchFieldException, ClassNotFoundException,
            IllegalAccessException {
        setField(loader, clazz.getName(), target, name, value);
    }

    /**
     * フィールド取得.
     * 
     * @param target
     *            設定対象のオブジェクトを設定します.<BR>
     *            [null]は設定できません.
     * @param name
     *            対象のフィールド名を設定します.
     * @return Object フィールドオブジェクト内容が返されます.
     * @exception NoSuchFieldException
     *                一致するフィールドが見つからない場合.
     * @exception ClassNotFoundException
     *                クラスが存在しない場合.
     * @exception IllegalAccessException
     *                指定条件に対してアクセスできない場合.
     */
    public static final Object getField(Object target, String name)
            throws NoSuchFieldException, ClassNotFoundException,
            IllegalAccessException {
        if (target == null) {
            throw new IllegalAccessException("targetにNULLが設定されています");
        }
        return getField(null, target.getClass(), target, name);
    }

    /**
     * フィールド取得.
     * 
     * @param loader
     *            対象のクラスローダーを設定します.
     * @param target
     *            設定対象のオブジェクトを設定します.<BR>
     *            [null]は設定できません.
     * @param name
     *            対象のフィールド名を設定します.
     * @return Object フィールドオブジェクト内容が返されます.
     * @exception NoSuchFieldException
     *                一致するフィールドが見つからない場合.
     * @exception ClassNotFoundException
     *                クラスが存在しない場合.
     * @exception IllegalAccessException
     *                指定条件に対してアクセスできない場合.
     */
    public static final Object getField(ClassLoader loader, Object target,
            String name) throws NoSuchFieldException, ClassNotFoundException,
            IllegalAccessException {
        if (target == null) {
            throw new IllegalAccessException("targetにNULLが設定されています");
        }
        return getField(loader, target.getClass(), target, name);
    }

    /**
     * フィールド取得.
     * 
     * @param clazzName
     *            対象のクラス名を設定します.
     * @param target
     *            設定対象のオブジェクトを設定します.<BR>
     *            [null]の場合、staticアクセスで処理します.
     * @param name
     *            対象のフィールド名を設定します.
     * @return Object フィールドオブジェクト内容が返されます.
     * @exception NoSuchFieldException
     *                一致するフィールドが見つからない場合.
     * @exception ClassNotFoundException
     *                クラスが存在しない場合.
     * @exception IllegalAccessException
     *                指定条件に対してアクセスできない場合.
     */
    public static final Object getField(String clazzName, Object target,
            String name) throws NoSuchFieldException, ClassNotFoundException,
            IllegalAccessException {
        return getField(null, clazzName, target, name);
    }

    /**
     * フィールド取得.
     * 
     * @param loader
     *            対象のクラスローダーを設定します.
     * @param clazzName
     *            対象のクラス名を設定します.
     * @param target
     *            設定対象のオブジェクトを設定します.<BR>
     *            [null]の場合、staticアクセスで処理します.
     * @param name
     *            対象のフィールド名を設定します.
     * @return Object フィールドオブジェクト内容が返されます.
     * @exception NoSuchFieldException
     *                一致するフィールドが見つからない場合.
     * @exception ClassNotFoundException
     *                クラスが存在しない場合.
     * @exception IllegalAccessException
     *                指定条件に対してアクセスできない場合.
     */
    public static final Object getField(ClassLoader loader, String clazzName,
            Object target, String name) throws NoSuchFieldException,
            ClassNotFoundException, IllegalAccessException {
        FastFieldChild f = getFiledChild(loader, clazzName, target, name);
        if (f == null) {
            throw new NoSuchFieldException("指定フィールド " + name + " はクラス "
                    + clazzName + " に存在しません");
        }
        return f.get(target);
    }

    /**
     * フィールド取得.
     * 
     * @param clazz
     *            対象のクラス名を設定します.
     * @param target
     *            設定対象のオブジェクトを設定します.<BR>
     *            [null]の場合、staticアクセスで処理します.
     * @param name
     *            対象のフィールド名を設定します.
     * @return Object フィールドオブジェクト内容が返されます.
     * @exception NoSuchFieldException
     *                一致するフィールドが見つからない場合.
     * @exception ClassNotFoundException
     *                クラスが存在しない場合.
     * @exception IllegalAccessException
     *                指定条件に対してアクセスできない場合.
     */
    public static final Object getField(Class clazz, Object target, String name)
            throws NoSuchFieldException, ClassNotFoundException,
            IllegalAccessException {
        return getField(null, clazz, target, name);
    }

    /**
     * フィールド取得.
     * 
     * @param loader
     *            対象のクラスローダーを設定します.
     * @param clazz
     *            対象のクラス名を設定します.
     * @param target
     *            設定対象のオブジェクトを設定します.<BR>
     *            [null]の場合、staticアクセスで処理します.
     * @param name
     *            対象のフィールド名を設定します.
     * @return Object フィールドオブジェクト内容が返されます.
     * @exception NoSuchFieldException
     *                一致するフィールドが見つからない場合.
     * @exception ClassNotFoundException
     *                クラスが存在しない場合.
     * @exception IllegalAccessException
     *                指定条件に対してアクセスできない場合.
     */
    public static final Object getField(ClassLoader loader, Class clazz,
            Object target, String name) throws NoSuchFieldException,
            ClassNotFoundException, IllegalAccessException {
        return getField(loader, clazz.getName(), target, name);
    }

    /**
     * フィールド要素を取得.
     * 
     * @param target
     *            設定対象のオブジェクトを設定します.<BR>
     *            [null]は設定できません.
     * @param name
     *            対象のフィールド名を設定します.
     * @return FastFieldChild フィールド要素が返却されます.
     * @exception NoSuchFieldException
     *                一致するフィールドが見つからない場合.
     * @exception ClassNotFoundException
     *                クラスが存在しない場合.
     * @exception IllegalAccessException
     *                指定条件に対してアクセスできない場合.
     */
    public static final FastFieldChild getFiledChild(Object target, String name)
            throws NoSuchFieldException, ClassNotFoundException,
            IllegalAccessException {
        if (target == null) {
            throw new IllegalAccessException("targetにNULLが設定されています");
        }
        return getFiledChild(null, target.getClass().getName(), target, name);
    }

    /**
     * フィールド要素を取得.
     * 
     * @param loader
     *            対象のクラスローダーを設定します.
     * @param target
     *            設定対象のオブジェクトを設定します.<BR>
     *            [null]は設定できません.
     * @param name
     *            対象のフィールド名を設定します.
     * @return FastFieldChild フィールド要素が返却されます.
     * @exception NoSuchFieldException
     *                一致するフィールドが見つからない場合.
     * @exception ClassNotFoundException
     *                クラスが存在しない場合.
     * @exception IllegalAccessException
     *                指定条件に対してアクセスできない場合.
     */
    public static final FastFieldChild getFiledChild(ClassLoader loader,
            Object target, String name) throws NoSuchFieldException,
            ClassNotFoundException, IllegalAccessException {
        if (target == null) {
            throw new IllegalAccessException("targetにNULLが設定されています");
        }
        return getFiledChild(loader, target.getClass().getName(), target, name);
    }

    /**
     * フィールド要素を取得.
     * 
     * @param clazzName
     *            対象のクラス名を設定します.
     * @param target
     *            設定対象のオブジェクトを設定します.<BR>
     *            [null]の場合、staticアクセスで処理します.
     * @param name
     *            対象のフィールド名を設定します.
     * @return FastFieldChild フィールド要素が返却されます.
     * @exception NoSuchFieldException
     *                一致するフィールドが見つからない場合.
     * @exception ClassNotFoundException
     *                クラスが存在しない場合.
     * @exception IllegalAccessException
     *                指定条件に対してアクセスできない場合.
     */
    public static final FastFieldChild getFiledChild(String clazzName,
            Object target, String name) throws NoSuchFieldException,
            ClassNotFoundException, IllegalAccessException {
        return getFiledChild(null, clazzName, target, name);
    }

    /**
     * フィールド要素を取得.
     * 
     * @param clazz
     *            対象のクラス名を設定します.
     * @param target
     *            設定対象のオブジェクトを設定します.<BR>
     *            [null]の場合、staticアクセスで処理します.
     * @param name
     *            対象のフィールド名を設定します.
     * @return FastFieldChild フィールド要素が返却されます.
     * @exception NoSuchFieldException
     *                一致するフィールドが見つからない場合.
     * @exception ClassNotFoundException
     *                クラスが存在しない場合.
     * @exception IllegalAccessException
     *                指定条件に対してアクセスできない場合.
     */
    public static final FastFieldChild getFiledChild(Class clazz,
            Object target, String name) throws NoSuchFieldException,
            ClassNotFoundException, IllegalAccessException {
        return getFiledChild(null, clazz.getName(), target, name);
    }

    /**
     * フィールド要素を取得.
     * 
     * @param loader
     *            対象のクラスローダーを設定します.
     * @param clazz
     *            対象のクラス名を設定します.
     * @param target
     *            設定対象のオブジェクトを設定します.<BR>
     *            [null]の場合、staticアクセスで処理します.
     * @param name
     *            対象のフィールド名を設定します.
     * @return FastFieldChild フィールド要素が返却されます.
     * @exception NoSuchFieldException
     *                一致するフィールドが見つからない場合.
     * @exception ClassNotFoundException
     *                クラスが存在しない場合.
     * @exception IllegalAccessException
     *                指定条件に対してアクセスできない場合.
     */
    public static final FastFieldChild getFiledChild(ClassLoader loader,
            Class clazz, Object target, String name)
            throws NoSuchFieldException, ClassNotFoundException,
            IllegalAccessException {
        return getFiledChild(loader, clazz.getName(), target, name);
    }

    /**
     * フィールド要素を取得.
     * 
     * @param loader
     *            対象のクラスローダーを設定します.
     * @param clazzName
     *            対象のクラス名を設定します.
     * @param target
     *            設定対象のオブジェクトを設定します.<BR>
     *            [null]の場合、staticアクセスで処理します.
     * @param name
     *            対象のフィールド名を設定します.
     * @return FastFieldChild フィールド要素が返却されます.
     * @exception NoSuchFieldException
     *                一致するフィールドが見つからない場合.
     * @exception ClassNotFoundException
     *                クラスが存在しない場合.
     * @exception IllegalAccessException
     *                指定条件に対してアクセスできない場合.
     */
    public static final FastFieldChild getFiledChild(ClassLoader loader,
            String clazzName, Object target, String name)
            throws NoSuchFieldException, ClassNotFoundException,
            IllegalAccessException {
        FastClassElement em = FastReflectClass.getClass(loader, clazzName);
        if (em == null) {
            throw new ClassNotFoundException("指定クラス " + clazzName + " は存在しません");
        }
        String superName;
        FastFieldChild f;
        final boolean staticFlag = (target == null);
        while (true) {
            if ((f = em.getField(staticFlag, name)) == null) {
                superName = em.getSuperClassName();
                em = null;
                if (superName != null) {
                    em = FastReflectClass.getClass(loader, superName);
                }
                if (em == null) {
                    return null;
                }
            } else {
                return f;
            }
        }
    }

    /**
     * メソッド呼び出し.
     * 
     * @param target
     *            設定対象のオブジェクトを設定します.<BR>
     *            [null]は設定できません.
     * @param name
     *            対象のメソッド名を設定します.
     * @return Object 戻り値が返されます.
     * @exception NoSuchMethodException
     *                一致するメソッドが見つからない場合.
     * @exception ClassNotFoundException
     *                クラスが存在しない場合.
     * @exception IllegalAccessException
     *                指定条件に対してアクセスできない場合.
     * @exception InvocationTargetException
     *                基本となるメソッドが例外をスローする場合.
     */
    public static final Object invoke(Object target, String name)
            throws NoSuchMethodException, ClassNotFoundException,
            IllegalAccessException, InvocationTargetException {
        if (target == null) {
            throw new IllegalAccessException("targetにNULLが設定されています");
        }
        return invoke(null, target.getClass(), target, name, (Object[]) null);
    }

    /**
     * メソッド呼び出し.
     * 
     * @param loader
     *            対象のクラスローダーを設定します.
     * @param target
     *            設定対象のオブジェクトを設定します.<BR>
     *            [null]は設定できません.
     * @param name
     *            対象のメソッド名を設定します.
     * @return Object 戻り値が返されます.
     * @exception NoSuchMethodException
     *                一致するメソッドが見つからない場合.
     * @exception ClassNotFoundException
     *                クラスが存在しない場合.
     * @exception IllegalAccessException
     *                指定条件に対してアクセスできない場合.
     * @exception InvocationTargetException
     *                基本となるメソッドが例外をスローする場合.
     */
    public static final Object invoke(ClassLoader loader, Object target,
            String name) throws NoSuchMethodException, ClassNotFoundException,
            IllegalAccessException, InvocationTargetException {
        if (target == null) {
            throw new IllegalAccessException("targetにNULLが設定されています");
        }
        return invoke(loader, target.getClass(), target, name, (Object[]) null);
    }

    /**
     * メソッド呼び出し.
     * 
     * @param target
     *            設定対象のオブジェクトを設定します.<BR>
     *            [null]は設定できません.
     * @param name
     *            対象のメソッド名を設定します.
     * @param args
     *            対象のメソッドパラメータを設定します.
     * @return Object 戻り値が返されます.
     * @exception NoSuchMethodException
     *                一致するメソッドが見つからない場合.
     * @exception ClassNotFoundException
     *                クラスが存在しない場合.
     * @exception IllegalAccessException
     *                指定条件に対してアクセスできない場合.
     * @exception InvocationTargetException
     *                基本となるメソッドが例外をスローする場合.
     */
    public static final Object invoke(Object target, String name,
            Object... args) throws NoSuchMethodException,
            ClassNotFoundException, IllegalAccessException,
            InvocationTargetException {
        if (target == null) {
            throw new IllegalAccessException("targetにNULLが設定されています");
        }
        return invoke(null, target.getClass(), target, name, args);
    }

    /**
     * メソッド呼び出し.
     * 
     * @param loader
     *            対象のクラスローダーを設定します.
     * @param target
     *            設定対象のオブジェクトを設定します.<BR>
     *            [null]は設定できません.
     * @param name
     *            対象のメソッド名を設定します.
     * @param args
     *            対象のメソッドパラメータを設定します.
     * @return Object 戻り値が返されます.
     * @exception NoSuchMethodException
     *                一致するメソッドが見つからない場合.
     * @exception ClassNotFoundException
     *                クラスが存在しない場合.
     * @exception IllegalAccessException
     *                指定条件に対してアクセスできない場合.
     * @exception InvocationTargetException
     *                基本となるメソッドが例外をスローする場合.
     */
    public static final Object invoke(ClassLoader loader, Object target,
            String name, Object... args) throws NoSuchMethodException,
            ClassNotFoundException, IllegalAccessException,
            InvocationTargetException {
        if (target == null) {
            throw new IllegalAccessException("targetにNULLが設定されています");
        }
        return invoke(loader, target.getClass(), target, name, args);
    }

    /**
     * メソッド呼び出し.
     * 
     * @param loader
     *            対象のクラスローダーを設定します.
     * @param target
     *            設定対象のオブジェクトを設定します.<BR>
     *            [null]は設定できません.
     * @param name
     *            対象のメソッド名を設定します.
     * @param args
     *            対象のメソッドパラメータを設定します.
     * @param types
     *            対象のメソッドパラメータタイプを設定します.
     * @return Object 戻り値が返されます.
     * @exception NoSuchMethodException
     *                一致するメソッドが見つからない場合.
     * @exception ClassNotFoundException
     *                クラスが存在しない場合.
     * @exception IllegalAccessException
     *                指定条件に対してアクセスできない場合.
     * @exception InvocationTargetException
     *                基本となるメソッドが例外をスローする場合.
     */
    public static final Object invokeTo(ClassLoader loader, Object target,
            String name, Object[] args, Class[] types)
            throws NoSuchMethodException, ClassNotFoundException,
            IllegalAccessException, InvocationTargetException {
        if (target == null) {
            throw new IllegalAccessException("targetにNULLが設定されています");
        }
        return invokeTo(loader, target.getClass(), target, name, args, types);
    }

    /**
     * メソッド呼び出し.
     * 
     * @param clazzName
     *            対象のクラス名を設定します.
     * @param target
     *            設定対象のオブジェクトを設定します.<BR>
     *            [null]の場合、staticアクセスで処理します.
     * @param name
     *            対象のメソッド名を設定します.
     * @return Object 戻り値が返されます.
     * @exception NoSuchMethodException
     *                一致するメソッドが見つからない場合.
     * @exception ClassNotFoundException
     *                クラスが存在しない場合.
     * @exception IllegalAccessException
     *                指定条件に対してアクセスできない場合.
     * @exception InvocationTargetException
     *                基本となるメソッドが例外をスローする場合.
     */
    public static final Object invoke(String clazzName, Object target,
            String name) throws NoSuchMethodException, ClassNotFoundException,
            IllegalAccessException, InvocationTargetException {
        return invoke(null, clazzName, target, name, (Object[]) null);
    }

    /**
     * メソッド呼び出し.
     * 
     * @param loader
     *            対象のクラスローダーを設定します.
     * @param clazzName
     *            対象のクラス名を設定します.
     * @param target
     *            設定対象のオブジェクトを設定します.<BR>
     *            [null]の場合、staticアクセスで処理します.
     * @param name
     *            対象のメソッド名を設定します.
     * @return Object 戻り値が返されます.
     * @exception NoSuchMethodException
     *                一致するメソッドが見つからない場合.
     * @exception ClassNotFoundException
     *                クラスが存在しない場合.
     * @exception IllegalAccessException
     *                指定条件に対してアクセスできない場合.
     * @exception InvocationTargetException
     *                基本となるメソッドが例外をスローする場合.
     */
    public static final Object invoke(ClassLoader loader, String clazzName,
            Object target, String name) throws NoSuchMethodException,
            ClassNotFoundException, IllegalAccessException,
            InvocationTargetException {
        return invoke(loader, clazzName, target, name, (Object[]) null);
    }

    /**
     * メソッド呼び出し.
     * 
     * @param clazz
     *            対象のクラスを設定します.
     * @param target
     *            設定対象のオブジェクトを設定します.<BR>
     *            [null]の場合、staticアクセスで処理します.
     * @param name
     *            対象のメソッド名を設定します.
     * @return Object 戻り値が返されます.
     * @exception NoSuchMethodException
     *                一致するメソッドが見つからない場合.
     * @exception ClassNotFoundException
     *                クラスが存在しない場合.
     * @exception IllegalAccessException
     *                指定条件に対してアクセスできない場合.
     * @exception InvocationTargetException
     *                基本となるメソッドが例外をスローする場合.
     */
    public static final Object invoke(Class clazz, Object target, String name)
            throws NoSuchMethodException, ClassNotFoundException,
            IllegalAccessException, InvocationTargetException {
        return invoke(null, clazz, target, name, (Object[]) null);
    }

    /**
     * メソッド呼び出し.
     * 
     * @param loader
     *            対象のクラスローダーを設定します.
     * @param clazz
     *            対象のクラスを設定します.
     * @param target
     *            設定対象のオブジェクトを設定します.<BR>
     *            [null]の場合、staticアクセスで処理します.
     * @param name
     *            対象のメソッド名を設定します.
     * @return Object 戻り値が返されます.
     * @exception NoSuchMethodException
     *                一致するメソッドが見つからない場合.
     * @exception ClassNotFoundException
     *                クラスが存在しない場合.
     * @exception IllegalAccessException
     *                指定条件に対してアクセスできない場合.
     * @exception InvocationTargetException
     *                基本となるメソッドが例外をスローする場合.
     */
    public static final Object invoke(ClassLoader loader, Class clazz,
            Object target, String name) throws NoSuchMethodException,
            ClassNotFoundException, IllegalAccessException,
            InvocationTargetException {
        return invoke(loader, clazz, target, name, (Object[]) null);
    }

    /**
     * メソッド呼び出し.
     * 
     * @param clazzName
     *            対象のクラス名を設定します.
     * @param target
     *            設定対象のオブジェクトを設定します.<BR>
     *            [null]の場合、staticアクセスで処理します.
     * @param name
     *            対象のメソッド名を設定します.
     * @param args
     *            対象のメソッドパラメータを設定します.
     * @return Object 戻り値が返されます.
     * @exception NoSuchMethodException
     *                一致するメソッドが見つからない場合.
     * @exception ClassNotFoundException
     *                クラスが存在しない場合.
     * @exception IllegalAccessException
     *                指定条件に対してアクセスできない場合.
     * @exception InvocationTargetException
     *                基本となるメソッドが例外をスローする場合.
     */
    public static final Object invoke(String clazzName, Object target,
            String name, Object... args) throws NoSuchMethodException,
            ClassNotFoundException, IllegalAccessException,
            InvocationTargetException {
        return invoke(null, clazzName, target, name, args);
    }

    /**
     * メソッド呼び出し.
     * 
     * @param loader
     *            対象のクラスローダーを設定します.
     * @param clazzName
     *            対象のクラス名を設定します.
     * @param target
     *            設定対象のオブジェクトを設定します.<BR>
     *            [null]の場合、staticアクセスで処理します.
     * @param name
     *            対象のメソッド名を設定します.
     * @param args
     *            対象のメソッドパラメータを設定します.
     * @return Object 戻り値が返されます.
     * @exception NoSuchMethodException
     *                一致するメソッドが見つからない場合.
     * @exception ClassNotFoundException
     *                クラスが存在しない場合.
     * @exception IllegalAccessException
     *                指定条件に対してアクセスできない場合.
     * @exception InvocationTargetException
     *                基本となるメソッドが例外をスローする場合.
     */
    public static final Object invoke(ClassLoader loader, String clazzName,
            Object target, String name, Object... args)
            throws NoSuchMethodException, ClassNotFoundException,
            IllegalAccessException, InvocationTargetException {
        FastClassElement em = FastReflectClass.getClass(loader, clazzName);
        if (em == null) {
            throw new ClassNotFoundException("指定クラス " + clazzName + " は存在しません");
        }
        String spclazz;
        Object[] ret = new Object[1];
        Class[] types = FastReflectUtil.getParamsType(args);
        while (true) {
            if (em.invokeMethod(ret, target, name, loader, types, args)) {
                return ret[0];
            }
            spclazz = em.getSuperClassName();
            if (spclazz == null) {
                throw new NoSuchMethodException("指定メソッド " + name + " はクラス "
                        + clazzName + " に存在しません");
            }
            em = FastReflectClass.getClass(loader, spclazz);
        }
    }

    /**
     * メソッド呼び出し.
     * 
     * @param clazz
     *            対象のクラスを設定します.
     * @param target
     *            設定対象のオブジェクトを設定します.<BR>
     *            [null]の場合、staticアクセスで処理します.
     * @param name
     *            対象のメソッド名を設定します.
     * @param args
     *            対象のメソッドパラメータを設定します.
     * @return Object 戻り値が返されます.
     * @exception NoSuchMethodException
     *                一致するメソッドが見つからない場合.
     * @exception ClassNotFoundException
     *                クラスが存在しない場合.
     * @exception IllegalAccessException
     *                指定条件に対してアクセスできない場合.
     * @exception InvocationTargetException
     *                基本となるメソッドが例外をスローする場合.
     */
    public static final Object invoke(Class clazz, Object target, String name,
            Object... args) throws NoSuchMethodException,
            ClassNotFoundException, IllegalAccessException,
            InvocationTargetException {
        return invoke(null, clazz, target, name, args);
    }

    /**
     * メソッド呼び出し.
     * 
     * @param loader
     *            対象のクラスローダーを設定します.
     * @param clazz
     *            対象のクラスを設定します.
     * @param target
     *            設定対象のオブジェクトを設定します.<BR>
     *            [null]の場合、staticアクセスで処理します.
     * @param name
     *            対象のメソッド名を設定します.
     * @param args
     *            対象のメソッドパラメータを設定します.
     * @return Object 戻り値が返されます.
     * @exception NoSuchMethodException
     *                一致するメソッドが見つからない場合.
     * @exception ClassNotFoundException
     *                クラスが存在しない場合.
     * @exception IllegalAccessException
     *                指定条件に対してアクセスできない場合.
     * @exception InvocationTargetException
     *                基本となるメソッドが例外をスローする場合.
     */
    public static final Object invoke(ClassLoader loader, Class clazz,
            Object target, String name, Object... args)
            throws NoSuchMethodException, ClassNotFoundException,
            IllegalAccessException, InvocationTargetException {
        String clazzName = clazz.getName();
        FastClassElement em = FastReflectClass.getClass(loader, clazzName);
        if (em == null) {
            throw new ClassNotFoundException("指定クラス " + clazzName + " は存在しません");
        }
        Object[] ret = new Object[1];
        Class[] types = FastReflectUtil.getParamsType(args);
        String spclazz;
        while (true) {
            if (em.invokeMethod(ret, target, name, loader, types, args)) {
                return ret[0];
            }
            spclazz = em.getSuperClassName();
            if (spclazz == null) {
                throw new NoSuchMethodException("指定メソッド " + name + " はクラス "
                        + clazzName + " に存在しません");
            }
            em = FastReflectClass.getClass(loader, spclazz);
        }
    }

    /**
     * メソッド呼び出し.
     * 
     * @param loader
     *            対象のクラスローダーを設定します.
     * @param clazzName
     *            対象のクラス名を設定します.
     * @param target
     *            設定対象のオブジェクトを設定します.<BR>
     *            [null]の場合、staticアクセスで処理します.
     * @param name
     *            対象のメソッド名を設定します.
     * @param args
     *            対象のメソッドパラメータを設定します.
     * @param types
     *            対象のメソッドパラメータタイプを設定します.
     * @return Object 戻り値が返されます.
     * @exception NoSuchMethodException
     *                一致するメソッドが見つからない場合.
     * @exception ClassNotFoundException
     *                クラスが存在しない場合.
     * @exception IllegalAccessException
     *                指定条件に対してアクセスできない場合.
     * @exception InvocationTargetException
     *                基本となるメソッドが例外をスローする場合.
     */
    public static final Object invokeTo(ClassLoader loader, String clazzName,
            Object target, String name, Object[] args, Class[] types)
            throws NoSuchMethodException, ClassNotFoundException,
            IllegalAccessException, InvocationTargetException {
        FastClassElement em = FastReflectClass.getClass(loader, clazzName);
        if (em == null) {
            throw new ClassNotFoundException("指定クラス " + clazzName + " は存在しません");
        }
        if (args != null && args.length > 0 && types == null) {
            types = FastReflectUtil.getParamsType(args);
        } else if (types != null) {
            types = FastReflectUtil.getParamsTypeByClass(types);
        }
        Object[] ret = new Object[1];
        String spclazz;
        while (true) {
            if (em.invokeMethod(ret, target, name, loader, types, args)) {
                return ret[0];
            }
            spclazz = em.getSuperClassName();
            if (spclazz == null) {
                throw new NoSuchMethodException("指定メソッド " + name + " はクラス "
                        + clazzName + " に存在しません");
            }
            em = FastReflectClass.getClass(loader, spclazz);
        }
    }

    /**
     * メソッド呼び出し.
     * 
     * @param loader
     *            対象のクラスローダーを設定します.
     * @param clazz
     *            対象のクラスを設定します.
     * @param target
     *            設定対象のオブジェクトを設定します.<BR>
     *            [null]の場合、staticアクセスで処理します.
     * @param name
     *            対象のメソッド名を設定します.
     * @param args
     *            対象のメソッドパラメータを設定します.
     * @param types
     *            対象のメソッドパラメータタイプを設定します.
     * @return Object 戻り値が返されます.
     * @exception NoSuchMethodException
     *                一致するメソッドが見つからない場合.
     * @exception ClassNotFoundException
     *                クラスが存在しない場合.
     * @exception IllegalAccessException
     *                指定条件に対してアクセスできない場合.
     * @exception InvocationTargetException
     *                基本となるメソッドが例外をスローする場合.
     */
    public static final Object invokeTo(ClassLoader loader, Class clazz,
            Object target, String name, Object[] args, Class[] types)
            throws NoSuchMethodException, ClassNotFoundException,
            IllegalAccessException, InvocationTargetException {
        String clazzName = clazz.getName();
        FastClassElement em = FastReflectClass.getClass(loader, clazzName);
        if (em == null) {
            throw new ClassNotFoundException("指定クラス " + clazzName + " は存在しません");
        }
        if (args != null && args.length > 0 && types == null) {
            types = FastReflectUtil.getParamsType(args);
        } else if (types != null) {
            types = FastReflectUtil.getParamsTypeByClass(types);
        }
        Object[] ret = new Object[1];
        String spclazz;
        while (true) {
            if (em.invokeMethod(ret, target, name, loader, types, args)) {
                return ret[0];
            }
            spclazz = em.getSuperClassName();
            if (spclazz == null) {
                throw new NoSuchMethodException("指定メソッド " + name + " はクラス "
                        + clazzName + " に存在しません");
            }
            em = FastReflectClass.getClass(loader, spclazz);
        }
    }
}
