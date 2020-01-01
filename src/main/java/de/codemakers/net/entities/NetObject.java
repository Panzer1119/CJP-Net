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

package de.codemakers.net.entities;

import de.codemakers.base.util.interfaces.Snowflake;

import java.io.Serializable;
import java.util.Objects;

public abstract class NetObject implements Serializable, Snowflake {
    
    protected final long id;
    protected final NetEndpoint source;
    protected final NetEndpoint destination;
    
    public NetObject() {
        this.id = generateId();
        this.source = null;
        this.destination = null;
    }
    
    public NetObject(NetEndpoint source, NetEndpoint destination) {
        this.id = generateId();
        this.source = source;
        this.destination = destination;
    }
    
    public NetObject(long id) {
        this.id = id;
        this.source = null;
        this.destination = null;
    }
    
    public NetObject(long id, NetEndpoint source, NetEndpoint destination) {
        this.id = id;
        this.source = source;
        this.destination = destination;
    }
    
    @Override
    public long getId() {
        return id;
    }
    
    public NetEndpoint getSource() {
        return source;
    }
    
    public NetEndpoint getDestination() {
        return destination;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final NetObject netObject = (NetObject) o;
        return id == netObject.id && Objects.equals(source, netObject.source) && Objects.equals(destination, netObject.destination);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, source, destination);
    }
    
    @Override
    public String toString() {
        return "NetObject{" + "id=" + id + ", source=" + source + ", destination=" + destination + '}';
    }
    
}
