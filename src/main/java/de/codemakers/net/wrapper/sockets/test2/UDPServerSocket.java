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

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import de.codemakers.base.Standard;
import de.codemakers.base.logger.Logger;
import de.codemakers.base.multiplets.Doublet;
import de.codemakers.base.util.interfaces.*;
import de.codemakers.io.streams.PipedStream;
import de.codemakers.net.exceptions.NetRuntimeException;

import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class UDPServerSocket implements Closeable, Connectable, Disconnectable, Resettable, Startable, Stoppable {
    
    public static final int DEFAULT_BUFFER_SIZE = 128;
    
    protected final int port;
    protected final AtomicBoolean stopped = new AtomicBoolean(false);
    protected final Table<InetAddress, Integer, PipedStream> pipedStreamTable = HashBasedTable.create();
    protected int bufferSize = DEFAULT_BUFFER_SIZE;
    protected DatagramSocket datagramSocket;
    protected Thread thread = null;
    //Awaiting Stuff
    protected final AtomicBoolean awaitingConnection = new AtomicBoolean(false);
    protected Doublet<InetAddress, Integer> connection_temp = null;
    
    public UDPServerSocket(int port, int bufferSize) {
        this.port = port;
        this.bufferSize = bufferSize;
    }
    
    public UDPServerSocket(int port) {
        this.port = port;
    }
    
    public boolean allowNewConnection(InetAddress inetAddress, int port) {
        return true;
    }
    
    public Doublet<InetAddress, Integer> accept() {
        return accept(-1, null);
    }
    
    public Doublet<InetAddress, Integer> accept(long timeout, TimeUnit timeUnit) {
        if (awaitingConnection.get()) {
            throw new NetRuntimeException("Already awaiting a new connection");
        }
        awaitingConnection.set(true);
        final long timeout_ = (timeUnit == null ? -1 : (timeout == -1 ? -1 : timeUnit.toMillis(timeout)));
        final long started = System.currentTimeMillis();
        while (awaitingConnection.get() && connection_temp == null && (timeout_ == -1 || (System.currentTimeMillis() - started) < timeout_)) {
            Standard.silentError(() -> Thread.sleep(100));
        }
        awaitingConnection.set(false);
        final Doublet<InetAddress, Integer> temp = connection_temp;
        connection_temp = null;
        return temp;
    }
    
    @Override
    public void closeIntern() throws Exception {
        datagramSocket.close();
    }
    
    @Override
    public boolean connect(boolean reconnect) throws Exception {
        try {
            datagramSocket = new DatagramSocket(port);
        } catch (Exception ex) {
            Logger.handleError(ex);
        }
        return datagramSocket.isConnected() && !datagramSocket.isClosed();
    }
    
    @Override
    public boolean disconnect() throws Exception {
        datagramSocket.disconnect();
        return datagramSocket.isClosed();
    }
    
    @Override
    public boolean start() throws Exception {
        if (thread != null) {
            return false;
        }
        stopped.set(false);
        //reset(); //TODO Necessary? Or more like do i want to force reset this here?
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
                    PipedStream pipedStream = pipedStreamTable.get(inetAddress, port);
                    if (pipedStream == null) {
                        if (awaitingConnection.get()) {
                            connection_temp = new Doublet<>(inetAddress, port);
                            awaitingConnection.set(false);
                        }
                        if (!allowNewConnection(inetAddress, port)) {
                            continue;
                        }
                        pipedStream = new PipedStream();
                        pipedStream.convertOutputStream(BufferedOutputStream::new);
                        pipedStreamTable.put(inetAddress, port, pipedStream);
                    }
                    pipedStream.getOutputStream().write(buffer, 0, read);
                }
            } catch (Exception ex) {
                if (!(ex instanceof InterruptedException)) {
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
        thread.interrupt();
        thread = null;
        return !thread.isAlive();
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
    
    public List<InputStream> getInputStreams() {
        return pipedStreamTable.values().stream().map(PipedStream::getInputStream).collect(Collectors.toList());
    }
    
    public List<InputStream> getInputStreams(InetAddress inetAddress) {
        return pipedStreamTable.row(inetAddress).values().stream().map(PipedStream::getInputStream).collect(Collectors.toList());
    }
    
    public List<InputStream> getInputStreams(int port) {
        return pipedStreamTable.column(port).values().stream().map(PipedStream::getInputStream).collect(Collectors.toList());
    }
    
    public InputStream getInputStream(InetAddress inetAddress, int port) {
        return pipedStreamTable.get(inetAddress, port).getInputStream();
    }
    
    @Override
    public boolean reset() throws Exception {
        pipedStreamTable.values().forEach((pipedStream) -> {
            pipedStream.resetWithoutException();
            pipedStream.convertOutputStream(BufferedOutputStream::new);
        });
        return true;
    }
    
}
