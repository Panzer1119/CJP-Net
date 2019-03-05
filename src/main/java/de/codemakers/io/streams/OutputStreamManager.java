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

package de.codemakers.io.streams;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import de.codemakers.io.streams.exceptions.StreamClosedException;

import java.io.IOException;
import java.io.OutputStream;

public class OutputStreamManager {
    
    protected final OutputStream outputStream;
    protected final BiMap<Byte, OutputStream> outputStreams = Maps.synchronizedBiMap(HashBiMap.create());
    
    public OutputStreamManager(OutputStream outputStream) {
        this.outputStream = outputStream;
    }
    
    public OutputStream getOutputStream() {
        return outputStream;
    }
    
    public BiMap<Byte, OutputStream> getOutputStreams() {
        return outputStreams;
    }
    
    public OutputStream getOutputStream(byte id) {
        return outputStreams.get(id);
    }
    
    public byte getId(OutputStream outputStream) {
        return outputStreams.inverse().get(outputStream);
    }
    
    public byte getLowestId() {
        return outputStreams.keySet().stream().sorted().findFirst().orElse(Byte.MAX_VALUE);
    }
    
    public byte getHighestId() {
        return outputStreams.keySet().stream().sorted().skip(outputStreams.size() - 1).findFirst().orElse(Byte.MIN_VALUE);
    }
    
    protected synchronized byte getNextId() {
        byte id = Byte.MIN_VALUE;
        while (outputStreams.containsKey(id)) {
            if (id == Byte.MAX_VALUE) {
                throw new ArrayIndexOutOfBoundsException("There is no id left for another " + OutputStream.class.getSimpleName());
            }
            id++;
        }
        return id;
    }
    
    protected synchronized void write(byte id, int b) throws IOException {
        if (!outputStreams.containsKey(id)) {
            throw new StreamClosedException("There is no " + OutputStream.class.getSimpleName() + " with the id " + id);
        }
        outputStreams.get(id).write(b);
    }
    
    public synchronized OutputStream createOutputStream() {
        final byte id = getNextId();
        final OutputStream outputStream = new OutputStream() {
            @Override
            public synchronized void write(int b) throws IOException {
                OutputStreamManager.this.write(id, b);
            }
    
            @Override
            public synchronized void flush() throws IOException {
                OutputStreamManager.this.outputStream.flush();
            }
    
            @Override
            public synchronized void close() throws IOException {
                //TODO Hier das machen mit dem NULL_BYTE senden und das escapen und sowas, aber dafür ne extra klasse in CJP-Base machen!
                OutputStreamManager.this.outputStreams.remove(id);
            }
        };
        outputStreams.put(id, outputStream);
        return outputStream;
    }
    
}
