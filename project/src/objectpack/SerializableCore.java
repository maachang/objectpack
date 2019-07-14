package objectpack;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import objectpack.reflect.FastReflect;

/**
 * シリアライズコアオブジェクト.
 * 
 * @version 2013/12/19
 * @author masahito suzuki
 */
public final class SerializableCore {
    private SerializableCore() {
    }

    /**
     * オブジェクトをバイナリに変換.
     * 
     * @param o
     *            対象のオブジェクトを設定します.
     * @return byte[] 変換されたバイナリ情報が返却されます.
     * @exception Exception
     *                例外.
     */
    public static final byte[] encodeBinary(Object o) throws Exception {
        return encodeBinary(null, o);
    }

    /**
     * オブジェクトをバイナリに変換.
     *
     * @parma buf 対象のバッファオブジェクトを設定します.
     * @param o
     *            対象のオブジェクトを設定します.
     * @return byte[] 変換されたバイナリ情報が返却されます.
     * @exception Exception
     *                例外.
     */
    public static final byte[] encodeBinary(ByteArrayIO buf, Object o)
            throws Exception {
        if (buf == null) {
            buf = new ByteArrayIO();
        }
        // 文字情報の集約先を生成.
        Map<String, Integer> stringCode = new HashMap<>();

        // 文字情報集約先の書き込み処理(4).
        buf.write(new byte[] { (byte) 0, (byte) 0, (byte) 0, (byte) 0 });

        // オブジェクト変換(4+n).
        encodeObject(stringCode, buf, o);

        // 文字情報格納位置を取得(endPoint=4+n).
        int endPoint = buf.size();

        // 集約文字情報の格納(m).
        convertExtractionString(buf, stringCode);

        // 先頭に文字情報集約先のアドレスをセット(b.length = 4 + n + m).
        byte[] b = buf.toByteArray();
        buf.clear();

        // 先頭に文字情報集約先のアドレスをセット.
        b[0] = (byte) (endPoint & 0x000000ff);
        b[1] = (byte) ((endPoint & 0x0000ff00) >> 8);
        b[2] = (byte) ((endPoint & 0x00ff0000) >> 16);
        b[3] = (byte) ((endPoint & 0xff000000) >> 24);

        return b;
    }

    /**
     * バイナリをオブジェクトに変換.
     * 
     * @param b
     *            対象のバイナリを設定します.
     * @return Object 変換されたオブジェクトが返却されます.
     * @exception Exception
     *                例外.
     */
    public static final Object decodeBinary(byte[] b) throws Exception {
        return decodeBinary(b, 0, b.length);
    }

    /**
     * バイナリをオブジェクトに変換.
     * 
     * @param b
     *            対象のバイナリを設定します.
     * @param off
     *            対象のオフセット値を設定します.
     * @param len
     *            対象の長さを設定します.
     * @return Object 変換されたオブジェクトが返却されます.
     * @exception Exception
     *                例外.
     */
    public static final Object decodeBinary(byte[] b, int off, int len)
            throws Exception {
        int[] p = new int[] { off };
        return decodeBinary(b, p, len);
    }

    /**
     * バイナリをオブジェクトに変換.
     * 
     * @param b
     *            対象のバイナリを設定します.
     * @param p
     *            対象のオフセット値を設定します.
     * @param len
     *            対象の長さを設定します.
     * @return Object 変換されたオブジェクトが返却されます.
     * @exception Exception
     *                例外.
     */
    public static final Object decodeBinary(byte[] b, int[] p, int len)
            throws Exception {
        // ポジションバックアップ・
        int pos = p[0];

        // 文字情報集約先の位置情報を取得.
        // endpoint = 4 + n.
        // n = body : 4 = head.
        int endPoint = (int) ((b[pos] & 0x000000ff)
                | ((b[pos + 1] & 0x000000ff) << 8)
                | ((b[pos + 2] & 0x000000ff) << 16) | ((b[pos + 3] & 0x000000ff) << 24));

        // 文字情報集約先の情報を取得.
        p[0] = endPoint;
        String[] stringMap = getExtractionString(b, p);

        // バックアップしたポジション情報を元に、データ解析.
        p[0] = pos + 4;
        len = endPoint;
        return decodeObject(stringMap, p, b, len);
    }

    /** 1バイトバイナリ変換. **/
    protected static final void byte1(OutputStream buf, int b) throws Exception {
        buf.write((b & 0xff));
    }

    /** 2バイトバイナリ変換. **/
    protected static final void byte2(OutputStream buf, int b) throws Exception {
        buf.write(new byte[] { (byte) ((b & 0xff00) >> 8), (byte) (b & 0xff) });
    }

