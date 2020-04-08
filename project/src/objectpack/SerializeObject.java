package objectpack;

/**
 * バイナリ変換用オブジェクト. このオブジェクトを継承して、所定のメソッド実装した場合、
 * SerialzableCoreでのオブジェクトバイナリ化において、 処理速度や容量がSerializableよりも少なくなります.
 */
public interface SerializeObject {

	/**
	 * シリアライズ処理.
	 * 
	 * @return Object[] シリアライズするためのオブジェクト配列を設定します.
	 * @exception Exception 例外.
	 */
	public Object[] toSerialize() throws Exception;

	/**
	 * シリアライズされたオブジェクト復元.
	 * 
	 * @param values toSerializeで返却した内容が設定されます.
	 * @exception Exception 例外.
	 */
	public void toObject(Object[] values) throws Exception;

}
