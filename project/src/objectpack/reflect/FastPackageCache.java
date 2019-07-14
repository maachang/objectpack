package objectpack.reflect;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Javaパッケージキャッシュオブジェクト.
 * 
 * @version 2013/12/07
 * @author masahito suzuki
 *
 */
@SuppressWarnings("rawtypes")
public abstract class FastPackageCache {

    /** １つのキャッシュ管理. **/
    private static class FastPackageCacheChild {
        private final List<String> list = new ArrayList<String>();
        private final AtomicLong lastAccess = new AtomicLong();

        public FastPackageCacheChild(String packageName) throws Exception {
            JavaPackageList.getInstance().getList(list, packageName);
            updateTime();
        }

        public void updateTime() {
            while (!lastAccess.compareAndSet(lastAccess.get(),
                    System.currentTimeMillis())) {
            }
        }

        public long getUpdateTime() {
            return lastAccess.get();
        }

        public List<String> getList() {
            updateTime();
            return list;
        }
    }

    /** キャッシュ管理. **/
    private static class FastPackageCacheThread extends Thread {
        public FastPackageCacheThread() {
            super.setDaemon(true);
            super.start();
        }

        public void run() {
            long timeout = FastPackageCache.CACHE_TIMEOUT;
            Map<String, FastPackageCacheChild> map = FastPackageCache.cache;
            Iterator it;
            String key;
            boolean endFlag = false;
            while (!endFlag) {
                try {
                    while (!endFlag) {
                        it = cache.keySet().iterator();
                        while (it.hasNext()) {
                            Thread.sleep(100);
                            key = (String) it.next();
                            FastPackageCacheChild ch = map.get(key);
                            if (ch == null
                                    || ch.getUpdateTime() + timeout < System
                                            .currentTimeMillis()) {
                                map.remove(key);
                            }
                        }
                        Thread.sleep(100);
                    }
                } catch (InterruptedException ie) {
                    endFlag = true;
                } catch (NullPointerException ne) {
                } catch (OutOfMemoryError mem) {
                } catch (Exception e) {
                } catch (ThreadDeath td) {
                    endFlag = true;
                }
            }
        }
    }

    private FastPackageCache() {

    }

    protected static final long CACHE_TIMEOUT = 120000;
    /** キャッシュ破棄時間は120秒後 **/
    protected static final Map<String, FastPackageCacheChild> cache = new ConcurrentHashMap<String, FastPackageCacheChild>();
    protected static final FastPackageCacheThread cacheThread = new FastPackageCacheThread();

    /**
     * パッケージ名内のオブジェクト一覧を取得.
     * 
     * @param packageName
     *            対象のパッケージ名を設定します.
     * @return List<String> オブジェクト一覧が返されます.
     * @exception Exception
     *                例外.
     */
    public static final List<String> getList(String packageName)
            throws Exception {
        if (packageName == null
                || (packageName = packageName.trim()).length() <= 0) {
            throw new IllegalArgumentException("パッケージ名が設定されていません");
        }
        if (packageName.endsWith(".")) {
            packageName = packageName.substring(0, packageName.length() - 1);
        }

        FastPackageCacheChild ch = cache.get(packageName);
        if (ch == null) {
            ch = new FastPackageCacheChild(packageName);
            cache.put(packageName, ch);
        }
        return ch.getList();
    }
}