    /** 4バイトバイナリ変換. **/
    protected static final void byte4(OutputStream buf, int b) throws Exception {
        // 4バイトの場合は、先頭2ビットをビット長とする.
        int bit = BinaryUtils.nlzs(b);
        int src = (bit >> 3)
                + ((bit & 1) | ((bit >> 1) & 1) | ((bit >> 2) & 1));
        bit = ((bit += 2) >> 3)
                + ((bit & 1) | ((bit >> 1) & 1) | ((bit >> 2) & 1));

        // 先頭2ビット条件が混同できる場合.
        if (bit == src) {
            switch (bit) {
            case 1:
                buf.write(new byte[] { (byte) (b & 0xff) });
                return;
            case 2:
                buf.write(new byte[] { (byte) (0x40 | ((b & 0xff00) >> 8)),
                        (byte) (b & 0xff) });
                return;
            case 3:
                buf.write(new byte[] { (byte) (0x80 | ((b & 0xff0000) >> 16)),
                        (byte) ((b & 0xff00) >> 8), (byte) (b & 0xff) });
                return;
            case 4:
                buf.write(new byte[] {
                        (byte) (0xc0 | ((b & 0xff000000) >> 24)),
                        (byte) ((b & 0xff0000) >> 16),
                        (byte) ((b & 0xff00) >> 8), (byte) (b & 0xff) });
                return;
            }
        }
        // 先頭2ビット条件が混同できない場合.
        switch (src) {
        case 0:
        case 1:
            buf.write(new byte[] { (byte) 0, (byte) (b & 0xff) });
            return;
        case 2:
            buf.write(new byte[] { (byte) 0x40, (byte) ((b & 0xff00) >> 8),
                    (byte) (b & 0xff) });
            return;
        case 3:
            buf.write(new byte[] { (byte) 0x80, (byte) ((b & 0xff0000) >> 16),
                    (byte) ((b & 0xff00) >> 8), (byte) (b & 0xff) });
            return;
        case 4:
            buf.write(new byte[] { (byte) 0xc0,
                    (byte) ((b & 0xff000000) >> 24),
                    (byte) ((b & 0xff0000) >> 16), (byte) ((b & 0xff00) >> 8),
                    (byte) (b & 0xff) });
            return;
        }
    }

