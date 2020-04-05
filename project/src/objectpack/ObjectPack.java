package objectpack;

import java.io.ByteArrayInputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * オブジェクトパック.
 */
public final class ObjectPack {
	protected ObjectPack() {
	}

	/**
	 * オブジェクトをバイナリでパック.
	 * 
	 * @param o 対象のオブジェクトを設定します.
	 * @return byte[] パック化されたバイナリが返却されます.
	 */
	public static final byte[] packB(Object o) throws Exception {
		return packB(o, false);
	}

	/**
	 * バイナリから、オブジェクトをアンパック.
	 * 
	 * @param b 対象の文字列を設定します.
	 * @return Object 変換されたオブジェクトが返却されます.
	 */
	public static final Object unpackB(byte[] b) throws Exception {
		return unpackB(b, false);
	}

	/**
	 * オブジェクトを文字列でパック.
	 * 
	 * @param o 対象のオブジェクトを設定します.
	 * @return String パック化されたオブジェクトが返却されます.
	 */
	public static final String pack(Object o) throws Exception {
		return pack(o);
	}

	/**
	 * 文字列からオブジェクトをアンパック.
	 * 
	 * @param o 対象の文字列を設定します.
	 * @return Object 変換されたオブジェクトが返却されます.
	 */
	public static final Object unpack(String o) throws Exception {
		return unpack(o, false);
	}

	/**
	 * オブジェクトをバイナリでパック.
	 * 
	 * @param o    対象のオブジェクトを設定します.
	 * @param gzip [true]の場合は、GZIP圧縮、[false]の場合は、Snappy圧縮、 [null]の場合は圧縮なしで処理します.
	 * @return byte[] パック化されたバイナリが返却されます.
	 */
	public static final byte[] packB(Object o, Boolean gzip) throws Exception {
		byte[] bin = SerializableCore.encodeBinary(o);
		if (gzip == null) {
			return bin;
		} else if (gzip) {
			ByteArrayIO ob = new ByteArrayIO();
			GZIPOutputStream os = new GZIPOutputStream(ob);
			os.write(bin);
			os.finish();
			os.flush();
			byte[] ret = ob.toByteArray();
			os.close();
			os = null;
			ob = null;
			return ret;
		} else {
			int capacity = JSnappy.calcMaxCompressLength(bin.length);
			JSnappyBuffer buf = new JSnappyBuffer(capacity);
			JSnappy.compress(bin, buf);
			return buf.toByteArray();
		}
	}

	/**
	 * バイナリからオブジェクトをアンパック.
	 * 
	 * @param b    対象の文字列を設定します.
	 * @param gzip [true]の場合は、GZIP圧縮、[false]の場合は、Snappy圧縮、 [null]の場合は圧縮なしで処理します.
	 * @return Object 変換されたオブジェクトが返却されます.
	 */
	public static final Object unpackB(byte[] b, Boolean gzip) throws Exception {
		if (gzip != null) {
			if (gzip) {
				int len;
				byte[] bin = new byte[4096];
				ByteArrayIO io = new ByteArrayIO();
				ByteArrayInputStream bi = new ByteArrayInputStream(b);
				GZIPInputStream in = new GZIPInputStream(bi);
				while ((len = in.read(bin)) != -1) {
					io.write(bin, 0, len);
				}
				bin = null;
				in.close();
				b = io.toByteArray();
				io.close();
				in = null;
				io = null;
			} else {
				JSnappyBuffer buf = new JSnappyBuffer(JSnappy.decompressLength(b, 0));
				JSnappy.decompress(b, buf);
				b = buf.toByteArray();
			}
		}
		return SerializableCore.decodeBinary(b);
	}

	/**
	 * オブジェクトを文字列にパック.
	 * 
	 * @param o    対象のオブジェクトを設定します.
	 * @param gzip [true]の場合は、GZIP圧縮、[false]の場合は、Snappy圧縮、 [null]の場合は圧縮なしで処理します.
	 * @return String パック化されたオブジェクトが返却されます.
	 */
	public static final String pack(Object o, Boolean gzip) throws Exception {
		return Base64.encode(packB(o, gzip));
	}

	/**
	 * 文字列からオブジェクトをアンパック.
	 * 
	 * @param o    + 対象の文字列を設定します.
	 * @param gzip [true]の場合は、GZIP圧縮、[false]の場合は、Snappy圧縮、 [null]の場合は圧縮なしで処理します.
	 * @return Object 変換されたオブジェクトが返却されます.
	 */
	public static final Object unpack(String o, Boolean gzip) throws Exception {
		return unpackB(Base64.decode(o), gzip);
	}
}
