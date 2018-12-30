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

import java.util.Objects;

public class RequestResponseEvent extends NetEvent {
    
    protected final long responseId;
    protected final Object data;
    
    public RequestResponseEvent(Object data) {
        this(nextId(), data);
    }
    
    public RequestResponseEvent(long responseId, Object data) {
        this.responseId = responseId;
        this.data = data;
    }
    
    public RequestResponseEvent(long timestamp, long responseId, Object data) {
        super(timestamp);
        this.responseId = responseId;
        this.data = data;
    }
    
    public RequestResponseEvent(long id, long timestamp, long responseId, Object data) {
        super(id, timestamp);
        this.responseId = responseId;
        this.data = data;
    }
    
    public final long getResponseId() {
        return responseId;
    }
    
    public final Object getData() {
        return data;
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" + "responseId=" + responseId + ", data=" + data + ", id=" + id + ", timestamp=" + timestamp + '}';
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), responseId, data);
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
        final RequestResponseEvent that = (RequestResponseEvent) o;
        return responseId == that.responseId && Objects.equals(data, that.data);
    }
    
}
