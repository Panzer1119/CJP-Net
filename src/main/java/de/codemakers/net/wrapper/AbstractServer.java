/*
 *     Copyright 2018 - 2020 Paul Hagedorn (Panzer1119)
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

import de.codemakers.net.wrapper.sockets.test2.AbstractSocket;
import de.codemakers.net.wrapper.sockets.test2.server.DefaultServerSocket;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class AbstractServer<S extends AbstractSocket, SS extends DefaultServerSocket<S>, C extends AbstractClient<S>> {
    
    protected final SS serverSocket;
    protected final List<C> clients = new CopyOnWriteArrayList<>();
    
    public AbstractServer(SS serverSocket) {
        this.serverSocket = Objects.requireNonNull(serverSocket, "serverSocket");
        init();
    }
    
    public SS getServerSocket() {
        return serverSocket;
    }
    
    public List<C> getClients() {
        return clients;
    }
    
    private void init() {
        serverSocket.addListener((socket) -> {
            final C client = processSocket(socket);
            if (client != null) {
                clients.add(client);
                onClient(client);
            }
        });
    }
    
    protected abstract void onClient(C client) throws Exception;
    
    protected abstract C processSocket(S socket) throws Exception;
    
    @Override
    public String toString() {
        return "AbstractServer{" + "serverSocket=" + serverSocket + ", clients=" + clients + '}';
    }
    
}
