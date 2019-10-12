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

package de.codemakers.net.wrapper.sockets;

import de.codemakers.base.CJP;
import de.codemakers.base.action.ReturningAction;
import de.codemakers.base.util.IDTimeUtil;
import de.codemakers.base.util.tough.ToughConsumer;
import de.codemakers.net.entities.Request;
import de.codemakers.net.entities.Response;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicLong;

@Deprecated
public abstract class SingleResponseServerSocket extends AbstractServerSocket {
    
    protected final CJP cjp;
    protected boolean multithreaded = true;
    
    public SingleResponseServerSocket(ServerSocket serverSocket) {
        this(serverSocket, CJP.createInstance());
    }
    
    public SingleResponseServerSocket(ServerSocket serverSocket, CJP cjp) {
        super(serverSocket);
        this.cjp = cjp == null ? CJP.createInstance() : cjp;
    }
    
    public SingleResponseServerSocket(int port) {
        this(port, CJP.createInstance());
    }
    
    public SingleResponseServerSocket(int port, CJP cjp) {
        super(port);
        this.cjp = cjp == null ? CJP.createInstance() : cjp;
    }
    
    public final boolean isMultithreaded() {
        return multithreaded;
    }
    
    public final SingleResponseServerSocket setMultithreaded(boolean multithreaded) {
        this.multithreaded = multithreaded;
        return this;
    }
    
    public abstract <T> Response<?> processRequest(Socket socket, Request<T> request) throws Exception;
    
    @Override
    protected void processSocket(long timestamp, Socket socket) throws Exception {
        final ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        final AtomicLong responseId = new AtomicLong(IDTimeUtil.createId());
        final ReturningAction<Response<?>> returningAction = new ReturningAction<>(cjp, () -> {
            final ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            final Request<?> request = (Request<?>) objectInputStream.readObject();
            if (request != null) {
                responseId.set(request.getId());
            }
            return processRequest(socket, request);
        });
        final ToughConsumer<Response<?>> responder = (response) -> {
            objectOutputStream.writeObject(response);
            objectOutputStream.flush();
            socket.close();
        };
        if (multithreaded) {
            returningAction.queue(responder, (throwable) -> responder.accept(new Response<>(responseId.get(), throwable)));
        } else {
            returningAction.queueSingle(responder, (throwable) -> responder.accept(new Response<>(responseId.get(), throwable)));
        }
    }
    
    @Override
    protected void processStop(long timestamp, boolean ok, boolean local, Throwable throwable) throws Exception {
        if (!ok && local) {
            stop();
            start();
        }
    }
    
}
