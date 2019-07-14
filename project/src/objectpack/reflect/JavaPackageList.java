package objectpack.reflect;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

/**
 * Javaパッケージクラスリストを取得.
 * 
 * @version 2013/12/07
 * @author masahito suzuki
 *
 */
@SuppressWarnings("rawtypes")
public class JavaPackageList {
    private JavaCompiler compiler = null;
    private JavaFileManager fileManager = null;
    private Set<JavaFileObject.Kind> kind = null;

    public static final JavaPackageList SNGL = new JavaPackageList();
    private static final int CLAZZ_NAME_LENGTH = 6;

    /**
     * コンストラクタ.
     */
    private JavaPackageList() {
        try {
            this.compiler = ToolProvider.getSystemJavaCompiler();
            this.fileManager = compiler.getStandardFileManager(
                    new DiagnosticCollector<JavaFileObject>(), null, null);
            this.kind = new HashSet<JavaFileObject.Kind>(
                    FastReflect.INIT_MAP_LENGTH);
            this.kind.add(JavaFileObject.Kind.CLASS);
        } catch (Exception e) {
        }
    }

    /**
     * オブジェクトを取得.
     * 
     * @return JavaPackageList オブジェクト情報が返されます.
     */
    public static final JavaPackageList getInstance() {
        return SNGL;
    }

    /**
     * 指定名のパッケージリストを取得.
     * 
     * @param packageName
     *            対象のパッケージ名を設定します.
     * @return List<String> パッケージ内容が返されます.
     * @exception Exception
     *                例外.
     */
    public List<String> getList(String packageName) throws Exception {
        List<String> ret = new ArrayList<String>();
        getList(ret, packageName);
        return ret;
    }

    /**
     * 指定名のパッケージリストを取得.
     * 
     * @param out
     *            出力先のリストオブジェクトを設定します.
     * @param packageName
     *            対象のパッケージ名を設定します.
     * @return int パッケージ数量が返されます.
     * @exception Exception
     *                例外.
     */
    public int getList(List<String> out, String packageName) throws Exception {
        if (out == null || packageName == null
                || (packageName = packageName.trim()).length() <= 0) {
            throw new IllegalArgumentException("引数は不正です");
        }
        out.clear();
        int ret = 0;
        // 最初にプラットフォームクラス一覧で検索.
        Iterable ib = this.fileManager.list(
                StandardLocation.PLATFORM_CLASS_PATH, packageName, this.kind,
                false);
        if (ib != null) {
            Iterator it = ib.iterator();
            while (it.hasNext()) {
                JavaFileObject f = (JavaFileObject) it.next();
                String n = f.getName();
                out.add(n.substring(0, n.length() - CLAZZ_NAME_LENGTH));
                ret++;
            }
        }
        // データが存在しない場合は、ユーザクラス一覧で検索.
        if (ret == 0) {
            ib = this.fileManager.list(StandardLocation.CLASS_PATH,
                    packageName, this.kind, false);
            if (ib != null) {
                Iterator it = ib.iterator();
                while (it.hasNext()) {
                    JavaFileObject f = (JavaFileObject) it.next();
                    String n = f.getName();
                    out.add(n.substring(0, n.length() - CLAZZ_NAME_LENGTH));
                    ret++;
                }
            }
        }
        return ret;
    }

    /**
     * 指定名のパッケージリストを取得.
     * 
     * @param out
     *            出力先のMapオブジェクトを設定します.
     * @param packageName
     *            対象のパッケージ名を設定します.
     * @return int パッケージ数量が返されます.
     * @exception Exception
     *                例外.
     */
    public int getList(Map<String, String> out, String packageName)
            throws Exception {
        if (out == null || packageName == null
                || (packageName = packageName.trim()).length() <= 0) {
            throw new IllegalArgumentException("引数は不正です");
        }
        out.clear();
        int ret = 0;
        Iterable ib = this.fileManager.list(
                StandardLocation.PLATFORM_CLASS_PATH, packageName, this.kind,
                false);
        if (!packageName.endsWith(".")) {
            packageName = packageName + ".";
        }
        if (ib != null) {
            Iterator it = ib.iterator();
            while (it.hasNext()) {
                JavaFileObject f = (JavaFileObject) it.next();
                String n = f.getName();
                n = n.substring(0, n.length() - CLAZZ_NAME_LENGTH);
                out.put(n, packageName + n);
                ret++;
            }
        }
        return ret;
    }

    /**
     * 指定クラス名が対象パッケージに存在するかチェック.
     * 
     * @param packageName
     *            対象のパッケージ名を設定します.
     * @param objectName
     *            対象のオブジェクト名が返されます.
     * @return boolean [true]の場合、対象クラス名は存在します.
     */
    public boolean isObject(String packageName, String objectName)
            throws Exception {
        if (packageName == null
                || (packageName = packageName.trim()).length() <= 0
                || objectName == null
                || (objectName = objectName.trim()).length() <= 0) {
            throw new IllegalArgumentException("引数は不正です");
        }
        Iterable ib = this.fileManager.list(
                StandardLocation.PLATFORM_CLASS_PATH, packageName, this.kind,
                false);
        if (!packageName.endsWith(".")) {
            packageName = packageName + ".";
        }
        if (ib != null) {
            Iterator it = ib.iterator();
            while (it.hasNext()) {
                JavaFileObject f = (JavaFileObject) it.next();
                String n = f.getName();
                n = n.substring(0, n.length() - CLAZZ_NAME_LENGTH);
                if (objectName.equals(n)) {
                    return true;
                }
            }
        }
        return false;
    }
}