    /** 8バイトバイナリ変換. **/
    protected static final void byte8(OutputStream buf, long b)
            throws Exception {
        // 8バイトの場合は、先頭3ビットをビット長とする.
        int bit = BinaryUtils.nlzs(b);
        int src = (bit >> 3)
                + ((bit & 1) | ((bit >> 1) & 1) | ((bit >> 2) & 1));
        bit = ((bit += 3) >> 3)
                + ((bit & 1) | ((bit >> 1) & 1) | ((bit >> 2) & 1));

        // 先頭3ビット条件が混同できる場合.
        if (bit == src) {
            switch (bit) {
            case 1:
                buf.write(new byte[] { (byte) (b & 0xffL) });
                return;
            case 2:
                buf.write(new byte[] { (byte) (0x20 | ((b & 0xff00L) >> 8L)),
                        (byte) (b & 0xffL) });
                return;
            case 3:
                buf.write(new byte[] {
                        (byte) (0x40 | ((b & 0xff0000L) >> 16L)),
                        (byte) ((b & 0xff00L) >> 8L), (byte) (b & 0xffL) });
                return;
            case 4:
                buf.write(new byte[] {
                        (byte) (0x60 | ((b & 0xff000000L) >> 24L)),
                        (byte) ((b & 0xff0000L) >> 16L),
                        (byte) ((b & 0xff00L) >> 8L), (byte) (b & 0xffL) });
                return;
            case 5:
                buf.write(new byte[] {
                        (byte) (0x80 | ((b & 0xff00000000L) >> 32L)),
                        (byte) ((b & 0xff000000L) >> 24L),
                        (byte) ((b & 0xff0000L) >> 16L),
                        (byte) ((b & 0xff00L) >> 8L), (byte) (b & 0xffL) });
                return;
            case 6:
                buf.write(new byte[] {
                        (byte) (0xA0 | ((b & 0xff0000000000L) >> 40L)),
                        (byte) ((b & 0xff00000000L) >> 32L),
                        (byte) ((b & 0xff000000L) >> 24L),
                        (byte) ((b & 0xff0000L) >> 16L),
                        (byte) ((b & 0xff00L) >> 8L), (byte) (b & 0xffL) });
                return;
            case 7:
                buf.write(new byte[] {
                        (byte) (0xC0 | ((b & 0xff000000000000L) >> 48L)),
                        (byte) ((b & 0xff0000000000L) >> 40L),
                        (byte) ((b & 0xff00000000L) >> 32L),
                        (byte) ((b & 0xff000000L) >> 24L),
                        (byte) ((b & 0xff0000L) >> 16L),
                        (byte) ((b & 0xff00L) >> 8L), (byte) (b & 0xffL) });
                return;
            case 8:
                buf.write(new byte[] {
                        (byte) (0xE0 | ((b & 0xff00000000000000L) >> 56L)),
                        (byte) ((b & 0xff000000000000L) >> 48L),
                        (byte) ((b & 0xff0000000000L) >> 40L),
                        (byte) ((b & 0xff00000000L) >> 32L),
                        (byte) ((b & 0xff000000L) >> 24L),
                        (byte) ((b & 0xff0000L) >> 16L),
                        (byte) ((b & 0xff00L) >> 8L), (byte) (b & 0xffL) });
                return;
            }
        }
        // 先頭3ビット条件が混同できない場合.
        switch (src) {
        case 0:
        case 1:
            buf.write(new byte[] { (byte) 0, (byte) (b & 0xffL) });
            return;
        case 2:
            buf.write(new byte[] { (byte) 0x20, (byte) ((b & 0xff00L) >> 8L),
                    (byte) (b & 0xffL) });
            return;
        case 3:
            buf.write(new byte[] { (byte) 0x40,
                    (byte) ((b & 0xff0000L) >> 16L),
                    (byte) ((b & 0xff00L) >> 8L), (byte) (b & 0xffL) });
            return;
        case 4:
            buf.write(new byte[] { (byte) 0x60,
                    (byte) ((b & 0xff000000L) >> 24L),
                    (byte) ((b & 0xff0000L) >> 16L),
                    (byte) ((b & 0xff00L) >> 8L), (byte) (b & 0xffL) });
            return;
        case 5:
            buf.write(new byte[] { (byte) 0x80,
                    (byte) ((b & 0xff00000000L) >> 32L),
                    (byte) ((b & 0xff000000L) >> 24L),
                    (byte) ((b & 0xff0000L) >> 16L),
                    (byte) ((b & 0xff00L) >> 8L), (byte) (b & 0xffL) });
            return;
        case 6:
            buf.write(new byte[] { (byte) 0xA0,
                    (byte) ((b & 0xff0000000000L) >> 40L),
                    (byte) ((b & 0xff00000000L) >> 32L),
                    (byte) ((b & 0xff000000L) >> 24L),
                    (byte) ((b & 0xff0000L) >> 16L),
                    (byte) ((b & 0xff00L) >> 8L), (byte) (b & 0xffL) });
            return;
        case 7:
            buf.write(new byte[] { (byte) 0xC0,
                    (byte) ((b & 0xff000000000000L) >> 48L),
                    (byte) ((b & 0xff0000000000L) >> 40L),
                    (byte) ((b & 0xff00000000L) >> 32L),
                    (byte) ((b & 0xff000000L) >> 24L),
                    (byte) ((b & 0xff0000L) >> 16L),
                    (byte) ((b & 0xff00L) >> 8L), (byte) (b & 0xffL) });
            return;
        case 8:
            buf.write(new byte[] { (byte) 0xE0,
                    (byte) ((b & 0xff00000000000000L) >> 56L),
                    (byte) ((b & 0xff000000000000L) >> 48L),
                    (byte) ((b & 0xff0000000000L) >> 40L),
                    (byte) ((b & 0xff00000000L) >> 32L),
                    (byte) ((b & 0xff000000L) >> 24L),
                    (byte) ((b & 0xff0000L) >> 16L),
                    (byte) ((b & 0xff00L) >> 8L), (byte) (b & 0xffL) });
            return;
        }
    }

    /** 1バイト数値変換. **/
    protected static final int byte1Int(byte[] b, int[] off) {
        return b[off[0]++] & 0xff;
    }

    /** 2バイト数値変換. **/
    protected static final int byte2Int(byte[] b, int[] off) {
        return ((b[off[0]++] & 0xff) << 8) | (b[off[0]++] & 0xff);
    }

    /** 4バイト数値変換. **/
    protected static final int byte4Int(byte[] b, int[] off) {
        int o = off[0];
        if ((b[o] & 0x3f) == 0) {
            // ヘッダ2ビットが単体１バイト定義の場合.
            switch (((b[o] & 0xc0) >> 6) + 1) {
            case 1:
                off[0] += 2;
                return (b[o + 1] & 0xff);
            case 2:
                off[0] += 3;
                return ((b[o + 1] & 0xff) << 8) | (b[o + 2] & 0xff);
            case 3:
                off[0] += 4;
                return ((b[o + 1] & 0xff) << 16) | ((b[o + 2] & 0xff) << 8)
                        | (b[o + 3] & 0xff);
            case 4:
                off[0] += 5;
                return ((b[o + 1] & 0xff) << 24) | ((b[o + 2] & 0xff) << 16)
                        | ((b[o + 3] & 0xff) << 8) | (b[o + 4] & 0xff);
            }
            throw new IllegalArgumentException("不正なbyte4Int条件:" + off[0]);
        }
        // ヘッダ2ビットが混在定義の場合.
        switch (((b[o] & 0xc0) >> 6) + 1) {
        case 1:
            off[0] += 1;
            return (b[o] & 0x3f);
        case 2:
            off[0] += 2;
            return ((b[o] & 0x3f) << 8) | (b[o + 1] & 0xff);
        case 3:
            off[0] += 3;
            return ((b[o] & 0x3f) << 16) | ((b[o + 1] & 0xff) << 8)
                    | (b[o + 2] & 0xff);
        case 4:
            off[0] += 4;
            return ((b[o] & 0x3f) << 24) | ((b[o + 1] & 0xff) << 16)
                    | ((b[o + 2] & 0xff) << 8) | (b[o + 3] & 0xff);
        }
        throw new IllegalArgumentException("不正なbyte4Int条件:" + off[0]);
    }

