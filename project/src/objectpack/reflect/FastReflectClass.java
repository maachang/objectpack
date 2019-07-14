package objectpack.reflect;

import java.lang.ref.SoftReference;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 高速クラス読み込み.
 *
 * @version 2013/12/07
 * @author masahito suzuki
 *
 */
@SuppressWarnings("rawtypes")
abstract class FastReflectClass {
    private FastReflectClass() {
    }

    /**
     * キャッシュクラス. 未アクセス時の破棄時間.
     */
    private static final long WAIT_TIME = 60000L * 5L; // 5分.

    /**
     * クラスキャッシュ用.
     */
    protected static final Map<String, SoftReference<FastClassElement>> cacheClass = new ConcurrentHashMap<String, SoftReference<FastClassElement>>();

    /** 監視オブジェクト. **/
    protected static final FastReflectMon mon = new FastReflectMon();

    /**
     * 指定パッケージ名のクラス情報を取得.
     * 
     * @param name
     *            対象のクラス名を設定します.
     * @return FastClassElement 対象のクラス情報が返されます.
     * @exception ClassNotFoundException
     *                ロードクラス例外.
     */
    public static final FastClassElement getClass(String name)
            throws ClassNotFoundException {
        if (name == null || name.length() <= 0) {
            throw new IllegalArgumentException("クラス名が指定されていません");
        }
        FastClassElement ret = null;
        SoftReference<FastClassElement> ref = cacheClass.get(name);
        if (ref == null || (ret = ref.get()) == null) {
            Class c;
            if ((c = Class.forName(name)) != null) {
                ret = new FastClassElement(c);
                cacheClass.put(name, new SoftReference<FastClassElement>(ret));
            }
        }
        if (ret != null) {
            ret.update();
        }
        return ret;
    }

    /**
     * 指定パッケージ名のクラス情報を取得.
     * 
     * @param cl
     *            対象のクラスローダを設定します.
     * @param name
     *            対象のクラス名を設定します.
     * @return Class 対象のクラス情報が返されます.
     * @exception ClassNotFoundException
     *                ロードクラス例外.
     */
    public static final FastClassElement getClass(ClassLoader cl, String name)
            throws ClassNotFoundException {
        if (name == null || name.length() <= 0) {
            throw new IllegalArgumentException("クラス名が指定されていません");
        }
        FastClassElement ret = null;
        SoftReference<FastClassElement> ref = cacheClass.get(name);
        if (ref == null || (ret = ref.get()) == null) {
            Class c;
            if (cl == null) {
                c = Class.forName(name);
            } else {
                c = cl.loadClass(name);
            }
            if (c != null) {
                ret = new FastClassElement(c);
                cacheClass.put(name, new SoftReference<FastClassElement>(ret));
            }
        }
        if (ret != null) {
            ret.update();
        }
        return ret;
    }

    // オブジェクト未利用監視.
    private static class FastReflectMon extends Thread {
        FastReflectMon() {
            try {
                this.setDaemon(true);
                this.start();
            } catch (Exception e) {
            }
        }

        public void run() {
            long WAIT = FastReflectClass.WAIT_TIME;
            Map<String, SoftReference<FastClassElement>> m;
            String k;
            FastClassElement v;
            SoftReference<FastClassElement> ref;
            m = FastReflectClass.cacheClass;
            Iterator it;
            while (true) {
                try {
                    while (true) {
                        if (m.size() > 0) {
                            it = m.keySet().iterator();
                            while (it.hasNext()) {
                                Thread.sleep(50L);
                                k = (String) it.next();
                                ref = m.get(k);
                                v = null;
                                if (ref == null || (v = ref.get()) == null) {
                                    m.remove(k);
                                } else if (v.getLastAccessTime() + WAIT <= System
                                        .currentTimeMillis()) {
                                    m.remove(k);
                                }
                            }
                        } else {
                            Thread.sleep(50L);
                        }
                    }
                } catch (InterruptedException ite) {
                    break;
                } catch (ThreadDeath d) {
                    throw d;
                } catch (Throwable e) {
                }
            }
        }
    }
}
