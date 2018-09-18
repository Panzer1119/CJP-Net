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

import de.codemakers.base.CJP;
import de.codemakers.base.action.ReturningAction;
import de.codemakers.base.util.tough.ToughConsumer;
import de.codemakers.net.entities.Response;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public abstract class SingleResponseServerSocket extends AbstractServerSocket {
    
    protected final CJP cjp;
    protected boolean multithreaded = true;
    
    public SingleResponseServerSocket(ServerSocket serverSocket) {
        this(serverSocket, CJP.getInstance());
    }
    
    public SingleResponseServerSocket(ServerSocket serverSocket, CJP cjp) {
        super(serverSocket);
        this.cjp = cjp;
    }
    
    public SingleResponseServerSocket(int port) {
        this(port, CJP.getInstance());
    }
    
    public SingleResponseServerSocket(int port, CJP cjp) {
        super(port);
        this.cjp = cjp;
    }
    
    public final boolean isMultithreaded() {
        return multithreaded;
    }
    
    public final SingleResponseServerSocket setMultithreaded(boolean multithreaded) {
        this.multithreaded = multithreaded;
        return this;
    }
    
    public abstract Object processRequest(Socket socket, Object request) throws Exception;
    
    @Override
    protected void processSocket(long timestamp, Socket socket) throws Exception {
        final ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        final ReturningAction<Object> returningAction = new ReturningAction<>(cjp, () -> {
            final ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            return processRequest(socket, objectInputStream.readObject());
        });
        final ToughConsumer<Object> respond = (object) -> {
            objectOutputStream.writeObject(object);
            objectOutputStream.flush();
            socket.close();
        };
        final ToughConsumer<Object> success = (object) -> respond.accept(new Response<>(object));
        final ToughConsumer<Throwable> failure = (throwable) -> respond.accept(new Response<>(throwable));
        if (multithreaded) {
            returningAction.queue(success, failure);
        } else {
            returningAction.queueSingle(success, failure);
        }
    }
    
    @Override
    protected void processStop(long timestamp, boolean ok, boolean local, Throwable throwable) throws Exception {
        if (!ok && local) {
            stop();
            start(); //TODO Test this, prevent an infinite loop
        }
    }
    
}