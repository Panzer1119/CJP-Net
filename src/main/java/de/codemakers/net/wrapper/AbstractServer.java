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

import de.codemakers.base.exceptions.CJPNullPointerException;
import de.codemakers.net.events.SocketAcceptedEvent;
import de.codemakers.net.wrapper.sockets.AdvancedServerSocket;

import java.net.ServerSocket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class AbstractServer extends AdvancedServerSocket {
    
    private final List<AbstractClient> clients = new CopyOnWriteArrayList<>();
    
    public AbstractServer(ServerSocket serverSocket) {
        super(serverSocket);
        init();
    }
    
    public AbstractServer(int port) {
        super(port);
        init();
    }
    
    private final void init() {
        addEventListener(SocketAcceptedEvent.class, (socketAcceptedEvent) -> {
            if (socketAcceptedEvent == null) {
                throw new CJPNullPointerException("SocketAcceptedEvent may not be null");
            }
            final AbstractClient client = processSocket(socketAcceptedEvent);
            if (client != null) {
                clients.add(client);
            }
            return false;
        });
    }
    
    abstract AbstractClient processSocket(SocketAcceptedEvent socketAcceptedEvent);
    
    public final List<AbstractClient> getClients() {
        return clients;
    }
    
}
