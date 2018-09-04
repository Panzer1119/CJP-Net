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

package de.codemakers.net.wrapper.advanced;

import de.codemakers.base.events.EventHandler;
import de.codemakers.base.events.EventListener;
import de.codemakers.base.events.IEventHandler;
import de.codemakers.base.exceptions.NotYetImplementedRuntimeException;
import de.codemakers.base.util.interfaces.Startable;
import de.codemakers.base.util.interfaces.Stoppable;
import de.codemakers.net.events.ClientEvent;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class AdvancedSocket implements Closeable, IEventHandler<ClientEvent>, Startable, Stoppable {
    
    private final EventHandler<ClientEvent> clientEventHandler = new EventHandler<>();
    
    private InetAddress inetAddress = null;
    private int port = -1;
    private Socket socket = null;
    private Thread thread = null;
    private AtomicBoolean running = new AtomicBoolean(false);
    
    public AdvancedSocket(Socket socket) {
        this.socket = socket;
        if (socket != null) {
            inetAddress = socket.getInetAddress();
            port = socket.getPort();
        }
    }
    
    public AdvancedSocket(InetAddress inetAddress, int port) {
        this.inetAddress = inetAddress;
        this.port = port;
    }
    
    @Override
    public IEventHandler<ClientEvent> addEventListener(Class<ClientEvent> aClass, EventListener<ClientEvent> eventListener) {
        return clientEventHandler.addEventListener(aClass, eventListener);
    }
    
    @Override
    public IEventHandler<ClientEvent> removeEventListener(Class<ClientEvent> aClass, EventListener<ClientEvent> eventListener) {
        return clientEventHandler.removeEventListener(aClass, eventListener);
    }
    
    @Override
    public IEventHandler<ClientEvent> clearEventListeners() {
        return clientEventHandler.clearEventListeners();
    }
    
    @Override
    public List<EventListener<ClientEvent>> getEventListeners(Class<ClientEvent> aClass) {
        return clientEventHandler.getEventListeners(aClass);
    }
    
    @Override
    public void onEvent(ClientEvent clientEvent) {
        clientEventHandler.onEvent(clientEvent);
    }
    
    public final boolean isRunning() {
        return running.get();
    }
    
    public final InetAddress getInetAddress() {
        return inetAddress;
    }
    
    public final AdvancedSocket setInetAddress(InetAddress inetAddress) {
        this.inetAddress = inetAddress;
        return this;
    }
    
    public final int getPort() {
        return port;
    }
    
    public final AdvancedSocket setPort(int port) {
        this.port = port;
        return this;
    }
    
    public final Socket getSocket() {
        return socket;
    }
    
    public final AdvancedSocket setSocket(Socket socket) {
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
