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

public class ObjectReceived extends NetEvent {
    
    private final Object object;
    
    public ObjectReceived(Object object) {
        super();
        this.object = object;
    }
    
    public ObjectReceived(long timestamp, Object object) {
        super(timestamp);
        this.object = object;
    }
    
    public ObjectReceived(long id, long timestamp, Object object) {
        super(id, timestamp);
        this.object = object;
    }
    
    public final Object getObject() {
        return object;
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" + "object=" + object + ", id=" + id + ", timestamp=" + timestamp + '}';
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), object);
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
        final ObjectReceived that = (ObjectReceived) o;
        return Objects.equals(object, that.object);
    }
}
