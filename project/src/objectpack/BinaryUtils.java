package objectpack;

/**
 * バイナリ関連処理.
 * 
 * @version 2013/11/22
 * @author masahito suzuki
 *
 */
public class BinaryUtils {
    private BinaryUtils() {
    }

    /** 小文字、大文字の幅. **/
    private static final int SMALL_BIG_CODE = (int) 'a' - (int) 'A';

    /**
     * 英字の大文字小文字を区別せずにチェック.
     * 
     * @param src
     *            比較元文字を設定します.
     * @param dest
     *            比較先文字を設定します.
     * @return boolean [true]の場合、一致します.
     */
    public static final boolean eqEng(byte[] src, byte[] dest) {
        if (src == null || dest == null) {
            return false;
        }
        int len = src.length;
        if (len != dest.length) {
            return false;
        }
        byte s, d;
        for (int i = 0; i < len; i++) {
            if ((s = src[i]) == (d = dest[i])
                    || (s >= (byte) 'A' && s <= (byte) 'z' && d >= (byte) 'A'
                            && d <= (byte) 'z' && (s + SMALL_BIG_CODE == d
                            || s - SMALL_BIG_CODE == d
                            || s == d + SMALL_BIG_CODE
                            || s == d - SMALL_BIG_CODE
                            || s + SMALL_BIG_CODE == d + SMALL_BIG_CODE
                            || s - SMALL_BIG_CODE == d + SMALL_BIG_CODE
                            || s + SMALL_BIG_CODE == d - SMALL_BIG_CODE || s
                            - SMALL_BIG_CODE == d - SMALL_BIG_CODE))) {
                continue;
            }
            return false;
        }
        return true;
    }

    /**
     * 英字の大文字小文字を区別せずにチェック.
     * 
     * @param src
     *            比較元文字を設定します.
     * @param off
     *            srcのオフセット値を設定します.
     * @param len
     *            srcのlength値を設定します.
     * @param dest
     *            比較先文字を設定します.
     * @return boolean [true]の場合、一致します.
     */
    public static final boolean eqEng(byte[] src, int off, int len, byte[] dest) {
        if (src == null || dest == null) {
            return false;
        }
        if (len != dest.length) {
            return false;
        }
        byte s, d;
        for (int i = 0; i < len; i++) {
            if ((s = src[i + off]) == (d = dest[i])
                    || (s >= (byte) 'A' && s <= (byte) 'z' && d >= (byte) 'A'
                            && d <= (byte) 'z' && (s + SMALL_BIG_CODE == d
                            || s - SMALL_BIG_CODE == d
                            || s == d + SMALL_BIG_CODE
                            || s == d - SMALL_BIG_CODE
                            || s + SMALL_BIG_CODE == d + SMALL_BIG_CODE
                            || s - SMALL_BIG_CODE == d + SMALL_BIG_CODE
                            || s + SMALL_BIG_CODE == d - SMALL_BIG_CODE || s
                            - SMALL_BIG_CODE == d - SMALL_BIG_CODE))) {
                continue;
            }
            return false;
        }
        return true;
    }

    /**
     * 英字の大文字小文字を区別しない、バイトチェック.
     * 
     * @param s
     *            比較の文字を設定します.
     * @param d
     *            比較の文字を設定します.
     * @return boolean [true]の場合、一致します.
     */
    public static final boolean oneEng(byte s, byte d) {
        return (s == d || (s >= (byte) 'A' && s <= (byte) 'z'
                && d >= (byte) 'A' && d <= (byte) 'z' && (s + SMALL_BIG_CODE == d
                || s - SMALL_BIG_CODE == d
                || s == d + SMALL_BIG_CODE
                || s == d - SMALL_BIG_CODE
                || s + SMALL_BIG_CODE == d + SMALL_BIG_CODE
                || s - SMALL_BIG_CODE == d + SMALL_BIG_CODE
                || s + SMALL_BIG_CODE == d - SMALL_BIG_CODE || s
                - SMALL_BIG_CODE == d - SMALL_BIG_CODE)));
    }

