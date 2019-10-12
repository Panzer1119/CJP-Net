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

package de.codemakers.net.wrapper.sockets.test2.server;

import de.codemakers.base.util.tough.ToughConsumer;
import de.codemakers.base.util.tough.ToughFunction;
import de.codemakers.net.wrapper.sockets.test2.AbstractSocket;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class DefaultServerSocket<S extends AbstractSocket> extends ProcessingServerSocket<S> {
    
    protected final List<ToughConsumer<S>> listeners = new CopyOnWriteArrayList<>();
    protected final ToughFunction<Socket, S> socketConverter;
    
    public DefaultServerSocket(int port, ToughFunction<Socket, S> socketConverter) {
        super(port);
        this.socketConverter = Objects.requireNonNull(socketConverter, "socketConverter");
    }
    
    public DefaultServerSocket(ServerSocket serverSocket, ToughFunction<Socket, S> socketConverter) {
        super(serverSocket);
        this.socketConverter = Objects.requireNonNull(socketConverter, "socketConverter");
    }
    
    public List<ToughConsumer<S>> getListeners() {
        return listeners;
    }
    
    public DefaultServerSocket addListener(ToughConsumer<S> socketConsumer) {
        listeners.add(socketConsumer);
        return this;
    }
    
    public DefaultServerSocket removeListener(ToughConsumer<S> socketConsumer) {
        listeners.remove(socketConsumer);
        return this;
    }
    
    public DefaultServerSocket clearListeners() {
        listeners.clear();
        return this;
    }
    
    public ToughFunction<Socket, S> getSocketConverter() {
        return socketConverter;
    }
    
    @Override
    protected void onSocket(S socket) throws Exception {
        listeners.forEach((toughConsumer) -> toughConsumer.acceptWithoutException(socket));
    }
    
    @Override
    protected S processSocket(Socket socket) throws Exception {
        return socketConverter.apply(socket);
    }
    
    @Override
    public String toString() {
        return "DefaultServerSocket{" + "listeners=" + listeners + ", socketConverter=" + socketConverter + ", thread=" + thread + ", cjp=" + cjp + ", started=" + started + ", stopRequested=" + stopRequested + ", errored=" + errored + ", error=" + error + ", port=" + port + ", serverSocket=" + serverSocket + '}';
    }
    
}
