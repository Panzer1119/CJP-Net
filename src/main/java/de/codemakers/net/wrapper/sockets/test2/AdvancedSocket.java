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

public class AdvancedSocket extends AbstractSocket implements Closeable, Connectable, Disconnectable {
    
    protected volatile boolean connected = false;
    protected volatile boolean localCloseRequested = false;
    protected volatile boolean isErrored = false;
    protected volatile Throwable error = null;
    
    public AdvancedSocket(InetAddress inetAddress, int port) {
        super(inetAddress, port);
    }
    
    public AdvancedSocket(Socket socket) {
        super(socket);
    }
    
    public boolean isConnected() {
        return connected && !isErrored;
    }
    
    public boolean isLocalCloseRequested() {
        return localCloseRequested;
    }
    
    public boolean isErrored() {
        return isErrored;
    }
    
    public Throwable getError() {
        return error;
    }
    
    protected AdvancedSocket error(Throwable error) {
        this.isErrored = true;
        this.error = error;
        this.localCloseRequested = false;
        return this;
    }
    
    public AdvancedSocket resetError() {
        isErrored = false;
        error = null;
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
        connected = socket.isConnected() && !socket.isClosed();
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
        return new Socket(inetAddress, port);
    }
    
    @Override
    public boolean connect(boolean reconnect) throws Exception {
        if (isConnected()) {
            return false;
        }
        closeIntern();
        localCloseRequested = false;
        if (reconnect) {
            socket = null;
        }
        socket = createSocket();
        resetError();
        connected = socket != null && socket.isConnected() && !socket.isClosed();
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
        localCloseRequested = true;
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
        if (outputStream != null) {
            outputStream.close();
        }
        if (inputStream != null) {
            inputStream.close();
        }
        if (socket != null) {
            socket.close();
        }
        connected = false;
    }
    
    @Override
    public String toString() {
        return "AdvancedSocket{" + "connected=" + connected + ", localCloseRequested=" + localCloseRequested + ", isErrored=" + isErrored + ", error=" + error + ", inetAddress=" + inetAddress + ", port=" + port + ", socket=" + socket + ", outputStream=" + outputStream + ", inputStream=" + inputStream + '}';
    }
    
}
