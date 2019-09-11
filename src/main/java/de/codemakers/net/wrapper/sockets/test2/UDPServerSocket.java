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

package de.codemakers.net.wrapper.sockets.test2;

import de.codemakers.base.Standard;
import de.codemakers.base.logger.Logger;
import de.codemakers.base.util.interfaces.Closeable;
import de.codemakers.base.util.interfaces.Resettable;
import de.codemakers.base.util.interfaces.Startable;
import de.codemakers.base.util.interfaces.Stoppable;
import de.codemakers.io.streams.PipedStream;
import de.codemakers.net.exceptions.NetRuntimeException;

import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class UDPServerSocket implements Closeable, Resettable, Startable, Stoppable {
    
    public static final int DEFAULT_BUFFER_SIZE = 128;
    
    protected final int port;
    protected final AtomicBoolean stopped = new AtomicBoolean(false);
    protected final Map<InetAddress, PipedStream> pipedStreams = new ConcurrentHashMap<>();
    protected final AtomicBoolean awaitingConnection = new AtomicBoolean(false);
    protected final AtomicBoolean splitDataPerSource = new AtomicBoolean(true);
    protected int bufferSize;
    protected DatagramSocket datagramSocket = null;
    protected Thread thread = null;
    protected InetAddress inetAddress_temp = null;
    protected PipedStream pipedStream = new PipedStream();
    
    public UDPServerSocket(int port, int bufferSize) {
        this.port = port;
        this.bufferSize = bufferSize;
    }
    
    public UDPServerSocket(int port) {
        this(port, DEFAULT_BUFFER_SIZE);
    }
    
    public UDPServerSocket(DatagramSocket datagramSocket, int bufferSize) {
        this.port = datagramSocket.getLocalPort();
        this.datagramSocket = datagramSocket;
        this.bufferSize = bufferSize;
    }
    
    public UDPServerSocket(DatagramSocket datagramSocket) {
        this(datagramSocket, DEFAULT_BUFFER_SIZE);
    }
    
    public boolean allowNewConnection(InetAddress inetAddress, int port) {
        return true;
    }
    
    public InetAddress accept() {
        return accept(-1, null);
    }
    
    public InetAddress accept(long timeout, TimeUnit timeUnit) {
        if (awaitingConnection.get()) {
            throw new NetRuntimeException("Already awaiting a new connection");
        }
        if (!splitDataPerSource.get()) {
            throw new NetRuntimeException("The data is combined in one Stream");
        }
        awaitingConnection.set(true);
        final long timeout_ = (timeUnit == null ? -1 : (timeout == -1 ? -1 : timeUnit.toMillis(timeout)));
        final long started = System.currentTimeMillis();
        while (awaitingConnection.get() && inetAddress_temp == null && (timeout_ == -1 || (System.currentTimeMillis() - started) < timeout_)) {
            Standard.silentError(() -> Thread.sleep(100));
            if (!splitDataPerSource.get()) {
                throw new NetRuntimeException("The data is combined in one Stream");
            }
        }
        awaitingConnection.set(false);
        final InetAddress temp = inetAddress_temp;
        inetAddress_temp = null;
        return temp;
    }
    
    @Override
    public void closeIntern() throws Exception {
        datagramSocket.close();
    }
    
    @Override
    public boolean reset() throws Exception {
        pipedStreams.values().forEach(PipedStream::resetWithoutException);
        pipedStream.resetWithoutException();
        if (datagramSocket == null) {
            datagramSocket = new DatagramSocket(port);
        }
        return true;
    }
    
    @Override
    public boolean start() throws Exception {
        if (thread != null) {
            return false;
        }
        stopped.set(false);
        resetWithoutException();
        thread = new Thread(() -> {
            try {
                final byte[] buffer = new byte[bufferSize];
                int read = 0;
                while (!stopped.get() && read != -1) {
                    final DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
                    datagramSocket.receive(datagramPacket);
                    read = datagramPacket.getLength();
                    final InetAddress inetAddress = datagramPacket.getAddress();
                    final int port = datagramPacket.getPort();
                    PipedStream pipedStream = null;
                    if (splitDataPerSource.get()) {
                        pipedStream = pipedStreams.computeIfAbsent(inetAddress, (inetAddress_) -> {
                            if (awaitingConnection.get()) {
                                inetAddress_temp = inetAddress;
                                awaitingConnection.set(false);
                            }
                            if (allowNewConnection(inetAddress, port)) {
                                return new PipedStream();
                            }
                            return null;
                        });
                    } else {
                        pipedStream = this.pipedStream;
                    }
                    if (pipedStream == null) {
                        continue;
                    }
                    pipedStream.getOutputStream().write(buffer, 0, read);
                    pipedStream.getOutputStream().flush(); //TODO When should this be done?
                }
            } catch (Exception ex) {
                if (!(ex instanceof InterruptedException) && !(ex instanceof SocketException)) {
                    Logger.handleError(ex);
                }
            }
        });
        thread.start();
        return thread.isAlive();
    }
    
    @Override
    public boolean stop() throws Exception {
        if (thread == null) {
            return false;
        }
        stopped.set(true);
        awaitingConnection.set(false);
        //datagramSocket.disconnect();
        Standard.silentError(() -> Thread.sleep(100));
        thread.interrupt();
        thread = null;
        return true;
    }
    
    public int getPort() {
        return port;
    }
    
    public int getBufferSize() {
        return bufferSize;
    }
    
    public DatagramSocket getDatagramSocket() {
        return datagramSocket;
    }
    
    public UDPServerSocket setDatagramSocket(DatagramSocket datagramSocket) {
        this.datagramSocket = datagramSocket;
        return this;
    }
    
    public boolean isDataSplitPerSource() {
        return splitDataPerSource.get();
    }
    
    public UDPServerSocket setSplitDataPerSource(boolean splitDataPerSource) {
        this.splitDataPerSource.set(splitDataPerSource);
        return this;
    }
    
    public Map<InetAddress, InputStream> getInputStreamsMapped() {
        if (!splitDataPerSource.get()) {
            throw new NetRuntimeException("The data is combined in one Stream");
        }
        return pipedStreams.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, (entry) -> entry.getValue().getInputStream()));
    }
    
    public List<InputStream> getInputStreams() {
        if (!splitDataPerSource.get()) {
            throw new NetRuntimeException("The data is combined in one Stream");
        }
        return pipedStreams.values().stream().map(PipedStream::getInputStream).collect(Collectors.toList());
    }
    
    public InputStream getInputStream(InetAddress inetAddress) {
        if (!splitDataPerSource.get()) {
            throw new NetRuntimeException("The data is combined in one Stream");
        }
        if (!pipedStreams.containsKey(inetAddress)) {
            return null;
        }
        return pipedStreams.get(inetAddress).getInputStream();
    }
    
    public InputStream getInputStream() {
        if (splitDataPerSource.get()) {
            throw new NetRuntimeException("The data is split per InetAddress");
        }
        return pipedStream.getInputStream();
    }
    
    public boolean closeConnections() {
        pipedStreams.values().forEach(PipedStream::closeWithoutException);
        return !pipedStreams.isEmpty();
    }
    
    public boolean closeConnection(InetAddress inetAddress) {
        final PipedStream pipedStream = pipedStreams.get(inetAddress);
        if (pipedStream != null) {
            pipedStream.closeWithoutException();
            return true;
        }
        return false;
    }
    
    public boolean removeConnections() {
        closeConnections();
        pipedStreams.clear();
        return pipedStreams.isEmpty();
    }
    
    public boolean removeConnection(InetAddress inetAddress) {
        closeConnection(inetAddress);
        return pipedStreams.remove(inetAddress) != null;
    }
    
    @Override
    public String toString() {
        return "UDPServerSocket{" + "port=" + port + ", stopped=" + stopped + ", pipedStreams=" + pipedStreams + ", awaitingConnection=" + awaitingConnection + ", splitDataPerSource=" + splitDataPerSource + ", bufferSize=" + bufferSize + ", datagramSocket=" + datagramSocket + ", thread=" + thread + ", inetAddress_temp=" + inetAddress_temp + ", pipedStream=" + pipedStream + '}';
    }
    
}
