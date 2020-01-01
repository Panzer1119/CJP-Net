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

import java.util.Objects;

public class NetObjectHolder<T> extends NetObject {
    
    protected final T object;
    
    public NetObjectHolder(T object) {
        this.object = object;
    }
    
    public NetObjectHolder(T object, NetEndpoint source, NetEndpoint destination) {
        super(source, destination);
        this.object = object;
    }
    
    public NetObjectHolder(long id, T object) {
        super(id);
        this.object = object;
    }
    
    public NetObjectHolder(long id, T object, NetEndpoint source, NetEndpoint destination) {
        super(id, source, destination);
        this.object = object;
    }
    
    public T getObject() {
        return object;
    }
    
    public <D> D getObject(Class<D> clazz) {
        return (D) object;
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
        final NetObjectHolder<?> that = (NetObjectHolder<?>) o;
        return Objects.equals(object, that.object);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), object);
    }
    
    @Override
    public String toString() {
        return "NetObjectHolder{" + "object=" + object + ", id=" + id + ", source=" + source + ", destination=" + destination + '}';
    }
    
}
