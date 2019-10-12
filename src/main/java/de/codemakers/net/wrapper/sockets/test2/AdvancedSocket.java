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

import de.codemakers.base.util.interfaces.Connectable;
import de.codemakers.base.util.interfaces.Disconnectable;
import de.codemakers.net.exceptions.NetRuntimeException;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class AdvancedSocket extends AbstractSocket<AdvancedSocket> implements Closeable, Connectable, Disconnectable {
    
    protected final AtomicBoolean connected = new AtomicBoolean(false);
    protected final AtomicBoolean localCloseRequested = new AtomicBoolean(false);
    protected final AtomicBoolean isErrored = new AtomicBoolean(false);
    protected final AtomicReference<Throwable> error = new AtomicReference<>(null);
    
    public AdvancedSocket(InetAddress inetAddress, int port) {
        super(inetAddress, port);
    }
    
    public AdvancedSocket(Socket socket) {
        super(socket);
    }
    
    public boolean isConnected() {
        return connected.get(); //FIXME What is if isErrored is true? return false then?
    }
    
    protected AdvancedSocket setConnected(boolean connected) {
        this.connected.set(connected);
        return this;
    }
    
    public boolean isLocalCloseRequested() {
        return localCloseRequested.get();
    }
    
    protected AdvancedSocket setLocalCloseRequested(boolean localCloseRequested) {
        this.localCloseRequested.set(localCloseRequested);
        return this;
    }
    
    public boolean isErrored() {
        return isErrored.get();
    }
    
    protected AdvancedSocket setIsErrored(boolean isErrored) {
        this.isErrored.set(isErrored);
        return this;
    }
    
    public Throwable getError() {
        return error.get();
    }
    
    protected AdvancedSocket setError(Throwable error) {
        this.error.set(error);
        return this;
    }
    
    protected AdvancedSocket error(Throwable error) {
        setIsErrored(true);
        setError(error);
        setLocalCloseRequested(false);
        return this;
    }
    
    public AdvancedSocket resetError() {
        setIsErrored(false);
        setError(null);
        return this;
    }
    
    protected boolean onConnection(boolean successful) throws Exception {
        return successful;
    }
    
    protected boolean onDisconnection() throws Exception {
        return true;
    }
    
    @Override
    public AdvancedSocket setSocket(Socket socket) {
        Objects.requireNonNull(socket);
        setInetAddress(socket.getInetAddress());
        setPort(socket.getPort());
        this.socket = socket;
        resetError();
        setConnected(socket.isConnected() && !socket.isClosed());
        final boolean successful = isConnected();
        try {
            if (successful) {
                outputStream = socket.getOutputStream();
                inputStream = socket.getInputStream();
            }
        } catch (Exception ex) {
            throw new NetRuntimeException(ex);
        }
        try {
            onConnection(successful);
        } catch (Exception ex) {
            throw new NetRuntimeException(ex);
        }
        return this;
    }
    
    @Override
    protected Socket createSocket() throws Exception {
        return new Socket(getInetAddress(), getPort());
    }
    
    @Override
    public boolean connect(boolean reconnect) throws Exception {
        if (isConnected()) {
            return false;
        }
        closeIntern();
        setLocalCloseRequested(false);
        if (reconnect) {
            socket = null;
        }
        socket = createSocket();
        resetError();
        setConnected(socket != null && socket.isConnected() && !socket.isClosed());
        final boolean successful = isConnected();
        if (successful) {
            outputStream = socket.getOutputStream();
            inputStream = socket.getInputStream();
        }
        return onConnection(successful);
    }
    
    @Override
    public boolean disconnect() throws Exception {
        if (!isConnected()) {
            return false;
        }
        setLocalCloseRequested(true);
        onDisconnection();
        closeIntern();
        return true;
    }
    
    protected void closeIntern() throws Exception {
        close();
        socket = null;
        outputStream = null;
        inputStream = null;
    }
    
    @Override
    public void close() throws IOException {
        try {
            if (outputStream != null) {
                outputStream.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
            if (socket != null) {
                socket.close();
            }
            setConnected(false);
        } catch (Exception ex) {
            setConnected(false);
            throw new IOException(ex);
        }
    }
    
    @Override
    public String toString() {
        return "AdvancedSocket{" + "connected=" + connected + ", localCloseRequested=" + localCloseRequested + ", isErrored=" + isErrored + ", error=" + error + ", netEndpoint=" + netEndpoint + ", socket=" + socket + ", outputStream=" + outputStream + ", inputStream=" + inputStream + '}';
    }
    
}