    /** 8バイト数値変換. **/
    protected static final long byte8Long(byte[] b, int[] off) {
        int o = off[0];
        if ((b[o] & 0x1f) == 0) {
            // ヘッダ3ビットが単体１バイト定義の場合.
            switch (((b[o] & 0xe0) >> 5) + 1) {
            case 1:
                off[0] += 2;
                return (long) (b[o + 1] & 0xff);
            case 2:
                off[0] += 3;
                return (long) (((b[o + 1] & 0xff) << 8) | (b[o + 2] & 0xff));
            case 3:
                off[0] += 4;
                return (long) (((b[o + 1] & 0xff) << 16)
                        | ((b[o + 2] & 0xff) << 8) | (b[o + 3] & 0xff));
            case 4:
                off[0] += 5;
                return (long) (((b[o + 1] & 0xff) << 24)
                        | ((b[o + 2] & 0xff) << 16) | ((b[o + 3] & 0xff) << 8) | (b[o + 4] & 0xff));
            case 5:
                off[0] += 6;
                return (long) (((b[o + 1] & 0xffL) << 32L)
                        | ((b[o + 2] & 0xffL) << 24L)
                        | ((b[o + 3] & 0xffL) << 16L)
                        | ((b[o + 4] & 0xffL) << 8L) | (b[o + 5] & 0xffL));
            case 6:
                off[0] += 7;
                return (long) (((b[o + 1] & 0xffL) << 40L)
                        | ((b[o + 2] & 0xffL) << 32L)
                        | ((b[o + 3] & 0xffL) << 24L)
                        | ((b[o + 4] & 0xffL) << 16L)
                        | ((b[o + 5] & 0xffL) << 8L) | (b[o + 6] & 0xffL));
            case 7:
                off[0] += 8;
                return (long) (((b[o + 1] & 0xffL) << 48L)
                        | ((b[o + 2] & 0xffL) << 40L)
                        | ((b[o + 3] & 0xffL) << 32L)
                        | ((b[o + 4] & 0xffL) << 24L)
                        | ((b[o + 5] & 0xffL) << 16L)
                        | ((b[o + 6] & 0xffL) << 8L) | (b[o + 7] & 0xffL));
            case 8:
                off[0] += 9;
                return (long) (((b[o + 1] & 0xffL) << 56L)
                        | ((b[o + 2] & 0xffL) << 48L)
                        | ((b[o + 3] & 0xffL) << 40L)
                        | ((b[o + 4] & 0xffL) << 32L)
                        | ((b[o + 5] & 0xffL) << 24L)
                        | ((b[o + 6] & 0xffL) << 16L)
                        | ((b[o + 7] & 0xffL) << 8L) | (b[o + 8] & 0xffL));
            }
            throw new IllegalArgumentException("不正なbyte8Long条件:" + off[0]);
        }
        // ヘッダ3ビットが混在定義の場合.
        switch (((b[o] & 0xe0) >> 5) + 1) {
        case 1:
            off[0] += 1;
            return (long) (b[o] & 0x1f);
        case 2:
            off[0] += 2;
            return (long) (((b[o] & 0x1f) << 8) | (b[o + 1] & 0xff));
        case 3:
            off[0] += 3;
            return (long) (((b[o] & 0x1f) << 16) | ((b[o + 1] & 0xff) << 8) | (b[o + 2] & 0xff));
        case 4:
            off[0] += 4;
            return (long) (((b[o] & 0x1f) << 24) | ((b[o + 1] & 0xff) << 16)
                    | ((b[o + 2] & 0xff) << 8) | (b[o + 3] & 0xff));
        case 5:
            off[0] += 5;
            return (long) (((b[o] & 0x1fL) << 32L)
                    | ((b[o + 1] & 0xffL) << 24L) | ((b[o + 2] & 0xffL) << 16L)
                    | ((b[o + 3] & 0xffL) << 8L) | (b[o + 4] & 0xffL));
        case 6:
            off[0] += 6;
            return (long) (((b[o] & 0x1fL) << 40L)
                    | ((b[o + 1] & 0xffL) << 32L) | ((b[o + 2] & 0xffL) << 24L)
                    | ((b[o + 3] & 0xffL) << 16L) | ((b[o + 4] & 0xffL) << 8L) | (b[o + 5] & 0xffL));
        case 7:
            off[0] += 7;
            return (long) (((b[o] & 0x1fL) << 48L)
                    | ((b[o + 1] & 0xffL) << 40L) | ((b[o + 2] & 0xffL) << 32L)
                    | ((b[o + 3] & 0xffL) << 24L) | ((b[o + 4] & 0xffL) << 16L)
                    | ((b[o + 5] & 0xffL) << 8L) | (b[o + 6] & 0xffL));
        case 8:
            off[0] += 8;
            return (long) (((b[o] & 0x1fL) << 56L)
                    | ((b[o + 1] & 0xffL) << 48L) | ((b[o + 2] & 0xffL) << 40L)
                    | ((b[o + 3] & 0xffL) << 32L) | ((b[o + 4] & 0xffL) << 24L)
                    | ((b[o + 5] & 0xffL) << 16L) | ((b[o + 6] & 0xffL) << 8L) | (b[o + 7] & 0xffL));
        }
        throw new IllegalArgumentException("不正なbyte8Long条件:" + off[0]);
    }

