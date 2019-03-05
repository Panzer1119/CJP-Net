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
import java.io.InputStream;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class InputStreamManager extends InputStream {
    
    public static final int DEFAULT_BUFFER_SIZE = 8192; //TODO Clean this up
    
    protected final InputStream inputStream;
    protected final BiMap<Byte, EndableInputStream> endableInputStreams = Maps.synchronizedBiMap(HashBiMap.create());
    //protected final Map<Byte, int[]> buffers = new ConcurrentHashMap<>(); //TODO Clean this up
    protected final Map<Byte, Queue<Integer>> queues = new ConcurrentHashMap<>();
    
    public InputStreamManager(InputStream inputStream) {
        this.inputStream = inputStream;
    }
    
    public InputStream getInputStream() {
        return inputStream;
    }
    
    public BiMap<Byte, EndableInputStream> getInputStreams() {
        return endableInputStreams;
    }
    
    public EndableInputStream getEndableInputStream(byte id) {
        return endableInputStreams.get(id);
    }
    
    public byte getId(EndableInputStream inputStream) {
        return endableInputStreams.inverse().get(inputStream);
    }
    
    public byte getLowestId() {
        return endableInputStreams.keySet().stream().sorted().findFirst().orElse(Byte.MAX_VALUE);
    }
    
    public byte getHighestId() {
        return endableInputStreams.keySet().stream().sorted().skip(endableInputStreams.size() - 1).findFirst().orElse(Byte.MIN_VALUE);
    }
    
    protected synchronized byte getNextId() {
        byte id = Byte.MIN_VALUE;
        while (endableInputStreams.containsKey(id)) {
            if (id == Byte.MAX_VALUE) {
                throw new ArrayIndexOutOfBoundsException("There is no id left for another " + InputStream.class.getSimpleName());
            }
            id++;
        }
        return id;
    }
    
    @Override
    public synchronized int read() throws IOException {
        return inputStream.read();
    }
    
    @Override
    public synchronized void close() throws IOException {
        inputStream.close();
    }
    
    protected synchronized byte readIntern() throws IOException {
        final byte id = (byte) (read() & 0xFF); //TODO Is this int to byte conversion working?
        //final byte b = (byte) (inputStream.read() & 0xFF); //TODO Is this int to byte conversion working? //TODO Clean this up
        final int b = read();
        final Queue<Integer> queue = queues.get(id);
        if (queue != null) {
            queue.add(b);
        } else {
            //TODO What if we got data for a no longer/never existing InputStream?
        }
        return id;
    }
    
    protected synchronized int read(byte id) throws IOException {
        if (!endableInputStreams.containsKey(id)) {
            throw new StreamClosedException("There is no " + EndableInputStream.class.getSimpleName() + " with the id " + id);
        }
        final Queue<Integer> queue = queues.get(id);
        while (queue.isEmpty()) {
            readIntern();
        }
        /* //TODO Clean this up
        int[] buffer = null;
        while ((buffer = buffers.get(id)) == null) {
            //FIXME We need a new call to this call method, to fill the buffer??
            try {
                Thread.sleep(10);
            } catch (Exception ex) {
                //Logger.handleError(ex);
            }
        }
        final int b = buffer[0];
        System.arraycopy(buffer, 1, buffer, 0, buffer.length - 1);
        buffer = Arrays.copyOf(buffer, buffer.length - 1);
        buffers.put(id, buffer);
        */
        return queue.remove();
    }
    
    public synchronized EndableInputStream createEndableInputStream() {
        return createEndableInputStream(getNextId());
    }
    
    public synchronized EndableInputStream createEndableInputStream(byte id) {
        final InputStream inputStream = new InputStream() {
            @Override
            public int read() throws IOException {
                return InputStreamManager.this.read(id);
            }
            
            @Override
            public void close() throws IOException {
                InputStreamManager.this.endableInputStreams.remove(id);
            }
        };
        final EndableInputStream endableInputStream = new EndableInputStream(inputStream);
        endableInputStreams.put(id, endableInputStream);
        queues.put(id, new ConcurrentLinkedQueue<>());
        return endableInputStream;
    }
    
}
