/*
 *     Copyright 2018 - 2019 Paul Hagedorn (Panzer1119)
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package de.codemakers.net.entities;

import de.codemakers.base.multiplets.Doublet;
import de.codemakers.base.util.HashUtil;

import java.util.Arrays;

public class SmallDataPacketTest {
    
    public static final void main(String[] args) throws Exception {
        final String string_1 = "Test 1";
        final byte[] data_1 = string_1.getBytes();
        System.out.println("data=" + Arrays.toString(data_1));
        System.out.println("data.length=" + data_1.length);
        final byte[] hash = HashUtil.XX_HASHER_32_FASTEST.hashWithoutException(data_1);
        System.out.println("hash=" + Arrays.toString(hash));
        System.out.println("hash.length=" + hash.length);
        final SmallDataPacket smallDataPacket = new SmallDataPacket(5, data_1, hash);
        //final SmallDataPacket smallDataPacket = new SmallDataPacket(Long.MIN_VALUE, data, (byte[]) null);
        System.out.println("smallDataPacket=" + smallDataPacket);
        System.out.println("smallDataPacket.getLength()    =" + smallDataPacket.getLength());
        System.out.println("smallDataPacket.getHashLength()=" + smallDataPacket.getHashLength());
        final byte[] temp = smallDataPacket.getBytes();
        System.out.println("temp=" + Arrays.toString(temp));
        System.out.println("data_s_full=" + Arrays.toString(smallDataPacket.getDataFullLength()));
        System.out.println("data_s     =" + Arrays.toString(smallDataPacket.getData()));
        System.out.println("hash_s_full=" + Arrays.toString(smallDataPacket.getHashFullLength()));
        System.out.println("hash_s     =" + Arrays.toString(smallDataPacket.getHash()));
        final String string_2 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789ABCDEFGH";
        final byte[] data_2 = string_2.getBytes();
        System.out.println("hash32=" + Arrays.toString(HashUtil.XX_HASHER_32_FASTEST.hashWithoutException(data_2)));
        System.out.println("hash64=" + Arrays.toString(HashUtil.XX_HASHER_64.hashWithoutException(data_2)));
        System.out.println(String.format("data_2.length=%d, MAX_DATA_BYTES=%d", data_2.length, SmallDataPacket.MAX_DATA_BYTES));
        final Doublet<SmallDataPacket[], byte[]> smallDataPacketsAndHash = SmallDataPacket.bytesToSmallDataPacketsAndHash(data_2, HashUtil.XX_HASHER_32_FASTEST, HashUtil.XX_HASHER_64);
        System.out.println("smallDataPackets.length=" + smallDataPacketsAndHash.getA().length);
        System.out.println("smallDataPackets=" + Arrays.toString(smallDataPacketsAndHash.getA()));
        //smallDataPacketsAndHash.getB()[0] = 0; //Only for testing the hash check
        System.out.println("Hash  =" + Arrays.toString(smallDataPacketsAndHash.getB()));
        final byte[] data_2r = SmallDataPacket.smallDataPacketsToBytes(smallDataPacketsAndHash, HashUtil.XX_HASHER_32_FASTEST, HashUtil.XX_HASHER_64);
        System.out.println("data_2 =" + Arrays.toString(data_2));
        System.out.println("data_2r=" + Arrays.toString(data_2r));
        System.out.println("data_2 =" + new String(data_2));
        System.out.println("data_2r=" + new String(data_2r));
    }
    
}
