package objectpack;

import java.io.OutputStream;

/**
 * バイナリ変換用オブジェクト. このオブジェクトを継承して、所定のメソッド実装した場合、
 * SerializeConverterでのオブジェクトバイナリ化において、 処理速度や容量がSerializableよりも少なくなります.
 */
public interface SerializeObject {

    /**
     * バイナリ出力. この処理は、SerializeConverterのencodeBinaryメソッドから 呼び出されます.
     * 
     * @param buf
     *            バイナリを格納するバッファ.
     * @exception Exception
     *                例外.
     */
    public void toBinary(OutputStream buf) throws Exception;

    /**
     * バイナリ入力.
     * 
     * @param binary
     *            対象のバイナリが設定されます.
     * @param offset
     *            対象のオフセット値が設定されます.
     * @return int この処理で利用したバイナリオフセット値を返却します.
     * @exception Exception
     *                例外.
     */
    public int toObject(byte[] binary, int offset) throws Exception;

}
