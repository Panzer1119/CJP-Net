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

public class TunnelInputStream extends InputStream {
    
    protected final InputStream inputStream;
    protected final BiMap<Byte, EndableInputStream> inputStreams = Maps.synchronizedBiMap(HashBiMap.create());
    protected final Map<Byte, Queue<Integer>> queues = new ConcurrentHashMap<>();
    
    public TunnelInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
        //this.inputStream = new BufferedInputStream(inputStream);
    }
    
    public InputStream getInputStream() {
        return inputStream;
    }
    
    public BiMap<Byte, EndableInputStream> getInputStreams() {
        return inputStreams;
    }
    
    public EndableInputStream getInputStream(byte id) {
        return inputStreams.get(id);
    }
    
    public byte getId(InputStream inputStream) {
        return inputStreams.inverse().get(inputStream);
    }
    
    public byte getLowestId() {
        return inputStreams.keySet().stream().sorted().findFirst().orElse(Byte.MAX_VALUE);
    }
    
    public byte getHighestId() {
        return inputStreams.keySet().stream().sorted().skip(inputStreams.size() - 1).findFirst().orElse(Byte.MIN_VALUE);
    }
    
    protected synchronized byte getNextId() {
        byte id = Byte.MIN_VALUE;
        while (inputStreams.containsKey(id)) {
            if (id == Byte.MAX_VALUE) {
                throw new ArrayIndexOutOfBoundsException("There is no id left for another " + EndableInputStream.class.getSimpleName());
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
        final byte id = (byte) (read() & 0xFF);
        final int b = read();
        Queue<Integer> queue = queues.get(id);
        if (queue != null) {
            queue.add(b);
        } else {
            //TODO What if we got data for a no longer/never existing InputStream? //Create a new Queue?
            queue = new ConcurrentLinkedQueue<>();
            queues.put(id, queue);
            queue.add(b);
            //Logger.logDebug(String.format("Created new %s for \"%s\" and added \"%d\" to it", queue.getClass().getSimpleName(), id, b)); //FIXME Remove debug code
        }
        //Logger.logDebug(String.format("ADDING   id=%s, queue.size()=%d", id, queue.size())); //FIXME Remove debug code
        return id;
    }
    
    protected synchronized int read(byte id) throws IOException {
        if (!inputStreams.containsKey(id)) {
            throw new StreamClosedException("There is no " + EndableInputStream.class.getSimpleName() + " with the id " + id);
        }
        final Queue<Integer> queue = queues.get(id);
        while (queue.isEmpty()) {
            readIntern();
        }
        //Logger.logDebug(String.format("REMOVING id=%s, queue.size()=%d", id, queue.size())); //FIXME Remove debug code
        return queue.remove();
    }
    
    protected synchronized int available(byte id) throws IOException {
        final Queue<Integer> queue = queues.get(id);
        if (queue == null) {
            return -1;
        }
        return queue.size();
    }
    
    public synchronized EndableInputStream createInputStream() {
        return createInputStream(getNextId());
    }
    
    public synchronized EndableInputStream createInputStream(byte id) {
        if (inputStreams.containsKey(id)) {
            return inputStreams.get(id);
        }
        final InputStream inputStream = new InputStream() {
            @Override
            public synchronized int read() throws IOException {
                return TunnelInputStream.this.read(id);
            }
            
            @Override
            public synchronized int available() throws IOException {
                return TunnelInputStream.this.available(id);
            }
            
            @Override
            public synchronized void close() throws IOException {
                TunnelInputStream.this.inputStreams.remove(id);
            }
        };
        final EndableInputStream endableInputStream = new EndableInputStream(inputStream);
        inputStreams.put(id, endableInputStream);
        queues.computeIfAbsent(id, (id_) -> new ConcurrentLinkedQueue<>());
        return endableInputStream;
    }
    
}