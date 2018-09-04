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
import de.codemakers.net.events.DataReceivedEvent;
import de.codemakers.net.events.DisconnectedEvent;
import de.codemakers.net.events.NetEvent;

import java.net.InetAddress;
import java.net.Socket;
import java.util.List;

public class AdvancedSocket extends AbstractSocket implements IEventHandler<NetEvent> {
    
    private final EventHandler<NetEvent> netEventHandler = new EventHandler<>();
    
    public AdvancedSocket(Socket socket) {
        super(socket);
    }
    
    public AdvancedSocket(InetAddress inetAddress, int port) {
        super(inetAddress, port);
    }
    
    @Override
    public IEventHandler<NetEvent> addEventListener(Class<NetEvent> aClass, EventListener<NetEvent> eventListener) {
        return netEventHandler.addEventListener(aClass, eventListener);
    }
    
    @Override
    public IEventHandler<NetEvent> removeEventListener(Class<NetEvent> aClass, EventListener<NetEvent> eventListener) {
        return netEventHandler.removeEventListener(aClass, eventListener);
    }
    
    @Override
    public IEventHandler<NetEvent> clearEventListeners() {
        return netEventHandler.clearEventListeners();
    }
    
    @Override
    public List<EventListener<NetEvent>> getEventListeners(Class<NetEvent> aClass) {
        return netEventHandler.getEventListeners(aClass);
    }
    
    @Override
    public void onEvent(NetEvent netEvent) {
        netEventHandler.onEvent(netEvent);
    }
    
    @Override
    protected void processInput(long timestamp, byte[] data) throws Exception {
        onEvent(new DataReceivedEvent(timestamp, data));
    }
    
    @Override
    protected void processDisconnect(long timestamp, boolean ok, boolean local, Throwable throwable) throws Exception {
        onEvent(new DisconnectedEvent(timestamp, ok, local, throwable));
    }
    
}
