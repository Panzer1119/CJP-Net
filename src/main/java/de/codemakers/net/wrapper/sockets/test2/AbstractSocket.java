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

import de.codemakers.base.util.tough.ToughFunction;
import de.codemakers.net.entities.NetEndpoint;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public abstract class AbstractSocket<THIS> {
    
    protected final NetEndpoint netEndpoint = new NetEndpoint(null, -1);
    protected Socket socket = null;
    protected OutputStream outputStream = null;
    protected InputStream inputStream = null;
    
    public AbstractSocket(InetAddress inetAddress, int port) {
        setInetAddress(inetAddress);
        setPort(port);
    }
    
    public AbstractSocket(NetEndpoint netEndpoint) {
        setFromEndpoint(netEndpoint);
    }
    
    public AbstractSocket(Socket socket) {
        this(socket.getInetAddress(), socket.getPort());
        setSocket(socket);
    }
    
    public InetAddress getInetAddress() {
        return netEndpoint.getInetAddress();
    }
    
    public THIS setInetAddress(InetAddress inetAddress) {
        netEndpoint.setInetAddress(inetAddress);
        return (THIS) this;
    }
    
    public int getPort() {
        return netEndpoint.getPort();
    }
    
    public THIS setPort(int port) {
        netEndpoint.setPort(port);
        return (THIS) this;
    }
    
    public NetEndpoint getNetEndpoint() {
        return netEndpoint;
    }
    
    public THIS setFromEndpoint(NetEndpoint netEndpoint) {
        setInetAddress(netEndpoint.getInetAddress());
        setPort(netEndpoint.getPort());
        return (THIS) this;
    }
    
    public Socket getSocket() {
        return socket;
    }
    
    public abstract THIS setSocket(Socket socket);
    
    protected abstract Socket createSocket() throws Exception;
    
    public <T extends OutputStream> T getOutputStream(Class<T> clazz) {
        return (T) outputStream;
    }
    
    public <T extends OutputStream> T getOutputStream() {
        return (T) outputStream;
    }
    
    public THIS processOutputStream(ToughFunction<OutputStream, OutputStream> toughFunction) {
        if (toughFunction != null) {
            final OutputStream outputStream = toughFunction.applyWithoutException(this.outputStream);
            if (outputStream != null) {
                this.outputStream = outputStream;
            }
        }
        return (THIS) this;
    }
    
    public <T extends InputStream> T getInputStream(Class<T> clazz) {
        return (T) inputStream;
    }
    
    public <T extends InputStream> T getInputStream() {
        return (T) inputStream;
    }
    
    public THIS processInputStream(ToughFunction<InputStream, InputStream> toughFunction) {
        if (toughFunction != null) {
            final InputStream inputStream = toughFunction.applyWithoutException(this.inputStream);
            if (inputStream != null) {
                this.inputStream = inputStream;
            }
        }
        return (THIS) this;
    }
    
    @Override
    public String toString() {
        return "AbstractSocket{" + "netEndpoint=" + netEndpoint + ", socket=" + socket + ", outputStream=" + outputStream + ", inputStream=" + inputStream + '}';
    }
    
}
