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

import java.util.Arrays;

public class DataReceivedEvent extends ClientEvent {
    
    private final byte[] data;
    
    public DataReceivedEvent(byte[] data) {
        this.data = data;
    }
    
    public DataReceivedEvent(long timestamp, byte[] data) {
        super(timestamp);
        this.data = data;
    }
    
    public DataReceivedEvent(long id, long timestamp, byte[] data) {
        super(id, timestamp);
        this.data = data;
    }
    
    public final byte[] getData() {
        return data;
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" + "data=" + Arrays.toString(data) + ", id=" + id + ", timestamp=" + timestamp + '}';
    }
    
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Arrays.hashCode(data);
        return result;
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
        final DataReceivedEvent that = (DataReceivedEvent) o;
        return Arrays.equals(data, that.data);
    }
    
}
