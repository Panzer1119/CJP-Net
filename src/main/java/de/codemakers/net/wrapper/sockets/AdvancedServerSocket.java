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
import de.codemakers.net.events.DisconnectedEvent;
import de.codemakers.net.events.NetEvent;
import de.codemakers.net.events.SocketAcceptedEvent;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class AdvancedServerSocket extends AbstractServerSocket implements IEventHandler<NetEvent> {
    
    private final EventHandler<NetEvent> netEventHandler = new EventHandler<>();
    
    public AdvancedServerSocket(ServerSocket serverSocket) {
        super(serverSocket);
    }
    
    public AdvancedServerSocket(int port) {
        super(port);
    }
    
    @Override
    protected void processSocket(long timestamp, Socket socket) throws Exception {
        onEvent(new SocketAcceptedEvent(timestamp, socket));
    }
    
    @Override
    protected void processDisconnect(long timestamp, boolean ok, boolean local, Throwable throwable) throws Exception {
        onEvent(new DisconnectedEvent(timestamp, ok, local, throwable));
    }
    
    @Override
    public final <E extends NetEvent> IEventHandler<NetEvent> addEventListener(Class<E> aClass, EventListener<E> eventListener) {
        return netEventHandler.addEventListener(aClass, eventListener);
    }
    
    @Override
    public final <E extends NetEvent> IEventHandler<NetEvent> removeEventListener(Class<E> aClass, EventListener<E> eventListener) {
        return netEventHandler.removeEventListener(aClass, eventListener);
    }
    
    @Override
    public final IEventHandler<NetEvent> clearEventListeners() {
        return netEventHandler.clearEventListeners();
    }
    
    @Override
    public <E extends NetEvent> List<EventListener<E>> getEventListeners(Class<E> aClass) {
        return netEventHandler.getEventListeners(aClass);
    }
    
    @Override
    public final boolean onEvent(NetEvent socketAcceptedEvent) throws Exception {
        return netEventHandler.onEvent(socketAcceptedEvent);
    }
    
}
