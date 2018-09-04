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
import java.util.List;

public class AdvancedSocket implements Closeable, IEventHandler<ClientEvent>, Startable, Stoppable {
    
    private final EventHandler<ClientEvent> clientEventHandler = new EventHandler<>();
    
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
