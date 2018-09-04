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

import de.codemakers.base.events.EventHandler;
import de.codemakers.base.events.EventListener;
import de.codemakers.base.events.IEventHandler;
import de.codemakers.net.events.SocketAcceptedEvent;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class AdvancedServerSocket extends AbstractServerSocket implements IEventHandler<SocketAcceptedEvent> {
    
    private final EventHandler<SocketAcceptedEvent> socketAcceptedEventHandler = new EventHandler<>();
    
    public AdvancedServerSocket(ServerSocket serverSocket) {
        super(serverSocket);
    }
    
    public AdvancedServerSocket(int port) {
        super(port);
    }
    
    @Override
    protected void processSocket(Socket socket, long timestamp) throws Exception {
        onEvent(new SocketAcceptedEvent(timestamp, socket));
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
    
}
