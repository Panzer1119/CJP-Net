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

import de.codemakers.base.util.tough.ToughFunction;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public abstract class AbstractSocket {
    
    protected InetAddress inetAddress = null;
    protected int port = -1;
    protected Socket socket = null;
    protected OutputStream outputStream = null;
    protected InputStream inputStream = null;
    
    public AbstractSocket(InetAddress inetAddress, int port) {
        setInetAddress(inetAddress);
        setPort(port);
    }
    
    public AbstractSocket(Socket socket) {
        this(socket.getInetAddress(), socket.getPort());
        setSocket(socket);
    }
    
    public InetAddress getInetAddress() {
        return inetAddress;
    }
    
    public abstract AbstractSocket setInetAddress(InetAddress inetAddress);
    
    public int getPort() {
        return port;
    }
    
    public abstract AbstractSocket setPort(int port);
    
    public Socket getSocket() {
        return socket;
    }
    
    public abstract AbstractSocket setSocket(Socket socket);
    
    public <T extends OutputStream> T getOutputStream(Class<T> clazz) {
        return (T) outputStream;
    }
    
    public <T extends OutputStream> T getOutputStream() {
        return (T) outputStream;
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
    
    public <T extends InputStream> T getInputStream(Class<T> clazz) {
        return (T) inputStream;
    }
    
    public <T extends InputStream> T getInputStream() {
        return (T) inputStream;
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
    
    public abstract boolean isConnected();
    
    @Override
    public String toString() {
        return "AbstractSocket{" + "inetAddress=" + inetAddress + ", port=" + port + ", socket=" + socket + ", outputStream=" + outputStream + ", inputStream=" + inputStream + '}';
    }
    
}
