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

package de.codemakers.net.wrapper.sockets;

import de.codemakers.base.action.ReturningAction;
import de.codemakers.base.logger.Logger;
import de.codemakers.base.util.StringUtil;
import de.codemakers.base.util.interfaces.Connectable;
import de.codemakers.base.util.interfaces.Disconnectable;
import de.codemakers.base.util.interfaces.Startable;
import de.codemakers.base.util.interfaces.Stoppable;
import de.codemakers.base.util.tough.ToughConsumer;
import de.codemakers.base.util.tough.ToughFunction;
import de.codemakers.net.exceptions.NetRuntimeException;

import java.io.*;
import java.net.*;
import java.nio.channels.AlreadyBoundException;
import java.util.concurrent.atomic.AtomicBoolean;

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
            this.inetAddress = socket.getInetAddress();
            this.port = socket.getPort();
        }
    }
    
    public AbstractSocket(InetAddress inetAddress, int port) {
        this.inetAddress = inetAddress;
        this.port = port;
    }
    
    protected String createThreadName() {
        return String.format("%-%s:%d.Thread", StringUtil.classToSimpleName(getClass()), ((inetAddress instanceof Inet6Address) ? "[" + inetAddress.getHostAddress() + "]" : inetAddress.getHostAddress()), port);
    }
    
    protected abstract void processInput(long timestamp, Object input) throws Exception;
    
    protected abstract void processDisconnect(long timestamp, boolean ok, boolean local, Throwable throwable) throws Exception;
    
    public abstract boolean send(Object object) throws Exception;
    
    public boolean send(Object object, ToughConsumer<Throwable> failure) {
        try {
            return send(object);
        } catch (Exception ex) {
            if (failure != null) {
                failure.acceptWithoutException(ex);
            } else {
                Logger.handleError(ex);
            }
            return false;
        }
    }
    
    public boolean sendWithoutException(Object object) {
        return send(object, null);
    }
    
    public ReturningAction<Boolean> sendAction(Object object) {
        return new ReturningAction<>(() -> send(object));
    }
    
    public boolean send(byte[] data) throws Exception {
        if (data != null && getOutputStream() != null) {
            getOutputStream().write(data);
            getOutputStream().flush();
            return true;
        }
        return false;
    }
    
    public boolean send(byte[] data, ToughConsumer<Throwable> failure) {
        try {
            return send(data);
        } catch (Exception ex) {
            if (failure != null) {
                failure.acceptWithoutException(ex);
            } else {
                Logger.handleError(ex);
            }
            return false;
        }
    }
    
    public boolean sendWithoutException(byte[] data) {
        return send(data, null);
    }
    
    public ReturningAction<Boolean> sendAction(byte[] data) {
        return new ReturningAction<>(() -> send(data));
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
                running.set(false);
                try {
                    processDisconnect(timestamp, localCloseRequested.get(), localCloseRequested.get(), ex);
                } catch (Exception ex2) {
                    Logger.handleError(ex2);
                }
                disconnectWithoutException();
            } catch (Exception ex) {
                final long timestamp = System.currentTimeMillis();
                running.set(false);
                try {
                    processDisconnect(timestamp, false, localCloseRequested.get(), ex);
                } catch (Exception ex2) {
                    Logger.handleError(ex2);
                }
                Logger.handleError(ex);
                disconnectWithoutException();
            }
        });
        thread.setName(createThreadName());
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
            throw new NetRuntimeException("Socket was not created");
        }
        thread.start();
        return true;
    }
    
    public final boolean isRunning() {
        return running.get() || (socket != null && socket.isConnected() && !socket.isClosed());
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
    
    public final boolean isObjectOutputStream() {
        return getOutputStream() instanceof ObjectOutputStream;
    }
    
    public final boolean openObjectOutputStream() {
        if (!isObjectOutputStream()) {
            processOutputStream(ObjectOutputStream::new);
        }
        return isObjectOutputStream();
    }
    
    private boolean initSocket() throws IOException {
        if (socket != null) {
            return false;
        }
        localCloseRequested.set(false);
        socket = new Socket(inetAddress, port);
        setOutputStream(socket.getOutputStream());
        setInputStream(socket.getInputStream());
        return true;
    }
    
    @Override
    public boolean connect(boolean reconnect) throws Exception {
        if (isRunning()) {
            return false;
        }
        if (socket == null) {
            initSocket();
        } else if (reconnect) {
            if (socket.isClosed()/* && !socket.isConnected()*/) { //TODO when the Socket is closed by user, it may return true for isConnected
                socket.connect(new InetSocketAddress(inetAddress, port));
            } else {
                throw new AlreadyBoundException();
            }
        } else {
            if (socket.isClosed()/* && !socket.isConnected()*/) { //TODO when the Socket is closed by user, it may return true for isConnected
                socket = null;
            } else {
                throw new AlreadyBoundException();
            }
        }
        return socket != null && socket.isConnected() && socket.isBound() && !socket.isClosed(); //TODO Test this
    }
    
    @Override
    public boolean disconnect() throws Exception {
        if (!isRunning()) {
            return false;
        }
        if (socket != null) {
            close();
            socket = null; //TODO Maybe do not set this null? Because if you want to reuse this socket...
            //TODO Set socket to null, because otherwise it can not be reused?
            setInputStream(null);
            setOutputStream(null);
            return true;
        }
        return false;
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
        localCloseRequested.set(true);
        if (outputStream != null) {
            outputStream.close();
        }
        if (inputStream != null) {
            inputStream.close();
        }
        if (socket != null) {
            socket.close();
        }
    }
    
}
