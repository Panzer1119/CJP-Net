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
import java.util.Objects;

public class ConnectionEvent extends NetEvent {
    
    protected final InetAddress inetAddress;
    protected final int port;
    
    public ConnectionEvent(InetAddress inetAddress, int port) {
        super();
        this.inetAddress = inetAddress;
        this.port = port;
    }
    
    public ConnectionEvent(long timestamp, InetAddress inetAddress, int port) {
        super(timestamp);
        this.inetAddress = inetAddress;
        this.port = port;
    }
    
    public ConnectionEvent(long id, long timestamp, InetAddress inetAddress, int port) {
        super(id, timestamp);
        this.inetAddress = inetAddress;
        this.port = port;
    }
    
    public final InetAddress getInetAddress() {
        return inetAddress;
    }
    
    public final int getPort() {
        return port;
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" + "inetAddress=" + inetAddress + ", port=" + port + ", id=" + id + ", timestamp=" + timestamp + '}';
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), inetAddress, port);
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
        final ConnectionEvent that = (ConnectionEvent) o;
        return port == that.port && Objects.equals(inetAddress, that.inetAddress);
    }
    
}
