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

public class ResponseEvent extends RequestResponseEvent {
    
    protected final boolean accepted;
    
    public ResponseEvent(Object data, boolean accepted) {
        super(data);
        this.accepted = accepted;
    }
    
    public ResponseEvent(long responseId, Object data, boolean accepted) {
        super(responseId, data);
        this.accepted = accepted;
    }
    
    public ResponseEvent(long timestamp, long responseId, Object data, boolean accepted) {
        super(timestamp, responseId, data);
        this.accepted = accepted;
    }
    
    public ResponseEvent(long id, long timestamp, long responseId, Object data, boolean accepted) {
        super(id, timestamp, responseId, data);
        this.accepted = accepted;
    }
    
    public final boolean isAccepted() {
        return accepted;
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" + "accepted=" + accepted + ", responseId=" + responseId + ", data=" + data + ", id=" + id + ", timestamp=" + timestamp + '}';
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), accepted);
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
        final ResponseEvent that = (ResponseEvent) o;
        return accepted == that.accepted;
    }
    
}
