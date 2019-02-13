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

import de.codemakers.base.entities.results.ReturningResult;
import de.codemakers.base.util.interfaces.Snowflake;

import java.io.Serializable;
import java.util.Objects;

public class Response<T> implements Serializable, Snowflake {
    
    protected final long id;
    protected final ReturningResult<T> result;
    protected transient Request<?> request = null;
    
    public Response(T response) {
        this(new ReturningResult<>(true, null, response));
    }
    
    public Response(Throwable throwable) {
        this(new ReturningResult<>(false, throwable, (T) null));
    }
    
    public Response(ReturningResult<T> result) {
        Objects.requireNonNull(result);
        this.id = generateId();
        this.result = result;
    }
    
    public Response(long id, T response) {
        this(id, new ReturningResult<>(true, null, response));
    }
    
    public Response(long id, Throwable throwable) {
        this(id, new ReturningResult<>(false, throwable, (T) null));
    }
    
    public Response(long id, ReturningResult<T> result) {
        Objects.requireNonNull(result);
        this.id = id;
        this.result = result;
    }
    
    @Override
    public long getId() {
        return id;
    }
    
    public ReturningResult<T> getResult() {
        return result;
    }
    
    public T getResponse() {
        return result.getResult();
    }
    
    public Throwable getThrowable() {
        return result.getThrowable();
    }
    
    public boolean wasSuccessful() {
        return result.wasSuccessful();
    }
    
    public boolean hasErrored() {
        return result.hasThrowable();
    }
    
    public <R> Request<R> getRequest() {
        return (Request<R>) request;
    }
    
    protected Response<T> setRequest(Request<?> request) {
        this.request = request;
        return this;
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" + "id=" + id + ", result=" + result + (request == null ? "" : ", request=" + request) + '}';
    }
    
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || !getClass().isAssignableFrom(object.getClass())) {
            return false;
        }
        final Response<?> that = (Response<?>) object;
        return id == that.id && Objects.equals(result, that.result);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, result);
    }
    
}
