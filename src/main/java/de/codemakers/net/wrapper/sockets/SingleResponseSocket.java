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
import de.codemakers.base.exceptions.CJPException;
import de.codemakers.base.util.tough.ToughConsumer;
import de.codemakers.net.entities.Request;
import de.codemakers.net.entities.Response;
import de.codemakers.net.exceptions.NotAcceptedResponseException;
import de.codemakers.net.exceptions.NotConnectedException;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Objects;
import java.util.concurrent.Future;

public class SingleResponseSocket extends AbstractSocket {
    
    protected final CJP cjp;
    
    public SingleResponseSocket(Socket socket) {
        this(socket, CJP.createInstance());
    }
    
    public SingleResponseSocket(Socket socket, CJP cjp) {
        super(socket);
        this.cjp = cjp == null ? CJP.createInstance() : cjp;
    }
    
    public SingleResponseSocket(InetAddress inetAddress, int port) {
        this(inetAddress, port, CJP.createInstance());
    }
    
    public SingleResponseSocket(InetAddress inetAddress, int port, CJP cjp) {
        super(inetAddress, port);
        this.cjp = cjp == null ? CJP.createInstance() : cjp;
    }
    
    @Override
    protected void processInput(long timestamp, Object input) throws Exception {
        throw new UnsupportedOperationException();
    }
    
    @Override
    protected void processDisconnect(long timestamp, boolean ok, boolean local, Throwable throwable) throws Exception {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean send(Object object) throws Exception {
        throw new UnsupportedOperationException();
    }
    
    public <T, R> ReturningAction<Response<R>> requestResponse(T request) {
        return new ReturningAction<Response<R>>(cjp, () -> {
            Response<R> response = null;
            try {
                if (!connect(true)) {
                    throw new NotConnectedException(Response.class.getSimpleName() + " request failed, because the " + getClass().getSimpleName() + " could not connect to the " + SingleResponseServerSocket.class.getSimpleName());
                }
                final ObjectOutputStream objectOutputStream = new ObjectOutputStream(getOutputStream());
                final Request<T> request_ = new Request<>(request);
                objectOutputStream.writeObject(request_);
                final ObjectInputStream objectInputStream = new ObjectInputStream(getInputStream()); //FIXME Maybe do this before sending the request? because maybe this is too slow and the response can not be received
                response = (Response<R>) objectInputStream.readObject();
                Objects.requireNonNull(response);
                if (response.getId() != request_.getId()) {
                    throw new NotAcceptedResponseException(response.getClass().getSimpleName() + " id does not match " + request_.getClass().getSimpleName() + " id");
                }
            } catch (Exception ex) {
                disconnectWithoutException();
                throw new CJPException(ex);
            }
            disconnectWithoutException();
            return response;
        }) {
            @Override
            public void queue(ToughConsumer<Response<R>> success, ToughConsumer<Throwable> failure) {
                super.queueSingle(success, failure);
            }
            
            @Override
            public Future<Response<R>> submit() {
                return super.submitSingle();
            }
        };
    }
    
}
