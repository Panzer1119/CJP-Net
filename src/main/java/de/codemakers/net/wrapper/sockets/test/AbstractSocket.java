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

package de.codemakers.net.wrapper.sockets.test;

import de.codemakers.base.util.interfaces.Connectable;
import de.codemakers.base.util.interfaces.Disconnectable;
import de.codemakers.base.util.tough.ToughFunction;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

@Deprecated
public abstract class AbstractSocket implements Closeable, Connectable, Disconnectable {
    
    protected InetAddress inetAddress;
    protected int port;
    protected Socket socket = null;
    protected OutputStream outputStream = null;
    protected InputStream inputStream = null;
    protected final AtomicBoolean connected = new AtomicBoolean(false);
    protected final AtomicBoolean localCloseRequested = new AtomicBoolean(false);
    
    public AbstractSocket(InetAddress inetAddress, int port) {
        Objects.requireNonNull(inetAddress);
        this.inetAddress = inetAddress;
        this.port = port;
        //this.socket = createSocket(); //Not a good idea, maybe you do not want to instantly connect to the server, when this constructor was called
    }
    
    public AbstractSocket(Socket socket) {
        Objects.requireNonNull(socket);
        this.socket = socket;
        this.inetAddress = socket.getInetAddress();
        this.port = socket.getPort();
        connected.set(socket.isConnected());
    }
    
    public InetAddress getInetAddress() {
        return inetAddress;
    }
    
    public int getPort() {
        return port;
    }
    
    public Socket getSocket() {
        return socket;
    }
    
    public <T extends OutputStream> T getOutputStream() {
        return (T) outputStream;
    }
    
    public <T extends InputStream> T getInputStream() {
        return (T) inputStream;
    }
    
    public AbstractSocket processOutputStream(ToughFunction<OutputStream, OutputStream> function) {
        if (function != null) {
            final OutputStream outputStream = function.applyWithoutException(this.outputStream);
            if (outputStream != null) {
                this.outputStream = outputStream;
            }
        }
        return this;
    }
    
    public AbstractSocket processInputStream(ToughFunction<InputStream, InputStream> function) {
        if (function != null) {
            final InputStream inputStream = function.applyWithoutException(this.inputStream);
            if (inputStream != null) {
                this.inputStream = inputStream;
            }
        }
        return this;
    }
    
    public boolean isConnected() {
        return connected.get() || isSocketNotClosedAndConnected();
    }
    
    protected boolean isSocketConnected() {
        return socket != null && socket.isConnected();
    }
    
    protected boolean isSocketClosed() {
        return socket != null && socket.isClosed();
    }
    
    protected boolean isSocketNotClosedAndConnected() {
        return socket != null && !socket.isClosed() && socket.isConnected();
    }
    
    protected abstract Socket connectIntern(boolean reconnect) throws Exception;
    
    @Override
    public boolean connect(boolean reconnect) throws Exception {
        if (isConnected()) {
            return false; //throw new AlreadyBoundException();
        }
        localCloseRequested.set(false);
        if (reconnect) {
            socket = null;
        }
        return (socket = connectIntern(reconnect)) != null;
    }
    
    @Override
    public boolean disconnect() throws Exception {
        if (!isConnected()) {
            return false;
        }
        if (socket != null) {
            close();
            socket = null;
            inputStream = null;
            outputStream = null;
        }
        //connected.set(false);
        return !isConnected();
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
        connected.set(false);
    }
    
}
