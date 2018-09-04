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

package de.codemakers.net.events;

import java.net.InetAddress;
import java.net.Socket;
import java.util.Objects;

public class SocketAcceptedEvent extends ConnectionEvent {
    
    protected final Socket socket;
    
    public SocketAcceptedEvent(Socket socket) {
        super(socket.getInetAddress(), socket.getPort());
        this.socket = socket;
    }
    
    public SocketAcceptedEvent(long timestamp, Socket socket) {
        super(timestamp, socket.getInetAddress(), socket.getPort());
        this.socket = socket;
    }
    
    public SocketAcceptedEvent(long id, long timestamp, Socket socket) {
        super(id, timestamp, socket.getInetAddress(), socket.getPort());
        this.socket = socket;
    }
    
    public final Socket getSocket() {
        return socket;
    }
    
    public final InetAddress getLocalAddress() {
        if (socket == null) {
            return null;
        }
        return socket.getLocalAddress();
    }
    
    public final int getLocalPort() {
        if (socket == null) {
            return -1;
        }
        return socket.getLocalPort();
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" + "socket=" + socket + ", inetAddress=" + inetAddress + ", port=" + port + ", id=" + id + ", timestamp=" + timestamp + '}';
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), socket);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        final SocketAcceptedEvent that = (SocketAcceptedEvent) o;
        return Objects.equals(socket, that.socket);
    }
    
}
