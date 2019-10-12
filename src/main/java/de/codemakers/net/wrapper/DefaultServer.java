/*
 *     Copyright 2018 - 2019 Paul Hagedorn (Panzer1119)
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

import de.codemakers.base.util.tough.ToughConsumer;
import de.codemakers.base.util.tough.ToughFunction;
import de.codemakers.net.wrapper.sockets.test2.AdvancedSocket;
import de.codemakers.net.wrapper.sockets.test2.server.DefaultServerSocket;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class DefaultServer<S extends AdvancedSocket, SS extends DefaultServerSocket<S>, C extends AdvancedClient<S>> extends AdvancedServer<S, SS, C> {
    
    protected final List<ToughConsumer<C>> listeners = new CopyOnWriteArrayList<>();
    protected final ToughFunction<S, C> socketConverter;
    
    public DefaultServer(SS serverSocket, ToughFunction<S, C> socketConverter) {
        super(serverSocket);
        this.socketConverter = Objects.requireNonNull(socketConverter, "socketConverter");
    }
    
    public List<ToughConsumer<C>> getListeners() {
        return listeners;
    }
    
    public DefaultServer addListener(ToughConsumer<C> socketConsumer) {
        listeners.add(socketConsumer);
        return this;
    }
    
    public DefaultServer removeListener(ToughConsumer<C> socketConsumer) {
        listeners.remove(socketConsumer);
        return this;
    }
    
    public DefaultServer clearListeners() {
        listeners.clear();
        return this;
    }
    
    public ToughFunction<S, C> getSocketConverter() {
        return socketConverter;
    }
    
    @Override
    protected void onClient(C client) throws Exception {
        listeners.forEach((toughConsumer) -> toughConsumer.acceptWithoutException(client));
    }
    
    @Override
    protected C processSocket(S socket) throws Exception {
        return socketConverter.apply(socket);
    }
    
    @Override
    public String toString() {
        return "DefaultServer{" + "socketConverter=" + socketConverter + ", serverSocket=" + serverSocket + ", clients=" + clients + '}';
    }
    
}