    /**
     * 抽出文字列のバイナリ化.
     *
     * @param buf
     *            対象のバッファを設定します.
     * @param ext
     *            抽出文字列情報を設定します
     */
    protected static final void convertExtractionString(OutputStream buf,
            Map<String, Integer> ext) throws Exception {
        byte[] b;
        String key;
        int len = ext.size();
        String[] list = new String[len];
        Iterator<String> it = ext.keySet().iterator();
        while (it.hasNext()) {
            key = it.next();
            list[ext.get(key)] = key;
        }
        byte4(buf, len);
        for (int i = 0; i < len; i++) {
            b = list[i].getBytes("UTF8");
            byte4(buf, b.length); // 長さ.
            buf.write(b, 0, b.length); // body.
        }
    }

    /**
     * 抽出文字列の取得.
     * 
     * @param b
     *            対象バイナリを設定します.
     * @param pos
     *            開始ポジションを設定します.
     * @return String[] 抽出文字列が返却されます.
     */
    protected static final String[] getExtractionString(byte[] b, int[] pos)
            throws Exception {
        int bLen;
        int len = byte4Int(b, pos);
        String[] ret = new String[len];
        for (int i = 0; i < len; i++) {
            bLen = byte4Int(b, pos);
            ret[i] = new String(b, pos[0], bLen, "UTF8");
            pos[0] += bLen;
        }
        return ret;
    }

    /**
     * 文字バイナリ変換.
     *
     * @param buf
     *            対象のバッファを設定します.
     * @param s
     *            対象の情報を設定します.
     */
    protected static final void stringBinary(Map<String, Integer> stringCode,
            OutputStream buf, String s) throws Exception {
        Integer n = stringCode.get(s);
        if (n == null) {
            stringCode.put(s, (n = stringCode.size()));
        }
        // 番号セット.
        byte4(buf, n);
    }

    /**
     * バイナリ文字変換.
     * 
     * @param stringMap
     * @param b
     *            対象のバイナリを設定します.
     * @param pos
     *            対象のポジションを設定します.
     * @return String 対象の情報が返却されます.
     */
    protected static final String byteString(String[] stringMap, byte[] b,
            int[] pos) throws Exception {
        return stringMap[byte4Int(b, pos)];
    }

    /**
     * シリアライズ変換.
     * 
     * @param buf
     *            対象のバッファを設定します.
     * @param s
     *            対象の情報を設定します.
     */
    protected static final void serialBinary(OutputStream buf, Serializable s)
            throws Exception {
        byte[] b = SerializableUtil.toBinary(s);
        byte4(buf, b.length); // 長さ.
        buf.write(b, 0, b.length); // body.
    }

    /**
     * シリアライズ変換.
     * 
     * @param b
     *            対象のバイナリを設定します.
     * @param pos
     *            対象のポジションを設定します.
     * @return Object 対象の情報が返却されます.
     */
    protected static final Object byteSerial(byte[] b, int[] pos)
            throws Exception {
        int len = byte4Int(b, pos);
        if (len == 0) {
            return null;
        }
        Object ret = SerializableUtil.toObject(b, pos[0], len);
        pos[0] += len;
        return ret;
    }

