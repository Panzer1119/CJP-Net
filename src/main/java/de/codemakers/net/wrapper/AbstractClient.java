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

import de.codemakers.base.util.tough.ToughFunction;
import de.codemakers.net.entities.NetEndpoint;
import de.codemakers.net.wrapper.sockets.test2.AbstractSocket;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

public abstract class AbstractClient<S extends AbstractSocket> {
    
    protected final S socket;
    
    public AbstractClient(S socket) {
        this.socket = Objects.requireNonNull(socket, "socket");
    }
    
    public S getSocket() {
        return socket;
    }
    
    public NetEndpoint getNetEndpoint() {
        return socket.getNetEndpoint();
    }
    
    public <T extends OutputStream> T getOutputStream(Class<T> clazz) {
        return (T) socket.getOutputStream(clazz);
    }
    
    public <T extends OutputStream> T getOutputStream() {
        return (T) socket.getOutputStream();
    }
    
    public AbstractClient processOutputStream(ToughFunction<OutputStream, OutputStream> toughFunction) {
        socket.processOutputStream(toughFunction);
        return this;
    }
    
    public <T extends InputStream> T getInputStream(Class<T> clazz) {
        return (T) socket.getInputStream(clazz);
    }
    
    public <T extends InputStream> T getInputStream() {
        return (T) socket.getInputStream();
    }
    
    public AbstractClient processInputStream(ToughFunction<InputStream, InputStream> toughFunction) {
        socket.processInputStream(toughFunction);
        return this;
    }
    
    @Override
    public String toString() {
        return "AbstractClient{" + "socket=" + socket + '}';
    }
    
}
