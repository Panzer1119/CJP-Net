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

package de.codemakers.net.entities;

import de.codemakers.base.entities.AbstractEndpoint;

import java.net.InetAddress;
import java.util.Objects;

public class NetEndpoint extends AbstractEndpoint {
    
    protected final InetAddress inetAddress;
    protected final int port;
    
    public NetEndpoint(InetAddress inetAddress, int port) {
        this.inetAddress = inetAddress;
        this.port = port;
    }
    
    public NetEndpoint(long id, InetAddress inetAddress, int port) {
        super(id);
        this.inetAddress = inetAddress;
        this.port = port;
    }
    
    public InetAddress getInetAddress() {
        return inetAddress;
    }
    
    public int getPort() {
        return port;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (id != 0 && !super.equals(o)) {
            return false;
        }
        final NetEndpoint that = (NetEndpoint) o;
        return port == that.port && Objects.equals(inetAddress, that.inetAddress);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), inetAddress, port);
    }
    
    @Override
    public String toString() {
        return "NetEndpoint{" + "inetAddress=" + inetAddress + ", port=" + port + ", id=" + id + '}';
    }
    
}