    /**
     * オブジェクトデータ変換.
     * 
     * @param buf
     *            対象のバッファを設定します.
     * @param o
     *            対象のオブジェクトを設定します.
     * @exception Exception
     *                例外.
     */
    @SuppressWarnings("rawtypes")
    protected static final void encodeObject(Map<String, Integer> stringCode,
            OutputStream buf, Object o) throws Exception {
        byte[] b;
        if (o == null) {
            head(buf, 0xff); // null.
        } else if (o instanceof String) {
            head(buf, 1); // string.
            stringBinary(stringCode, buf, (String) o);
        } else if (o instanceof Boolean) {
            head(buf, 2); // boolean.
            byte1(buf, ((Boolean) o).booleanValue() ? 1 : 0);
        } else if (o instanceof Character) {
            head(buf, 3); // char.
            byte2(buf, (Character) o);
        } else if (o instanceof Number) {

            if (o instanceof Byte) {
                head(buf, 4); // byte.
                byte1(buf, (Byte) o);
            } else if (o instanceof Short) {
                head(buf, 5); // Short.
                byte2(buf, (Short) o);
            } else if (o instanceof Integer) {
                head(buf, 6); // Integer.
                byte4(buf, (Integer) o);
            } else if (o instanceof Long) {
                head(buf, 7); // Long.
                byte8(buf, (Long) o);
            } else if (o instanceof Float) {
                head(buf, 8); // Float.
                byte4(buf, Float.floatToRawIntBits((Float) o));
            } else if (o instanceof Double) {
                head(buf, 9); // Double.
                byte8(buf, Double.doubleToRawLongBits((Double) o));
            } else if (o instanceof AtomicInteger) {
                head(buf, 10); // AtomicInteger.
                byte4(buf, ((AtomicInteger) o).get());
            } else if (o instanceof AtomicLong) {
                head(buf, 11); // AtomicLong.
                byte8(buf, ((AtomicLong) o).get());
            } else if (o instanceof BigDecimal) {
                head(buf, 12); // BigDecimal.
                // 文字変換.
                stringBinary(stringCode, buf, o.toString());
            } else if (o instanceof BigInteger) {
                head(buf, 13); // BigInteger.
                // 文字変換.
                stringBinary(stringCode, buf, o.toString());
            }
        } else if (o instanceof java.util.Date) {
            head(buf, 14); // Date.
            if (o instanceof java.sql.Date) {
                byte1(buf, 1);
            } else if (o instanceof java.sql.Time) {
                byte1(buf, 2);
            } else if (o instanceof java.sql.Timestamp) {
                byte1(buf, 3);
            } else if ("java.util.Date".equals(o.getClass().getName())) {
                byte1(buf, 4);
            }
            // 他日付オブジェクトの場合.
            else {
                byte1(buf, 5);
                // オブジェクト名をセット.
                stringBinary(stringCode, buf, o.getClass().getName());
            }
            byte8(buf, ((Date) o).getTime());
        } else if (o instanceof SerializeObject) {
            head(buf, 15); // SerializeObject.
            // オブジェクト名をセット.
            stringBinary(stringCode, buf, o.getClass().getName());
            // バイナリ変換.
            ((SerializeObject) o).toBinary(buf);
        } else if (o.getClass().isArray()) {
            if (o instanceof boolean[]) {
                head(buf, 20); // boolean配列.
                boolean[] c = (boolean[]) o;
                int len = c.length;
                byte4(buf, len); // 長さ.
                for (int i = 0; i < len; i++) {
                    byte1(buf, c[i] ? 1 : 0);
                }
            } else if (o instanceof byte[]) {
                head(buf, 21); // byte配列.
                b = (byte[]) o;
                byte4(buf, b.length); // 長さ.
                buf.write(b, 0, b.length); // body.
                b = null;
            } else if (o instanceof char[]) {
                head(buf, 22); // char配列.
                char[] c = (char[]) o;
                int len = c.length;
                byte4(buf, len); // 長さ.
                for (int i = 0; i < len; i++) {
                    byte2(buf, c[i]);
                }
            } else if (o instanceof short[]) {
                head(buf, 23); // short配列.
                short[] c = (short[]) o;
                int len = c.length;
                byte4(buf, len); // 長さ.
                for (int i = 0; i < len; i++) {
                    byte2(buf, c[i]);
                }
            } else if (o instanceof int[]) {
                head(buf, 24); // int配列.
                int[] c = (int[]) o;
                int len = c.length;
                byte4(buf, len); // 長さ.
                for (int i = 0; i < len; i++) {
                    byte4(buf, c[i]);
                }
            } else if (o instanceof long[]) {
                head(buf, 25); // long配列.
                long[] c = (long[]) o;
                int len = c.length;
                byte4(buf, len); // 長さ.
                for (int i = 0; i < len; i++) {
                    byte8(buf, c[i]);
                }
            } else if (o instanceof float[]) {
                head(buf, 26); // float配列.
                float[] c = (float[]) o;
                int len = c.length;
                byte4(buf, len); // 長さ.
                for (int i = 0; i < len; i++) {
                    byte4(buf, Float.floatToRawIntBits(c[i]));
                }
            } else if (o instanceof double[]) {
                head(buf, 27); // double配列.
                double[] c = (double[]) o;
                int len = c.length;
                byte4(buf, len); // 長さ.
                for (int i = 0; i < len; i++) {
                    byte8(buf, Double.doubleToRawLongBits(c[i]));
                }
            } else if (o instanceof String[]) {
                head(buf, 28); // String配列.
                String[] c = (String[]) o;
                int len = c.length;
                byte4(buf, len); // 長さ.
                for (int i = 0; i < len; i++) {
                    stringBinary(stringCode, buf, c[i]);
                }
            } else {
                String s = o.getClass().getName();
                // 配列オブジェクトの場合.
                if (s.startsWith("[L")) {
                    // 他配列.
                    head(buf, 50); // 他配列.

                    // オブジェクト名をセット.
                    stringBinary(stringCode, buf, s
                            .substring(2, s.length() - 1).trim());

                    // 配列データセット.
                    int len = Array.getLength(o);
                    byte4(buf, len); // 長さ.
                    for (int i = 0; i < len; i++) {
                        encodeObject(stringCode, buf, Array.get(o, i));
                    }
                }
                // 多重配列の場合.
                // 多重配列はサポート外.
                else {
                    // nullをセット.
                    head(buf, 0xff); // null.
                }
            }
        } else if (o instanceof List) {
            head(buf, 51); // Listオブジェクト.
            List lst = (List) o;
            int len = lst.size();
            byte4(buf, len); // 長さ.
            for (int i = 0; i < len; i++) {
                encodeObject(stringCode, buf, lst.get(i));
            }
        } else if (o instanceof Map) {
            head(buf, 52); // Mapオブジェクト.
            Object k;
            Map map = (Map) o;
            byte4(buf, map.size()); // 長さ.
            Iterator it = map.keySet().iterator();
            while (it.hasNext()) {
                k = it.next();
                encodeObject(stringCode, buf, k); // キー.
                encodeObject(stringCode, buf, map.get(k)); // 要素.
            }
        } else if (o instanceof Set) {
            head(buf, 53); // Setオブジェクト.
            Set set = (Set) o;
            byte4(buf, set.size()); // 長さ.
            Iterator it = set.iterator();
            while (it.hasNext()) {
                encodeObject(stringCode, buf, it.next()); // キー.
            }
        } else if (o instanceof Serializable) {
            head(buf, 60); // シリアライズオブジェクト.
            // シリアライズ.
            serialBinary(buf, (Serializable) o);
        } else {
            // それ以外のオブジェクトは変換しない.
            head(buf, 0xff); // null.
        }
    }

