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

package de.codemakers.net.wrapper;

import de.codemakers.base.events.EventHandler;
import de.codemakers.base.events.EventListener;
import de.codemakers.base.events.IEventHandler;
import de.codemakers.base.logger.Logger;
import de.codemakers.net.events.SocketAcceptedEvent;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class AdvancedServerSocket implements IEventHandler<SocketAcceptedEvent> {
    
    private final EventHandler<SocketAcceptedEvent> socketAcceptedEventHandler = new EventHandler<>();
    
    private int port = -1;
    private ServerSocket serverSocket = null;
    private Thread thread = null;
    private AtomicBoolean running = new AtomicBoolean(false);
    
    public AdvancedServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
        if (serverSocket != null) {
            port = serverSocket.getLocalPort();
        }
    }
    
    public AdvancedServerSocket(int port) {
        this.port = port;
    }
    
    @Override
    public final IEventHandler<SocketAcceptedEvent> addEventListener(Class<SocketAcceptedEvent> aClass, EventListener<SocketAcceptedEvent> eventListener) {
        return socketAcceptedEventHandler.addEventListener(aClass, eventListener);
    }
    
    @Override
    public final IEventHandler<SocketAcceptedEvent> removeEventListener(Class<SocketAcceptedEvent> aClass, EventListener<SocketAcceptedEvent> eventListener) {
        return socketAcceptedEventHandler.removeEventListener(aClass, eventListener);
    }
    
    @Override
    public final IEventHandler<SocketAcceptedEvent> clearEventListeners() {
        return socketAcceptedEventHandler.clearEventListeners();
    }
    
    @Override
    public final List<EventListener<SocketAcceptedEvent>> getEventListeners(Class<SocketAcceptedEvent> aClass) {
        return socketAcceptedEventHandler.getEventListeners(aClass);
    }
    
    @Override
    public final void onEvent(SocketAcceptedEvent socketAcceptedEvent) {
        socketAcceptedEventHandler.onEvent(socketAcceptedEvent);
    }
    
    private final boolean initThread() {
        if (thread != null) {
            return false;
        }
        thread = new Thread(() -> {
            running.set(true);
            try {
                while (isRunning()) {
                    final Socket socket = serverSocket.accept();
                    if (socket != null) {
                        socketAcceptedEventHandler.onEvent(new SocketAcceptedEvent(socket));
                    }
                }
            } catch (Exception ex) {
                running.set(false);
                Logger.handleError(ex);
            }
        });
        return true;
    }
    
    public final boolean startThread() {
        if (isRunning()) {
            return false;
        }
        if (thread == null) {
            initThread();
        }
        if (serverSocket == null) {
            throw new IllegalArgumentException("ServerSocket was not created");
        }
        thread.start();
        return true;
    }
    
    public final boolean isRunning() {
        return running.get();
    }
    
    public final int getPort() {
        return port;
    }
    
    public final AdvancedServerSocket setPort(int port) {
        this.port = port;
        return this;
    }
    
    public final ServerSocket getServerSocket() {
        return serverSocket;
    }
    
    public final AdvancedServerSocket setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
        return this;
    }
    
}
