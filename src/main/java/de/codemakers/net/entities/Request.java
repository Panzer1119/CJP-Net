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

import de.codemakers.base.action.ReturningAction;
import de.codemakers.base.entities.results.ReturningResult;
import de.codemakers.base.util.interfaces.Snowflake;
import de.codemakers.base.util.tough.ToughFunction;

import java.io.Serializable;
import java.util.Objects;

public class Request<T> implements Serializable, Snowflake {
    
    protected final long id;
    protected final T request;
    
    public Request(T request) {
        this.id = generateId();
        this.request = request;
    }
    
    public Request(long id, T request) {
        this.id = id;
        this.request = request;
    }
    
    @Override
    public long getId() {
        return id;
    }
    
    public T getRequest() {
        return request;
    }
    
    public <R> Response<R> respond(ToughFunction<T, R> function) {
        Objects.requireNonNull(function);
        return new Response<>(id, new ReturningResult<>(function, request)).setRequest(this);
    }
    
    public <R> ReturningAction<Response<R>> respondAction(ToughFunction<T, R> function) {
        return new ReturningAction<>(() -> respond(function));
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" + "id=" + id + ", request=" + request + '}';
    }
    
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || !getClass().isAssignableFrom(object.getClass())) {
            return false;
        }
        final Request request1 = (Request) object;
        return id == request1.id && Objects.equals(this.request, request1.request);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, request);
    }
    
}
