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

package de.codemakers.net.wrapper.sockets.test2.server;

import de.codemakers.base.util.interfaces.Startable;
import de.codemakers.base.util.interfaces.Stoppable;
import de.codemakers.net.exceptions.NetRuntimeException;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Objects;

public abstract class AdvancedServerSocket extends AbstractServerSocket implements Closeable, Startable, Stoppable {
    
    protected volatile boolean started = false;
    protected volatile boolean stopRequested = false;
    protected volatile boolean isErrored = false;
    protected volatile Throwable error = null;
    
    public AdvancedServerSocket(int port) {
        super(port);
    }
    
    public AdvancedServerSocket(ServerSocket serverSocket) {
        super(serverSocket);
    }
    
    public boolean isStarted() {
        return started && !isErrored;
    }
    
    public boolean isStopRequested() {
        return stopRequested;
    }
    
    public boolean isErrored() {
        return isErrored;
    }
    
    public Throwable getError() {
        return error;
    }
    
    public AdvancedServerSocket error(Throwable error) {
        this.isErrored = true;
        this.error = error;
        this.stopRequested = false;
        return this;
    }
    
    public AdvancedServerSocket resetError() {
        isErrored = false;
        error = null;
        return this;
    }
    
    protected abstract boolean onStart(boolean successful) throws Exception;
    
    protected abstract boolean onStop() throws Exception;
    
    @Override
    public AbstractServerSocket setServerSocket(ServerSocket serverSocket) {
        Objects.requireNonNull(serverSocket);
        setPort(serverSocket.getLocalPort());
        this.serverSocket = serverSocket;
        resetError();
        started = serverSocket.isBound() && !serverSocket.isClosed();
        try {
            onStart(isStarted());
        } catch (Exception ex) {
            throw new NetRuntimeException(ex);
        }
        return null;
    }
    
    @Override
    protected ServerSocket createServerSocket() throws Exception {
        return new ServerSocket(port);
    }
    
    @Override
    public boolean start() throws Exception {
        if (isStarted()) {
            return false;
        }
        closeIntern();
        stopRequested = false;
        serverSocket = createServerSocket();
        resetError();
        started = serverSocket != null && serverSocket.isBound() && !serverSocket.isClosed();
        return onStart(isStarted());
    }
    
    @Override
    public boolean stop() throws Exception {
        if (!isStarted()) {
            return false;
        }
        stopRequested = true;
        onStop();
        closeIntern();
        return true;
    }
    
    protected void closeIntern() throws Exception {
        close();
        serverSocket = null;
    }
    
    @Override
    public void close() throws IOException {
        if (serverSocket != null) {
            serverSocket.close();
        }
        started = false;
    }
    
    @Override
    public String toString() {
        return "AdvancedServerSocket{" + "started=" + started + ", stopRequested=" + stopRequested + ", isErrored=" + isErrored + ", error=" + error + ", port=" + port + ", serverSocket=" + serverSocket + '}';
    }
    
}
