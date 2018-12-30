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

import de.codemakers.base.logger.Logger;
import de.codemakers.base.util.interfaces.Connectable;
import de.codemakers.base.util.interfaces.Disconnectable;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Objects;

public abstract class NormalSocket extends AbstractSocket implements Closeable, Connectable, Disconnectable {
    
    protected boolean connected = false;
    protected boolean localCloseRequested = false;
    
    public NormalSocket(InetAddress inetAddress, int port) {
        super(inetAddress, port);
    }
    
    public NormalSocket(Socket socket) {
        super(socket);
    }
    
    @Override
    public NormalSocket setInetAddress(InetAddress inetAddress) {
        Objects.requireNonNull(inetAddress);
        this.inetAddress = inetAddress;
        return this;
    }
    
    @Override
    public NormalSocket setPort(int port) {
        this.port = port;
        return this;
    }
    
    @Override
    public NormalSocket setSocket(Socket socket) {
        Objects.requireNonNull(socket);
        setInetAddress(socket.getInetAddress());
        setPort(socket.getPort());
        this.socket = socket;
        connected = socket.isConnected() && !socket.isClosed();
        if (connected) {
            try {
                connected();
            } catch (Exception ex) {
                Logger.handleError(ex);
            }
        }
        return this;
    }
    
    public boolean isConnected() {
        return connected;
    }
    
    protected abstract void connected() throws Exception;
    
    protected abstract Socket createSocket(boolean reconnect) throws Exception;
    
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
        socket = createSocket(reconnect);
        connected = socket != null && socket.isConnected() && !socket.isClosed();
        if (isConnected()) {
            connected();
            return true;
        }
        return false;
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
