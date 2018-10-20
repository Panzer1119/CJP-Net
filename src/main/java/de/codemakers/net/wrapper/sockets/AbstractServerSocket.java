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
import de.codemakers.base.util.interfaces.Startable;
import de.codemakers.base.util.interfaces.Stoppable;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.channels.AlreadyBoundException;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractServerSocket implements Closeable, Startable, Stoppable {
    
    private int port = -1;
    private ServerSocket serverSocket = null;
    private Thread thread = null;
    private AtomicBoolean running = new AtomicBoolean(false);
    private AtomicBoolean localCloseRequested = new AtomicBoolean(false);
    
    public AbstractServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
        if (serverSocket != null) {
            port = serverSocket.getLocalPort();
        }
    }
    
    public AbstractServerSocket(int port) {
        this.port = port;
    }
    
    protected abstract void processSocket(long timestamp, Socket socket) throws Exception;
    
    protected abstract void processStop(long timestamp, boolean ok, boolean local, Throwable throwable) throws Exception;
    
    private final boolean initThread() {
        if (thread != null) {
            if (thread.isInterrupted()) {
                //FIXME What to do?
            } else {
                return false;
            }
        }
        thread = new Thread(() -> {
            running.set(true);
            try {
                while (isRunning()) {
                    final Socket socket = serverSocket.accept();
                    final long timestamp = System.currentTimeMillis();
                    if (socket != null) {
                        try {
                            processSocket(timestamp, socket);
                        } catch (Exception ex) {
                            Logger.handleError(ex);
                        }
                    }
                }
            } catch (SocketException ex) {
                final long timestamp = System.currentTimeMillis();
                try {
                    processStop(timestamp, localCloseRequested.get(), true, ex);
                } catch (Exception ex2) {
                    Logger.handleError(ex2);
                }
            } catch (Exception ex) {
                running.set(false);
                Logger.handleError(ex); //TODO ignore the Exceptions thrown, if the ServerSocket was stopped by user
            }
        });
        return true;
    }
    
    public final boolean startThread() {
        if (thread != null && thread.isAlive()) {
            return false;
        }
        if (thread == null || thread.isInterrupted()) {
            initThread();
        }
        if (serverSocket == null) {
            throw new IllegalArgumentException(ServerSocket.class.getSimpleName() + " was not created");
        }
        thread.start();
        return true;
    }
    
    public final boolean isRunning() {
        return running.get() || (serverSocket != null && !serverSocket.isClosed());
    }
    
    public final int getPort() {
        return port;
    }
    
    public final AbstractServerSocket setPort(int port) {
        this.port = port;
        return this;
    }
    
    public final ServerSocket getServerSocket() {
        return serverSocket;
    }
    
    public final AbstractServerSocket setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
        return this;
    }
    
    private boolean initServerSocket() throws IOException {
        if (serverSocket != null) {
            return false;
        }
        localCloseRequested.set(false);
        serverSocket = new ServerSocket(port);
        return true;
    }
    
    @Override
    public boolean start() throws Exception {
        if (isRunning()) {
            throw new AlreadyBoundException();
        }
        if (serverSocket == null) {
            initServerSocket();
        }
        return startThread();
    }
    
    @Override
    public boolean stop() throws Exception {
        if (isRunning()) {
            if (thread != null) {
                thread.interrupt(); //TODO fix this, because blocking methods can not be interrupted by this
                thread = null;
            }
            close();
        } else {
            return false;
        }
        running.set(false);
        return true;
    }
    
    @Override
    public void close() throws IOException {
        if (serverSocket != null) {
            localCloseRequested.set(true);
            serverSocket.close();
        }
    }
    
    @Override
    public String toString() {
        return "AbstractServerSocket{" + "port=" + port + ", serverSocket=" + serverSocket + ", thread=" + thread + ", running=" + running + ", localCloseRequested=" + localCloseRequested + '}';
    }
    
}
