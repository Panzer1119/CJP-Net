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

import de.codemakers.base.action.RunningAction;
import de.codemakers.base.events.EventHandler;
import de.codemakers.base.events.EventListener;
import de.codemakers.base.events.IEventHandler;
import de.codemakers.net.events.DisconnectedEvent;
import de.codemakers.net.events.NetEvent;
import de.codemakers.net.events.ObjectReceived;

import java.io.ObjectOutputStream;
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
    public <E extends NetEvent> IEventHandler<NetEvent> addEventListener(Class<E> aClass, EventListener<E> eventListener) {
        return netEventHandler.addEventListener(aClass, eventListener);
    }
    
    @Override
    public <E extends NetEvent> IEventHandler<NetEvent> removeEventListener(Class<E> aClass, EventListener<E> eventListener) {
        return netEventHandler.removeEventListener(aClass, eventListener);
    }
    
    @Override
    public IEventHandler<NetEvent> clearEventListeners() {
        return netEventHandler.clearEventListeners();
    }
    
    @Override
    public <E extends NetEvent> List<EventListener<E>> getEventListeners(Class<E> aClass) {
        return netEventHandler.getEventListeners(aClass);
    }
    
    @Override
    public void onEvent(NetEvent netEvent) {
        netEventHandler.onEvent(netEvent);
    }
    
    @Override
    protected void processInput(long timestamp, Object input) throws Exception {
        onEvent(new ObjectReceived(timestamp, input));
    }
    
    @Override
    protected void processDisconnect(long timestamp, boolean ok, boolean local, Throwable throwable) throws Exception {
        onEvent(new DisconnectedEvent(timestamp, ok, local, throwable));
    }
    
    @Override
    public boolean send(Object object) throws Exception {
        if (!isObjectOutputStream()) {
            throw new UnsupportedOperationException(getClass().getSimpleName() + " has no ObjectOutputStream");
        }
        ((ObjectOutputStream) getOutputStream()).writeObject(object);
        getOutputStream().flush();
        return true;
    }
    
    public boolean isObjectOutputStream() {
        return getOutputStream() instanceof ObjectOutputStream;
    }
    
    public RunningAction sendObject(Object object) {
        return new RunningAction(() -> send(object));
    }
    
    public RunningAction sendData(byte[] data) {
        return new RunningAction(() -> send(data));
    }
    
}
