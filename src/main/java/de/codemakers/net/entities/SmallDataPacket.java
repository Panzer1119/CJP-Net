/*
 *     Copyright 2018 Paul Hagedorn (Panzer1119)
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

import de.codemakers.base.exceptions.CJPRuntimeException;
import de.codemakers.base.multiplets.Doublet;
import de.codemakers.base.util.ConvertUtil;
import de.codemakers.base.util.interfaces.Hasher;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

public class SmallDataPacket implements Serializable {
    
    public static final int ID_BYTES = Short.BYTES;
    public static final int LENGTH_BYTES = 1;
    public static final int MAX_DATA_BYTES = Long.SIZE * 2;
    public static final int MAX_HASH_BYTES = Long.BYTES;
    
    private final byte[] bytes;
    
    public SmallDataPacket(byte[] bytes) {
        this.bytes = bytes;
    }
    
    public SmallDataPacket(int id, byte[] data, Hasher hasher) {
        this(id, data, hasher == null ? null : hasher.hashWithoutException(data));
    }
    
    public SmallDataPacket(int id, byte[] data, byte[] hash) {
        this(id, data == null ? -1 : data.length, data, hash == null ? -1 : hash.length, hash);
    }
    
    public SmallDataPacket(int id, int length, byte[] data, int hash_length, byte[] hash) {
        if (length > MAX_DATA_BYTES || (data != null && data.length > MAX_DATA_BYTES)) {
            throw new IllegalArgumentException("length is too big");
        }
        if (hash_length > MAX_HASH_BYTES || (hash != null && hash.length > MAX_HASH_BYTES)) {
            throw new IllegalArgumentException("hash_length is too big");
        }
        bytes = new byte[ID_BYTES + LENGTH_BYTES + Math.max(0, length) + LENGTH_BYTES + Math.max(0, hash_length)];
        System.arraycopy(ConvertUtil.shortToByteArray((short) id), 0, bytes, 0, ID_BYTES);
        bytes[ID_BYTES] = (byte) (length - 1);
        if (data != null) {
            System.arraycopy(data, 0, bytes, ID_BYTES + LENGTH_BYTES, length);
        }
        bytes[ID_BYTES + LENGTH_BYTES + length] = (byte) (hash_length - 1);
        if (hash != null) {
            System.arraycopy(hash, 0, bytes, ID_BYTES + LENGTH_BYTES + length + LENGTH_BYTES, hash_length);
        }
    }
    
    public static Doublet<SmallDataPacket[], byte[]> bytesToSmallDataPacketsAndHash(byte[] data) {
        return bytesToSmallDataPacketsAndHash(data, null);
    }
    
    public static Doublet<SmallDataPacket[], byte[]> bytesToSmallDataPacketsAndHash(byte[] data, Hasher hasher) {
        return bytesToSmallDataPacketsAndHash(data, hasher, hasher);
    }
    
    public static Doublet<SmallDataPacket[], byte[]> bytesToSmallDataPacketsAndHash(byte[] data, Hasher hasher_packets, Hasher hasher_data) {
        return new Doublet<>(bytesToSmallDataPackets(data, hasher_packets), hasher_data == null ? null : hasher_data.hashWithoutException(data));
    }
    
    public static SmallDataPacket[] bytesToSmallDataPackets(byte[] data) {
        return bytesToSmallDataPackets(data, null);
    }
    
    public static SmallDataPacket[] bytesToSmallDataPackets(byte[] data, Hasher hasher) {
        final SmallDataPacket[] smallDataPackets = new SmallDataPacket[(int) Math.ceil(data.length * 1.0 / MAX_DATA_BYTES)];
        System.out.println("smallDataPackets.length=" + smallDataPackets.length);
        byte[] temp = new byte[MAX_DATA_BYTES];
        for (short i = 0; i < smallDataPackets.length; i++) {
            if (i == smallDataPackets.length - 1) {
                temp = new byte[data.length % MAX_DATA_BYTES];
            }
            System.arraycopy(data, i * MAX_DATA_BYTES, temp, 0, temp.length);
            smallDataPackets[i] = new SmallDataPacket(i, temp, hasher);
        }
        return smallDataPackets;
    }
    
    public static byte[] smallDataPacketsToBytes(Doublet<SmallDataPacket[], byte[]> smallDataPacketsAndHash) {
        return smallDataPacketsToBytes(smallDataPacketsAndHash, null);
    }
    
    public static byte[] smallDataPacketsToBytes(Doublet<SmallDataPacket[], byte[]> smallDataPacketsAndHash, Hasher hasher) {
        return smallDataPacketsToBytes(smallDataPacketsAndHash, hasher, hasher);
    }
    
    public static byte[] smallDataPacketsToBytes(Doublet<SmallDataPacket[], byte[]> smallDataPacketsAndHash, Hasher hasher_packets, Hasher hasher_data) {
        final byte[] data = smallDataPacketsToBytes(smallDataPacketsAndHash.getA(), hasher_packets);
        if (!Arrays.equals(hasher_data == null ? null : hasher_data.hashWithoutException(data), smallDataPacketsAndHash.getB())) {
            throw new CJPRuntimeException("Hash does not match");
        }
        return data;
    }
    
    public static byte[] smallDataPacketsToBytes(SmallDataPacket[] smallDataPackets) {
        return smallDataPacketsToBytes(smallDataPackets, null);
    }
    
    public static byte[] smallDataPacketsToBytes(SmallDataPacket[] smallDataPackets, Hasher hasher) {
        Objects.requireNonNull(smallDataPackets);
        if (smallDataPackets.length < 1) {
            return null;
        }
        final byte[] data = new byte[MAX_DATA_BYTES * (smallDataPackets.length - 1) + smallDataPackets[smallDataPackets.length - 1].getLength()];
        for (int i = 0; i < smallDataPackets.length; i++) {
            final SmallDataPacket smallDataPacket = smallDataPackets[i];
            if (!Arrays.equals(hasher == null ? null : hasher.hashWithoutException(smallDataPacket.getData()), smallDataPacket.getHash())) {
                throw new CJPRuntimeException("Hash does not match");
            }
            System.arraycopy(smallDataPacket.getData(), 0, data, i * MAX_DATA_BYTES, smallDataPacket.getLength());
        }
        return data;
    }
    
    public final byte[] getBytes() {
        return bytes;
    }
    
    public final short getId() {
        return ConvertUtil.byteArrayToShort(Arrays.copyOf(bytes, ID_BYTES));
    }
    
    public final int getLength() {
        return (int) bytes[ID_BYTES] + 1;
    }
    
    public final byte[] getData() {
        if (getLength() < 0) {
            return null;
        }
        final byte[] data = new byte[getLength()];
        System.arraycopy(bytes, ID_BYTES + LENGTH_BYTES, data, 0, getLength());
        return data;
    }
    
    public final byte[] getDataFullLength() {
        if (getLength() < 0) {
            return null;
        }
        return Arrays.copyOf(getData(), MAX_DATA_BYTES);
    }
    
    public final int getHashLength() {
        return (int) bytes[ID_BYTES + LENGTH_BYTES + getLength()] + 1;
    }
    
    public final byte[] getHash() {
        if (getHashLength() < 0) {
            return null;
        }
        final byte[] hash = new byte[getHashLength()];
        System.arraycopy(bytes, ID_BYTES + LENGTH_BYTES + getLength() + LENGTH_BYTES, hash, 0, getHashLength());
        return hash;
    }
    
    public final byte[] getHashFullLength() {
        if (getHashLength() < 0) {
            return null;
        }
        return Arrays.copyOf(getHash(), MAX_HASH_BYTES);
    }
    
    @Override
    public String toString() {
        return "SmallDataPacket{" + "bytes=" + Arrays.toString(bytes) + '}';
    }
    
}
