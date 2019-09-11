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
import java.net.SocketException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class UDPServerSocket implements Closeable, Connectable, Disconnectable, Resettable, Startable, Stoppable {
    
    public static boolean DEBUG = false;
    
    public static final int DEFAULT_BUFFER_SIZE = 128;
    
    protected final int port;
    protected final AtomicBoolean stopped = new AtomicBoolean(false);
    protected final Table<InetAddress, Integer, PipedStream> pipedStreamTable = HashBasedTable.create();
    protected final AtomicBoolean awaitingConnection = new AtomicBoolean(false);
    protected int bufferSize;
    protected DatagramSocket datagramSocket;
    protected Thread thread = null;
    protected Doublet<InetAddress, Integer> connection_temp = null;
    
    public UDPServerSocket(int port, int bufferSize) {
        this.port = port;
        this.bufferSize = bufferSize;
    }
    
    public UDPServerSocket(int port) {
        this(port, DEFAULT_BUFFER_SIZE);
    }
    
    public UDPServerSocket(DatagramSocket datagramSocket, int bufferSize) {
        this.port = datagramSocket.getPort(); //TODO This is a "Receiving" DatagramSocket so should i use getLocalPort() instead?
        this.datagramSocket = datagramSocket;
        this.bufferSize = bufferSize;
    }
    
    public UDPServerSocket(DatagramSocket datagramSocket) {
        this(datagramSocket, DEFAULT_BUFFER_SIZE);
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
        if (DEBUG) {
            if ((timeout_ != -1 && (System.currentTimeMillis() - started >= timeout_))) {
                Logger.logDebug(String.format("%s timed out while waiting for a connection", this));
            }
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
    public boolean connect(boolean reconnect) throws Exception { //FIXME The connect/disconnect functions should be in the UDPSocket Class and NOT in this class (UDPServerSocket)?!
        try {
            datagramSocket = new DatagramSocket(port);
        } catch (Exception ex) {
            Logger.handleError(ex);
        }
        return datagramSocket.isConnected() && !datagramSocket.isClosed();
    }
    
    @Override
    public boolean disconnect() throws Exception { //FIXME The connect/disconnect functions should be in the UDPSocket Class and NOT in this class (UDPServerSocket)?!
        awaitingConnection.set(false);
        datagramSocket.disconnect();
        return datagramSocket.isClosed();
    }
    
    @Override
    public boolean start() throws Exception {
        if (thread != null) {
            return false;
        }
        stopped.set(false);
        //resetWithoutException(); //TODO Necessary? Or more like do i want to force reset this here?
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
        if (!pipedStreamTable.contains(inetAddress, port)) {
            return null;
        }
        return pipedStreamTable.get(inetAddress, port).getInputStream();
    }
    
    public boolean closeConnections() {
        pipedStreamTable.values().forEach(PipedStream::closeWithoutException);
        return !pipedStreamTable.isEmpty();
    }
    
    public boolean closeConnections(InetAddress inetAddress) {
        pipedStreamTable.rowMap().get(inetAddress).values().forEach(PipedStream::closeWithoutException);
        return !pipedStreamTable.isEmpty();
    }
    
    public boolean closeConnections(int port) {
        pipedStreamTable.columnMap().get(port).values().forEach(PipedStream::closeWithoutException);
        return !pipedStreamTable.isEmpty();
    }
    
    public boolean closeConnection(InetAddress inetAddress, int port) {
        final PipedStream pipedStream = pipedStreamTable.get(inetAddress, port);
        if (pipedStream != null) {
            pipedStream.closeWithoutException();
            return true;
        }
        return false;
    }
    
    public boolean removeConnections() {
        closeConnections();
        pipedStreamTable.clear();
        return pipedStreamTable.isEmpty();
    }
    
    public boolean removeConnections(InetAddress inetAddress) {
        closeConnections(inetAddress);
        return pipedStreamTable.rowMap().remove(inetAddress) != null;
    }
    
    public boolean removeConnections(int port) {
        closeConnections(port);
        return pipedStreamTable.columnMap().remove(port) != null;
    }
    
    public boolean removeConnection(InetAddress inetAddress, int port) {
        closeConnection(inetAddress, port);
        return pipedStreamTable.remove(inetAddress, port) != null;
    }
    
    @Override
    public boolean reset() throws Exception {
        pipedStreamTable.values().forEach((pipedStream) -> {
            pipedStream.resetWithoutException();
            pipedStream.convertOutputStream(BufferedOutputStream::new);
        });
        return true;
    }
    
    @Override
    public String toString() {
        return "UDPServerSocket{" + "port=" + port + ", stopped=" + stopped + ", pipedStreamTable=" + pipedStreamTable + ", awaitingConnection=" + awaitingConnection + ", bufferSize=" + bufferSize + ", datagramSocket=" + datagramSocket + ", thread=" + thread + ", connection_temp=" + connection_temp + '}';
    }
    
}