    /**
     * オブジェクト解析.
     * 
     * @param pos
     *            対象のポジションを設定します.
     * @param b
     *            対象のバイナリを設定します.
     * @param length
     *            対象の長さを設定します.
     * @return Object 変換されたオブジェクトが返却されます.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected static final Object decodeObject(String[] stringMap, int[] pos,
            byte[] b, int length) throws Exception {
        if (length <= pos[0]) {
            throw new IOException("指定長[" + length + "byte]を越えて、処理を行おうとしています:"
                    + pos[0]);
        }

        int i, len;
        Object ret;
        int code = byte1Int(b, pos);
        switch (code) {
        case 1: {
            // string.
            return byteString(stringMap, b, pos);
        }
        case 2: {
            // boolean.
            ret = (byte1Int(b, pos) == 1);
            return ret;
        }
        case 3: {
            // char.
            ret = (char) byte2Int(b, pos);
            return ret;
        }
        case 4: {
            // byte.
            ret = (byte) byte1Int(b, pos);
            return ret;
        }
        case 5: {
            // short.
            ret = (short) byte2Int(b, pos);
            return ret;
        }
        case 6: {
            // int.
            ret = byte4Int(b, pos);
            return ret;
        }
        case 7: {
            // long.
            ret = byte8Long(b, pos);
            return ret;
        }
        case 8: {
            // float.
            ret = Float.intBitsToFloat(byte4Int(b, pos));
            return ret;
        }
        case 9: {
            // double.
            ret = Double.longBitsToDouble(byte8Long(b, pos));
            return ret;
        }
        case 10: {
            // AtomicInteger.
            ret = new AtomicInteger(byte4Int(b, pos));
            return ret;
        }
        case 11: {
            // AtomicLong.
            ret = new AtomicLong(byte8Long(b, pos));
            return ret;
        }
        case 12: {
            // BigDecimal.
            return new BigDecimal(byteString(stringMap, b, pos));
        }
        case 13: {
            // BigInteger.
            return new BigInteger(byteString(stringMap, b, pos));
        }
        case 14: {
            // Date.
            int type = byte1Int(b, pos);
            if (type == 1) {
                ret = new java.sql.Date(byte8Long(b, pos));
            } else if (type == 2) {
                ret = new java.sql.Time(byte8Long(b, pos));
            } else if (type == 3) {
                ret = new java.sql.Timestamp(byte8Long(b, pos));
            } else if (type == 4) {
                ret = new java.util.Date(byte8Long(b, pos));
            } else {
                // 他オブジェクトの場合はオブジェクトを生成して処理.
                String cls = byteString(stringMap, b, pos);
                ret = FastReflect.newInstance(cls);
                ((java.util.Date) ret).setTime(byte8Long(b, pos));
            }
            return ret;
        }
        case 15: {
            // SerializeObject.
            String cls = byteString(stringMap, b, pos);
            ret = FastReflect.newInstance(cls);
            pos[0] = ((SerializeObject) ret).toObject(b, pos[0]);
            return ret;
        }
        case 20: {
            // boolean配列.
            len = byte4Int(b, pos);
            boolean[] lst = new boolean[len];
            for (i = 0; i < len; i++) {
                lst[i] = (byte1Int(b, pos) == 1);
            }
            return lst;
        }
        case 21: {
            // byte配列.
            len = byte4Int(b, pos);
            byte[] lst = new byte[len];
            System.arraycopy(b, pos[0], lst, 0, len);
            pos[0] += len;
            return lst;
        }
        case 22: {
            // char配列.
            len = byte4Int(b, pos);
            char[] lst = new char[len];
            for (i = 0; i < len; i++) {
                lst[i] = (char) byte2Int(b, pos);
            }
            return lst;
        }
        case 23: {
            // short配列.
            len = byte4Int(b, pos);
            short[] lst = new short[len];
            for (i = 0; i < len; i++) {
                lst[i] = (short) byte2Int(b, pos);
            }
            return lst;
        }
        case 24: {
            // int配列.
            len = byte4Int(b, pos);
            int[] lst = new int[len];
            for (i = 0; i < len; i++) {
                lst[i] = byte4Int(b, pos);
            }
            return lst;
        }
        case 25: {
            // long配列.
            len = byte4Int(b, pos);
            long[] lst = new long[len];
            for (i = 0; i < len; i++) {
                lst[i] = byte8Long(b, pos);
            }
            return lst;
        }
        case 26: {
            // float配列.
            len = byte4Int(b, pos);
            float[] lst = new float[len];
            for (i = 0; i < len; i++) {
                lst[i] = Float.intBitsToFloat(byte4Int(b, pos));
            }
            return lst;
        }
        case 27: {
            // double配列.
            len = byte4Int(b, pos);
            double[] lst = new double[len];
            for (i = 0; i < len; i++) {
                lst[i] = Double.longBitsToDouble(byte8Long(b, pos));
            }
            return lst;
        }
        case 28: {
            // String配列.
            len = byte4Int(b, pos);
            String[] lst = new String[len];
            for (i = 0; i < len; i++) {
                lst[i] = byteString(stringMap, b, pos);
            }
            return lst;
        }
        case 50: {
            // 配列.
            String cls = byteString(stringMap, b, pos);
            len = byte4Int(b, pos);
            Object lst = Array.newInstance(FastReflect.getClass(cls), len);
            for (i = 0; i < len; i++) {
                Array.set(lst, i, decodeObject(stringMap, pos, b, length));
            }
            return lst;
        }
        case 51: {
            // List.
            len = byte4Int(b, pos);
            List lst = new ArrayList();
            for (i = 0; i < len; i++) {
                lst.add(decodeObject(stringMap, pos, b, length));
            }
            return lst;
        }
        case 52: {
            // Map.
            len = byte4Int(b, pos);
            Map map = new HashMap();
            for (i = 0; i < len; i++) {
                map.put(decodeObject(stringMap, pos, b, length),
                        decodeObject(stringMap, pos, b, length));
            }
            return map;
        }
        case 53: {
            // Set.
            len = byte4Int(b, pos);
            Set set = new HashSet();
            for (i = 0; i < len; i++) {
                set.add(decodeObject(stringMap, pos, b, length));
            }
            return set;
        }
        case 60: {
            // シリアライズ可能オブジェクト.
            return byteSerial(b, pos);
        }
        case 0xff: {
            // NULL.
            return null;
        }
        }
        throw new IOException("不明なタイプ[" + code + "]を検出しました");
    }

    /** ヘッダ文字セット. **/
    private static final void head(OutputStream buf, int n) throws Exception {
        byte1(buf, n);
    }

}
