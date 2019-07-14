/*
 *  Copyright 2011 Tor-Einar Jarnbjo
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package objectpack.jsnappy;

/**
 * Reusable byte array buffer, which can be used for input/output in the Snappy
 * encoder oder decoder.
 * 
 * @author Tor-Einar Jarnbjo
 * @since 1.0
 */
public class JSnappyBuffer {

    private byte[] data;
    private int length;

    /**
     * Creates an unitialized buffer.
     */
    public JSnappyBuffer() {
    }

    /**
     * Creates an initialized buffer with the specified capacity.
     * 
     * @param capacity
     *            initial buffer length in bytes
     */
    public JSnappyBuffer(int capacity) {
        data = new byte[capacity];
    }

    /**
     * オブジェクトリセット.
     * 
     * @param length
     *            リセットするバイナリ長を設定します.
     */
    public void reset(int length) {
        if (data == null || data.length != length) {
            data = new byte[length];
        }
    }

    /**
     * オブジェクト再利用.
     * 
     * @param length
     *            再利用時のバイナリ長を設定します.
     */
    public void clear(int length) {
        if (data == null || data.length < length) {
            data = new byte[length];
        }
    }

    /**
     * Returns the byte array used as a backing store for this buffer. Note that
     * invoking ensureCapacity can cause the backing array to be replaced.
     * 
     * @return
     */
    public byte[] getData() {
        return data;
    }

    /**
     * Returns the length of this buffer. The backing array should contain valid
     * data from index 0 to length - 1.
     * 
     * @return
     */
    public int getLength() {
        return length;
    }

    /**
     * Sets the length of this buffer. The backing array should contain valid
     * data from index 0 to length - 1.
     * 
     * @param length
     *            buffer length
     */
    public void setLength(int length) {
        this.length = length;
    }

    /**
     * Returns a copy of the buffers internal array in a newly allocated byte
     * array with the buffer's length.
     * 
     * @return
     */
    public byte[] toByteArray() {
        byte[] res = new byte[length];
        System.arraycopy(data, 0, res, 0, length);
        return res;
    }

}
