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

import java.io.Serializable;
import java.util.Objects;

public class Response<T> implements Serializable {
    
    private final T response;
    private final Throwable throwable;
    
    public Response(T response) {
        this(response, null);
    }
    
    public Response(Throwable throwable) {
        this(null, throwable);
    }
    
    public Response(T response, Throwable throwable) {
        this.response = response;
        this.throwable = throwable;
    }
    
    public final T getResponse() {
        return response;
    }
    
    public final Throwable getThrowable() {
        return throwable;
    }
    
    public final boolean isSuccessful() {
        return throwable == null;
    }
    
    public final boolean isErrored() {
        return throwable != null;
    }
    
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        final Response<?> response1 = (Response<?>) object;
        return Objects.equals(response, response1.response) && Objects.equals(throwable, response1.throwable);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(response, throwable);
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" + "response=" + response + ", throwable=" + throwable + '}';
    }
    
}
