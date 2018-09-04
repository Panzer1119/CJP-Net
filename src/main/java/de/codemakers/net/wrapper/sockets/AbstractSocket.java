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

import de.codemakers.base.exceptions.NotYetImplementedRuntimeException;
import de.codemakers.base.util.interfaces.Startable;
import de.codemakers.base.util.interfaces.Stoppable;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractSocket implements Closeable, Startable, Stoppable {
    
    private InetAddress inetAddress = null;
    private int port = -1;
    private Socket socket = null;
    private Thread thread = null;
    private AtomicBoolean running = new AtomicBoolean(false);
    
    public AbstractSocket(Socket socket) {
        this.socket = socket;
        if (socket != null) {
            inetAddress = socket.getInetAddress();
            port = socket.getPort();
        }
    }
    
    public AbstractSocket(InetAddress inetAddress, int port) {
        this.inetAddress = inetAddress;
        this.port = port;
    }
    
    protected abstract void processInput(long timestamp, byte[] data) throws Exception;
    
    protected abstract void processDisconnect(long timestamp, boolean ok, boolean local, Throwable throwable) throws Exception;
    
    public final boolean isRunning() {
        return running.get();
    }
    
    public final InetAddress getInetAddress() {
        return inetAddress;
    }
    
    public final AbstractSocket setInetAddress(InetAddress inetAddress) {
        this.inetAddress = inetAddress;
        return this;
    }
    
    public final int getPort() {
        return port;
    }
    
    public final AbstractSocket setPort(int port) {
        this.port = port;
        return this;
    }
    
    public final Socket getSocket() {
        return socket;
    }
    
    public final AbstractSocket setSocket(Socket socket) {
        this.socket = socket;
        return this;
    }
    
    @Override
    public void start() throws Exception {
        //TODO Implement
        throw new NotYetImplementedRuntimeException();
    }
    
    @Override
    public void stop() throws Exception {
        //TODO Implement
        throw new NotYetImplementedRuntimeException();
    }
    
    @Override
    public void close() throws IOException {
        //TODO Implement
        throw new NotYetImplementedRuntimeException();
    }
    
}
