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

import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.atomic.AtomicBoolean;

public class UDPSocket implements Closeable, Resettable, Startable, Stoppable {
    
    public static final int DEFAULT_BUFFER_SIZE = 128;
    
    protected final InetAddress inetAddress;
    protected int port_sender;
    protected int port_receiver;
    protected final AtomicBoolean stopped = new AtomicBoolean(false);
    protected final PipedStream pipedStream = new PipedStream();
    protected int bufferSize;
    protected DatagramSocket datagramSocket = null;
    protected Thread thread = null;
    
    public UDPSocket(InetAddress inetAddress, int port, int bufferSize) {
        this.inetAddress = inetAddress;
        this.port_sender = port;
        this.port_receiver = port_sender;
        this.bufferSize = bufferSize;
    }
    
    public UDPSocket(InetAddress inetAddress, int port) {
        this(inetAddress, port, DEFAULT_BUFFER_SIZE);
    }
    
    public UDPSocket(DatagramSocket datagramSocket, int bufferSize) {
        this.inetAddress = datagramSocket.getInetAddress();
        this.port_sender = datagramSocket.getPort();
        this.port_receiver = port_sender;
        this.datagramSocket = datagramSocket;
        this.bufferSize = bufferSize;
    }
    
    public UDPSocket(DatagramSocket datagramSocket) {
        this(datagramSocket, DEFAULT_BUFFER_SIZE);
    }
    
    @Override
    public void closeIntern() throws Exception {
        pipedStream.close();
    }
    
    @Override
    public boolean reset() throws Exception {
        pipedStream.resetWithoutException();
        //pipedStream.convertOutputStream(BufferedOutputStream::new); //TODO Necessary?
        if (datagramSocket == null) {
            datagramSocket = new DatagramSocket(port_sender, inetAddress);
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
                    read = pipedStream.getInputStream().read(buffer, 0, buffer.length);
                    final DatagramPacket datagramPacket = new DatagramPacket(buffer, read, inetAddress, port_receiver);
                    datagramSocket.send(datagramPacket);
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
        this.port_sender = port_sender;
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