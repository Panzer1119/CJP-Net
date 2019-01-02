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

package de.codemakers.net.wrapper.sockets.test2;

import de.codemakers.base.util.interfaces.Connectable;
import de.codemakers.base.util.interfaces.Disconnectable;
import de.codemakers.net.exceptions.NetRuntimeException;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Objects;

public abstract class NormalSocket extends AbstractSocket implements Closeable, Connectable, Disconnectable {
    
    protected volatile boolean connected = false;
    protected volatile boolean localCloseRequested = false;
    
    public NormalSocket(InetAddress inetAddress, int port) {
        super(inetAddress, port);
    }
    
    public NormalSocket(Socket socket) {
        super(socket);
    }
    
    public boolean isConnected() {
        return connected;
    }
    
    public boolean isLocalCloseRequested() {
        return localCloseRequested;
    }
    
    protected abstract void onConnection(boolean successful) throws Exception;
    
    @Override
    public NormalSocket setSocket(Socket socket) {
        Objects.requireNonNull(socket);
        setInetAddress(socket.getInetAddress());
        setPort(socket.getPort());
        this.socket = socket;
        connected = socket.isConnected() && !socket.isClosed();
        try {
            onConnection(isConnected());
        } catch (Exception ex) {
            throw new NetRuntimeException(ex);
        }
        return this;
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
        connected = socket != null && socket.isConnected() && !socket.isClosed();
        final boolean success = isConnected();
        onConnection(success);
        return success;
    }
    
    @Override
    public boolean disconnect() throws Exception {
        if (!isConnected()) {
            return false;
        }
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
        localCloseRequested = true;
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
        return "NormalSocket{" + "connected=" + connected + ", localCloseRequested=" + localCloseRequested + ", inetAddress=" + inetAddress + ", port=" + port + ", socket=" + socket + ", outputStream=" + outputStream + ", inputStream=" + inputStream + '}';
    }
    
}
