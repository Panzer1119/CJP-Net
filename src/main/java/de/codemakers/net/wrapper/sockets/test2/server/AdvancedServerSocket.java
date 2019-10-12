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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public abstract class AdvancedServerSocket<THIS> extends AbstractServerSocket<THIS> implements Closeable, Startable, Stoppable {
    
    protected final AtomicBoolean started = new AtomicBoolean(false);
    protected final AtomicBoolean stopRequested = new AtomicBoolean(false);
    protected final AtomicBoolean errored = new AtomicBoolean(false);
    protected final AtomicReference<Throwable> error = new AtomicReference<>(null);
    
    public AdvancedServerSocket(int port) {
        super(port);
    }
    
    public AdvancedServerSocket(ServerSocket serverSocket) {
        super(serverSocket);
    }
    
    public boolean isStarted() {
        return started.get(); //FIXME What is if isErrored is true? return false then?
    }
    
    protected AdvancedServerSocket setStarted(boolean started) {
        this.started.set(started);
        return this;
    }
    
    public boolean isStopRequested() {
        return stopRequested.get();
    }
    
    protected THIS setStopRequested(boolean stopRequested) {
        this.stopRequested.set(stopRequested);
        return (THIS) this;
    }
    
    public boolean isErrored() {
        return errored.get();
    }
    
    protected THIS setErrored(boolean errored) {
        this.errored.set(errored);
        return (THIS) this;
    }
    
    public Throwable getError() {
        return error.get();
    }
    
    protected THIS setError(Throwable error) {
        this.error.set(error);
        return (THIS) this;
    }
    
    public THIS error(Throwable error) {
        setErrored(true);
        setError(error);
        setStopRequested(false);
        return (THIS) this;
    }
    
    public THIS resetError() {
        setErrored(false);
        setError(null);
        return (THIS) this;
    }
    
    protected abstract boolean onStart(boolean successful) throws Exception;
    
    protected abstract boolean onStop() throws Exception;
    
    @Override
    public THIS setServerSocket(ServerSocket serverSocket) {
        Objects.requireNonNull(serverSocket);
        setPort(serverSocket.getLocalPort());
        this.serverSocket = serverSocket;
        resetError();
        setStarted(serverSocket.isBound() && !serverSocket.isClosed());
        try {
            onStart(isStarted());
        } catch (Exception ex) {
            throw new NetRuntimeException(ex);
        }
        return (THIS) this;
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
        setStopRequested(false);
        serverSocket = createServerSocket();
        resetError();
        setStarted(serverSocket != null && serverSocket.isBound() && !serverSocket.isClosed());
        return onStart(isStarted());
    }
    
    @Override
    public boolean stop() throws Exception {
        if (!isStarted()) {
            return false;
        }
        setStopRequested(true);
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
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
            setStarted(false);
        } catch (Exception ex) {
            setStarted(false);
            throw new IOException(ex);
        }
    }
    
    @Override
    public String toString() {
        return "AdvancedServerSocket{" + "started=" + started + ", stopRequested=" + stopRequested + ", errored=" + errored + ", error=" + error + ", port=" + port + ", serverSocket=" + serverSocket + '}';
    }
    
}
