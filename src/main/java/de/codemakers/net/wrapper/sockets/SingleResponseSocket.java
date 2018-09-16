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

import de.codemakers.base.action.ReturningAction;
import de.codemakers.net.entities.Response;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class SingleResponseSocket extends AbstractSocket {
    
    public SingleResponseSocket(Socket socket) {
        super(socket);
    }
    
    public SingleResponseSocket(InetAddress inetAddress, int port) {
        super(inetAddress, port);
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
        return false;
    }
    
    public ReturningAction<Response> requestResponse(Object request) {
        return new ReturningAction<>(() -> {
            Object response = null;
            try {
                connect(true);
                final ObjectOutputStream objectOutputStream = new ObjectOutputStream(getOutputStream());
                objectOutputStream.writeObject(request);
                final ObjectInputStream objectInputStream = new ObjectInputStream(getInputStream());
                response = objectInputStream.readObject();
            } catch (Exception ex) {
                disconnectWithoutException();
                throw ex;
            }
            disconnectWithoutException();
            return (Response) response;
        });
    }
    
}
