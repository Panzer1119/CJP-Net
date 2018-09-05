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

package de.codemakers.net.wrapper.sockets;

import de.codemakers.base.logger.Logger;
import de.codemakers.base.util.interfaces.Connectable;
import de.codemakers.base.util.interfaces.Disconnectable;
import de.codemakers.base.util.interfaces.Startable;
import de.codemakers.base.util.interfaces.Stoppable;
import de.codemakers.base.util.tough.ToughFunction;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.channels.AlreadyBoundException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public abstract class AbstractSocket implements Closeable, Connectable, Disconnectable, Startable, Stoppable {
    
    private InetAddress inetAddress = null;
    private int port = -1;
    private Socket socket = null;
    private Thread thread = null;
    private AtomicBoolean running = new AtomicBoolean(false);
    private AtomicBoolean localCloseRequested = new AtomicBoolean(false);
    private InputStream inputStream;
    private OutputStream outputStream;
    
    public AbstractSocket(Socket socket) {
        this.socket = socket;
        if (socket != null) {
            inetAddress = socket.getInetAddress();
            port = socket.getPort();
        }
    }
    
    public AbstractSocket(InetAddress inetAddress, int port) {
        this.inetAddress = inetAddress;
        this.port = port;
    }
    
    protected abstract void processInput(long timestamp, Object input) throws Exception;
    
    protected abstract void processDisconnect(long timestamp, boolean ok, boolean local, Throwable throwable) throws Exception;
    
    public abstract boolean send(Object object) throws Exception;
    
    public boolean send(Object object, Consumer<Throwable> failure) {
        try {
            return send(object);
        } catch (Exception ex) {
            if (failure != null) {
                failure.accept(ex);
            } else {
                Logger.handleError(ex);
            }
            return false;
        }
    }
    
    public boolean sendWithoutException(Object object) {
        return send(object, null);
    }
    
    public boolean send(byte[] data) throws Exception {
        if (data != null) {
            getOutputStream().write(data);
            return true;
        }
        return false;
    }
    
    public boolean send(byte[] data, Consumer<Throwable> failure) {
        try {
            return send(data);
        } catch (Exception ex) {
            if (failure != null) {
                failure.accept(ex);
            } else {
                Logger.handleError(ex);
            }
            return false;
        }
    }
    
    public boolean sendWithoutException(byte[] data) {
        return send(data, null);
    }
    
    private final boolean initThread() {
        if (thread != null) {
            return false;
        }
        thread = new Thread(() -> {
            running.set(true);
            try {
                final ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                Object object = null;
                while (isRunning() && (object = objectInputStream.readObject()) != null) {
                    final long timestamp = System.currentTimeMillis();
                    try {
                        processInput(timestamp, object);
                    } catch (Exception ex) {
                        Logger.handleError(ex);
                    }
                }
            } catch (SocketException ex) {
                final long timestamp = System.currentTimeMillis();
                System.err.println("ERROR CLIENTS SOCKET CLOSED:"); //TODO Debug only
                try {
                    processDisconnect(timestamp, localCloseRequested.get(), localCloseRequested.get(), ex);
                } catch (Exception ex2) {
                    Logger.handleError(ex2);
                }
            } catch (Exception ex) {
                running.set(false);
                try {
                    processDisconnect(System.currentTimeMillis(), false, false, ex);
                } catch (Exception ex2) {
                    Logger.handleError(ex2);
                }
                Logger.handleError(ex); //TODO ignore the Exceptions thrown, if the ServerSocket was stopped by user
            }
        });
        return true;
    }
    
    public final boolean startThread() {
        if (isRunning()) {
            return false;
        }
        if (thread == null) {
            initThread();
        }
        if (socket == null) {
            throw new IllegalArgumentException("Socket was not created");
        }
        thread.start();
        return true;
    }
    
    public final boolean isRunning() {
        return running.get();
    }
    
    public final InetAddress getInetAddress() {
        return inetAddress;
    }
    
    public final AbstractSocket setInetAddress(InetAddress inetAddress) {
        this.inetAddress = inetAddress;
        return this;
    }
    
    public final int getPort() {
        return port;
    }
    
    public final AbstractSocket setPort(int port) {
        this.port = port;
        return this;
    }
    
    public final Socket getSocket() {
        return socket;
    }
    
    public final AbstractSocket setSocket(Socket socket) {
        this.socket = socket;
        return this;
    }
    
    public final InputStream getInputStream() {
        return inputStream;
    }
    
    public final AbstractSocket setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
        return this;
    }
    
    public final AbstractSocket processInputStream(ToughFunction<InputStream, InputStream> function) {
        if (function != null) {
            final InputStream inputStream = function.applyWithoutException(this.inputStream);
            if (inputStream != null) {
                this.inputStream = inputStream;
            }
        }
        return this;
    }
    
    public final OutputStream getOutputStream() {
        return outputStream;
    }
    
    public final AbstractSocket setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
        return this;
    }
    
    public final AbstractSocket processOutputStream(ToughFunction<OutputStream, OutputStream> function) {
        if (function != null) {
            final OutputStream outputStream = function.applyWithoutException(this.outputStream);
            if (outputStream != null) {
                this.outputStream = outputStream;
            }
        }
        return this;
    }
    
    private boolean initSocket() throws IOException {
        if (socket != null) {
            return false;
        }
        localCloseRequested.set(false);
        socket = new Socket(inetAddress, port);
        return true;
    }
    
    @Override
    public boolean connect() throws Exception {
        if (isRunning()) {
            return false;
        }
        if (socket == null) {
            initSocket();
        }
        return socket != null && socket.isConnected(); //TODO Test this
    }
    
    @Override
    public boolean disconnect() throws Exception {
        if (!isRunning()) {
            return false;
        }
        if (socket != null) {
            System.err.println("Trying to closing SOCKET");
            close();
            socket = null;
        }
        return socket == null;
    }
    
    @Override
    public boolean start() throws Exception {
        if (isRunning()) {
            throw new AlreadyBoundException();
        }
        if (!connect()) {
            return false;
        }
        startThread();
        return true;
    }
    
    @Override
    public boolean stop() throws Exception {
        if (isRunning()) {
            /*
            if (thread != null) {
                thread.interrupt(); //TODO fix this, because blocking methods can not be interrupted by this
                thread = null;
            }
            */
            if (!disconnect()) {
                return false;
            }
            if (thread != null) {
                thread.interrupt();
                thread = null;
            }
        } else {
            return false;
        }
        running.set(false);
        return true;
    }
    
    @Override
    public void close() throws IOException {
        if (socket != null) {
            localCloseRequested.set(true);
            socket.close();
        }
    }
    
}
