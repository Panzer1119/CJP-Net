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

package de.codemakers.net.entities;

import de.codemakers.base.util.interfaces.Snowflake;

import java.io.Serializable;
import java.util.Objects;

public class Request implements Serializable, Snowflake {
    
    private final long id;
    private final Object request;
    
    public Request(Object request) {
        this.id = generateId();
        this.request = request;
    }
    
    public Request(long id, Object request) {
        this.id = id;
        this.request = request;
    }
    
    public final Object getRequest() {
        return request;
    }
    
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        final Request request1 = (Request) object;
        return id == request1.id && Objects.equals(this.request, request1.request);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, request);
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" + "id=" + id + ", request=" + request + '}';
    }
    
    @Override
    public long getId() {
        return id;
    }
    
}
