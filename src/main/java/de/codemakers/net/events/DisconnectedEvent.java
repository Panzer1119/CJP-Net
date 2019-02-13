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

package de.codemakers.net.events;

import java.util.Objects;

public class DisconnectedEvent extends NetEvent {
    
    private final boolean ok;
    private final boolean local;
    private final Throwable throwable;
    
    public DisconnectedEvent(boolean ok, boolean local, Throwable throwable) {
        super();
        this.ok = ok;
        this.local = local;
        this.throwable = throwable;
    }
    
    public DisconnectedEvent(long timestamp, boolean ok, boolean local, Throwable throwable) {
        super(timestamp);
        this.ok = ok;
        this.local = local;
        this.throwable = throwable;
    }
    
    public DisconnectedEvent(long id, long timestamp, boolean ok, boolean local, Throwable throwable) {
        super(id, timestamp);
        this.ok = ok;
        this.local = local;
        this.throwable = throwable;
    }
    
    public final boolean isOk() {
        return ok;
    }
    
    public final boolean isLocal() {
        return local;
    }
    
    public final Throwable getThrowable() {
        return throwable;
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" + "ok=" + ok + ", local=" + local + ", throwable=" + throwable + ", id=" + id + ", timestamp=" + timestamp + '}';
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), ok, throwable);
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
        final DisconnectedEvent that = (DisconnectedEvent) o;
        return ok == that.ok && Objects.equals(throwable, that.throwable);
    }
    
}
