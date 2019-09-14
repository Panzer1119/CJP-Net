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
import de.codemakers.base.util.interfaces.*;
import de.codemakers.io.streams.PipedStream;
import de.codemakers.net.exceptions.NetRuntimeException;

import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.atomic.AtomicBoolean;

public class UDPSocket implements Closeable, Connectable, Resettable, Startable, Stoppable {
    
    public static final int DEFAULT_BUFFER_SIZE = 128;
    
    protected final InetAddress inetAddress;
    protected int port_sender;
    protected int port_receiver;
    protected final AtomicBoolean stopped = new AtomicBoolean(false);
    protected final PipedStream pipedStream = new PipedStream();
    protected int bufferSize;
    protected DatagramSocket datagramSocket = null;
    protected Thread thread = null;
    
    public UDPSocket(InetAddress inetAddress, int port_sender, int port_receiver, int bufferSize) {
        this.inetAddress = inetAddress;
        this.port_sender = port_sender;
        this.port_receiver = port_receiver;
        this.bufferSize = bufferSize;
    }
    
    public UDPSocket(InetAddress inetAddress, int port_sender, int port_receiver) {
        this(inetAddress, port_sender, port_receiver, DEFAULT_BUFFER_SIZE);
    }
    
    public UDPSocket(InetAddress inetAddress, int port) {
        this(inetAddress, port, port);
    }
    
    public UDPSocket(DatagramSocket datagramSocket, int port, int bufferSize) {
        this(datagramSocket.getInetAddress(), datagramSocket.getLocalPort(), port, bufferSize);
        this.datagramSocket = datagramSocket;
    }
    
    public UDPSocket(DatagramSocket datagramSocket, int port) {
        this(datagramSocket, port, DEFAULT_BUFFER_SIZE);
    }
    
    public UDPSocket(DatagramSocket datagramSocket) {
        this(datagramSocket, datagramSocket.getLocalPort());
    }
    
    private void initDatagramSocket() throws Exception {
        if (datagramSocket == null) {
            datagramSocket = new DatagramSocket(port_sender, inetAddress);
        }
    }
    
    @Override
    public boolean connect(boolean reconnect) throws Exception {
        initDatagramSocket();
        datagramSocket.send(new DatagramPacket(new byte[0], 0, inetAddress, port_receiver));
        return true;
    }
    
    @Override
    public void closeIntern() throws Exception {
        pipedStream.close();
    }
    
    @Override
    public boolean reset() throws Exception {
        pipedStream.resetWithoutException();
        initDatagramSocket();
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
                    read = pipedStream.getInputStream().read(buffer, 0, buffer.length);
                    datagramSocket.send(new DatagramPacket(buffer, read, inetAddress, port_receiver));
                }
            } catch (Exception ex) {
                if (!(ex instanceof InterruptedException) && !(ex instanceof IOException)) {
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
        Standard.silentError(() -> Thread.sleep(100));
        thread.interrupt();
        thread = null;
        return true;
    }
    
    public InetAddress getInetAddress() {
        return inetAddress;
    }
    
    public int getPortSender() {
        return port_sender;
    }
    
    public UDPSocket setPortSender(int port_sender) {
        if (!stopped.get()) {
            throw new NetRuntimeException(getClass().getSimpleName() + " is currently running on port " + this.port_sender);
        }
        if (this.port_sender != port_sender) {
            this.port_sender = port_sender;
            datagramSocket = null;
            Standard.silentError(this::initDatagramSocket);
        }
        return this;
    }
    
    public int getPortReceiver() {
        return port_receiver;
    }
    
    public UDPSocket setPortReceiver(int port_receiver) {
        this.port_receiver = port_receiver;
        return this;
    }
    
    public DatagramSocket getDatagramSocket() {
        return datagramSocket;
    }
    
    public UDPSocket setDatagramSocket(DatagramSocket datagramSocket) {
        this.datagramSocket = datagramSocket;
        return this;
    }
    
    public OutputStream getOutputStream() {
        return pipedStream.getOutputStream();
    }
    
    @Override
    public String toString() {
        return "UDPSocket{" + "inetAddress=" + inetAddress + ", port_sender=" + port_sender + ", port_receiver=" + port_receiver + ", stopped=" + stopped + ", pipedStream=" + pipedStream + ", bufferSize=" + bufferSize + ", datagramSocket=" + datagramSocket + ", thread=" + thread + '}';
    }
    
}