    /**
     * バイナリindexOf.
     * 
     * @param buf
     *            設定対象のバイナリ情報を設定します.
     * @param chk
     *            チェック対象のバイナリ情報を設定します.
     * @param off
     *            設定対象のオフセット値を設定します.
     * @param vLen
     *            対象のバイナリ長を設定します.
     * @return int マッチする位置が返却されます. [-1]の場合は情報は存在しません.
     */
    public static final int indexOf(final byte[] buf, byte chk, final int off,
            int vLen) {
        for (int i = off; i < vLen; i++) {
            if (chk != buf[i]) {
                while (++i < vLen && chk != buf[i])
                    ;
                if (vLen != i) {
                    return i;
                }
            } else {
                return i;
            }
        }
        return -1;
    }

    /**
     * バイナリindexOf.
     * 
     * @param buf
     *            設定対象のバイナリ情報を設定します.
     * @param chk
     *            チェック対象のバイナリ情報を設定します.
     * @param off
     *            設定対象のオフセット値を設定します.
     * @param vLen
     *            対象のバイナリ長を設定します.
     * @return int マッチする位置が返却されます. [-1]の場合は情報は存在しません.
     */
    public static final int indexOf(final byte[] buf, final byte[] chk,
            final int off, int vLen) {
        int len = chk.length;
        // 単数バイナリ検索.
        if (len == 1) {
            byte first = chk[0];
            for (int i = off; i < vLen; i++) {
                if (first != buf[i]) {
                    while (++i < vLen && first != buf[i])
                        ;
                    if (vLen != i) {
                        return i;
                    }
                } else {
                    return i;
                }
            }
        }
        // 複数バイナリ検索.
        else {
            byte first = chk[0];
            vLen = vLen - (len - 1);
            int j, k, next;
            for (int i = off; i < vLen; i++) {
                if (first != buf[i]) {
                    while (++i < vLen && buf[i] != first)
                        ;
                }
                if (i < vLen) {
                    for (next = i + len, j = i + 1, k = 1; j < next
                            && buf[j] == chk[k]; j++, k++)
                        ;
                    if (j == next) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }

    /**
     * バイナリlastIndexOf.
     * 
     * @param buf
     *            設定対象のバイナリ情報を設定します.
     * @param chk
     *            チェック対象のバイナリ情報を設定します.
     * @param off
     *            設定対象のオフセット値を設定します.
     * @return int マッチする位置が返却されます. [-1]の場合は情報は存在しません.
     */
    public static final int lastIndexOf(final byte[] buf, final byte[] chk,
            int off) {
        int len = chk.length - 1;
        // 単数バイナリ検索.
        if (len == 0) {
            byte last = chk[0];
            for (int i = off; i >= 0; i--) {
                if (last != buf[i]) {
                    while (--i >= 0 && buf[i] != last)
                        ;
                    if (off != i) {
                        return i;
                    }
                } else {
                    return i;
                }
            }
        }
        // 複数バイナリ検索.
        else {
            int j, k, next;
            byte last = chk[len];
            for (int i = off; i >= 0; i--) {
                if (last != buf[i]) {
                    while (--i >= 0 && buf[i] != last)
                        ;
                }
                if (i >= len) {
                    for (next = i - (len + 1), j = i - 1, k = len - 1; j > next
                            && buf[j] == chk[k]; j--, k--)
                        ;
                    if (j == next) {
                        return j + 1;
                    }
                }
            }
        }
        return -1;
    }

    /**
     * 英字の大文字小文字を区別しない、バイナリindexOf.
     * 
     * @param buf
     *            設定対象のバイナリ情報を設定します.
     * @param chk
     *            チェック対象のバイナリ情報を設定します.
     * @param off
     *            設定対象のオフセット値を設定します.
     * @param vLen
     *            対象のバイナリ長を設定します.
     * @return int マッチする位置が返却されます. [-1]の場合は情報は存在しません.
     */
    public static final int indexOfEng(final byte[] buf, final byte[] chk,
            final int off, int vLen) {
        int len = chk.length;
        // 単数バイナリ検索.
        if (len == 1) {
            byte first = chk[0];
            for (int i = off; i < vLen; i++) {
                if (!oneEng(first, buf[i])) {
                    while (++i < vLen && !oneEng(first, buf[i]))
                        ;
                    if (vLen != i) {
                        return i;
                    }
                } else {
                    return i;
                }
            }
        }
        // 複数バイナリ検索.
        else {
            byte first = chk[0];
            vLen = vLen - (len - 1);
            int j, k, next;
            for (int i = off; i < vLen; i++) {
                if (!oneEng(first, buf[i])) {
                    while (++i < vLen && !oneEng(first, buf[i]))
                        ;
                }
                if (i < vLen) {
                    for (next = i + len, j = i + 1, k = 1; j < next
                            && oneEng(buf[j], chk[k]); j++, k++)
                        ;
                    if (j == next) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }

    /**
     * 英字の大文字小文字を区別しない、バイナリlastIndexOf.
     * 
     * @param buf
     *            設定対象のバイナリ情報を設定します.
     * @param chk
     *            チェック対象のバイナリ情報を設定します.
     * @param off
     *            設定対象のオフセット値を設定します.
     * @return int マッチする位置が返却されます. [-1]の場合は情報は存在しません.
     */
    public static final int lastIndexOfEng(final byte[] buf, final byte[] chk,
            int off) {
        int len = chk.length - 1;
        // 単数バイナリ検索.
        if (len == 0) {
            byte last = chk[0];
            for (int i = off; i >= 0; i--) {
                if (!oneEng(last, buf[i])) {
                    while (--i >= 0 && !oneEng(last, buf[i]))
                        ;
                    if (off != i) {
                        return i;
                    }
                } else {
                    return i;
                }
            }
        }
        // 複数バイナリ検索.
        else {
            int j, k, next;
            byte last = chk[len];
            for (int i = off; i >= 0; i--) {
                if (!oneEng(last, buf[i])) {
                    while (--i >= 0 && !oneEng(last, buf[i]))
                        ;
                }
                if (i >= len) {
                    for (next = i - (len + 1), j = i - 1, k = len - 1; j > next
                            && oneEng(buf[j], chk[k]); j--, k--)
                        ;
                    if (j == next) {
                        return j + 1;
                    }
                }
            }
        }
        return -1;
    }

    /**
     * 対象バイナリを文字列化. ※マルチバイト文字が格納されている場合は不可.
     * 
     * @param src
     *            対象のバイナリを設定します.
     * @return String 文字列が返却されます.
     */
    public static final String toString(byte[] src) {
        return toString(src, 0, src.length);
    }

    /**
     * 対象バイナリを文字列化. ※マルチバイト文字が格納されている場合は不可.
     * 
     * @param src
     *            対象のバイナリを設定します.
     * @param off
     *            対象のオフセットを設定します.
     * @param len
     *            対象の長さを設定します.
     * @return String 文字列が返却されます.
     */
    public static final String toString(byte[] src, int off, int len) {
        char[] c = new char[len];
        for (int i = 0; i < len; i++) {
            c[i] = (char) (src[i + off] & 0xff);
        }
        return new String(c);
    }

    /**
     * バイナリ情報の内容を出力.
     * 
     * @param src
     *            対象のバイナリを設定します.
     * @return String 文字列が返却されます.
     */
    public static final String toBinaryString(byte[] src) {
        return toBinaryString(src, 0, src.length);
    }

    /**
     * バイナリ情報の内容を出力.
     * 
     * @param src
     *            対象のバイナリを設定します.
     * @param off
     *            対象のオフセットを設定します.
     * @param len
     *            対象の長さを設定します.
     * @return String 文字列が返却されます.
     */
    public static final String toBinaryString(byte[] src, int off, int len) {
        String n;
        StringBuilder buf = new StringBuilder();
        buf.append("\n +0 +1 +2 +3 +4 +5 +6 +7    +8 +9 +A +B +C +D +E +F ");
        for (int i = 0; i < len; i++) {
            if ((i & 0x0f) == 0) {
                buf.append("\n ");
            } else if ((i & 0x0f) == 8) {
                buf.append("   ");
            } else {
                buf.append(" ");
            }
            n = Integer.toHexString(src[off + i] & 0x000000ff);
            buf.append("00".substring(n.length())).append(n);
        }
        buf.append("\n");
        return buf.toString();
    }

    /**
     * 有効最大ビット長を取得.
     * 
     * @param x
     *            対象の数値を設定します.
     * @return int 左ゼロビット数が返却されます.
     */
    public static final int nlzs(int x) {
        if (x == 0) {
            return 0;
        }
        x |= (x >> 1);
        x |= (x >> 2);
        x |= (x >> 4);
        x |= (x >> 8);
        x |= (x >> 16);
        x = (x & 0x55555555) + (x >> 1 & 0x55555555);
        x = (x & 0x33333333) + (x >> 2 & 0x33333333);
        x = (x & 0x0f0f0f0f) + (x >> 4 & 0x0f0f0f0f);
        x = (x & 0x00ff00ff) + (x >> 8 & 0x00ff00ff);
        return (x & 0x0000ffff) + (x >> 16 & 0x0000ffff);
    }

    /**
     * 有効最大ビット長を取得.
     * 
     * @param x
     *            対象の数値を設定します.
     * @return int 左ゼロビット数が返却されます.
     */
    public static final int nlzs(long x) {
        int xx = (int) ((x & 0xffffffff00000000L) >> 32L);
        if (nlzs(xx) == 0) {
            return nlzs((int) (x & 0x00000000ffffffff));
        }
        return nlzs(xx) + 32;
    }

    /**
     * ビットサイズを取得.
     * 
     * @param x
     *            対象の数値を設定します.
     * @return int ビット数が返却されます.
     */
    public static final int bitMask(int x) {
        if (x <= 0) {
            return 0;
        }
        x |= (x >> 1);
        x |= (x >> 2);
        x |= (x >> 4);
        x |= (x >> 8);
        x |= (x >> 16);
        x = (x & 0x55555555) + (x >> 1 & 0x55555555);
        x = (x & 0x33333333) + (x >> 2 & 0x33333333);
        x = (x & 0x0f0f0f0f) + (x >> 4 & 0x0f0f0f0f);
        x = (x & 0x00ff00ff) + (x >> 8 & 0x00ff00ff);
        x = (x & 0x0000ffff) + (x >> 16 & 0x0000ffff);
        return 1 << (((x & 0x0000ffff) + (x >> 16 & 0x0000ffff)) - 1);
    }

    /**
     * 有効最大バイト長を取得.
     * 
     * @param x
     *            対象の数値を設定します.
     * @param addBit
     *            追加ビット長を設定します.
     * @return int 有効最大バイト長が返却されます.
     */
    public static final int getMaxByte(int x) {
        return getMaxByte(x, 0);
    }

    /**
     * 有効最大バイト長を取得.
     * 
     * @param x
     *            対象の数値を設定します.
     * @param addBit
     *            追加ビット長を設定します.
     * @return int 有効最大バイト長が返却されます.
     */
    public static final int getMaxByte(int x, int addBit) {
        x = nlzs(x) + addBit;
        return (x >> 3) + ((x & 1) | ((x >> 1) & 1) | ((x >> 2) & 1));
    }

    /**
     * 有効最大バイト長を取得.
     * 
     * @param x
     *            対象の数値を設定します.
     * @return int 有効最大バイト長が返却されます.
     */
    public static final int getMaxByte(long x) {
        return getMaxByte(x, 0);
    }

    /**
     * 有効最大バイト長を取得.
     * 
     * @param xx
     *            対象の数値を設定します.
     * @param addBit
     *            追加ビット長を設定します.
     * @return int 有効最大バイト長が返却されます.
     */
    public static final int getMaxByte(long xx, int addBit) {
        int x = nlzs(xx) + addBit;
        return (x >> 3) + ((x & 1) | ((x >> 1) & 1) | ((x >> 2) & 1));
    }

}
